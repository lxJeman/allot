use axum::{
    routing::{get, post},
    Json, Router,
};
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};
use std::collections::HashMap;
use std::net::SocketAddr;
use std::sync::{Arc, Mutex};
use std::time::{Instant, SystemTime, UNIX_EPOCH};
use tower_http::cors::{Any, CorsLayer};
use tracing::{error, info};

// ============================================================================
// REQUEST/RESPONSE TYPES
// ============================================================================

#[derive(Deserialize)]
struct AnalyzeRequest {
    // Support both old image format and new text format
    image: Option<String>, // Base64 encoded image (legacy)
    extracted_text: Option<String>, // Pre-extracted text (new)
    width: u32,
    height: u32,
    source: Option<String>, // "local_ml_kit" or "google_vision"
    #[allow(dead_code)]
    timestamp: u64,
}

#[derive(Serialize)]
struct AnalyzeResponse {
    id: String,
    status: String,
    analysis: Analysis,
    processing_time_ms: u64,
    timestamp: u64,
    benchmark: BenchmarkData,
}

#[derive(Serialize)]
struct Analysis {
    category: String,
    confidence: f32,
    harmful: bool,
    action: String,
    details: AnalysisDetails,
}

#[derive(Serialize)]
struct AnalysisDetails {
    detected_text: String,
    content_type: String,
    risk_factors: Vec<String>,
    recommendation: String,
}

#[derive(Serialize)]
struct BenchmarkData {
    ocr_time_ms: u64, // Keep for backward compatibility
    ml_kit_time_ms: u64, // New field for ML Kit timing
    classification_time_ms: u64,
    total_time_ms: u64,
    text_length: usize,
    cached: bool,
    source: String, // "google_vision", "local_ml_kit", etc.
    tokens_used: Option<TokenUsage>,
}

#[derive(Serialize)]
struct TokenUsage {
    prompt_tokens: u32,
    completion_tokens: u32,
    total_tokens: u32,
}

#[derive(Serialize)]
struct HealthResponse {
    status: String,
    message: String,
    uptime: String,
}

// ============================================================================
// GOOGLE VISION API TYPES
// ============================================================================

#[derive(Serialize)]
struct VisionRequest {
    requests: Vec<VisionImageRequest>,
}

#[derive(Serialize)]
struct VisionImageRequest {
    image: VisionImage,
    features: Vec<VisionFeature>,
}

#[derive(Serialize)]
struct VisionImage {
    content: String, // Base64 image
}

#[derive(Serialize)]
struct VisionFeature {
    #[serde(rename = "type")]
    feature_type: String,
    #[serde(rename = "maxResults")]
    max_results: u32,
}

#[derive(Deserialize)]
struct VisionResponse {
    responses: Vec<VisionAnnotation>,
}

#[derive(Deserialize)]
struct VisionAnnotation {
    #[serde(rename = "textAnnotations")]
    text_annotations: Option<Vec<TextAnnotation>>,
}

#[derive(Deserialize)]
struct TextAnnotation {
    description: String,
}

// ============================================================================
// GROQ API TYPES
// ============================================================================

#[derive(Serialize)]
struct GroqRequest {
    model: String,
    messages: Vec<GroqMessage>,
    temperature: f32,
    max_tokens: u32,
}

#[derive(Serialize)]
struct GroqMessage {
    role: String,
    content: String,
}

#[derive(Deserialize)]
struct GroqResponse {
    choices: Vec<GroqChoice>,
    usage: Option<GroqUsage>,
}

#[derive(Deserialize)]
struct GroqChoice {
    message: GroqMessageResponse,
}

#[derive(Deserialize)]
struct GroqUsage {
    prompt_tokens: u32,
    completion_tokens: u32,
    total_tokens: u32,
}

#[derive(Deserialize)]
struct GroqMessageResponse {
    content: String,
}

#[derive(Deserialize)]
struct ClassificationResult {
    category: String,
    confidence: f32,
    harmful: bool,
    action: String,
    risk_factors: Vec<String>,
    recommendation: String,
}

struct ClassificationWithTokens {
    result: ClassificationResult,
    tokens: Option<GroqUsage>,
}

