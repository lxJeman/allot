/**
 * AI Detection Service - Updated for Local ML Kit Integration
 * 
 * Now works with Local ML Kit text extraction + Rust Backend (Groq Classification)
 * for real-time harmful content detection. Google Vision API removed.
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
    mlKitTimeMs: number; // Changed from ocrTimeMs
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

// Removed Google Vision API key - no longer needed
const BACKEND_URL = 'http://192.168.100.55:3000';

// ============================================================================
// AI DETECTION SERVICE
// ============================================================================

class AIDetectionService {
  private config: DetectionConfig = DEFAULT_CONFIG;
  private cache: Map<string, CachedResult> = new Map();
  private activeRequests: Map<string, AbortController> = new Map();
  private cancelledRequests: Set<string> = new Set();
  private stats = {
    totalRequests: 0,
    cacheHits: 0,
    cacheMisses: 0,
    avgProcessingTime: 0,
    cancelledRequests: 0,
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
   * Main detection pipeline - now works with pre-extracted text from Local ML Kit
   */
  async detectHarmfulContent(extractedText: string, imageWidth?: number, imageHeight?: number): Promise<DetectionResult> {
    const startTime = Date.now();
    const requestId = `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    this.stats.totalRequests++;

    console.log(`🎯 [AI Detection] Starting pipeline with pre-extracted text... (ID: ${requestId})`);
    console.log(`📝 [AI Detection] Text length: ${extractedText.length} chars`);

    // Create abort controller for this request
    const abortController = new AbortController();
    this.activeRequests.set(requestId, abortController);

    try {
      // If no text provided, return safe
      if (!extractedText.trim()) {
        console.log('ℹ️  [AI Detection] No text provided - marking as safe');
        return this.createSafeResult(0, 0, startTime);
      }

      // Step 1: Normalize text
      const normalizedText = this.normalizeText(extractedText);
      console.log(`🔄 [AI Detection] Text normalized: ${extractedText.length} -> ${normalizedText.length} chars`);

      // Step 2: Check cache
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

      // Step 3: Check if request was cancelled before backend call
      if (this.cancelledRequests.has(requestId)) {
        console.log(`🚫 [AI Detection] Request ${requestId} was cancelled before backend call`);
        throw new Error('Request cancelled due to scroll detection');
      }

      // Step 4: Send extracted text to backend for classification
      const classificationStart = Date.now();
      const result = await this.classifyTextWithBackend(extractedText, imageWidth, imageHeight, requestId);
      const classificationTime = Date.now() - classificationStart;

      // Step 5: Check if request was cancelled after backend call
      if (this.cancelledRequests.has(requestId)) {
        console.log(`🚫 [AI Detection] Request ${requestId} was cancelled after backend call - ignoring result`);
        throw new Error('Request cancelled due to scroll detection');
      }

      console.log(`🧠 [AI Detection] Classification: ${result.category} (${result.confidence * 100}%) - ${result.action}`);

      // Step 6: Cache result
      if (this.config.enableCache) {
        const textHash = await sha256(normalizedText);
        this.cacheResult(textHash, result);
      }

      // Step 7: Apply block list filter
      const shouldBlock = this.shouldBlockContent(result);
      if (shouldBlock) {
        console.log(`🚫 [AI Detection] Content blocked: ${result.category}`);
      }

      const totalTime = Date.now() - startTime;
      this.updateStats(totalTime);

      console.log(`✅ [AI Detection] Pipeline complete: ${totalTime}ms (Classification: ${classificationTime}ms)`);

      return result;

    } catch (error) {
      if (error.message?.includes('cancelled') || this.cancelledRequests.has(requestId)) {
        this.stats.cancelledRequests++;
        console.log(`🚫 [AI Detection] Request cancelled (ID: ${requestId})`);
        throw new Error('Request cancelled due to scroll detection');
      }
      console.error(`❌ [AI Detection] Pipeline failed (ID: ${requestId}):`, error);
      throw error;
    } finally {
      // Clean up abort controller and cancelled flag
      this.activeRequests.delete(requestId);
      this.cancelledRequests.delete(requestId);
    }
  }

  /**
   * Legacy method for backward compatibility - now extracts text first using SmartDetectionModule
   */
  async detectHarmfulContentFromImage(base64Image: string): Promise<DetectionResult> {
    console.log('⚠️  [AI Detection] Legacy image method called - extracting text first...');
    
    try {
      // Use SmartDetectionModule to extract text locally
      const { SmartDetectionModule } = require('react-native').NativeModules;
      
      if (!SmartDetectionModule) {
        throw new Error('SmartDetectionModule not available');
      }

      const extractionResult = await SmartDetectionModule.extractText(base64Image);
      const extractedText = extractionResult.extractedText || '';
      
      console.log(`📝 [AI Detection] Text extracted locally: ${extractedText.length} chars`);
      
      // Now process the extracted text
      return this.detectHarmfulContent(extractedText);
      
    } catch (error) {
      console.error('❌ [AI Detection] Failed to extract text locally:', error);
      throw error;
    }
  }

  /**
   * Classify extracted text using Rust backend (Groq API)
   */
  private async classifyTextWithBackend(extractedText: string, imageWidth?: number, imageHeight?: number, requestId?: string): Promise<DetectionResult> {
    const startTime = Date.now();
    
    // Check if cancelled before making request
    if (requestId && this.cancelledRequests.has(requestId)) {
      throw new Error('Request cancelled before backend call');
    }

    console.log(`🌐 [AI Detection] Making backend request (ID: ${requestId})`);
    
    // Add network timeout to prevent hanging
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
      console.log(`⏰ [AI Detection] Network timeout - aborting request (ID: ${requestId})`);
      controller.abort();
    }, 5000); // 5 second network timeout - give backend enough time
    
    try {
      const classificationStartTime = Date.now();
      const response = await fetch(`${BACKEND_URL}/analyze`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          extracted_text: extractedText,
          width: imageWidth || 720,
          height: imageHeight || 1600,
          timestamp: Date.now(),
          source: 'local_ml_kit',
          request_id: requestId,
        }),
        signal: controller.signal,
      });
      
      clearTimeout(timeoutId);
      
      // Check if cancelled after request
      if (requestId && this.cancelledRequests.has(requestId)) {
        throw new Error('Request cancelled after backend call');
      }

      if (!response.ok) {
        throw new Error(`Backend error: ${response.status}`);
      }

      const data = await response.json();
      const classificationTime = Date.now() - classificationStartTime;

      console.log(`✅ [AI Detection] Backend response received (ID: ${requestId})`);

      return {
        id: data.id,
        category: data.analysis.category,
        confidence: data.analysis.confidence,
        harmful: data.analysis.harmful,
        action: data.analysis.action,
        detectedText: extractedText,
        riskFactors: data.analysis.risk_factors || [],
        recommendation: data.analysis.recommendation || 'No recommendation provided',
        benchmark: {
          mlKitTimeMs: 0, // Already extracted by ML Kit
          classificationTimeMs: classificationTime,
          totalTimeMs: Date.now() - startTime,
          textLength: extractedText.length,
          cached: false,
        },
        timestamp: Date.now(),
      };
    } catch (error) {
      clearTimeout(timeoutId);
      
      if (error.name === 'AbortError') {
        console.log(`⏰ [AI Detection] Request aborted due to timeout (ID: ${requestId})`);
        throw new Error('Network timeout - backend unreachable');
      }
      
      throw error;
    }
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
  private createSafeResult(mlKitTime: number, classificationTime: number, startTime: number): DetectionResult {
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
        mlKitTimeMs: mlKitTime,
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
   * Cancel all active requests (called when scroll is detected)
   */
  cancelAllRequests() {
    const activeCount = this.activeRequests.size;
    console.log(`🚫 [AI Detection] Cancelling ${activeCount} active requests due to scroll`);
    
    for (const [requestId, controller] of this.activeRequests.entries()) {
      // Mark as cancelled
      this.cancelledRequests.add(requestId);
      
      // Try to abort the controller (may not work in React Native)
      try {
        controller.abort();
        console.log(`🚫 [AI Detection] Aborted request: ${requestId}`);
      } catch (error) {
        console.log(`⚠️ [AI Detection] Could not abort request ${requestId}:`, error);
      }
    }
    
    this.stats.cancelledRequests += activeCount;
    
    console.log(`✅ [AI Detection] All requests marked as cancelled (${activeCount} total)`);
    console.log(`📊 [AI Detection] Cancelled requests will be ignored when they complete`);
  }

  /**
   * Get statistics
   */
  getStats() {
    return {
      ...this.stats,
      activeRequests: this.activeRequests.size,
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
