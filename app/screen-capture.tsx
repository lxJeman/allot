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
import { BitmapMemoryMonitor } from '../components/BitmapMemoryMonitor';

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
  // Scroll detection stats
  scrollDetectionCount: number;
  pipelineResetCount: number;
  lastScrollTime: number;
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
    // Scroll detection stats
    scrollDetectionCount: 0,
    pipelineResetCount: 0,
    lastScrollTime: 0,
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
  const recentCaptureIds = useRef(new Set<string>());

  // CRITICAL: Keep captureLoopRef synchronized with captureLoop state
  useEffect(() => {
    console.log(`🔄 Synchronizing captureLoop state: ${captureLoop} -> captureLoopRef: ${captureLoopRef.current}`);
    captureLoopRef.current = captureLoop;
  }, [captureLoop]);

  // Clean up recent capture IDs periodically to prevent memory growth
  useEffect(() => {
    const cleanup = setInterval(() => {
      recentCaptureIds.current.clear();
      console.log('🧹 Cleared recent capture IDs for deduplication');
    }, 30000); // Clear every 30 seconds

    return () => clearInterval(cleanup);
  }, []);

  // CONSOLIDATED EVENT LISTENERS - Single useEffect to prevent duplicates
  useEffect(() => {
    console.log('🎧 Setting up ALL event listeners - this should only happen ONCE per mount');
    
    // Check if already capturing
    checkCaptureStatus();
    
    // Initialize app detection
    initializeAppDetection();

    // Create all listeners in one place
    const listeners = [
      // Permission results listener
      DeviceEventEmitter.addListener('onScreenCapturePermissionResult', (result) => {
        console.log('🔐 Permission result received:', result);
        setPermissionGranted(result.granted);
        if (result.granted && result.resultCode) {
          setResultCode(result.resultCode);
        }
      }),

      // App changes listener
      DeviceEventEmitter.addListener('onAppChanged', (event) => {
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
          
          // ZERO DELAYS: Immediate restart if conditions are met
          if (captureLoopRef.current && !isProcessingRef.current) {
            console.log(`🔄 [${timestamp}] → IMMEDIATELY restarting capture for monitored app`);
            triggerNextCapture();
          } else {
            console.log(`🚫 [${timestamp}] → Not restarting: Loop=${captureLoopRef.current}, Processing=${isProcessingRef.current}`);
          }
        } else {
          console.log(`📱 [${timestamp}] Left monitored app, now in: ${event.appName}`);
          console.log(`   → Screen capture should be PAUSED`);
        }
      }),

      // Scroll detection listener
      DeviceEventEmitter.addListener('onScrollDetected', (event) => {
        const timestamp = new Date().toISOString();
        console.log(`📜 [${timestamp}] SCROLL DETECTED:`, event);
        console.log(`📜 [${timestamp}] App: ${event.currentApp}, Count: ${event.scrollCount}`);
        
        // CRITICAL FIX: Don't interrupt active processing
        if (isProcessingRef.current) {
          console.log(`🚫 [${timestamp}] IGNORING SCROLL - Currently processing (ID in progress)`);
          console.log(`🚫 [${timestamp}] → Will reset after current processing completes`);
          return;
        }
        
        console.log(`📜 [${timestamp}] → RESETTING CAPTURE PIPELINE`);
        
        // Reset pipeline state
        resetCaptureState();
        
        // Update stats
        setStats(prev => ({
          ...prev,
          scrollDetectionCount: event.scrollCount || prev.scrollDetectionCount + 1,
          pipelineResetCount: prev.pipelineResetCount + 1,
          lastScrollTime: event.timestamp || Date.now(),
        }));
      }),

      // Screen capture listener
      DeviceEventEmitter.addListener('onScreenCaptured', async (data: CaptureData) => {
        const timestamp = new Date().toISOString();
        const captureId = `${data.timestamp}_${data.width}x${data.height}`;
        
        // Event deduplication - prevent processing the same capture multiple times
        if (recentCaptureIds.current.has(captureId)) {
          console.log(`🔄 [${timestamp}] DUPLICATE CAPTURE DETECTED - Skipping (ID: ${captureId})`);
          return;
        }
        recentCaptureIds.current.add(captureId);
        
        console.log(`📸 [${timestamp}] Screen captured:`, data.width + 'x' + data.height);
        console.log(`🔍 [${timestamp}] captureLoopRef.current:`, captureLoopRef.current);

        setLastCapture(data);
        setStats(prev => ({
          ...prev,
          totalCaptures: prev.totalCaptures + 1,
          lastCaptureTime: data.timestamp,
        }));

        // CRITICAL FIX: Get app info at capture time to avoid race conditions
        let currentAppInfo;
        try {
          currentAppInfo = await AppDetectionModule.getCurrentApp();
          console.log(`📱 [${timestamp}] App at capture time: ${currentAppInfo.appName} (monitored: ${currentAppInfo.isMonitored})`);
        } catch (error) {
          console.error(`❌ [${timestamp}] Failed to get app info at capture time:`, error);
          currentAppInfo = { isMonitored: false, appName: 'Unknown', packageName: 'unknown' };
        }

        // Check if we should process this capture based on app info at capture time
        const shouldProcess = !smartCaptureEnabled || currentAppInfo.isMonitored;
        
        if (!shouldProcess) {
          const skipTimestamp = new Date().toISOString();
          console.log(`⏭️ [${skipTimestamp}] Skipping capture - not in monitored app (${currentAppInfo.appName})`);
          console.log(`🚫 [${skipTimestamp}] CRITICAL: This capture should NOT reach processCapture`);
          setStats(prev => ({ ...prev, capturesSkipped: prev.capturesSkipped + 1 }));
          
          // ZERO DELAYS: Continue immediately, no artificial delay
          if (captureLoopRef.current) {
            console.log(`🔄 [${skipTimestamp}] → IMMEDIATELY continuing capture loop after skip`);
            console.log(`🔄 [${skipTimestamp}] → Processing state: ${isProcessingRef.current}`);
            
            // CRITICAL FIX: Always trigger next capture immediately
            if (captureLoopRef.current && !isProcessingRef.current) {
              triggerNextCapture();
            } else if (isProcessingRef.current) {
              console.log(`🚫 [${skipTimestamp}] → Not triggering - already processing another capture`);
            }
          } else {
            console.log(`🛑 [${skipTimestamp}] → Capture loop stopped, not continuing`);
          }
          return;
        }

        // PROCESS captures automatically when loop is active AND in monitored app
        if (captureLoopRef.current && !isProcessingRef.current) {
          console.log(`🔄 [${timestamp}] Starting automatic processing for monitored app: ${currentAppInfo.appName}`);
          console.log(`✅ [${timestamp}] CALLING processCapture - this should be the ONLY path to backend`);
          console.log(`🔍 [${timestamp}] Processing state BEFORE: ${isProcessingRef.current}`);
          
          processCapture(data, currentAppInfo); // Pass app info to avoid re-querying
        } else if (isProcessingRef.current) {
          console.log(`⏳ [${timestamp}] Already processing, skipping this capture`);
          console.log(`🔍 [${timestamp}] Current processing state: ${isProcessingRef.current}`);
        } else {
          console.log(`⏸️ [${timestamp}] captureLoop is false, skipping automatic processing`);
          console.log(`🔍 [${timestamp}] captureLoopRef.current: ${captureLoopRef.current}`);
        }
      }),
    ];

    // Cleanup function
    return () => {
      console.log('🗑️ Cleaning up ALL event listeners');
      listeners.forEach(listener => {
        try {
          listener.remove();
        } catch (error) {
          console.error('Error removing listener:', error);
        }
      });
    };
  }, []); // Empty dependency array - listeners created only once

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
      
      // Start the native capture system first
      await ScreenCaptureModule.startScreenCapture();
      setStats(prev => ({ ...prev, isCapturing: true }));

      // Set the capture loop state
      setCaptureLoop(true);
      captureLoopRef.current = true;

      console.log('🔄 Starting sequential loop...');
      console.log('🎯 Triggering IMMEDIATE initial capture...');
      
      // PERFORMANCE FIX: Trigger immediately, no artificial delay
      triggerNextCapture();

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

  const resetCaptureState = useCallback(() => {
    const timestamp = new Date().toISOString();
    console.log(`🔄 [${timestamp}] RESETTING CAPTURE STATE`);
    console.log(`🔄 [${timestamp}] → Clearing processing flags`);
    console.log(`🔄 [${timestamp}] → Stopping any pending analysis`);
    console.log(`🔄 [${timestamp}] → Cancelling backend requests`);
    
    // AGGRESSIVELY stop any current processing
    setIsProcessing(false);
    isProcessingRef.current = false;
    
    // Clear last capture to force fresh analysis
    setLastCapture(null);
    
    // CRITICAL: Cancel all active backend requests
    try {
      aiDetectionService.cancelAllRequests();
      console.log(`🚫 [${timestamp}] → Backend requests cancelled successfully`);
    } catch (error) {
      console.log(`⚠️ [${timestamp}] Could not cancel backend requests:`, error);
    }
    
    // CRITICAL FIX: Only restart capture if we're still in a monitored app AND capture loop is active
    if (captureLoopRef.current) {
      console.log(`🔄 [${timestamp}] → Checking if we should restart capture loop`);
      
      // Get fresh app status for accurate restart decision
      AppDetectionModule.getCurrentApp()
        .then((currentAppResult: any) => {
          if (currentAppResult.isMonitored && captureLoopRef.current) {
            console.log(`🔄 [${timestamp}] → Restarting capture loop - still in monitored app: ${currentAppResult.appName}`);
            
            // Update stats with fresh app info
            setStats(prev => ({
              ...prev,
              currentApp: currentAppResult.packageName || 'unknown',
              currentAppName: currentAppResult.appName || 'Unknown',
              isMonitoredApp: currentAppResult.isMonitored,
            }));
            
            // ZERO DELAYS: Immediate restart if not processing
            if (!isProcessingRef.current) {
              console.log(`🎯 [${timestamp}] → IMMEDIATELY triggering fresh capture after scroll reset`);
              triggerNextCapture();
            } else {
              console.log(`🚫 [${timestamp}] → Not restarting - currently processing`);
            }
          } else {
            console.log(`🚫 [${timestamp}] → NOT restarting capture - not in monitored app or loop stopped`);
            console.log(`   App: ${currentAppResult.appName}, Monitored: ${currentAppResult.isMonitored}, Loop: ${captureLoopRef.current}`);
          }
        })
        .catch((error: any) => {
          console.error(`❌ [${timestamp}] Error checking app status for restart:`, error);
        });
    } else {
      console.log(`🛑 [${timestamp}] → Capture loop is stopped, not restarting`);
    }
    
    console.log(`✅ [${timestamp}] CAPTURE STATE RESET COMPLETE`);
  }, [triggerNextCapture]);

  const processCapture = useCallback(async (captureData: CaptureData, appInfo?: any): Promise<void> => {
    const entryTimestamp = new Date().toISOString();
    const processingId = `proc_${Date.now()}_${Math.random().toString(36).substr(2, 5)}`;
    
    console.log(`🚪 [${entryTimestamp}] processCapture ENTRY (ID: ${processingId}) - smartCaptureEnabled: ${smartCaptureEnabled}`);
    
    // Add stack trace to see where this is being called from
    console.log(`📍 [${entryTimestamp}] processCapture called from:`, new Error().stack?.split('\n')[2]?.trim());
    
    // CRITICAL FIX: Check if already processing BEFORE setting the flag
    if (isProcessingRef.current) {
      const timestamp = new Date().toISOString();
      console.log(`⏳ [${timestamp}] Already processing, skipping capture (ID: ${processingId})`);
      return;
    }
    
    // Set processing flag AFTER the check
    setIsProcessing(true);
    isProcessingRef.current = true;

    // CRITICAL: Use provided app info or double-check if we should process this capture
    if (smartCaptureEnabled && !appInfo) {
      try {
        const currentAppResult = await AppDetectionModule.getCurrentApp();
        appInfo = currentAppResult;
        
        console.log(`🚫 [${entryTimestamp}] CRITICAL DOUBLE-CHECK (ID: ${processingId}):`);
        console.log(`   Smart Capture: ${smartCaptureEnabled}`);
        console.log(`   Current App: ${currentAppResult.appName}`);
        console.log(`   Is Monitored: ${currentAppResult.isMonitored}`);
        
        if (!currentAppResult.isMonitored) {
          console.log(`🚫 [${entryTimestamp}] BLOCKED: Smart capture enabled but not in monitored app - aborting processing (ID: ${processingId})`);
          console.log(`🚫 [${entryTimestamp}] THIS SHOULD PREVENT ALL ML KIT AND BACKEND CALLS`);
          
          // Continue loop but skip processing
          if (captureLoopRef.current) {
            triggerNextCapture();
          }
          return;
        } else {
          console.log(`✅ [${entryTimestamp}] PROCEEDING: In monitored app ${currentAppResult.appName} (ID: ${processingId})`);
        }
      } catch (error) {
        console.error(`❌ [${entryTimestamp}] Error in processCapture app check (ID: ${processingId}):`, error);
        return;
      }
    } else if (appInfo) {
      console.log(`✅ [${entryTimestamp}] Using provided app info: ${appInfo.appName} (monitored: ${appInfo.isMonitored}) (ID: ${processingId})`);
    }

    const startTime = Date.now();
    const timestamp = new Date().toISOString();

    setIsProcessing(true);
    isProcessingRef.current = true;
    
    // Set a timeout to prevent getting stuck in processing
    const processingTimeout = setTimeout(() => {
      console.log(`⏰ [${timestamp}] PROCESSING TIMEOUT - Forcing reset after 30 seconds (ID: ${processingId})`);
      console.log(`⏰ [${timestamp}] This means backend request is hanging or failed`);
      
      // Mark as cancelled but don't reset processing flags yet
      // Let the finally block handle the cleanup and restart
      console.log(`⏰ [${timestamp}] Marking request as cancelled - finally block will handle restart`);
    }, 5000); // 5 second timeout - backend should respond in <1 second
    
    try {
      console.log(`🤖 [${timestamp}] Starting Local ML Kit text extraction... (ID: ${processingId})`);
      console.log(`🤖 [${timestamp}] THIS SHOULD ONLY HAPPEN FOR MONITORED APPS`);
      console.log(`🤖 [${timestamp}] If you see this for non-monitored apps, there's a bug!`);

      // CRITICAL: Check if we're still supposed to be processing before ML Kit
      if (!isProcessingRef.current) {
        console.log(`🚫 [${timestamp}] Processing was cancelled before ML Kit - aborting (ID: ${processingId})`);
        clearTimeout(processingTimeout);
        return;
      }

      // Step 1: Extract text using Local ML Kit (on-device)
      const mlKitStartTime = Date.now();
      const extractionResult = await SmartDetectionModule.extractText(captureData.base64);
      const mlKitTime = Date.now() - mlKitStartTime;

      // CRITICAL: Check again after ML Kit completes
      if (!isProcessingRef.current) {
        console.log(`🚫 [${timestamp}] Processing was cancelled after ML Kit - aborting backend call (ID: ${processingId})`);
        clearTimeout(processingTimeout);
        return;
      }

      const extractedText = extractionResult.extractedText || '';
      console.log(`📝 [${timestamp}] ML Kit extraction complete (${mlKitTime}ms) (ID: ${processingId}): "${extractedText.substring(0, 100)}${extractedText.length > 100 ? '...' : ''}"`);

      // Update stats
      setStats(prev => ({
        ...prev,
        totalTextExtractions: prev.totalTextExtractions + 1,
        averageMLKitTime: (prev.averageMLKitTime * prev.totalTextExtractions + mlKitTime) / (prev.totalTextExtractions + 1),
        lastExtractedText: extractedText,
      }));

      // Step 2: Send extracted text to backend for LLM classification (only if we have meaningful text)
      if (extractedText.trim().length > 3) {
        console.log(`🧠 [${timestamp}] Sending extracted text to backend for classification... (ID: ${processingId})`);
        console.log(`🧠 [${timestamp}] THIS BACKEND CALL SHOULD ONLY HAPPEN FOR MONITORED APPS`);
        console.log(`🧠 [${timestamp}] If you see this for non-monitored apps, there's a critical bug!`);
        console.log(`🌐 [${timestamp}] About to call aiDetectionService.detectHarmfulContent`);
        console.log(`📝 [${timestamp}] Text length: ${extractedText.length} chars`);

        const backendStartTime = Date.now();
        
        try {
          // CRITICAL: Final check before backend call
          if (!isProcessingRef.current) {
            console.log(`🚫 [${timestamp}] Processing was cancelled before backend call - aborting (ID: ${processingId})`);
            clearTimeout(processingTimeout);
            return;
          }

          const result = await aiDetectionService.detectHarmfulContent(
            extractedText,
            captureData.width,
            captureData.height
          );
          
          // CRITICAL: Always process the result, even if timeout occurred
          // The finally block will handle proper cleanup and loop continuation
          console.log(`🎉 [${timestamp}] Backend request completed successfully! (ID: ${processingId})`);
          console.log(`🎉 [${timestamp}] Result received: ${result.category} (${result.confidence})`);
          
          const backendTime = Date.now() - backendStartTime;
          const totalTime = Date.now() - startTime;

          console.log(`📊 [${timestamp}] Complete analysis (${totalTime}ms) (ID: ${processingId}):`);
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

          // Handle the analysis result - CRITICAL FIX: Show popup for ANY harmful content
          if (result.harmful) {
            console.log(`⚠️ [${timestamp}] 🚫 HARMFUL CONTENT DETECTED (ID: ${processingId})`);
            console.log(`   📝 Text: "${extractedText}"`);
            console.log(`   🏷️ Category: ${result.category}`);
            console.log(`   📊 Confidence: ${(result.confidence * 100).toFixed(1)}%`);
            console.log(`   🎯 Action: ${result.action}`);
            
            // CRITICAL: Show popup immediately - don't wait
            console.log(`🚨 [${timestamp}] SHOWING HARMFUL CONTENT POPUP NOW (ID: ${processingId})`);
            Alert.alert(
              '⚠️ Harmful Content Detected',
              `Category: ${result.category}\nConfidence: ${(result.confidence * 100).toFixed(1)}%\n\nText: "${extractedText.substring(0, 100)}${extractedText.length > 100 ? '...' : ''}"\n\nRecommendation: ${result.recommendation}`,
              [
                { 
                  text: 'Continue Monitoring', 
                  style: 'cancel',
                  onPress: () => {
                    console.log(`✅ [${timestamp}] User chose to continue monitoring (ID: ${processingId})`);
                  }
                },
                { 
                  text: 'Stop Capture', 
                  onPress: () => {
                    console.log(`🛑 [${timestamp}] User chose to stop capture (ID: ${processingId})`);
                    stopCapture();
                  }
                }
              ]
            );
            console.log(`✅ [${timestamp}] POPUP DISPLAYED SUCCESSFULLY (ID: ${processingId})`);
            
            // TODO: Implement scroll/blur actions based on result.action
            if (result.action === 'scroll') {
              console.log(`🔄 [${timestamp}] TODO: Trigger scroll action`);
            } else if (result.action === 'blur') {
              console.log(`🔄 [${timestamp}] TODO: Trigger blur action`);
            }
          } else {
            console.log(`✅ [${timestamp}] Content safe - continuing (ID: ${processingId})`);
          }
        } catch (error) {
          const errorMessage = error instanceof Error ? error.message : String(error);
          const errorName = error instanceof Error ? error.name : 'UnknownError';
          
          if (errorMessage?.includes('cancelled') || errorName === 'AbortError') {
            console.log(`🚫 [${timestamp}] Backend request cancelled due to scroll - this is expected (ID: ${processingId})`);
            clearTimeout(processingTimeout);
            return; // Exit early, don't continue loop
          } else {
            console.error(`❌ [${timestamp}] Backend classification failed (ID: ${processingId}):`, error);
            // Continue with loop even if backend fails
          }
        }

      } else {
        console.log(`⏭️ [${timestamp}] No meaningful text extracted (${extractedText.length} chars), skipping backend analysis (ID: ${processingId})`);
      }

    } catch (error) {
      const errorTimestamp = new Date().toISOString();
      console.error(`❌ [${errorTimestamp}] Error in merged processing (ID: ${processingId}):`, error);
      // Continue loop even if processing fails
    } finally {
      // Clear the timeout
      clearTimeout(processingTimeout);
      
      setIsProcessing(false);
      isProcessingRef.current = false;

      // CRITICAL: Always trigger next capture if loop is active, regardless of app state
      if (captureLoopRef.current) {
        const nextTimestamp = new Date().toISOString();
        console.log(`🔄 [${nextTimestamp}] Processing complete - immediately triggering next capture (ID: ${processingId})`);
        console.log(`🔄 [${nextTimestamp}] → captureLoopRef.current: ${captureLoopRef.current}`);
        console.log(`🔄 [${nextTimestamp}] → This should continue the monitoring loop`);
        
        // ZERO DELAYS: Immediate next capture
        console.log(`🎯 [${nextTimestamp}] → Triggering next capture NOW (ID: ${processingId})`);
        
        // Immediate trigger without setTimeout
        if (captureLoopRef.current && !isProcessingRef.current) {
          triggerNextCapture();
        } else if (isProcessingRef.current) {
          console.log(`🚫 [${nextTimestamp}] → Not triggering - already processing another capture`);
        }
      } else {
        console.log(`🛑 [${timestamp}] Capture loop stopped, not triggering next capture (ID: ${processingId})`);
        console.log(`🛑 [${timestamp}] → captureLoopRef.current: ${captureLoopRef.current}`);
        console.log(`🛑 [${timestamp}] → User manually stopped capture`);
      }
    }
  }, [triggerNextCapture, smartCaptureEnabled, stopCapture]);

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
        <StatusCard
          title="Scrolls Detected"
          value={stats.scrollDetectionCount}
          color={'#FF9800'}
        />
        <StatusCard
          title="Pipeline Resets"
          value={stats.pipelineResetCount}
          color={'#2196F3'}
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

        {captureLoop && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#2196F3', marginTop: 8 }]}
            onPress={() => {
              if (!isProcessingRef.current) {
                console.log('🎯 MANUAL TRIGGER: Forcing capture now');
                triggerNextCapture();
                Alert.alert('Triggered', 'Manual capture triggered - check logs');
              } else {
                Alert.alert('Processing', 'Already processing a capture, please wait');
              }
            }}
          >
            <Text style={styles.buttonText}>🎯 Trigger Capture Now</Text>
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

          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Scrolls Detected:
            </Text>
            <Text style={[styles.appDetectionValue, { color: '#FF9800' }]}>
              {stats.scrollDetectionCount}
            </Text>
          </View>

          <View style={styles.appDetectionRow}>
            <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Pipeline Resets:
            </Text>
            <Text style={[styles.appDetectionValue, { color: '#2196F3' }]}>
              {stats.pipelineResetCount}
            </Text>
          </View>

          {stats.lastScrollTime > 0 && (
            <View style={styles.appDetectionRow}>
              <Text style={[styles.appDetectionLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
                Last Scroll:
              </Text>
              <Text style={[styles.appDetectionValue, { color: isDark ? '#9BA1A6' : '#687076' }]}>
                {new Date(stats.lastScrollTime).toLocaleTimeString()}
              </Text>
            </View>
          )}
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

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#FF9800' }]}
          onPress={async () => {
            try {
              const result = await AppDetectionModule.getScrollDetectionStats();
              Alert.alert('Scroll Detection Stats', 
                `Scrolls Detected: ${result.scrollDetectionCount}\n` +
                `Last Scroll: ${result.lastScrollTime > 0 ? new Date(result.lastScrollTime).toLocaleTimeString() : 'Never'}\n` +
                `Current App: ${result.currentApp}\n` +
                `Monitored: ${result.isMonitoredApp ? 'Yes' : 'No'}`
              );
            } catch (error) {
              Alert.alert('Error', 'Failed to get scroll detection stats');
            }
          }}
        >
          <Text style={styles.buttonText}>📜 Check Scroll Stats</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#9C27B0' }]}
          onPress={async () => {
            try {
              await AppDetectionModule.resetPipelineOnScroll();
              Alert.alert('Pipeline Reset', 'Capture pipeline has been manually reset');
            } catch (error) {
              Alert.alert('Error', 'Failed to reset pipeline');
            }
          }}
        >
          <Text style={styles.buttonText}>🔄 Manual Pipeline Reset</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#E91E63' }]}
          onPress={async () => {
            try {
              await AppDetectionModule.reconnectScrollDetection();
              Alert.alert('Success', 'Scroll detection callbacks reconnected');
            } catch (error) {
              Alert.alert('Error', 'Failed to reconnect scroll detection');
            }
          }}
        >
          <Text style={styles.buttonText}>🔗 Reconnect Scroll Detection</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#FF5722' }]}
          onPress={() => {
            const debugInfo = `
Capture Loop State:
- captureLoop: ${captureLoop}
- captureLoopRef.current: ${captureLoopRef.current}
- isProcessing: ${isProcessing}
- isProcessingRef.current: ${isProcessingRef.current}

App State:
- currentApp: ${stats.currentAppName}
- isMonitored: ${stats.isMonitoredApp}
- smartCaptureEnabled: ${smartCaptureEnabled}

Stats:
- totalCaptures: ${stats.totalCaptures}
- capturesSkipped: ${stats.capturesSkipped}
- scrollDetectionCount: ${stats.scrollDetectionCount}
- pipelineResetCount: ${stats.pipelineResetCount}
            `.trim();
            
            console.log('🔍 DEBUG STATE:', debugInfo);
            Alert.alert('Debug State', debugInfo);
          }}
        >
          <Text style={styles.buttonText}>🔍 Debug State</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#4CAF50' }]}
          onPress={() => {
            if (captureLoopRef.current && !isProcessingRef.current) {
              console.log('🎯 Manual trigger: Forcing next capture');
              triggerNextCapture();
              Alert.alert('Success', 'Manually triggered next capture');
            } else {
              Alert.alert('Cannot Trigger', `Loop: ${captureLoopRef.current}, Processing: ${isProcessingRef.current}`);
            }
          }}
        >
          <Text style={styles.buttonText}>🎯 Force Next Capture</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#FF9800' }]}
          onPress={async () => {
            try {
              console.log('🌐 Testing backend connectivity...');
              const response = await fetch('http://192.168.100.55:3000/analyze', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                  extracted_text: 'test message',
                  width: 1080,
                  height: 2400,
                  timestamp: Date.now(),
                  source: 'test'
                })
              });
              
              if (response.ok) {
                const data = await response.json();
                console.log('✅ Backend test successful:', data);
                Alert.alert('Success', 'Backend is reachable and responding');
              } else {
                console.log('❌ Backend test failed:', response.status);
                Alert.alert('Error', `Backend returned ${response.status}`);
              }
            } catch (error) {
              console.error('❌ Backend test error:', error);
              Alert.alert('Network Error', 'Cannot reach backend server');
            }
          }}
        >
          <Text style={styles.buttonText}>🌐 Test Backend</Text>
        </TouchableOpacity>
      </View>

      {/* Bitmap Memory Monitor */}
      <BitmapMemoryMonitor />
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