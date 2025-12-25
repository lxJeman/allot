import React, { useState, useEffect, useRef } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  Switch,
} from 'react-native';
import { NativeModules } from 'react-native';

const { SmartDetectionModule, ScreenCaptureModule, LocalTextExtractionModule, ScreenPermissionModule } = NativeModules;

interface LocalTextExtractionResult {
  extractedText: string;
  confidence: number;
  textDensity: number;
  processingTimeMs: number;
  textRegions: number;
  validationPassed?: boolean;
  validationScore?: number;
  fallbackUsed?: boolean;
  fallbackStrategy?: string;
  usedCache: boolean;
  roiDetected: boolean;
  isHighQuality: boolean;
}

interface ScreenCaptureStats {
  totalCaptures: number;
  successfulExtractions: number;
  averageProcessingTime: number;
  averageConfidence: number;
  cacheHitRate: number;
  totalTextExtracted: number;
}

const LocalTextExtractionTest: React.FC = () => {
  const [isCapturing, setIsCapturing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [captureStats, setCaptureStats] = useState<ScreenCaptureStats>({
    totalCaptures: 0,
    successfulExtractions: 0,
    averageProcessingTime: 0,
    averageConfidence: 0,
    cacheHitRate: 0,
    totalTextExtracted: 0,
  });
  const [recentExtractions, setRecentExtractions] = useState<LocalTextExtractionResult[]>([]);
  const [enableValidation, setEnableValidation] = useState(true);
  const [enableCaching, setEnableCaching] = useState(true);
  const [captureInterval, setCaptureInterval] = useState(1500); // 1.5 seconds default for better performance
  const [autoScroll, setAutoScroll] = useState(false);
  const [performanceMode, setPerformanceMode] = useState(true);
  const [backgroundMode, setBackgroundMode] = useState(true);

  // Refs for stable state access in loops
  const isCapturingRef = useRef(false);
  const isProcessingRef = useRef(false);
  const captureLoopRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    loadInitialData();
  }, []);

  // Sync refs with state
  useEffect(() => {
    isCapturingRef.current = isCapturing;
  }, [isCapturing]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (captureLoopRef.current) {
        clearInterval(captureLoopRef.current);
        captureLoopRef.current = null;
      }
    };
  }, []);

  const loadInitialData = async () => {
    try {
      // Load cache stats and performance metrics
      if (SmartDetectionModule?.getTextExtractionCacheStats) {
        const cacheStats = await SmartDetectionModule.getTextExtractionCacheStats();
        console.log('📊 Cache stats loaded:', cacheStats);
      }
    } catch (error) {
      console.error('Error loading initial data:', error);
    }
  };

  const checkModuleAvailability = (): boolean => {
    if (!SmartDetectionModule) {
      Alert.alert('Error', 'SmartDetectionModule not available');
      return false;
    }
    if (!ScreenCaptureModule) {
      Alert.alert('Error', 'ScreenCaptureModule not available');
      return false;
    }
    if (!ScreenPermissionModule) {
      Alert.alert('Error', 'ScreenPermissionModule not available');
      return false;
    }
    return true;
  };

  const startLocalTextCapture = async () => {
    if (!checkModuleAvailability()) return;

    try {
      setIsLoading(true);

      // Request screen capture permission
      console.log('🎬 Requesting screen capture permission...');
      const permissionResult = await ScreenPermissionModule.requestScreenCapturePermission();

      if (!permissionResult.granted) {
        Alert.alert('Permission Required', 'Screen capture permission is required for text extraction testing.');
        return;
      }

      // Start screen capture
      console.log('📸 Starting screen capture...');
      await ScreenCaptureModule.startScreenCapture();

      // Start local text extraction service if available
      if (LocalTextExtractionModule) {
        console.log('🤖 Starting local text extraction service...');
        try {
          await LocalTextExtractionModule.startLocalTextExtraction(
            captureInterval,
            enableValidation,
            enableCaching,
            true // enableROIOptimization
          );
          console.log('✅ Local text extraction service started');
        } catch (error) {
          console.log('⚠️ Local text extraction service not available, using direct extraction');
        }
      } else {
        console.log('⚠️ LocalTextExtractionModule not available, using direct extraction');
      }

      setIsCapturing(true);

      // If background mode is disabled, start React Native loop
      if (!backgroundMode) {
        console.log('📱 Starting React Native extraction loop (foreground only)');
        startTextExtractionLoop();
      } else {
        console.log('🌙 Background mode enabled - service will handle extraction independently');
      }

      Alert.alert(
        'Local Text Extraction Started',
        backgroundMode 
          ? `Background capture started! The service will continue running even when the app is minimized.\n\nCapture interval: ${captureInterval}ms\nCheck the notification to monitor progress.`
          : `Foreground capture started with ${captureInterval}ms interval.\n\nNote: Capture will pause when app is minimized.`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error starting local text capture:', error);
      Alert.alert('Error', `Failed to start capture: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const stopLocalTextCapture = async () => {
    try {
      setIsLoading(true);

      console.log('🛑 Stopping local text capture...');

      // Stop the loop first
      if (captureLoopRef.current) {
        clearInterval(captureLoopRef.current);
        captureLoopRef.current = null;
        console.log('✅ Extraction loop stopped');
      }

      // Stop local text extraction service if available
      if (LocalTextExtractionModule) {
        try {
          await LocalTextExtractionModule.stopLocalTextExtraction();
          console.log('✅ Local text extraction service stopped');
        } catch (error) {
          console.log('⚠️ Local text extraction service stop failed:', error);
        }
      }

      // Stop screen capture
      if (ScreenCaptureModule?.stopScreenCapture) {
        await ScreenCaptureModule.stopScreenCapture();
      }

      setIsCapturing(false);

      // Generate final report
      await generateFinalReport();

      Alert.alert(
        'Local Text Extraction Stopped',
        `Capture stopped. Check the final performance report below.`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error stopping capture:', error);
      Alert.alert('Error', `Failed to stop capture: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const startTextExtractionLoop = () => {
    console.log('🔄 Starting text extraction loop...');

    // Clear any existing loop
    if (captureLoopRef.current) {
      clearInterval(captureLoopRef.current);
    }

    const extractionLoop = () => {
      // Check if we should continue capturing using refs
      if (!isCapturingRef.current) {
        console.log('🛑 Loop stopped - capturing disabled');
        if (captureLoopRef.current) {
          clearInterval(captureLoopRef.current);
          captureLoopRef.current = null;
        }
        return;
      }

      // Check if we're already processing
      if (isProcessingRef.current) {
        console.log('⏳ Skipping frame - still processing previous');
        return;
      }

      // Perform extraction
      performLocalTextExtraction();
    };

    // Start the interval
    captureLoopRef.current = setInterval(extractionLoop, captureInterval);
    console.log(`✅ Loop started with ${captureInterval}ms interval`);
  };

  const performLocalTextExtraction = async () => {
    // Set processing flag
    isProcessingRef.current = true;

    try {
      const startTime = Date.now();

      console.log('🎯 Triggering capture...');

      // Capture current screen with timeout handling
      let captureResult;
      try {
        captureResult = await ScreenCaptureModule.captureNextFrame();
      } catch (captureError) {
        console.log(`⚠️ Capture failed: ${captureError instanceof Error ? captureError.message : 'Unknown error'}`);

        // Try to get the last captured frame as fallback
        try {
          console.log('🔄 Trying fallback to last captured frame...');
          captureResult = await ScreenCaptureModule.getLastCapturedFrame();
          console.log('✅ Using fallback frame');
        } catch (fallbackError) {
          console.log(`❌ Fallback also failed: ${fallbackError instanceof Error ? fallbackError.message : 'Unknown error'}`);
          throw captureError; // Re-throw original error
        }
      }

      if (!captureResult?.base64) {
        console.log('⏭️ No screen capture available');
        return;
      }

      console.log(`📸 Screen captured: ${captureResult.width}x${captureResult.height}`);

      // Extract text using our local ML system
      const extractionMethod = enableValidation ?
        SmartDetectionModule.testTextExtractionWithValidation :
        SmartDetectionModule.extractText;

      console.log('🔄 Starting text extraction...');
      const extractionResult = await extractionMethod(captureResult.base64);

      const totalTime = Date.now() - startTime;

      // Log to Rust backend (console logs are forwarded there)
      console.log('');
      console.log('� ════════s═══════════════════════════════');
      console.log('🔍 LOCAL TEXT EXTRACTION RESULT');
      console.log('🔍 ═══════════════════════════════════════');
      console.log(`📝 Extracted Text: "${extractionResult.extractedText || 'No text detected'}"`);
      console.log(`� Confidaence: ${(extractionResult.confidence * 100).toFixed(1)}%`);
      console.log(`📏 Text Density: ${(extractionResult.textDensity * 100).toFixed(1)}%`);
      console.log(`⏱️ ML Processing Time: ${extractionResult.processingTimeMs}ms`);
      console.log(`⏱️ Total Time (capture + ML): ${totalTime}ms`);
      console.log(`🎯 Text Regions: ${extractionResult.textRegions || 0}`);
      console.log(`� Usead Cache: ${extractionResult.usedCache ? 'Yes' : 'No'}`);
      console.log(`🎯 ROI Detected: ${extractionResult.roiDetected ? 'Yes' : 'No'}`);

      if (enableValidation) {
        console.log(`✅ Validation Passed: ${extractionResult.validationPassed ? 'Yes' : 'No'}`);
        console.log(`📊 Validation Score: ${extractionResult.validationScore ? (extractionResult.validationScore * 100).toFixed(1) + '%' : 'N/A'}`);
        console.log(`🔄 Fallback Used: ${extractionResult.fallbackUsed ? 'Yes' : 'No'}`);
        if (extractionResult.fallbackStrategy && extractionResult.fallbackStrategy !== 'NONE') {
          console.log(`�️ Fa═llback Strategy: ${extractionResult.fallbackStrategy}`);
        }
        console.log(`⭐ High Quality: ${extractionResult.isHighQuality ? 'Yes' : 'No'}`);
      }

      console.log('🔍 ═══════════════════════════════════════');
      console.log('');

      // Update UI state
      const newExtraction: LocalTextExtractionResult = {
        extractedText: extractionResult.extractedText || '',
        confidence: extractionResult.confidence || 0,
        textDensity: extractionResult.textDensity || 0,
        processingTimeMs: extractionResult.processingTimeMs || 0,
        textRegions: extractionResult.textRegions || 0,
        validationPassed: extractionResult.validationPassed,
        validationScore: extractionResult.validationScore,
        fallbackUsed: extractionResult.fallbackUsed,
        fallbackStrategy: extractionResult.fallbackStrategy,
        usedCache: extractionResult.usedCache || false,
        roiDetected: extractionResult.roiDetected || false,
        isHighQuality: extractionResult.isHighQuality || false,
      };

      setRecentExtractions(prev => [newExtraction, ...prev.slice(0, 9)]); // Keep last 10

      // Update stats
      setCaptureStats(prev => ({
        totalCaptures: prev.totalCaptures + 1,
        successfulExtractions: extractionResult.extractedText ? prev.successfulExtractions + 1 : prev.successfulExtractions,
        averageProcessingTime: (prev.averageProcessingTime * prev.totalCaptures + totalTime) / (prev.totalCaptures + 1),
        averageConfidence: (prev.averageConfidence * prev.totalCaptures + (extractionResult.confidence * 100)) / (prev.totalCaptures + 1),
        cacheHitRate: extractionResult.usedCache ?
          (prev.cacheHitRate * prev.totalCaptures + 100) / (prev.totalCaptures + 1) :
          (prev.cacheHitRate * prev.totalCaptures) / (prev.totalCaptures + 1),
        totalTextExtracted: prev.totalTextExtracted + (extractionResult.extractedText?.length || 0),
      }));

      // Auto-scroll if enabled and text detected
      if (autoScroll && extractionResult.extractedText && extractionResult.extractedText.length > 10) {
        console.log('📜 Auto-scrolling to next content...');
        // Implement auto-scroll logic here if needed
      }

      console.log(`🔄 Triggering next capture... (Total so far: ${captureStats.totalCaptures})`);

      // Log timestamp to help debug caching issues
      console.log(`⏰ Current time: ${Date.now()}, Last capture: ${captureResult?.timestamp || 'none'}`);

    } catch (error) {
      console.error('❌ Error in local text extraction:', error);
      console.log('❌ ═══════════════════════════════════════');
      console.log('❌ LOCAL TEXT EXTRACTION ERROR');
      console.log('❌ ═══════════════════════════════════════');
      console.log(`❌ Error: ${error instanceof Error ? error.message : 'Unknown error'}`);
      console.log('❌ ═══════════════════════════════════════');
    } finally {
      // Clear processing flag
      isProcessingRef.current = false;
    }
  };

  const testSingleExtraction = async () => {
    if (!checkModuleAvailability()) return;

    try {
      setIsLoading(true);

      console.log('🧪 Testing single screen capture and text extraction...');

      // Request permission if needed
      const permissionResult = await ScreenPermissionModule.requestScreenCapturePermission();

      if (!permissionResult.granted) {
        Alert.alert('Permission Required', 'Screen capture permission is required for text extraction testing.');
        return;
      }

      // Start capture first
      console.log('📸 Starting screen capture...');
      await ScreenCaptureModule.startScreenCapture();

      // Wait a brief moment for capture to initialize
      await new Promise(resolve => setTimeout(resolve, 200));

      // Use LocalTextExtractionModule if available, otherwise fallback
      if (LocalTextExtractionModule) {
        console.log('🤖 Using LocalTextExtractionModule for single extraction...');

        try {
          const result = await LocalTextExtractionModule.performSingleTextExtraction();

          console.log('✅ Single extraction result:', result);

          Alert.alert(
            'Single Extraction Complete',
            `Text: "${result.extractedText || 'No text detected'}"\n` +
            `Confidence: ${((result.confidence || 0) * 100).toFixed(1)}%\n` +
            `Processing Time: ${result.processingTime || 0}ms\n` +
            `Frame: ${result.frameWidth}x${result.frameHeight}`,
            [{ text: 'OK' }]
          );

        } catch (error) {
          console.error('LocalTextExtractionModule failed, trying fallback:', error);
          await performFallbackSingleExtraction();
        }
      } else {
        console.log('⚠️ LocalTextExtractionModule not available, using fallback...');
        await performFallbackSingleExtraction();
      }

    } catch (error) {
      console.error('Error in single extraction test:', error);
      Alert.alert('Error', `Failed to test extraction: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const performFallbackSingleExtraction = async () => {
    try {
      // Capture single frame using the updated method
      console.log('📸 Capturing frame...');
      const captureResult = await ScreenCaptureModule.captureNextFrame();

      if (!captureResult?.base64) {
        Alert.alert('Error', 'Failed to capture screen');
        return;
      }

      console.log(`✅ Frame captured: ${captureResult.width}x${captureResult.height}`);

      // Perform text extraction
      console.log('🔍 Extracting text...');
      const extractionResult = await SmartDetectionModule.extractText(captureResult.base64);

      console.log('✅ Text extraction result:', extractionResult);

      Alert.alert(
        'Single Extraction Complete (Fallback)',
        `Text: "${extractionResult.extractedText || 'No text detected'}"\n` +
        `Confidence: ${((extractionResult.confidence || 0) * 100).toFixed(1)}%\n` +
        `Processing Time: ${extractionResult.processingTimeMs || 0}ms\n` +
        `Frame: ${captureResult.width}x${captureResult.height}`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Fallback extraction failed:', error);
      Alert.alert('Error', `Fallback extraction failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  };

  const generateFinalReport = async () => {
    try {
      // Get comprehensive performance metrics
      const [cacheStats, metrics] = await Promise.all([
        SmartDetectionModule.getTextExtractionCacheStats?.() || {},
        SmartDetectionModule.getTextExtractionMetrics?.() || {},
      ]);

      console.log('');
      console.log('📊 ═══════════════════════════════════════');
      console.log('📊 FINAL PERFORMANCE REPORT');
      console.log('📊 ═══════════════════════════════════════');
      console.log(`� Total Cap═tures: ${captureStats.totalCaptures}`);
      console.log(`✅ Successful Extractions: ${captureStats.successfulExtractions}`);
      console.log(`📈 Success Rate: ${captureStats.totalCaptures > 0 ? ((captureStats.successfulExtractions / captureStats.totalCaptures) * 100).toFixed(1) : 0}%`);
      console.log(`⏱️ Average Total Time: ${captureStats.averageProcessingTime.toFixed(1)}ms`);
      console.log(`📊 Average Confidence: ${captureStats.averageConfidence.toFixed(1)}%`);
      console.log(`� Cacheg Hit Rate: ${captureStats.cacheHitRate.toFixed(1)}%`);
      console.log(`�  Total Text Extracted: ${captureStats.totalTextExtracted} characters`);

      if (cacheStats.hits !== undefined) {
        console.log(`💾 Cache Performance: ${cacheStats.hits} hits, ${cacheStats.misses} misses`);
        console.log(`💾 Cache Efficiency: ${cacheStats.hitRate ? (cacheStats.hitRate * 100).toFixed(1) : 0}%`);
      }

      if (metrics.totalExtractions) {
        console.log(`🔧 ML Kit Performance: ${metrics.averageProcessingTimeMs?.toFixed(1)}ms avg`);
        console.log(`🎯 ML Kit Success Rate: ${metrics.successRate ? (metrics.successRate * 100).toFixed(1) : 0}%`);
      }

      console.log('📊 ═══════════════════════════════════════');
      console.log('');

    } catch (error) {
      console.error('Error generating final report:', error);
    }
  };

  const clearAllData = async () => {
    try {
      if (SmartDetectionModule?.clearAllData) {
        await SmartDetectionModule.clearAllData();
      }

      setCaptureStats({
        totalCaptures: 0,
        successfulExtractions: 0,
        averageProcessingTime: 0,
        averageConfidence: 0,
        cacheHitRate: 0,
        totalTextExtracted: 0,
      });

      setRecentExtractions([]);

      Alert.alert('Success', 'All data cleared');
    } catch (error) {
      console.error('Error clearing data:', error);
      Alert.alert('Error', `Failed to clear data: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>🔍 Local Text Extraction Test</Text>
        <Text style={styles.subtitle}>
          Real-time screen capture with on-device ML Kit text extraction
        </Text>
      </View>

      {/* Module Status */}
      <View style={styles.statusCard}>
        <Text style={styles.statusTitle}>Module Status</Text>
        <Text style={[styles.statusText, { color: SmartDetectionModule ? '#4CAF50' : '#F44336' }]}>
          SmartDetectionModule: {SmartDetectionModule ? '✅ Available' : '❌ Not Found'}
        </Text>
        <Text style={[styles.statusText, { color: ScreenCaptureModule ? '#4CAF50' : '#F44336' }]}>
          ScreenCaptureModule: {ScreenCaptureModule ? '✅ Available' : '❌ Not Found'}
        </Text>
        <Text style={[styles.statusText, { color: ScreenPermissionModule ? '#4CAF50' : '#F44336' }]}>
          ScreenPermissionModule: {ScreenPermissionModule ? '✅ Available' : '❌ Not Found'}
        </Text>
        <Text style={[styles.statusText, { color: LocalTextExtractionModule ? '#4CAF50' : '#F44336' }]}>
          LocalTextExtractionModule: {LocalTextExtractionModule ? '✅ Available' : '❌ Not Found'}
        </Text>
      </View>

      {/* Configuration */}
      <View style={styles.configCard}>
        <Text style={styles.configTitle}>Configuration</Text>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Enable Validation & Fallback</Text>
          <Switch
            value={enableValidation}
            onValueChange={setEnableValidation}
            disabled={isCapturing}
          />
        </View>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Enable Caching</Text>
          <Switch
            value={enableCaching}
            onValueChange={setEnableCaching}
            disabled={isCapturing}
          />
        </View>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Auto-scroll on Text Detection</Text>
          <Switch
            value={autoScroll}
            onValueChange={setAutoScroll}
            disabled={isCapturing}
          />
        </View>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Performance Mode (Faster)</Text>
          <Switch
            value={performanceMode}
            onValueChange={setPerformanceMode}
            disabled={isCapturing}
          />
        </View>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Background Operation</Text>
          <Switch
            value={backgroundMode}
            onValueChange={setBackgroundMode}
            disabled={isCapturing}
          />
        </View>

        <View style={styles.configRow}>
          <Text style={styles.configLabel}>Capture Interval (ms)</Text>
          <View style={styles.intervalContainer}>
            <TouchableOpacity
              style={[styles.intervalButton, captureInterval === 1500 && styles.intervalButtonActive]}
              onPress={() => setCaptureInterval(1500)}
              disabled={isCapturing}
            >
              <Text style={[styles.intervalButtonText, captureInterval === 1500 && styles.intervalButtonTextActive]}>1.5s</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.intervalButton, captureInterval === 2000 && styles.intervalButtonActive]}
              onPress={() => setCaptureInterval(2000)}
              disabled={isCapturing}
            >
              <Text style={[styles.intervalButtonText, captureInterval === 2000 && styles.intervalButtonTextActive]}>2s</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.intervalButton, captureInterval === 3000 && styles.intervalButtonActive]}
              onPress={() => setCaptureInterval(3000)}
              disabled={isCapturing}
            >
              <Text style={[styles.intervalButtonText, captureInterval === 3000 && styles.intervalButtonTextActive]}>3s</Text>
            </TouchableOpacity>
          </View>
        </View>

        <Text style={styles.configInfo}>
          Capture Interval: {captureInterval}ms | Validation: {enableValidation ? 'ON' : 'OFF'} | Cache: {enableCaching ? 'ON' : 'OFF'} | Performance: {performanceMode ? 'ON' : 'OFF'} | Background: {backgroundMode ? 'ON' : 'OFF'}
        </Text>
      </View>

      {/* Control Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.button, styles.primaryButton]}
          onPress={testSingleExtraction}
          disabled={isLoading || isCapturing}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🧪 Testing...' : '🧪 Test Single Extraction'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, isCapturing ? styles.stopButton : styles.startButton]}
          onPress={isCapturing ? stopLocalTextCapture : startLocalTextCapture}
          disabled={isLoading}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '⏳ Processing...' : isCapturing ? '🛑 Stop Capture' : '🎬 Start Live Capture'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.clearButton]}
          onPress={clearAllData}
          disabled={isLoading || isCapturing}
        >
          <Text style={styles.buttonText}>🗑️ Clear All Data</Text>
        </TouchableOpacity>
      </View>

      {/* Loading Indicator */}
      {isLoading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Processing...</Text>
        </View>
      )}

      {/* Live Stats */}
      <View style={styles.statsCard}>
        <Text style={styles.statsTitle}>📊 Live Performance Stats</Text>
        <View style={styles.statsGrid}>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.totalCaptures}</Text>
            <Text style={styles.statLabel}>Total Captures</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.successfulExtractions}</Text>
            <Text style={styles.statLabel}>Successful</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.averageProcessingTime.toFixed(0)}ms</Text>
            <Text style={styles.statLabel}>Avg Time</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.averageConfidence.toFixed(1)}%</Text>
            <Text style={styles.statLabel}>Avg Confidence</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.cacheHitRate.toFixed(1)}%</Text>
            <Text style={styles.statLabel}>Cache Hit Rate</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statValue}>{captureStats.totalTextExtracted}</Text>
            <Text style={styles.statLabel}>Total Chars</Text>
          </View>
        </View>
      </View>

      {/* Recent Extractions */}
      {recentExtractions.length > 0 && (
        <View style={styles.extractionsCard}>
          <Text style={styles.extractionsTitle}>📝 Recent Extractions</Text>
          {recentExtractions.slice(0, 5).map((extraction, index) => (
            <View key={index} style={styles.extractionItem}>
              <Text style={styles.extractionText}>
                "{extraction.extractedText.substring(0, 100)}{extraction.extractedText.length > 100 ? '...' : ''}"
              </Text>
              <View style={styles.extractionMeta}>
                <Text style={styles.extractionMetaText}>
                  {(extraction.confidence * 100).toFixed(1)}% confidence • {extraction.processingTimeMs}ms
                  {extraction.usedCache && ' • 💾 Cached'}
                  {extraction.roiDetected && ' • 🎯 ROI'}
                  {extraction.validationPassed && ' • ✅ Validated'}
                  {extraction.fallbackUsed && ` • 🔄 ${extraction.fallbackStrategy}`}
                </Text>
              </View>
            </View>
          ))}
        </View>
      )}

      {/* Instructions */}
      <View style={styles.instructionsCard}>
        <Text style={styles.instructionsTitle}>📋 Instructions</Text>
        <Text style={styles.instructionsText}>
          1. <Text style={styles.bold}>Test Single Extraction</Text>: Capture current screen and extract text once{'\n'}
          2. <Text style={styles.bold}>Start Live Capture</Text>: Begin continuous screen capture and text extraction{'\n'}
          3. <Text style={styles.bold}>Change Screen Content</Text>: Navigate, scroll, or switch apps to see different text extracted{'\n'}
          4. <Text style={styles.bold}>Monitor Terminal</Text>: Check Rust backend logs for detailed extraction results{'\n'}
          5. <Text style={styles.bold}>Performance</Text>: Compare speed vs Google Vision API (should be 4-10x faster){'\n'}
          6. <Text style={styles.bold}>Quality</Text>: Validation ensures 95%+ accuracy with fallback mechanisms
        </Text>

        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>💡 Tip for Fresh Screenshots</Text>
          <Text style={styles.tipText}>
            The system captures every 2+ seconds. To see different content extracted, try:{'\n'}
            • Scroll up/down in this app{'\n'}
            • Switch to another app and back{'\n'}
            • Open Settings or other apps{'\n'}
            • Navigate between different screens
          </Text>
        </View>

        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>🌙 Background Operation</Text>
          <Text style={styles.tipText}>
            With Background Mode enabled:{'\n'}
            • Text extraction continues when app is minimized{'\n'}
            • Persistent notification shows extraction is active{'\n'}
            • Same performance as foreground mode{'\n'}
            • Tap notification to return to app{'\n'}
            • Use notification "Stop" button to completely stop
          </Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#007AFF',
    padding: 20,
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
    textAlign: 'center',
  },
  statusCard: {
    backgroundColor: 'white',
    margin: 16,
    padding: 16,
    borderRadius: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  statusTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#333',
  },
  statusText: {
    fontSize: 16,
    marginBottom: 8,
    fontFamily: 'monospace',
  },
  configCard: {
    backgroundColor: 'white',
    margin: 16,
    marginTop: 0,
    padding: 16,
    borderRadius: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  configTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#333',
  },
  configRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  configLabel: {
    fontSize: 16,
    color: '#333',
  },
  configInfo: {
    fontSize: 14,
    color: '#666',
    fontStyle: 'italic',
    marginTop: 8,
  },
  buttonContainer: {
    padding: 16,
    gap: 12,
  },
  button: {
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  primaryButton: {
    backgroundColor: '#007AFF',
  },
  startButton: {
    backgroundColor: '#4CAF50',
  },
  stopButton: {
    backgroundColor: '#F44336',
  },
  clearButton: {
    backgroundColor: '#FF9500',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
  loadingContainer: {
    alignItems: 'center',
    padding: 20,
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
  statsCard: {
    backgroundColor: 'white',
    margin: 16,
    padding: 16,
    borderRadius: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  statsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  statItem: {
    width: '48%',
    alignItems: 'center',
    marginBottom: 16,
    padding: 12,
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
  },
  statValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#007AFF',
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 4,
    textAlign: 'center',
  },
  extractionsCard: {
    backgroundColor: 'white',
    margin: 16,
    padding: 16,
    borderRadius: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  extractionsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  extractionItem: {
    marginBottom: 16,
    padding: 12,
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#007AFF',
  },
  extractionText: {
    fontSize: 14,
    color: '#333',
    marginBottom: 8,
    fontStyle: 'italic',
  },
  extractionMeta: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  extractionMetaText: {
    fontSize: 12,
    color: '#666',
  },
  instructionsCard: {
    backgroundColor: 'white',
    margin: 16,
    padding: 16,
    borderRadius: 8,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  instructionsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#333',
  },
  instructionsText: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
  },
  bold: {
    fontWeight: 'bold',
    color: '#333',
  },
  intervalContainer: {
    flexDirection: 'row',
    gap: 8,
  },
  intervalButton: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
    backgroundColor: '#f0f0f0',
    borderWidth: 1,
    borderColor: '#ddd',
  },
  intervalButtonActive: {
    backgroundColor: '#007AFF',
    borderColor: '#007AFF',
  },
  intervalButtonText: {
    fontSize: 12,
    color: '#666',
    fontWeight: '500',
  },
  intervalButtonTextActive: {
    color: 'white',
  },
  tipCard: {
    marginTop: 16,
    padding: 12,
    backgroundColor: '#e3f2fd',
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#2196F3',
  },
  tipTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1976D2',
    marginBottom: 8,
  },
  tipText: {
    fontSize: 14,
    color: '#1565C0',
    lineHeight: 20,
  },
});

export default LocalTextExtractionTest;