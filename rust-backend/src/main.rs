use axum::{
    routing::{get, post},
    Json, Router,
};
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};
use std::net::SocketAddr;
use std::time::Instant;
use tower_http::cors::{Any, CorsLayer};
use tracing::{error, info};

// ============================================================================
// REQUEST/RESPONSE TYPES
// ============================================================================

#[derive(Deserialize)]
struct AnalyzeRequest {
    image: String, // Base64 encoded image
    width: u32,
    height: u32,
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
    ocr_time_ms: u64,
    classification_time_ms: u64,
    total_time_ms: u64,
    text_length: usize,
    cached: bool,
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
            google_vision_api_key: "AIzaSyB_qQtOrwHrBCfq9vayfldfJ0QdDQ0D7Vo".to_string(),
            groq_api_key: "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".to_string(),
            groq_model: "openai/gpt-oss-20b".to_string(),
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

    info!("🚀 Starting Allot AI Detection Backend (Phase 3 - Ver 1.0)");
    info!("🧠 Model: openai/gpt-oss-20b");
    info!("👁️  OCR: Google Vision API");

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
    info!("📱 Device should connect to http://192.168.100.47:3000");
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

    info!(
        "📸 [{}] Received screen capture: {}x{}",
        request_id, payload.width, payload.height
    );

    let config = Config::from_env();

    // Step 1: Extract text using Google Vision API
    let ocr_start = Instant::now();
    let extracted_text = match extract_text_from_image(&config, &payload.image).await {
        Ok(text) => {
            let ocr_time = ocr_start.elapsed().as_millis() as u64;
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
    };
    let ocr_time = ocr_start.elapsed().as_millis() as u64;

    // Check if text is empty
    if extracted_text.trim().is_empty() {
        info!("ℹ️  [{}] No text detected in image", request_id);
        return create_safe_response(request_id, ocr_time, 0, total_start);
    }

    // Step 2: Normalize text
    let normalized_text = normalize_text(&extracted_text);
    info!(
        "🔄 [{}] Text normalized: {} -> {} chars",
        request_id,
        extracted_text.len(),
        normalized_text.len()
    );

    // Step 3: Check cache (using text hash)
    let text_hash = calculate_hash(&normalized_text);
    info!("🔑 [{}] Text hash: {}", request_id, &text_hash[..16]);

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
            return create_error_response(request_id, "Classification failed");
        }
    };
    let classification_time = classification_start.elapsed().as_millis() as u64;

    let total_time = total_start.elapsed().as_millis() as u64;

    // Enhanced logging with full details
    info!("✅ [{}] ═══════════════════════════════════════", request_id);
    info!("✅ [{}] ANALYSIS COMPLETE", request_id);
    info!("✅ [{}] ═══════════════════════════════════════", request_id);
    info!("📝 [{}] Extracted Text ({} chars):", request_id, extracted_text.len());
    if extracted_text.len() > 200 {
        info!("   \"{}...\"", &extracted_text[..200]);
    } else if !extracted_text.is_empty() {
        info!("   \"{}\"", extracted_text);
    } else {
        info!("   (no text detected)");
    }
    info!("🏷️  [{}] Category: {}", request_id, classification_with_tokens.result.category);
    info!("📊 [{}] Confidence: {:.1}%", request_id, classification_with_tokens.result.confidence * 100.0);
    info!("⚠️  [{}] Harmful: {}", request_id, if classification_with_tokens.result.harmful { "YES ⚠️" } else { "NO ✅" });
    info!("🎯 [{}] Action: {}", request_id, classification_with_tokens.result.action.to_uppercase());
    if !classification_with_tokens.result.risk_factors.is_empty() {
        info!("🚨 [{}] Risk Factors:", request_id);
        for factor in &classification_with_tokens.result.risk_factors {
            info!("   • {}", factor);
        }
    }
    info!("💡 [{}] Recommendation: {}", request_id, classification_with_tokens.result.recommendation);
    info!("⏱️  [{}] Timing: Total={}ms (OCR={}ms, LLM={}ms)", request_id, total_time, ocr_time, classification_time);
    info!("✅ [{}] ═══════════════════════════════════════", request_id);

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
            classification_time_ms: classification_time,
            total_time_ms: total_time,
            text_length: normalized_text.len(),
            cached: false,
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

    let system_prompt = r#"You are a content moderation AI. Analyze the provided text and classify it as either SAFE or HARMFUL.

HARMFUL content includes:
- Toxic: Hate speech, harassment, offensive language, personal attacks, bullying
- Political: Political discussions, election content, partisan debates
- Clickbait: Sensational headlines, engagement bait, misleading titles
- Violence: Graphic violence, threats, dangerous content
- NSFW: Adult content, explicit material

SAFE content includes:
- Educational content
- News and information
- Entertainment
- Normal social interactions
- Advertisements (unless toxic)

Respond ONLY with valid JSON in this exact format:
{
  "category": "safe_content" or "harmful_content",
  "confidence": 0.85,
  "harmful": true or false,
  "action": "continue" or "blur",
  "risk_factors": ["specific reason 1", "specific reason 2"],
  "recommendation": "Brief explanation of why this content is safe or harmful"
}

Rules:
- If harmful=true, action MUST be "blur"
- If harmful=false, action MUST be "continue"
- Be strict with toxic, political, and harmful content
- Be lenient with educational and entertainment content
- Include specific risk factors when harmful"#;

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

    // Parse JSON from response
    let result: ClassificationResult = serde_json::from_str(&content).map_err(|e| {
        format!(
            "Failed to parse classification result: {} | Content: {}",
            e, content
        )
    })?;

    // Extract token usage
    let tokens = groq_response.usage;

    Ok(ClassificationWithTokens { result, tokens })
}

// ============================================================================
// UTILITIES
// ============================================================================

fn normalize_text(text: &str) -> String {
    text.chars()
        .filter(|c| c.is_alphanumeric() || c.is_whitespace())
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<&str>>()
        .join(" ")
        .to_lowercase()
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
            classification_time_ms: 0,
            total_time_ms: 0,
            text_length: 0,
            cached: false,
            tokens_used: None,
        },
    })
}

fn create_safe_response(
    request_id: String,
    ocr_time: u64,
    classification_time: u64,
    total_start: Instant,
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
                recommendation: "No text detected in image".to_string(),
            },
        },
        processing_time_ms: total_time,
        timestamp: chrono::Utc::now().timestamp_millis() as u64,
        benchmark: BenchmarkData {
            ocr_time_ms: ocr_time,
            classification_time_ms: classification_time,
            total_time_ms: total_time,
            text_length: 0,
            cached: false,
            tokens_used: None,
        },
    })
}
