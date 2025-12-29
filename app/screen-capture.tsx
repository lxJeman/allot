import React, { useEffect, useState, useCallback, useRef } from 'react';
import {
  Alert,
  DeviceEventEmitter,
  NativeModules,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Image,
} from 'react-native';
import { Colors } from '../constants/theme';
import { useColorScheme } from '../hooks/use-color-scheme';
import { aiDetectionService } from '../services/aiDetectionService';

const { ScreenCaptureModule, ScreenPermissionModule, SmartDetectionModule } = NativeModules;

interface CaptureStats {
  totalCaptures: number;
  lastCaptureTime: number;
  isCapturing: boolean;
  captureInterval: number;
  // New merged system stats
  totalTextExtractions: number;
  successfulClassifications: number;
  averageMLKitTime: number;
  averageBackendTime: number;
  lastExtractedText: string;
  lastClassification: string;
}

interface CaptureData {
  base64: string;
  filePath: string;
  width: number;
  height: number;
  timestamp: number;
}

interface AnalysisResult {
  category: string;
  confidence: number;
  harmful: boolean;
  action: string;
  detectedText: string;
  riskFactors: string[];
  recommendation: string;
  benchmark: {
    mlKitTimeMs: number;
    classificationTimeMs: number;
    totalTimeMs: number;
    textLength: number;
    cached: boolean;
    source: string;
  };
}

