/**
 * AI Detection Service - Phase 3 Ver 1.0
 * 
 * Integrates Google Vision API (OCR) + Rust Backend (Groq Classification)
 * for real-time harmful content detection
 */

import AsyncStorage from '@react-native-async-storage/async-storage';
import { sha256 } from 'react-native-sha256';

// ============================================================================
// TYPES
// ============================================================================

export interface DetectionConfig {
  blockList: string[];
  minConfidence: number;
  enableCache: boolean;
  cacheExpiryHours: number;
}

export interface DetectionResult {
  id: string;
  category: string;
  confidence: number;
  harmful: boolean;
  action: 'continue' | 'scroll' | 'blur';
  detectedText: string;
  riskFactors: string[];
  recommendation: string;
  benchmark: {
    ocrTimeMs: number;
    classificationTimeMs: number;
    totalTimeMs: number;
    textLength: number;
    cached: boolean;
  };
  timestamp: number;
}

interface CachedResult {
  result: DetectionResult;
  cachedAt: number;
}

// ============================================================================
// CONFIGURATION
// ============================================================================

const DEFAULT_CONFIG: DetectionConfig = {
  blockList: ['political', 'toxic', 'clickbait'],
  minConfidence: 0.80,
  enableCache: true,
  cacheExpiryHours: 24,
};

const GOOGLE_VISION_API_KEY = 'AIzaSyB_qQtOrwHrBCfq9vayfldfJ0QdDQ0D7Vo';
const BACKEND_URL = 'http://192.168.100.47:3000';

// ============================================================================
// AI DETECTION SERVICE
// ============================================================================

class AIDetectionService {
  private config: DetectionConfig = DEFAULT_CONFIG;
  private cache: Map<string, CachedResult> = new Map();
  private stats = {
    totalRequests: 0,
    cacheHits: 0,
    cacheMisses: 0,
    avgProcessingTime: 0,
  };

  /**
   * Update detection configuration
   */
  updateConfig(config: Partial<DetectionConfig>) {
    this.config = { ...this.config, ...config };
    console.log('🔧 AI Detection config updated:', this.config);
  }

  /**
   * Get current configuration
   */
  getConfig(): DetectionConfig {
    return { ...this.config };
  }

  /**
   * Main detection pipeline
   */
  async detectHarmfulContent(base64Image: string): Promise<DetectionResult> {
    const startTime = Date.now();
    this.stats.totalRequests++;

    console.log('🎯 [AI Detection] Starting pipeline...');

    try {
      // Step 1: Extract text using Google Vision API
      const ocrStart = Date.now();
      const extractedText = await this.extractTextFromImage(base64Image);
      const ocrTime = Date.now() - ocrStart;

      console.log(`👁️  [AI Detection] OCR complete: ${extractedText.length} chars (${ocrTime}ms)`);

      // If no text detected, return safe
      if (!extractedText.trim()) {
        console.log('ℹ️  [AI Detection] No text detected - marking as safe');
        return this.createSafeResult(ocrTime, 0, startTime);
      }

      // Step 2: Normalize text
      const normalizedText = this.normalizeText(extractedText);
      console.log(`🔄 [AI Detection] Text normalized: ${extractedText.length} -> ${normalizedText.length} chars`);

      // Step 3: Check cache
      if (this.config.enableCache) {
        const textHash = await sha256(normalizedText);
        const cached = this.getCachedResult(textHash);
        
        if (cached) {
          this.stats.cacheHits++;
          console.log(`✅ [AI Detection] Cache hit! (${this.stats.cacheHits}/${this.stats.totalRequests})`);
          return {
            ...cached,
            benchmark: {
              ...cached.benchmark,
              cached: true,
            },
          };
        }
        
        this.stats.cacheMisses++;
        console.log(`❌ [AI Detection] Cache miss (${this.stats.cacheMisses}/${this.stats.totalRequests})`);
      }

      // Step 4: Send to backend for classification
      const classificationStart = Date.now();
      const result = await this.classifyWithBackend(base64Image);
      const classificationTime = Date.now() - classificationStart;

      console.log(`🧠 [AI Detection] Classification: ${result.category} (${result.confidence * 100}%) - ${result.action}`);

      // Step 5: Cache result
      if (this.config.enableCache) {
        const textHash = await sha256(normalizedText);
        this.cacheResult(textHash, result);
      }

      // Step 6: Apply block list filter
      const shouldBlock = this.shouldBlockContent(result);
      if (shouldBlock) {
        console.log(`🚫 [AI Detection] Content blocked: ${result.category}`);
      }

      const totalTime = Date.now() - startTime;
      this.updateStats(totalTime);

      console.log(`✅ [AI Detection] Pipeline complete: ${totalTime}ms (OCR: ${ocrTime}ms, LLM: ${classificationTime}ms)`);

      return result;

    } catch (error) {
      console.error('❌ [AI Detection] Pipeline failed:', error);
      throw error;
    }
  }

