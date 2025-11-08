/**
 * Phase 3 Integration Example
 * 
 * This example shows how to integrate the AI Detection Pipeline
 * with your existing screen capture system.
 */

import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Button, ActivityIndicator } from 'react-native';
import { NativeModules, NativeEventEmitter } from 'react-native';
import { aiDetectionService } from '@/services/aiDetectionService';

const { ScreenCaptureModule } = NativeModules;
const screenCaptureEmitter = new NativeEventEmitter(ScreenCaptureModule);

export default function Phase3IntegrationExample() {
  const [isCapturing, setIsCapturing] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [lastResult, setLastResult] = useState<any>(null);
  const [stats, setStats] = useState<any>(null);

  useEffect(() => {
    // Configure AI detection
    aiDetectionService.updateConfig({
      blockList: ['political', 'toxic', 'clickbait'],
      minConfidence: 0.80,
      enableCache: true,
      cacheExpiryHours: 24,
    });

    // Listen for screen captures
    const subscription = screenCaptureEmitter.addListener(
      'onScreenCaptured',
      handleScreenCapture
    );

    return () => {
      subscription.remove();
    };
  }, []);

  const handleScreenCapture = async (data: { image: string; width: number; height: number }) => {
    if (isProcessing) {
      console.log('⏭️  Skipping - already processing');
      return;
    }

    setIsProcessing(true);

    try {
      console.log('🎯 Processing screenshot...');

      // Detect harmful content
      const result = await aiDetectionService.detectHarmfulContent(data.image);

      console.log('✅ Detection complete:', {
        category: result.category,
        confidence: result.confidence,
        action: result.action,
        cached: result.benchmark.cached,
      });

      setLastResult(result);

      // Take action based on result
      if (result.harmful) {
        handleHarmfulContent(result);
      }

      // Log telemetry
      await aiDetectionService.logTelemetry(result, result.action);

      // Update stats
      setStats(aiDetectionService.getStats());

    } catch (error) {
      console.error('❌ Detection failed:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleHarmfulContent = (result: any) => {
    console.log('🚫 Harmful content detected:', result.category);

    switch (result.action) {
      case 'blur':
        console.log('🚫 Action: Blur overlay');
        // TODO: Show blur overlay
        // showBlurOverlay(result);
        break;

      case 'scroll':
        console.log('⏭️  Action: Auto-scroll');
        // TODO: Trigger auto-scroll
        // triggerAutoScroll();
        break;

      case 'continue':
        console.log('✅ Action: Continue (safe)');
        break;
    }
  };

  const startCapture = async () => {
    try {
      setIsCapturing(true);
      await ScreenCaptureModule.startScreenCapture();
      console.log('📸 Screen capture started');
    } catch (error) {
      console.error('❌ Failed to start capture:', error);
      setIsCapturing(false);
    }
  };

  const stopCapture = async () => {
    try {
      await ScreenCaptureModule.stopScreenCapture();
      setIsCapturing(false);
      console.log('⏹️  Screen capture stopped');
    } catch (error) {
      console.error('❌ Failed to stop capture:', error);
    }
  };

  const clearCache = () => {
    aiDetectionService.clearCache();
    setStats(aiDetectionService.getStats());
    console.log('🗑️  Cache cleared');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Phase 3 Integration Example</Text>

      {/* Controls */}
      <View style={styles.controls}>
        {!isCapturing ? (
          <Button title="Start Detection" onPress={startCapture} />
        ) : (
          <Button title="Stop Detection" onPress={stopCapture} color="red" />
        )}
        <View style={styles.spacer} />
        <Button title="Clear Cache" onPress={clearCache} />
      </View>

      {/* Status */}
      <View style={styles.status}>
        <Text style={styles.statusText}>
          Status: {isCapturing ? '🟢 Active' : '🔴 Inactive'}
        </Text>
        {isProcessing && (
          <View style={styles.processing}>
            <ActivityIndicator size="small" color="#007AFF" />
            <Text style={styles.processingText}>Processing...</Text>
          </View>
        )}
      </View>

      {/* Last Result */}
      {lastResult && (
        <View style={styles.result}>
          <Text style={styles.resultTitle}>Last Detection:</Text>
          <Text style={styles.resultText}>
            Category: {lastResult.category}
          </Text>
          <Text style={styles.resultText}>
            Confidence: {(lastResult.confidence * 100).toFixed(1)}%
          </Text>
          <Text style={styles.resultText}>
            Action: {lastResult.action}
          </Text>
          <Text style={styles.resultText}>
            Harmful: {lastResult.harmful ? '🚫 Yes' : '✅ No'}
          </Text>
          <Text style={styles.resultText}>
            Processing Time: {lastResult.benchmark.totalTimeMs}ms
          </Text>
          <Text style={styles.resultText}>
            Cached: {lastResult.benchmark.cached ? '✅ Yes' : '❌ No'}
          </Text>
          {lastResult.riskFactors.length > 0 && (
            <Text style={styles.resultText}>
              Risk Factors: {lastResult.riskFactors.join(', ')}
            </Text>
          )}
        </View>
      )}

      {/* Statistics */}
      {stats && (
        <View style={styles.stats}>
          <Text style={styles.statsTitle}>Statistics:</Text>
          <Text style={styles.statsText}>
            Total Requests: {stats.totalRequests}
          </Text>
          <Text style={styles.statsText}>
            Cache Hits: {stats.cacheHits}
          </Text>
          <Text style={styles.statsText}>
            Cache Misses: {stats.cacheMisses}
          </Text>
          <Text style={styles.statsText}>
            Cache Hit Rate: {stats.cacheHitRate}
          </Text>
          <Text style={styles.statsText}>
            Avg Processing Time: {stats.avgProcessingTime.toFixed(0)}ms
          </Text>
        </View>
      )}

      {/* Instructions */}
      <View style={styles.instructions}>
        <Text style={styles.instructionsTitle}>How to use:</Text>
        <Text style={styles.instructionsText}>
          1. Make sure backend is running: cd rust-backend && cargo run
        </Text>
        <Text style={styles.instructionsText}>
          2. Tap "Start Detection" to begin
        </Text>
        <Text style={styles.instructionsText}>
          3. Screenshots will be analyzed automatically
        </Text>
        <Text style={styles.instructionsText}>
          4. Harmful content will be detected and logged
        </Text>
        <Text style={styles.instructionsText}>
          5. Check statistics to see cache performance
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  controls: {
    marginBottom: 20,
  },
  spacer: {
    height: 10,
  },
  status: {
    padding: 15,
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
    marginBottom: 20,
  },
  statusText: {
    fontSize: 16,
    fontWeight: '600',
  },
  processing: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 10,
  },
  processingText: {
    marginLeft: 10,
    fontSize: 14,
    color: '#007AFF',
  },
  result: {
    padding: 15,
    backgroundColor: '#e8f4f8',
    borderRadius: 8,
    marginBottom: 20,
  },
  resultTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  resultText: {
    fontSize: 14,
    marginBottom: 5,
  },
  stats: {
    padding: 15,
    backgroundColor: '#f0f0f0',
    borderRadius: 8,
    marginBottom: 20,
  },
  statsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  statsText: {
    fontSize: 14,
    marginBottom: 5,
  },
  instructions: {
    padding: 15,
    backgroundColor: '#fff3cd',
    borderRadius: 8,
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  instructionsText: {
    fontSize: 12,
    marginBottom: 5,
    color: '#856404',
  },
});