export default function ScreenCaptureScreen() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';

  const [stats, setStats] = useState<CaptureStats>({
    totalCaptures: 0,
    lastCaptureTime: 0,
    isCapturing: false,
    captureInterval: 100, // 100ms default
    // New merged system stats
    totalTextExtractions: 0,
    successfulClassifications: 0,
    averageMLKitTime: 0,
    averageBackendTime: 0,
    lastExtractedText: '',
    lastClassification: 'none',
  });

  const [lastCapture, setLastCapture] = useState<CaptureData | null>(null);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [resultCode, setResultCode] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [captureLoop, setCaptureLoop] = useState(false);
  const isProcessingRef = useRef(false);
  const captureLoopRef = useRef(false);

  useEffect(() => {
    // Check if already capturing
    checkCaptureStatus();

    // Listen for permission results (this doesn't depend on captureLoop)
    const permissionListener = DeviceEventEmitter.addListener('onScreenCapturePermissionResult', (result) => {
      console.log('🔐 Permission result:', result);
      setPermissionGranted(result.granted);
      if (result.granted && result.resultCode) {
        setResultCode(result.resultCode);
      }
    });

    return () => {
      permissionListener.remove();
    };
  }, []);

  // Separate useEffect for capture listener that depends on captureLoop
  useEffect(() => {
    // Listen for screen capture events - recreate when captureLoop changes
    const captureListener = DeviceEventEmitter.addListener('onScreenCaptured', async (data: CaptureData) => {
      const timestamp = new Date().toISOString();
      console.log(`📸 [${timestamp}] Screen captured:`, data.width + 'x' + data.height);
      console.log(`🔍 [${timestamp}] captureLoop state:`, captureLoop);

      setLastCapture(data);
      setStats(prev => ({
        ...prev,
        totalCaptures: prev.totalCaptures + 1,
        lastCaptureTime: data.timestamp,
      }));

      // ALWAYS process captures automatically when loop is active
      if (captureLoopRef.current && !isProcessingRef.current) {
        console.log(`🔄 [${timestamp}] Starting automatic processing...`);
        processCapture(data); // Don't await here to prevent blocking
      } else if (isProcessingRef.current) {
        console.log(`⏳ [${timestamp}] Already processing, skipping this capture`);
      } else {
        console.log(`⏸️ [${timestamp}] captureLoop is false, skipping automatic processing`);
      }
    });

    return () => {
      captureListener.remove();
    };
  }, []); // Stable listener using refs

  const checkCaptureStatus = async () => {
    try {
      const isCapturing = await ScreenCaptureModule.isCapturing();
      setStats(prev => ({ ...prev, isCapturing }));
    } catch (error) {
      console.error('Error checking capture status:', error);
    }
  };

  const requestPermission = async () => {
    setLoading(true);
    try {
      console.log('🔐 Requesting screen capture permission...');
      const result = await ScreenPermissionModule.requestScreenCapturePermission();
      console.log('🔐 Permission result received:', result);

      if (result.granted) {
        setPermissionGranted(true);
        setResultCode(result.resultCode);
        Alert.alert('Permission Granted!', 'Screen capture permission has been granted. You can now start capturing.');
      } else {
        setPermissionGranted(false);
        setResultCode(null);
        Alert.alert('Permission Denied', 'Screen capture permission was denied. Please try again and allow the permission.');
      }
    } catch (error) {
      console.error('❌ Error requesting permission:', error);
      Alert.alert('Error', `Failed to request screen capture permission: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  const startCapture = async () => {
    if (!permissionGranted) {
      Alert.alert('Permission Required', 'Please grant screen capture permission first');
      return;
    }

    setLoading(true);
    try {
      console.log('🎬 Starting sequential screen capture...');
      // Start the native capture system
      await ScreenCaptureModule.startScreenCapture();
      setStats(prev => ({ ...prev, isCapturing: true }));

      // Start the response-driven loop
      setCaptureLoop(true);
      captureLoopRef.current = true;

      // Trigger the first capture to start the loop
      console.log('🔄 Starting sequential loop...');
      setTimeout(() => {
        console.log('🎯 Triggering first capture...');
        triggerNextCapture();
      }, 1000); // Give native system time to initialize

      Alert.alert('Success', 'Sequential screen capture started! Each screenshot waits for analysis.');
    } catch (error) {
      console.error('❌ Error starting capture:', error);
      Alert.alert('Error', `Failed to start screen capture: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  const stopCapture = async () => {
    setLoading(true);
    try {
      console.log('🛑 Stopping sequential screen capture...');

      // Stop the response-driven loop
      setCaptureLoop(false);
      captureLoopRef.current = false;

      // Stop the native capture system
      await ScreenCaptureModule.stopScreenCapture();
      setStats(prev => ({ ...prev, isCapturing: false }));
      setIsProcessing(false);
      isProcessingRef.current = false;

      Alert.alert('Success', 'Sequential screen capture stopped');
    } catch (error) {
      console.error('❌ Error stopping capture:', error);
      Alert.alert('Error', 'Failed to stop screen capture');
    } finally {
      setLoading(false);
    }
  };

  const setCaptureInterval = async (intervalMs: number) => {
    try {
      await ScreenCaptureModule.setCaptureInterval(intervalMs);
      setStats(prev => ({ ...prev, captureInterval: intervalMs }));
      Alert.alert('Success', `Capture interval set to ${intervalMs}ms`);
    } catch (error) {
      console.error('❌ Error setting interval:', error);
      Alert.alert('Error', 'Failed to set capture interval');
    }
  };

  const triggerNextCapture = useCallback(async () => {
    const timestamp = new Date().toISOString();
    try {
      console.log(`🎯 [${timestamp}] Calling captureNextFrame...`);
      await ScreenCaptureModule.captureNextFrame();
      console.log(`✅ [${timestamp}] captureNextFrame completed`);
    } catch (error) {
      console.error(`❌ [${timestamp}] Error triggering next capture:`, error);
    }
  }, []);

  const processCapture = useCallback(async (captureData: CaptureData): Promise<void> => {
    if (isProcessingRef.current) {
      const timestamp = new Date().toISOString();
      console.log(`⏳ [${timestamp}] Already processing, skipping capture`);
      return;
    }

    const startTime = Date.now();
    const timestamp = new Date().toISOString();

    setIsProcessing(true);
    isProcessingRef.current = true;
    try {
      console.log(`🤖 [${timestamp}] Starting Local ML Kit text extraction...`);

      // Step 1: Extract text using Local ML Kit (on-device)
      const mlKitStartTime = Date.now();
      const extractionResult = await SmartDetectionModule.extractText(captureData.base64);
      const mlKitTime = Date.now() - mlKitStartTime;

      const extractedText = extractionResult.extractedText || '';
      console.log(`📝 [${timestamp}] ML Kit extraction complete (${mlKitTime}ms): "${extractedText.substring(0, 100)}${extractedText.length > 100 ? '...' : ''}"`);

      // Update stats
      setStats(prev => ({
        ...prev,
        totalTextExtractions: prev.totalTextExtractions + 1,
        averageMLKitTime: (prev.averageMLKitTime * prev.totalTextExtractions + mlKitTime) / (prev.totalTextExtractions + 1),
        lastExtractedText: extractedText,
      }));

      // Step 2: Send extracted text to backend for LLM classification (only if we have meaningful text)
      if (extractedText.trim().length > 3) {
        console.log(`🧠 [${timestamp}] Sending extracted text to backend for classification...`);

        const backendStartTime = Date.now();
        const result = await aiDetectionService.detectHarmfulContent(
          extractedText,
          captureData.width,
          captureData.height
        );
        const backendTime = Date.now() - backendStartTime;
        const totalTime = Date.now() - startTime;

        console.log(`📊 [${timestamp}] Complete analysis (${totalTime}ms):`);
        console.log(`   🏷️ Category: ${result.category}`);
        console.log(`   📊 Confidence: ${(result.confidence * 100).toFixed(1)}%`);
        console.log(`   🚨 Harmful: ${result.harmful ? 'YES' : 'NO'}`);
        console.log(`   🎯 Action: ${result.action}`);
        console.log(`   ⏱️ ML Kit: ${mlKitTime}ms | Backend: ${backendTime}ms | Total: ${totalTime}ms`);
        console.log(`   🚀 Advantage: ${Math.round((800 / mlKitTime) * 10) / 10}x faster than Google Vision API`);

        // Update stats
        setStats(prev => ({
          ...prev,
          successfulClassifications: prev.successfulClassifications + 1,
          averageBackendTime: (prev.averageBackendTime * prev.successfulClassifications + backendTime) / (prev.successfulClassifications + 1),
          lastClassification: result.category,
        }));

        // Handle the analysis result
        if (result.harmful && result.action === 'scroll') {
          console.log(`⚠️ [${timestamp}] 🚫 HARMFUL CONTENT DETECTED - SCROLL ACTION`);
          console.log(`   📝 Text: "${extractedText}"`);
          console.log(`   🏷️ Category: ${result.category}`);
          console.log(`   📊 Confidence: ${(result.confidence * 100).toFixed(1)}%`);
          // TODO: Trigger scroll action
        } else if (result.harmful && result.action === 'blur') {
          console.log(`⚠️ [${timestamp}] 🚫 HARMFUL CONTENT DETECTED - BLUR ACTION`);
          console.log(`   📝 Text: "${extractedText}"`);
          console.log(`   🏷️ Category: ${result.category}`);
          console.log(`   📊 Confidence: ${(result.confidence * 100).toFixed(1)}%`);
          // TODO: Trigger blur action
        } else {
          console.log(`✅ [${timestamp}] Content safe - continuing`);
        }

      } else {
        console.log(`⏭️ [${timestamp}] No meaningful text extracted (${extractedText.length} chars), skipping backend analysis`);
      }

    } catch (error) {
      const errorTimestamp = new Date().toISOString();
      console.error(`❌ [${errorTimestamp}] Error in merged processing:`, error);
      // Continue loop even if processing fails
    } finally {
      setIsProcessing(false);
      isProcessingRef.current = false;

      // CRITICAL: Continue the loop after processing with PROPER DELAY
      if (captureLoopRef.current) {
        const nextTimestamp = new Date().toISOString();
        console.log(`🔄 [${nextTimestamp}] Triggering next capture...`);
        // MUCH LONGER delay to create true sequential processing
        setTimeout(() => {
          triggerNextCapture();
        }, 5000); // 5 second delay between complete cycles
      }
    }
  }, [triggerNextCapture]);

  const sendToBackend = async () => {
    if (!lastCapture) {
      Alert.alert('No Capture', 'No screen capture available to send');
      return;
    }

    try {
      console.log('🚀 Manual send to backend...');
      const response = await fetch('http://192.168.100.47:3000/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          image: lastCapture.base64,
          width: lastCapture.width,
          height: lastCapture.height,
          timestamp: lastCapture.timestamp,
        }),
      });

      const result = await response.json();
      console.log('📊 Backend response:', result);

      Alert.alert('Backend Response', JSON.stringify(result, null, 2));
    } catch (error) {
      console.error('❌ Error sending to backend:', error);
      Alert.alert('Backend Error', 'Failed to send to backend. Make sure the server is running and accessible');
    }
  };

  const StatusCard = ({ title, value, color }: { title: string; value: string | number; color: string }) => (
    <View style={[styles.statusCard, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
      <Text style={[styles.statusTitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>{title}</Text>
      <Text style={[styles.statusValue, { color }]}>{value}</Text>
    </View>
  );

  return (
    <ScrollView style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.header}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Screen Capture System
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Real-time screen analysis for content filtering
        </Text>
      </View>

      {/* Status Cards */}
      <View style={styles.statusGrid}>
        <StatusCard
          title="Status"
          value={captureLoop ? 'SEQUENTIAL' : 'STOPPED'}
          color={captureLoop ? '#4CAF50' : '#F44336'}
        />
        <StatusCard
          title="Total Captures"
          value={stats.totalCaptures}
          color={Colors[colorScheme ?? 'light'].tint}
        />
        <StatusCard
          title="Interval"
          value={`${stats.captureInterval}ms`}
          color={isDark ? '#ECEDEE' : '#11181C'}
        />
        <StatusCard
          title="Processing"
          value={isProcessing ? 'ANALYZING' : (captureLoop ? 'WAITING' : 'READY')}
          color={isProcessing ? '#FF9800' : (captureLoop ? '#2196F3' : '#4CAF50')}
        />
        <StatusCard
          title="Permission"
          value={permissionGranted ? 'GRANTED' : 'REQUIRED'}
          color={permissionGranted ? '#4CAF50' : '#FF9800'}
        />

      </View>

      {/* Control Buttons */}
      <View style={styles.controlSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Controls
        </Text>

        {!permissionGranted && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#FF9800' }]}
            onPress={requestPermission}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🔐 Request Permission</Text>
          </TouchableOpacity>
        )}

        {permissionGranted && !captureLoop && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#4CAF50' }]}
            onPress={startCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🎬 Start Sequential Capture</Text>
          </TouchableOpacity>
        )}

        {captureLoop && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#F44336' }]}
            onPress={stopCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🛑 Stop Sequential Capture</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Interval Controls */}
      <View style={styles.intervalSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Capture Interval
        </Text>

        <View style={styles.intervalButtons}>
          {[100, 500, 1000, 2000].map(interval => (
            <TouchableOpacity
              key={interval}
              style={[
                styles.intervalButton,
                {
                  backgroundColor: stats.captureInterval === interval
                    ? Colors[colorScheme ?? 'light'].tint
                    : (isDark ? '#2A2A2A' : '#F5F5F5')
                }
              ]}
              onPress={() => setCaptureInterval(interval)}
            >
              <Text style={[
                styles.intervalButtonText,
                {
                  color: stats.captureInterval === interval
                    ? 'white'
                    : (isDark ? '#ECEDEE' : '#11181C')
                }
              ]}>
                {interval}ms
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Last Capture Preview */}
      {lastCapture && (
        <View style={styles.previewSection}>
          <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Last Capture
          </Text>

          <View style={[styles.previewCard, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
            <Image
              source={{ uri: `data:image/jpeg;base64,${lastCapture.base64}` }}
              style={styles.previewImage}
              resizeMode="contain"
            />
            <View style={styles.previewInfo}>
              <Text style={[styles.previewText, { color: isDark ? '#9BA1A6' : '#687076' }]}>
                Size: {lastCapture.width}x{lastCapture.height}
              </Text>
              <Text style={[styles.previewText, { color: isDark ? '#9BA1A6' : '#687076' }]}>
                Time: {new Date(lastCapture.timestamp).toLocaleTimeString()}
              </Text>
            </View>

            <TouchableOpacity
              style={[styles.button, { backgroundColor: '#2196F3', marginTop: 10 }]}
              onPress={sendToBackend}
            >
              <Text style={styles.buttonText}>🚀 Send to Backend</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}

      {/* Test Buttons */}
      <View style={styles.testSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Test Functions
        </Text>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#9C27B0' }]}
          onPress={checkCaptureStatus}
        >
          <Text style={styles.buttonText}>🔄 Refresh Status</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#607D8B' }]}
          onPress={sendToBackend}
          disabled={!lastCapture}
        >
          <Text style={styles.buttonText}>📡 Test Backend Connection</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    marginBottom: 30,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    lineHeight: 24,
  },
  statusGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginBottom: 30,
  },
  statusCard: {
    width: '48%',
    padding: 15,
    borderRadius: 12,
    marginBottom: 10,
    alignItems: 'center',
  },
  statusTitle: {
    fontSize: 12,
    fontWeight: '600',
    marginBottom: 5,
    textTransform: 'uppercase',
  },
  statusValue: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  controlSection: {
    marginBottom: 30,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    marginBottom: 16,
  },
  button: {
    paddingVertical: 15,
    paddingHorizontal: 24,
    borderRadius: 12,
    alignItems: 'center',
    marginBottom: 12,
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  intervalSection: {
    marginBottom: 30,
  },
  intervalButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  intervalButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 8,
    borderRadius: 8,
    alignItems: 'center',
    marginHorizontal: 4,
  },
  intervalButtonText: {
    fontSize: 14,
    fontWeight: '600',
  },
  previewSection: {
    marginBottom: 30,
  },
  previewCard: {
    padding: 15,
    borderRadius: 12,
  },
  previewImage: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    marginBottom: 10,
  },
  previewInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  previewText: {
    fontSize: 14,
  },
  testSection: {
    marginTop: 20,
    paddingTop: 20,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  testButton: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 12,
  },
});