  /**
   * Extract text from image using Google Vision API
   */
  private async extractTextFromImage(base64Image: string): Promise<string> {
    const url = `https://vision.googleapis.com/v1/images:annotate?key=${GOOGLE_VISION_API_KEY}`;

    const requestBody = {
      requests: [
        {
          image: {
            content: base64Image,
          },
          features: [
            {
              type: 'TEXT_DETECTION',
              maxResults: 1,
            },
          ],
        },
      ],
    };

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Google Vision API error: ${response.status} - ${errorText}`);
    }

    const data = await response.json();

    // Extract full text from first annotation
    const textAnnotations = data.responses?.[0]?.textAnnotations;
    if (textAnnotations && textAnnotations.length > 0) {
      return textAnnotations[0].description || '';
    }

    return '';
  }

  /**
   * Classify text using Rust backend (Groq API)
   */
  private async classifyWithBackend(base64Image: string): Promise<DetectionResult> {
    const response = await fetch(`${BACKEND_URL}/analyze`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        image: base64Image,
        width: 720,
        height: 1600,
        timestamp: Date.now(),
      }),
    });

    if (!response.ok) {
      throw new Error(`Backend error: ${response.status}`);
    }

    const data = await response.json();

    return {
      id: data.id,
      category: data.analysis.category,
      confidence: data.analysis.confidence,
      harmful: data.analysis.harmful,
      action: data.analysis.action,
      detectedText: data.analysis.details.detected_text,
      riskFactors: data.analysis.details.risk_factors,
      recommendation: data.analysis.details.recommendation,
      benchmark: {
        ocrTimeMs: data.benchmark.ocr_time_ms,
        classificationTimeMs: data.benchmark.classification_time_ms,
        totalTimeMs: data.benchmark.total_time_ms,
        textLength: data.benchmark.text_length,
        cached: data.benchmark.cached,
      },
      timestamp: data.timestamp,
    };
  }

  /**
   * Normalize text (remove emojis, URLs, special chars)
   */
  private normalizeText(text: string): string {
    return text
      .replace(/https?:\/\/[^\s]+/g, '') // Remove URLs
      .replace(/[^\w\s]/g, '') // Remove special chars
      .replace(/\s+/g, ' ') // Normalize whitespace
      .trim()
      .toLowerCase();
  }

  /**
   * Check if content should be blocked based on config
   */
  private shouldBlockContent(result: DetectionResult): boolean {
    return (
      result.harmful &&
      result.confidence >= this.config.minConfidence &&
      this.config.blockList.includes(result.category)
    );
  }

  /**
   * Get cached result if valid
   */
  private getCachedResult(textHash: string): DetectionResult | null {
    const cached = this.cache.get(textHash);
    
    if (!cached) {
      return null;
    }

    const expiryMs = this.config.cacheExpiryHours * 60 * 60 * 1000;
    const isExpired = Date.now() - cached.cachedAt > expiryMs;

    if (isExpired) {
      this.cache.delete(textHash);
      return null;
    }

    return cached.result;
  }

  /**
   * Cache detection result
   */
  private cacheResult(textHash: string, result: DetectionResult) {
    this.cache.set(textHash, {
      result,
      cachedAt: Date.now(),
    });

    // Limit cache size to 100 entries
    if (this.cache.size > 100) {
      const firstKey = this.cache.keys().next().value;
      if (firstKey) {
        this.cache.delete(firstKey);
      }
    }
  }

  /**
   * Create safe result for no-text scenarios
   */
  private createSafeResult(ocrTime: number, classificationTime: number, startTime: number): DetectionResult {
    return {
      id: `safe-${Date.now()}`,
      category: 'safe_content',
      confidence: 1.0,
      harmful: false,
      action: 'continue',
      detectedText: '',
      riskFactors: [],
      recommendation: 'No text detected in image',
      benchmark: {
        ocrTimeMs: ocrTime,
        classificationTimeMs: classificationTime,
        totalTimeMs: Date.now() - startTime,
        textLength: 0,
        cached: false,
      },
      timestamp: Date.now(),
    };
  }

  /**
   * Update statistics
   */
  private updateStats(processingTime: number) {
    const totalTime = this.stats.avgProcessingTime * (this.stats.totalRequests - 1) + processingTime;
    this.stats.avgProcessingTime = totalTime / this.stats.totalRequests;
  }

  /**
   * Get statistics
   */
  getStats() {
    return {
      ...this.stats,
      cacheHitRate: this.stats.totalRequests > 0 
        ? (this.stats.cacheHits / this.stats.totalRequests * 100).toFixed(2) + '%'
        : '0%',
    };
  }

  /**
   * Clear cache
   */
  clearCache() {
    this.cache.clear();
    console.log('🗑️  Cache cleared');
  }

  /**
   * Log telemetry event
   */
  async logTelemetry(result: DetectionResult, action: string) {
    const telemetryEvent = {
      timestamp: Date.now(),
      category: result.category,
      confidence: result.confidence,
      action,
      harmful: result.harmful,
      textLength: result.benchmark.textLength,
      processingTime: result.benchmark.totalTimeMs,
    };

    console.log('📊 [Telemetry]', telemetryEvent);

    // Store in AsyncStorage for later analysis
    try {
      const key = `telemetry_${Date.now()}`;
      await AsyncStorage.setItem(key, JSON.stringify(telemetryEvent));
    } catch (error) {
      console.error('Failed to log telemetry:', error);
    }
  }
}

// ============================================================================
// EXPORT SINGLETON
// ============================================================================

export const aiDetectionService = new AIDetectionService();
