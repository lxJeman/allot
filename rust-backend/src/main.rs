use axum::{
    routing::{get, post},
    Json, Router,
};
use tower_http::cors::{CorsLayer, Any};
use serde::{Deserialize, Serialize};
use std::net::SocketAddr;
use std::time::Duration;
use base64::Engine;

#[derive(Deserialize)]
struct AnalyzeRequest {
    image: String,  // Base64 encoded image
    width: u32,
    height: u32,
    timestamp: u64,
}

#[derive(Serialize)]
struct AnalyzeResponse {
    id: String,
    status: String,
    analysis: Analysis,
    processing_time_ms: u64,
    timestamp: u64,
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
    detected_text: Vec<String>,
    content_type: String,
    risk_factors: Vec<String>,
    recommendation: String,
}

#[derive(Serialize)]
struct HealthResponse {
    status: String,
    message: String,
    uptime: String,
}

#[tokio::main]
async fn main() {
    println!("🚀 Starting Allot Backend Server...");
    
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
    println!("🌐 Server listening on http://0.0.0.0:3000");
    println!("📱 Device should connect to http://192.168.100.47:3000");
    println!("📡 Ready to receive screen captures from Allot app");
    
    let listener = tokio::net::TcpListener::bind(addr).await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

async fn health_check() -> Json<HealthResponse> {
    Json(HealthResponse {
        status: "healthy".to_string(),
        message: "Allot Backend is running and ready to analyze screen content".to_string(),
        uptime: "Running".to_string(),
    })
}

async fn analyze_screen(Json(payload): Json<AnalyzeRequest>) -> Json<AnalyzeResponse> {
    let start_time = std::time::Instant::now();
    
    println!("📸 Received screen capture: {}x{} pixels", payload.width, payload.height);
    
    // Simulate AI processing time (1-3 seconds as mentioned in requirements)
    let processing_delay = Duration::from_millis(2500); // 2.5 seconds
    tokio::time::sleep(processing_delay).await;
    
    // Simulate AI analysis results
    let analysis = simulate_ai_analysis(&payload);
    
    let processing_time = start_time.elapsed().as_millis() as u64;
    
    println!("🧠 Analysis complete: {} ({}ms)", analysis.category, processing_time);
    
    Json(AnalyzeResponse {
        id: uuid::Uuid::new_v4().to_string(),
        status: "completed".to_string(),
        analysis,
        processing_time_ms: processing_time,
        timestamp: chrono::Utc::now().timestamp_millis() as u64,
    })
}

fn simulate_ai_analysis(request: &AnalyzeRequest) -> Analysis {
    // Simulate different content analysis scenarios
    let scenarios = vec![
        ("safe_content", 0.95, false, "continue", vec!["Educational content", "News article"], "news", vec![], "Content appears safe to view"),
        ("political_content", 0.87, true, "scroll", vec!["Political discussion", "Election news"], "political", vec!["Political bias", "Controversial topic"], "Consider scrolling past political content"),
        ("toxic_content", 0.92, true, "blur", vec!["Toxic comment", "Hate speech"], "toxic", vec!["Offensive language", "Personal attacks"], "Content blocked due to toxic language"),
        ("clickbait", 0.78, true, "scroll", vec!["You won't believe what happened next!", "Shocking revelation"], "clickbait", vec!["Sensational headline", "Engagement bait"], "Potential clickbait detected"),
        ("advertisement", 0.85, false, "continue", vec!["Buy now", "Limited time offer"], "advertisement", vec!["Commercial content"], "Advertisement detected but not harmful"),
    ];
    
    // Randomly select a scenario based on timestamp
    let scenario_index = (request.timestamp % scenarios.len() as u64) as usize;
    let (category, confidence, harmful, action, detected_text, content_type, risk_factors, recommendation) = &scenarios[scenario_index];
    
    Analysis {
        category: category.to_string(),
        confidence: *confidence,
        harmful: *harmful,
        action: action.to_string(),
        details: AnalysisDetails {
            detected_text: detected_text.iter().map(|s| s.to_string()).collect(),
            content_type: content_type.to_string(),
            risk_factors: risk_factors.iter().map(|s| s.to_string()).collect(),
            recommendation: recommendation.to_string(),
        },
    }
}

// Helper function to decode base64 image (for future use)
#[allow(dead_code)]
fn decode_base64_image(base64_data: &str) -> Result<Vec<u8>, base64::DecodeError> {
    base64::engine::general_purpose::STANDARD.decode(base64_data)
}