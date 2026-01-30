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

const { ScreenCaptureModule, ScreenPermissionModule, SmartDetectionModule, AppDetectionModule } = NativeModules;

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
  // App detection stats
  currentApp: string;
  currentAppName: string;
  isMonitoredApp: boolean;
  appChangeCount: number;
  capturesSkipped: number;
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
  console.log('🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG');
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
    // App detection stats
    currentApp: '',
    currentAppName: 'Unknown',
    isMonitoredApp: false,
    appChangeCount: 0,
    capturesSkipped: 0,
  });

  const [lastCapture, setLastCapture] = useState<CaptureData | null>(null);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [resultCode, setResultCode] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [captureLoop, setCaptureLoop] = useState(false);
  const [smartCaptureEnabled, setSmartCaptureEnabled] = useState(true); // New: Smart app-based capture
  const isProcessingRef = useRef(false);
  const captureLoopRef = useRef(false);

  useEffect(() => {
    // Check if already capturing
    checkCaptureStatus();
    
    // Initialize app detection
    initializeAppDetection();

    // Listen for permission results (this doesn't depend on captureLoop)
    const permissionListener = DeviceEventEmitter.addListener('onScreenCapturePermissionResult', (result) => {
      console.log('🔐 Permission result:', result);
      setPermissionGranted(result.granted);
      if (result.granted && result.resultCode) {
        setResultCode(result.resultCode);
      }
    });

    // Listen for app changes from AppDetectionModule
    const appChangeListener = DeviceEventEmitter.addListener('onAppChanged', (event) => {
      const timestamp = new Date().toISOString();
      console.log(`📱 [${timestamp}] App changed:`, event);
      
      setStats(prev => ({
        ...prev,
        currentApp: event.packageName,
        currentAppName: event.appName,
        isMonitoredApp: event.isMonitored,
        appChangeCount: prev.appChangeCount + 1,
      }));

      if (event.isMonitored) {
        console.log(`🎯 [${timestamp}] ENTERED MONITORED APP: ${event.appName}`);
        console.log(`   → Screen capture should be ACTIVE`);
      } else {
        console.log(`📱 [${timestamp}] Left monitored app, now in: ${event.appName}`);
        console.log(`   → Screen capture should be PAUSED`);
      }
    });

    return () => {
      permissionListener.remove();
      appChangeListener.remove();
    };
  }, []);

  // Separate useEffect for capture listener that depends on captureLoop
  useEffect(() => {
    console.log('🎧 Creating capture listener - this should only happen once per mount');
    
    // Listen for screen capture events - recreate when captureLoop changes
    const captureListener = DeviceEventEmitter.addListener('onScreenCaptured', async (data: CaptureData) => {
      const timestamp = new Date().toISOString();
      console.log(`📸 [${timestamp}] Screen captured:`, data.width + 'x' + data.height);
      console.log(`🔍 [${timestamp}] captureLoop state:`, captureLoop);
      console.log(`🔍 [${timestamp}] captureLoopRef.current:`, captureLoopRef.current);

      setLastCapture(data);
      setStats(prev => ({
        ...prev,
        totalCaptures: prev.totalCaptures + 1,
        lastCaptureTime: data.timestamp,
      }));

      // Check if we should process this capture based on current app
      const shouldProcess = await shouldProcessCapture();
      
      if (!shouldProcess) {
        const skipTimestamp = new Date().toISOString();
        console.log(`⏭️ [${skipTimestamp}] Skipping capture - not in monitored app (${stats.currentAppName})`);
        console.log(`🚫 [${skipTimestamp}] CRITICAL: This capture should NOT reach processCapture`);
        setStats(prev => ({ ...prev, capturesSkipped: prev.capturesSkipped + 1 }));
        
        // Continue loop but skip processing
        if (captureLoopRef.current) {
          setTimeout(() => {
            triggerNextCapture();
          }, 1000); // Shorter delay when skipping
        }
        return;
      }

      // PROCESS captures automatically when loop is active AND in monitored app
      if (captureLoopRef.current && !isProcessingRef.current) {
        console.log(`🔄 [${timestamp}] Starting automatic processing for monitored app...`);
        console.log(`✅ [${timestamp}] CALLING processCapture - this should be the ONLY path to backend`);
        processCapture(data); // Don't await here to prevent blocking
      } else if (isProcessingRef.current) {
        console.log(`⏳ [${timestamp}] Already processing, skipping this capture`);
      } else {
        console.log(`⏸️ [${timestamp}] captureLoop is false, skipping automatic processing`);
      }
    });

    return () => {
      console.log('🗑️ Cleaning up capture listener');
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

  const initializeAppDetection = async () => {
    try {
      console.log('🔍 Initializing app detection...');
      
      // First check if accessibility service is enabled
      const isServiceEnabled = await AppDetectionModule.isAccessibilityServiceEnabled();
      console.log('🔧 Accessibility service enabled:', isServiceEnabled);
      
      if (!isServiceEnabled) {
        console.warn('⚠️ Accessibility service is not enabled - app detection will not work');
        console.warn('⚠️ Smart capture will be disabled to prevent false processing');
        
        // Disable smart capture when service is not available
        setSmartCaptureEnabled(false);
        
        setStats(prev => ({
          ...prev,
          currentApp: 'service_disabled',
          currentAppName: 'Service Disabled',
          isMonitoredApp: false,
        }));
        
        return;
      }
      
      // Get current app status
      const currentAppResult = await AppDetectionModule.getCurrentApp();
      console.log('📱 Current app on startup:', currentAppResult);
      
      setStats(prev => ({
        ...prev,
        currentApp: currentAppResult.packageName || 'unknown',
        currentAppName: currentAppResult.appName || 'Unknown',
        isMonitoredApp: currentAppResult.isMonitored || false,
      }));

      console.log('✅ App detection initialized successfully');
      
    } catch (error) {
      console.error('❌ Error initializing app detection:', error);
      
      // Disable smart capture on error to prevent false processing
      setSmartCaptureEnabled(false);
      console.warn('⚠️ Smart capture disabled due to app detection error');
    }
  };

  const shouldProcessCapture = async (): Promise<boolean> => {
    if (!smartCaptureEnabled) {
      console.log('🔄 Smart capture disabled - processing all captures');
      return true; // Always process if smart capture is disabled
    }

    try {
      // Get real-time app status
      const isMonitored = await AppDetectionModule.isMonitoredApp();
      const currentAppResult = await AppDetectionModule.getCurrentApp();
      
      console.log(`🤔 shouldProcessCapture check:`);
      console.log(`   App: ${currentAppResult.appName} (${currentAppResult.packageName})`);
      console.log(`   Monitored: ${isMonitored}`);
      console.log(`   Smart Capture: ${smartCaptureEnabled}`);
      console.log(`   Service Available: ${currentAppResult.serviceAvailable !== false}`);
      console.log(`   Decision: ${isMonitored ? 'PROCESS' : 'SKIP'}`);
      
      // Update stats with current app info
      setStats(prev => ({
        ...prev,
        currentApp: currentAppResult.packageName || 'unknown',
        currentAppName: currentAppResult.appName || 'Unknown',
        isMonitoredApp: isMonitored,
      }));
      
      return isMonitored;
    } catch (error) {
      console.error('❌ Error checking if app is monitored:', error);
      return false; // Err on the side of not processing
    }
  };

  const openAccessibilitySettings = async () => {
    try {
      console.log('⚙️ Opening accessibility settings...');
      const success = await AppDetectionModule.openAccessibilitySettings();
      
      if (success) {
        Alert.alert(
          'Enable App Detection',
          'Please find "Allot" in the accessibility services list and enable it.\n\nThis allows the app to detect which social media apps you\'re using for smart capture.',
          [
            { 
              text: 'I\'ve Enabled It', 
              onPress: () => {
                // Re-initialize app detection after user enables service
                setTimeout(() => {
                  initializeAppDetection();
                }, 1000);
              }
            },
            { text: 'Cancel', style: 'cancel' }
          ]
        );
      } else {
        Alert.alert('Error', 'Could not open accessibility settings. Please go to Settings > Accessibility manually.');
      }
    } catch (error) {
      console.error('❌ Error opening accessibility settings:', error);
      Alert.alert('Error', 'Could not open accessibility settings.');
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
    const entryTimestamp = new Date().toISOString();
    console.log(`🚪 [${entryTimestamp}] processCapture ENTRY - smartCaptureEnabled: ${smartCaptureEnabled}`);
    
    // Add stack trace to see where this is being called from
    console.log(`📍 [${entryTimestamp}] processCapture called from:`, new Error().stack?.split('\n')[2]?.trim());
    
    if (isProcessingRef.current) {
      const timestamp = new Date().toISOString();
      console.log(`⏳ [${timestamp}] Already processing, skipping capture`);
      return;
    }

    // CRITICAL: Double-check if we should process this capture
    if (smartCaptureEnabled) {
      try {
        const isMonitored = await AppDetectionModule.isMonitoredApp();
        const currentAppResult = await AppDetectionModule.getCurrentApp();
        
        console.log(`🚫 [${entryTimestamp}] CRITICAL DOUBLE-CHECK:`);
        console.log(`   Smart Capture: ${smartCaptureEnabled}`);
        console.log(`   Current App: ${currentAppResult.appName}`);
        console.log(`   Is Monitored: ${isMonitored}`);
        
        if (!isMonitored) {
          console.log(`🚫 [${entryTimestamp}] BLOCKED: Smart capture enabled but not in monitored app - aborting processing`);
          console.log(`🚫 [${entryTimestamp}] THIS SHOULD PREVENT ALL ML KIT AND BACKEND CALLS`);
          
          // Continue loop but skip processing
          if (captureLoopRef.current) {
            setTimeout(() => {
              triggerNextCapture();
            }, 1000);
          }
          return;
        } else {
          console.log(`✅ [${entryTimestamp}] PROCEEDING: In monitored app ${currentAppResult.appName}`);
        }
      } catch (error) {
        console.error(`❌ [${entryTimestamp}] Error in processCapture app check:`, error);
        return;
      }
    }

    const startTime = Date.now();
    const timestamp = new Date().toISOString();

    setIsProcessing(true);
    isProcessingRef.current = true;
    try {
      console.log(`🤖 [${timestamp}] Starting Local ML Kit text extraction...`);
      console.log(`🤖 [${timestamp}] THIS SHOULD ONLY HAPPEN FOR MONITORED APPS`);
      console.log(`🤖 [${timestamp}] If you see this for non-monitored apps, there's a bug!`);

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
        console.log(`🧠 [${timestamp}] THIS BACKEND CALL SHOULD ONLY HAPPEN FOR MONITORED APPS`);
        console.log(`🧠 [${timestamp}] If you see this for non-monitored apps, there's a critical bug!`);

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
  }, [triggerNextCapture, smartCaptureEnabled]);

  const sendToBackend = async () => {
    if (!lastCapture) {
      Alert.alert('No Capture', 'No screen capture available to send');
      return;
    }

    // Check if smart capture is enabled and we're not in a monitored app
    if (smartCaptureEnabled) {
      try {
        const isMonitored = await AppDetectionModule.isMonitoredApp();
        if (!isMonitored) {
          Alert.alert(
            'Smart Capture Enabled', 
            'Smart Capture is ON and you\'re not in a monitored app. This would normally be skipped.\n\nDo you want to send anyway?',
            [
              { text: 'Cancel', style: 'cancel' },
              { text: 'Send Anyway', onPress: () => performBackendSend() }
            ]
          );
          return;
        }
      } catch (error) {
        console.error('Error checking app status for manual send:', error);
      }
    }

    performBackendSend();
  };

  const performBackendSend = async () => {
    if (!lastCapture) return;

    try {
      console.log('🚀 Manual send to backend...');
      const response = await fetch('http://192.168.100.55:3000/analyze', {
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
          title="Current App"
          value={stats.currentAppName}
          color={stats.isMonitoredApp ? '#4CAF50' : '#FF9800'}
        />
        <StatusCard
          title="App Status"
          value={stats.isMonitoredApp ? 'MONITORED' : 'NOT MONITORED'}
          color={stats.isMonitoredApp ? '#4CAF50' : '#9E9E9E'}
        />
        <StatusCard
          title="Processing"
          value={isProcessing ? 'ANALYZING' : (captureLoop ? 'WAITING' : 'READY')}
          color={isProcessing ? '#FF9800' : (captureLoop ? '#2196F3' : '#4CAF50')}
        />
        <StatusCard
          title="Skipped"
          value={stats.capturesSkipped}
          color={'#9E9E9E'}
        />
      </View>

      {/* Control Buttons */}
      <View style={styles.controlSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Controls
        </Text>

        {/* Smart Capture Toggle */}
        <TouchableOpacity
          style={[
            styles.button, 
            { 
              backgroundColor: smartCaptureEnabled ? '#4CAF50' : '#9E9E9E',
              marginBottom: 16 
            }
          ]}
          onPress={() => setSmartCaptureEnabled(!smartCaptureEnabled)}
        >
          <Text style={styles.buttonText}>
            {smartCaptureEnabled ? '🎯 Smart Capture: ON' : '📱 Smart Capture: OFF'}
          </Text>
        </TouchableOpacity>

        <Text style={[
          styles.smartCaptureDescription, 
          { color: isDark ? '#9BA1A6' : '#687076' }
        ]}>
          {smartCaptureEnabled 
            ? '✅ Only captures when social media apps are active (saves resources)'
            : '⚠️ Captures all apps (uses more resources)'
          }
        </Text>

        {/* Accessibility Service Status */}
        {smartCaptureEnabled && (
          <View style={[styles.serviceStatusSection, { 
            backgroundColor: isDark ? '#1C1C1E' : '#F2F2F7',
            borderColor: stats.currentApp === 'service_disabled' ? '#FF3B30' : '#34C759'
          }]}>
            <Text style={[styles.serviceStatusTitle, { 
              color: stats.currentApp === 'service_disabled' ? '#FF3B30' : '#34C759' 
            }]}>
              📱 App Detection Status
            </Text>
            
            <Text style={[styles.serviceStatusText, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {stats.currentApp === 'service_disabled' 
                ? '❌ Accessibility service not enabled'
                : `✅ Current: ${stats.currentAppName}`
              }
            </Text>
            
            <Text style={[styles.serviceStatusText, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              Status: {stats.isMonitoredApp ? '🎯 MONITORED' : '🚫 NOT MONITORED'}
            </Text>
            
            {stats.currentApp === 'service_disabled' && (
              <TouchableOpacity
                style={[styles.enableServiceButton, { backgroundColor: '#FF9500' }]}
                onPress={openAccessibilitySettings}
              >
                <Text style={styles.buttonText}>⚙️ Enable App Detection</Text>
              </TouchableOpacity>
            )}
          </View>
        )}

        {!permissionGranted && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#FF9800' }]}
            onPress={requestPermission}
            disabled={loading}
          >
            <Text style={styles.buttonText}>� Request Permission</Text>
          </TouchableOpacity>
        )}

        {permissionGranted && !captureLoop && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#4CAF50' }]}
            onPress={startCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>
              {smartCaptureEnabled ? '🎯 Start Smart Capture' : '🎬 Start Sequential Capture'}
            </Text>
          </TouchableOpacity>
        )}

        {captureLoop && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#F44336' }]}
            onPress={stopCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🛑 Stop Capture</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* App Detection Info */}
      <View style={styles.appDetectionSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          App Detection Status
        </Text>

        <View style={[styles.appDetectionCard, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Current App:
            </Text>
            <Text style={[
              styles.appDetectionValue, 
              { color: stats.isMonitoredApp ? '#4CAF50' : (isDark ? '#ECEDEE' : '#11181C') }
            ]}>
              {stats.currentAppName}
            </Text>
          </View>

          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Package:
            </Text>
            <Text style={[styles.appDetectionValue, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              {stats.currentApp || 'Unknown'}
            </Text>
          </View>

          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Status:
            </Text>
            <Text style={[
              styles.appDetectionValue, 
              { color: stats.isMonitoredApp ? '#4CAF50' : '#9E9E9E' }
            ]}>
              {stats.isMonitoredApp ? '🎯 MONITORED' : '📱 Not Monitored'}
            </Text>
          </View>

          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              App Changes:
            </Text>
            <Text style={[styles.appDetectionValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {stats.appChangeCount}
            </Text>
          </View>
        </View>
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
          style={[styles.testButton, { backgroundColor: '#4CAF50' }]}
          onPress={async () => {
            try {
              const result = await AppDetectionModule.getCurrentApp();
              Alert.alert('Current App', `App: ${result.appName}\nPackage: ${result.packageName}\nMonitored: ${result.isMonitored ? 'Yes' : 'No'}`);
            } catch (error) {
              Alert.alert('Error', 'Failed to get current app info');
            }
          }}
        >
          <Text style={styles.buttonText}>📱 Check Current App</Text>
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
  smartCaptureDescription: {
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 16,
    textAlign: 'center',
  },
  serviceStatusSection: {
    padding: 15,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 2,
  },
  serviceStatusTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    textAlign: 'center',
  },
  serviceStatusText: {
    fontSize: 14,
    marginBottom: 4,
    textAlign: 'center',
  },
  enableServiceButton: {
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginTop: 8,
  },
  appDetectionSection: {
    marginBottom: 30,
  },
  appDetectionCard: {
    padding: 15,
    borderRadius: 12,
  },
  appDetectionRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  appDetectionLabel: {
    fontSize: 14,
    fontWeight: '500',
  },
  appDetectionValue: {
    fontSize: 14,
    fontWeight: '600',
    flex: 1,
    textAlign: 'right',
  },
});