// ============================================================================
// CONFIGURATION
// ============================================================================

struct Config {
    google_vision_api_key: String,
    groq_api_key: String,
    groq_model: String,
}

impl Config {
    fn from_env() -> Self {
        Self {
            google_vision_api_key: std::env::var("GOOGLE_VISION_API_KEY")
                .expect("GOOGLE_VISION_API_KEY must be set"),
            groq_api_key: std::env::var("GROQ_API_KEY")
                .expect("GROQ_API_KEY must be set"),
            groq_model: std::env::var("GROQ_MODEL")
                .unwrap_or_else(|_| "openai/gpt-oss-20b".to_string()),
        }
    }
}

// ============================================================================
// CACHE
// ============================================================================

#[derive(Clone)]
struct CachedResult {
    category: String,
    confidence: f32,
    harmful: bool,
    action: String,
    risk_factors: Vec<String>,
    recommendation: String,
    cached_at: u64,
}

type ResultCache = Arc<Mutex<HashMap<String, CachedResult>>>;

fn create_cache() -> ResultCache {
    Arc::new(Mutex::new(HashMap::new()))
}

fn get_cached_result(cache: &ResultCache, hash: &str) -> Option<CachedResult> {
    let cache_lock = cache.lock().unwrap();
    let result = cache_lock.get(hash)?;

    // Check if expired (24 hours)
    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs();
    let age = now - result.cached_at;

    if age > 86400 {
        // 24 hours
        None
    } else {
        Some(result.clone())
    }
}

fn set_cached_result(cache: &ResultCache, hash: String, result: &ClassificationResult) {
    let mut cache_lock = cache.lock().unwrap();

    let cached = CachedResult {
        category: result.category.clone(),
        confidence: result.confidence,
        harmful: result.harmful,
        action: result.action.clone(),
        risk_factors: result.risk_factors.clone(),
        recommendation: result.recommendation.clone(),
        cached_at: SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_secs(),
    };

    cache_lock.insert(hash, cached);

    // Limit cache size to 1000 entries
    if cache_lock.len() > 1000 {
        // Remove oldest entry
        if let Some(oldest_key) = cache_lock.keys().next().cloned() {
            cache_lock.remove(&oldest_key);
        }
    }
}

// ============================================================================
// MAIN
// ============================================================================

#[tokio::main]
async fn main() {
    // Initialize tracing
    tracing_subscriber::fmt()
        .with_target(false)
        .with_thread_ids(false)
        .with_level(true)
        .with_ansi(true)
        .init();

    info!("🚀 Starting Allot AI Detection Backend (Merged System - Ver 2.0)");
    info!("🧠 Model: openai/gpt-oss-20b");
    info!("🤖 OCR: Local ML Kit (on-device) + Google Vision API (legacy fallback)");

    let cors = CorsLayer::new()
        .allow_origin(Any)
        .allow_methods(Any)
        .allow_headers(Any);

    let app = Router::new()
        .route("/", get(health_check))
        .route("/health", get(health_check))
        .route("/analyze", post(analyze_screen))
        .layer(cors);

    let addr = SocketAddr::from(([0, 0, 0, 0], 3000));
    info!("🌐 Server listening on http://0.0.0.0:3000");
    info!("📱 Device should connect to http://192.168.100.55:3000");
    info!("📡 Ready to receive screen captures");

    let listener = tokio::net::TcpListener::bind(addr).await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

// ============================================================================
// HANDLERS
// ============================================================================

async fn health_check() -> Json<HealthResponse> {
    Json(HealthResponse {
        status: "healthy".to_string(),
        message: "Allot AI Detection Backend is running".to_string(),
        uptime: "Running".to_string(),
    })
}

async fn analyze_screen(Json(payload): Json<AnalyzeRequest>) -> Json<AnalyzeResponse> {
    let request_id = uuid::Uuid::new_v4().to_string();
    let total_start = Instant::now();

    let source = payload.source.as_deref().unwrap_or("unknown");
    
    info!(
        "📸 [{}] Received request: {}x{} (source: {})",
        request_id, payload.width, payload.height, source
    );

    let config = Config::from_env();
    let mut ocr_time = 0u64;
    let mut ml_kit_time = 0u64;

    // Determine if we have pre-extracted text or need to extract from image
    let extracted_text = if let Some(text) = payload.extracted_text {
        // NEW PATH: Pre-extracted text from Local ML Kit
        info!(
            "🤖 [{}] Using pre-extracted text from Local ML Kit: {} chars",
            request_id,
            text.len()
        );
        ml_kit_time = 0; // ML Kit timing is handled in native layer
        text
    } else if let Some(image) = payload.image {
        // LEGACY PATH: Extract text using Google Vision API
        info!("👁️  [{}] Extracting text using Google Vision API", request_id);
        
        let ocr_start = Instant::now();
        match extract_text_from_image(&config, &image).await {
            Ok(text) => {
                ocr_time = ocr_start.elapsed().as_millis() as u64;
                info!(
                    "👁️  [{}] OCR complete: {} chars extracted ({}ms)",
                    request_id,
                    text.len(),
                    ocr_time
                );
                text
            }
            Err(e) => {
                error!("❌ [{}] OCR failed: {}", request_id, e);
                return create_error_response(request_id, "OCR failed");
            }
        }
    } else {
        error!("❌ [{}] No image or extracted_text provided", request_id);
        return create_error_response(request_id, "No image or text provided");
    };

    // Check if text is empty
    if extracted_text.trim().is_empty() {
        info!("ℹ️  [{}] No text detected", request_id);
        return create_safe_response(request_id, ocr_time, ml_kit_time, 0, total_start, source);
    }

    // Step 2: Filter out overlay text (our own warning messages)
    let filtered_text = filter_overlay_text(&extracted_text);
    
    // Step 3: Normalize text
    let normalized_text = normalize_text(&filtered_text);
    info!(
        "🔄 [{}] Text filtered & normalized: {} -> {} -> {} chars",
        request_id,
        extracted_text.len(),
        filtered_text.len(),
        normalized_text.len()
    );

    // Step 4: Check cache (using text hash)
    let text_hash = calculate_hash(&normalized_text);
    info!("🔑 [{}] Text hash: {}", request_id, &text_hash[..16]);

    // Create cache if not exists
    static CACHE: once_cell::sync::Lazy<ResultCache> =
        once_cell::sync::Lazy::new(|| create_cache());

    // Check if we already analyzed this text
    if let Some(cached) = get_cached_result(&CACHE, &text_hash) {
        info!("💾 [{}] Cache HIT - returning cached result", request_id);
        let total_time = total_start.elapsed().as_millis() as u64;

        return Json(AnalyzeResponse {
            id: request_id,
            status: "completed".to_string(),
            analysis: Analysis {
                category: cached.category,
                confidence: cached.confidence,
                harmful: cached.harmful,
                action: cached.action,
                details: AnalysisDetails {
                    detected_text: extracted_text,
                    content_type: "text".to_string(),
                    risk_factors: cached.risk_factors,
                    recommendation: cached.recommendation,
                },
            },
            processing_time_ms: total_time,
            timestamp: chrono::Utc::now().timestamp_millis() as u64,
            benchmark: BenchmarkData {
                ocr_time_ms: ocr_time,
                ml_kit_time_ms: ml_kit_time,
                classification_time_ms: 0,
                total_time_ms: total_time,
                text_length: normalized_text.len(),
                cached: true,
                source: source.to_string(),
                tokens_used: None,
            },
        });
    }

    info!("🔍 [{}] Cache MISS - analyzing with AI", request_id);

    // Step 4: Classify using Groq API
    let classification_start = Instant::now();
    let classification_with_tokens = match classify_text(&config, &normalized_text).await {
        Ok(result) => {
            let classification_time = classification_start.elapsed().as_millis() as u64;
            let token_info = if let Some(ref tokens) = result.tokens {
                format!(
                    " | Tokens: {} in, {} out, {} total",
                    tokens.prompt_tokens, tokens.completion_tokens, tokens.total_tokens
                )
            } else {
                String::new()
            };
            info!(
                "🧠 [{}] Classification complete: {} ({:.2}% confidence) ({}ms){}",
                request_id,
                result.result.category,
                result.result.confidence * 100.0,
                classification_time,
                token_info
            );
            result
        }
        Err(e) => {
            error!("❌ [{}] Classification failed: {}", request_id, e);
            // Return safe result as fallback to avoid blocking the pipeline
            let classification_time = classification_start.elapsed().as_millis() as u64;
            let total_time = total_start.elapsed().as_millis() as u64;
            
            info!("⚠️  [{}] Returning SAFE as fallback due to classification error", request_id);
            
            return Json(AnalyzeResponse {
                id: request_id,
                status: "completed_with_warning".to_string(),
                analysis: Analysis {
                    category: "safe_content".to_string(),
                    confidence: 0.5,
                    harmful: false,
                    action: "continue".to_string(),
                    details: AnalysisDetails {
                        detected_text: extracted_text,
                        content_type: "text".to_string(),
                        risk_factors: vec!["Classification error - marked as safe by default".to_string()],
                        recommendation: "Classification failed, defaulting to safe to avoid false positives".to_string(),
                    },
                },
                processing_time_ms: total_time,
                timestamp: chrono::Utc::now().timestamp_millis() as u64,
                benchmark: BenchmarkData {
                    ocr_time_ms: ocr_time,
                    ml_kit_time_ms: ml_kit_time,
                    classification_time_ms: classification_time,
                    total_time_ms: total_time,
                    text_length: normalized_text.len(),
                    cached: false,
                    source: source.to_string(),
                    tokens_used: None,
                },
            });
        }
    };
    let classification_time = classification_start.elapsed().as_millis() as u64;

    // Cache the result
    set_cached_result(
        &CACHE,
        text_hash.clone(),
        &classification_with_tokens.result,
    );
    info!("💾 [{}] Result cached for future requests", request_id);

    let total_time = total_start.elapsed().as_millis() as u64;

    // Enhanced logging with full details
    info!(
        "✅ [{}] ═══════════════════════════════════════",
        request_id
    );
    info!("✅ [{}] ANALYSIS COMPLETE", request_id);
    info!(
        "✅ [{}] ═══════════════════════════════════════",
        request_id
    );
    info!(
        "📝 [{}] Extracted Text ({} chars):",
        request_id,
        extracted_text.len()
    );
    if extracted_text.len() > 200 {
        // Safe substring that respects UTF-8 boundaries
        let truncated: String = extracted_text.chars().take(200).collect();
        info!("   \"{}...\"", truncated);
    } else if !extracted_text.is_empty() {
        info!("   \"{}\"", extracted_text);
    } else {
        info!("   (no text detected)");
    }
    info!(
        "🏷️  [{}] Category: {}",
        request_id, classification_with_tokens.result.category
    );
    info!(
        "📊 [{}] Confidence: {:.1}%",
        request_id,
        classification_with_tokens.result.confidence * 100.0
    );
    info!(
        "⚠️  [{}] Harmful: {}",
        request_id,
        if classification_with_tokens.result.harmful {
            "YES ⚠️"
        } else {
            "NO ✅"
        }
    );
    info!(
        "🎯 [{}] Action: {}",
        request_id,
        classification_with_tokens.result.action.to_uppercase()
    );
    if !classification_with_tokens.result.risk_factors.is_empty() {
        info!("🚨 [{}] Risk Factors:", request_id);
        for factor in &classification_with_tokens.result.risk_factors {
            info!("   • {}", factor);
        }
    }
    info!(
        "💡 [{}] Recommendation: {}",
        request_id, classification_with_tokens.result.recommendation
    );
    info!(
        "⏱️  [{}] Timing: Total={}ms (OCR={}ms, LLM={}ms)",
        request_id, total_time, ocr_time, classification_time
    );
    info!(
        "✅ [{}] ═══════════════════════════════════════",
        request_id
    );

    // Convert token usage to serializable format
    let tokens_used = classification_with_tokens.tokens.map(|t| TokenUsage {
        prompt_tokens: t.prompt_tokens,
        completion_tokens: t.completion_tokens,
        total_tokens: t.total_tokens,
    });

    Json(AnalyzeResponse {
        id: request_id,
        status: "completed".to_string(),
        analysis: Analysis {
            category: classification_with_tokens.result.category,
            confidence: classification_with_tokens.result.confidence,
            harmful: classification_with_tokens.result.harmful,
            action: classification_with_tokens.result.action,
            details: AnalysisDetails {
                detected_text: extracted_text,
                content_type: "text".to_string(),
                risk_factors: classification_with_tokens.result.risk_factors,
                recommendation: classification_with_tokens.result.recommendation,
            },
        },
        processing_time_ms: total_time,
        timestamp: chrono::Utc::now().timestamp_millis() as u64,
        benchmark: BenchmarkData {
            ocr_time_ms: ocr_time,
            ml_kit_time_ms: ml_kit_time,
            classification_time_ms: classification_time,
            total_time_ms: total_time,
            text_length: normalized_text.len(),
            cached: false,
            source: source.to_string(),
            tokens_used,
        },
    })
}

// ============================================================================
// GOOGLE VISION API
// ============================================================================

async fn extract_text_from_image(config: &Config, base64_image: &str) -> Result<String, String> {
    let client = reqwest::Client::new();
    let url = format!(
        "https://vision.googleapis.com/v1/images:annotate?key={}",
        config.google_vision_api_key
    );

    let request_body = VisionRequest {
        requests: vec![VisionImageRequest {
            image: VisionImage {
                content: base64_image.to_string(),
            },
            features: vec![VisionFeature {
                feature_type: "TEXT_DETECTION".to_string(),
                max_results: 1,
            }],
        }],
    };

    let response = client
        .post(&url)
        .json(&request_body)
        .send()
        .await
        .map_err(|e| format!("Vision API request failed: {}", e))?;

    if !response.status().is_success() {
        let status = response.status();
        let error_text = response.text().await.unwrap_or_default();
        return Err(format!("Vision API error {}: {}", status, error_text));
    }

    let vision_response: VisionResponse = response
        .json()
        .await
        .map_err(|e| format!("Failed to parse Vision API response: {}", e))?;

    // Extract full text from first annotation
    if let Some(annotations) = vision_response
        .responses
        .get(0)
        .and_then(|r| r.text_annotations.as_ref())
    {
        if let Some(first_annotation) = annotations.get(0) {
            return Ok(first_annotation.description.clone());
        }
    }

    Ok(String::new())
}

// ============================================================================
// GROQ API
// ============================================================================

async fn classify_text(config: &Config, text: &str) -> Result<ClassificationWithTokens, String> {
    let client = reqwest::Client::new();
    let url = "https://api.groq.com/openai/v1/chat/completions";

    let system_prompt = r##"Detect WAR content on social media. Be FAST and DECISIVE.

HARMFUL (war_content):
- War hashtags: #war #military #ukraine #army #combat #soldier
- War keywords: war, military, soldier, combat, ukraine, army, tank, troops
- Active conflicts: Ukraine, Gaza, Syria
- Military content (ignore "fake" disclaimers)

SAFE (safe_content):
- No war keywords/hashtags
- Entertainment, memes, sports
- Random social posts

RULES:
1. 2+ war keywords OR 1+ war hashtag = HARMFUL
2. Ignore disclaimers ("fake", "training")
3. When unsure with war context = HARMFUL

Respond with JSON ONLY:
{"category":"safe_content","confidence":0.95,"harmful":false,"action":"continue","risk_factors":[],"recommendation":"reason"}

Be CONCISE. Speed matters."##;

    let user_prompt = format!("Analyze this text:\n\n{}", text);

    let request_body = GroqRequest {
        model: config.groq_model.clone(),
        messages: vec![
            GroqMessage {
                role: "system".to_string(),
                content: system_prompt.to_string(),
            },
            GroqMessage {
                role: "user".to_string(),
                content: user_prompt,
            },
        ],
        temperature: 0.3,
        max_tokens: 500,
    };

    let response = client
        .post(url)
        .bearer_auth(&config.groq_api_key)
        .json(&request_body)
        .send()
        .await
        .map_err(|e| format!("Groq API request failed: {}", e))?;

    if !response.status().is_success() {
        let status = response.status();
        let error_text = response.text().await.unwrap_or_default();
        return Err(format!("Groq API error {}: {}", status, error_text));
    }

    let groq_response: GroqResponse = response
        .json()
        .await
        .map_err(|e| format!("Failed to parse Groq API response: {}", e))?;

    let content = groq_response
        .choices
        .get(0)
        .ok_or("No choices in Groq response")?
        .message
        .content
        .clone();

    // Clean the response - remove markdown code blocks if present
    let cleaned_content = content
        .trim()
        .trim_start_matches("```json")
        .trim_start_matches("```")
        .trim_end_matches("```")
        .trim();

    // Parse JSON from response
    let result: ClassificationResult = serde_json::from_str(cleaned_content).map_err(|e| {
        format!(
            "Failed to parse classification result: {} | Content: {}",
            e, cleaned_content
        )
    })?;

    // Extract token usage
    let tokens = groq_response.usage;

    Ok(ClassificationWithTokens { result, tokens })
}

// ============================================================================
// UTILITIES
// ============================================================================

fn filter_overlay_text(text: &str) -> String {
    // Remove our own overlay warning messages to prevent re-detection
    let overlay_patterns = [
        "HARMFUL CONTENT DETECTED",
        "A HARMFUL CONTENT DETECTED",
        "Category: WAR_CONTENT",
        "Confidence:",
        "Scrolling to next content",
        "WAR_CONTENT",
        "SAFE_CONTENT",
    ];
    
    let mut filtered = text.to_string();
    for pattern in &overlay_patterns {
        filtered = filtered.replace(pattern, "");
    }
    
    filtered
}

fn normalize_text(text: &str) -> String {
    // Less aggressive normalization to preserve content differences
    // Keep more characters to distinguish between different screens
    text.chars()
        .filter(|c| c.is_alphanumeric() || c.is_whitespace() || ".,!?-_()[]{}".contains(*c))
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<&str>>()
        .join(" ")
        .to_lowercase()
        // Add position-aware hashing by including character positions
        + &format!("_len{}_pos{}", text.len(), text.chars().take(10).collect::<String>().len())
}

fn calculate_hash(text: &str) -> String {
    let mut hasher = Sha256::new();
    hasher.update(text.as_bytes());
    hex::encode(hasher.finalize())
}

fn create_error_response(request_id: String, error: &str) -> Json<AnalyzeResponse> {
    Json(AnalyzeResponse {
        id: request_id,
        status: "error".to_string(),
        analysis: Analysis {
            category: "error".to_string(),
            confidence: 0.0,
            harmful: false,
            action: "continue".to_string(),
            details: AnalysisDetails {
                detected_text: error.to_string(),
                content_type: "error".to_string(),
                risk_factors: vec![],
                recommendation: "Error occurred during processing".to_string(),
            },
        },
        processing_time_ms: 0,
        timestamp: chrono::Utc::now().timestamp_millis() as u64,
        benchmark: BenchmarkData {
            ocr_time_ms: 0,
            ml_kit_time_ms: 0,
            classification_time_ms: 0,
            total_time_ms: 0,
            text_length: 0,
            cached: false,
            source: "error".to_string(),
            tokens_used: None,
        },
    })
}

fn create_safe_response(
    request_id: String,
    ocr_time: u64,
    ml_kit_time: u64,
    classification_time: u64,
    total_start: Instant,
    source: &str,
) -> Json<AnalyzeResponse> {
    let total_time = total_start.elapsed().as_millis() as u64;

    Json(AnalyzeResponse {
        id: request_id,
        status: "completed".to_string(),
        analysis: Analysis {
            category: "safe_content".to_string(),
            confidence: 1.0,
            harmful: false,
            action: "continue".to_string(),
            details: AnalysisDetails {
                detected_text: String::new(),
                content_type: "no_text".to_string(),
                risk_factors: vec![],
                recommendation: "No text detected".to_string(),
            },
        },
        processing_time_ms: total_time,
        timestamp: chrono::Utc::now().timestamp_millis() as u64,
        benchmark: BenchmarkData {
            ocr_time_ms: ocr_time,
            ml_kit_time_ms: ml_kit_time,
            classification_time_ms: classification_time,
            total_time_ms: total_time,
            text_length: 0,
            cached: false,
            source: source.to_string(),
            tokens_used: None,
        },
    })
}
