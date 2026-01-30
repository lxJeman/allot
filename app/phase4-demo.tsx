/**
 * Phase 4 Demo - Integrated AI Content Monitoring
 * 
 * Features:
 * - App detection (only monitors TikTok and other social media)
 * - Real-time screenshot analysis
 * - Automatic harmful content detection
 * - Auto-scroll when harmful content detected
 * - Content blocking overlay
 */

import React, { useEffect, useState, useRef } from 'react';
import {
  Alert,
  DeviceEventEmitter,
  NativeModules,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Switch,
} from 'react-native';
import { Colors } from '../constants/theme';
import { useColorScheme } from '../hooks/use-color-scheme';

const { 
  ScreenCaptureModule, 
  ScreenPermissionModule,
  AppDetectionModule,
  ContentBlockingModule,
  NotificationModule 
} = NativeModules;

interface DetectionResult {
  id: string;
  category: string;
  confidence: number;
  harmful: boolean;
  action: 'continue' | 'scroll' | 'blur';
  detectedText: string;
  benchmark: {
    ocr_time_ms: number;
    classification_time_ms: number;
    total_time_ms: number;
    tokens_used?: {
      prompt_tokens: number;
      completion_tokens: number;
      total_tokens: number;
    };
  };
}

export default function Phase4DemoScreen() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  
  // State
  const [isMonitoring, setIsMonitoring] = useState(false);
  const [currentApp, setCurrentApp] = useState<string>('Unknown');
  const [isMonitoredApp, setIsMonitoredApp] = useState(false);
  const [stats, setStats] = useState({
    totalAnalyzed: 0,
    harmfulDetected: 0,
    autoScrolled: 0,
    avgProcessingTime: 0,
  });
  const [lastResult, setLastResult] = useState<DetectionResult | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [autoScrollEnabled, setAutoScrollEnabled] = useState(true);
  
  // Refs
  const isProcessingRef = useRef(false);
  const isMonitoringRef = useRef(false);
  const autoScrollEnabledRef = useRef(true);
  
  useEffect(() => {
    isMonitoringRef.current = isMonitoring;
  }, [isMonitoring]);
  
  useEffect(() => {
    autoScrollEnabledRef.current = autoScrollEnabled;
  }, [autoScrollEnabled]);
  
  useEffect(() => {
    // Listen for app changes
    const appChangeListener = DeviceEventEmitter.addListener('onAppChanged', (data) => {
      console.log('📱 App changed:', data);
      setCurrentApp(getAppName(data.packageName));
      setIsMonitoredApp(data.isMonitored);
      
      if (data.isMonitored) {
        console.log('✅ Switched to monitored app - monitoring active');
      } else {
        console.log('⏸️  Switched to non-monitored app - monitoring paused');
      }
    });
    
    // Listen for screen captures - DISABLED TO PREVENT INTERFERENCE
    // const captureListener = DeviceEventEmitter.addListener('onScreenCaptured', async (data) => {
    //   // Only process if monitoring is active AND we're in a monitored app
    //   if (isMonitoringRef.current && isMonitoredApp && !isProcessingRef.current) {
    //     console.log('🎯 Processing capture from monitored app...');
    //     await processCapture(data);
    //   }
    // });
    
    // Check current app on mount
    checkCurrentApp();
    
    return () => {
      appChangeListener.remove();
      // captureListener.remove(); // Disabled
    };
  }, [isMonitoredApp]);
  
  const getAppName = (packageName: string): string => {
    const appNames: Record<string, string> = {
      'com.zhiliaoapp.musically': 'TikTok',
      'com.instagram.android': 'Instagram',
      'com.facebook.katana': 'Facebook',
      'com.twitter.android': 'Twitter',
      'com.reddit.frontpage': 'Reddit',
    };
    return appNames[packageName] || packageName;
  };
  
  const checkCurrentApp = async () => {
    try {
      const result = await AppDetectionModule.getCurrentApp();
      setCurrentApp(getAppName(result.packageName));
      setIsMonitoredApp(result.isMonitored);
    } catch (error) {
      console.error('Error checking current app:', error);
    }
  };
  
  const processCapture = async (data: any) => {
    if (isProcessingRef.current) {
      console.log('⏭️  Already processing, skipping...');
      return;
    }
    
    isProcessingRef.current = true;
    setIsProcessing(true);
    
    try {
      const startTime = Date.now();
      console.log('🚀 Sending to backend for analysis...');
      
      // Send to backend
      const response = await fetch('http://192.168.100.55:3000/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          image: data.base64,
          width: data.width,
          height: data.height,
          timestamp: data.timestamp,
        }),
      });
      
      if (!response.ok) {
        throw new Error(`Backend error: ${response.status}`);
      }
      
      const result = await response.json();
      const processingTime = Date.now() - startTime;
      
      console.log('📊 Analysis result:', {
        category: result.analysis.category,
        confidence: result.analysis.confidence,
        harmful: result.analysis.harmful,
        action: result.analysis.action,
        processingTime: `${processingTime}ms`,
      });
      
      // Update stats
      setStats(prev => ({
        totalAnalyzed: prev.totalAnalyzed + 1,
        harmfulDetected: result.analysis.harmful ? prev.harmfulDetected + 1 : prev.harmfulDetected,
        autoScrolled: prev.autoScrolled,
        avgProcessingTime: Math.round(
          (prev.avgProcessingTime * prev.totalAnalyzed + processingTime) / (prev.totalAnalyzed + 1)
        ),
      }));
      
      // Update last result
      setLastResult({
        id: result.id,
        category: result.analysis.category,
        confidence: result.analysis.confidence,
        harmful: result.analysis.harmful,
        action: result.analysis.action,
        detectedText: result.analysis.details.detected_text,
        benchmark: result.benchmark,
      });
      
      // Take action if harmful
      if (result.analysis.harmful) {
        await handleHarmfulContent(result.analysis);
      }
      
    } catch (error) {
      console.error('❌ Error processing capture:', error);
    } finally {
      isProcessingRef.current = false;
      setIsProcessing(false);
    }
  };
  
  const handleHarmfulContent = async (analysis: any) => {
    console.log('🚫 Harmful content detected:', analysis.category);
    
    try {
      // Show content warning overlay
      await ContentBlockingModule.showContentWarning(
        `⚠️ ${analysis.category.toUpperCase()} CONTENT DETECTED\n\n${analysis.details.recommendation}`,
        2 // Show for 2 seconds
      );
      
      // Auto-scroll if enabled
      if (autoScrollEnabledRef.current && (analysis.action === 'scroll' || analysis.action === 'blur')) {
        console.log('⏭️  Auto-scrolling...');
        
        // Wait a moment for user to see the warning
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        const scrollResult = await AppDetectionModule.performAutoScroll();
        if (scrollResult) {
          console.log('✅ Auto-scroll successful');
          setStats(prev => ({
            ...prev,
            autoScrolled: prev.autoScrolled + 1,
          }));
        } else {
          console.log('⚠️  Auto-scroll failed');
        }
      }
      
    } catch (error) {
      console.error('❌ Error handling harmful content:', error);
    }
  };
  
  const startMonitoring = async () => {
    try {
      // Check permissions
      const permissions = await ScreenPermissionModule.getAllPermissionsStatus();
      
      if (!permissions.screenCapture) {
        Alert.alert('Permission Required', 'Please grant screen capture permission first');
        return;
      }
      
      if (!permissions.accessibility) {
        Alert.alert('Permission Required', 'Please enable accessibility service for app detection and auto-scroll');
        return;
      }
      
      // Start screen capture
      await ScreenCaptureModule.startScreenCapture();
      
      // Show persistent notification
      await NotificationModule.showPersistentNotification(
        'Allot Monitoring Active',
        'Analyzing content in real-time'
      );
      
      setIsMonitoring(true);
      console.log('✅ Monitoring started');
      
    } catch (error) {
      console.error('❌ Error starting monitoring:', error);
      Alert.alert('Error', 'Failed to start monitoring');
    }
  };
  
  const stopMonitoring = async () => {
    try {
      await ScreenCaptureModule.stopScreenCapture();
      await NotificationModule.hidePersistentNotification();
      setIsMonitoring(false);
      console.log('⏹️  Monitoring stopped');
    } catch (error) {
      console.error('❌ Error stopping monitoring:', error);
    }
  };
  
  return (
    <ScrollView style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.header}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Phase 4 Demo
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          AI-Powered Content Monitoring
        </Text>
      </View>
      
      {/* Current App Status */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Current App
        </Text>
        <View style={styles.appStatus}>
          <Text style={[styles.appName, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            {currentApp}
          </Text>
          <View style={[
            styles.badge,
            { backgroundColor: isMonitoredApp ? '#4CAF50' : '#9E9E9E' }
          ]}>
            <Text style={styles.badgeText}>
              {isMonitoredApp ? '✓ Monitored' : 'Not Monitored'}
            </Text>
          </View>
        </View>
        {!isMonitoredApp && (
          <Text style={[styles.hint, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            Switch to TikTok, Instagram, or other social media to start monitoring
          </Text>
        )}
      </View>
      
      {/* Monitoring Control */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Monitoring Control
        </Text>
        
        <View style={styles.controlRow}>
          <Text style={[styles.controlLabel, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Auto-Scroll on Harmful Content
          </Text>
          <Switch
            value={autoScrollEnabled}
            onValueChange={setAutoScrollEnabled}
            trackColor={{ false: '#767577', true: '#4CAF50' }}
          />
        </View>
        
        {!isMonitoring ? (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#4CAF50' }]}
            onPress={startMonitoring}
          >
            <Text style={styles.buttonText}>▶️ Start Monitoring</Text>
          </TouchableOpacity>
        ) : (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#F44336' }]}
            onPress={stopMonitoring}
          >
            <Text style={styles.buttonText}>⏹️ Stop Monitoring</Text>
          </TouchableOpacity>
        )}
        
        {isProcessing && (
          <Text style={[styles.processingText, { color: '#FF9800' }]}>
            🔄 Processing...
          </Text>
        )}
      </View>
      
      {/* Statistics */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Statistics
        </Text>
        <View style={styles.statsGrid}>
          <View style={styles.statItem}>
            <Text style={[styles.statValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {stats.totalAnalyzed}
            </Text>
            <Text style={[styles.statLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Total Analyzed
            </Text>
          </View>
          <View style={styles.statItem}>
            <Text style={[styles.statValue, { color: '#F44336' }]}>
              {stats.harmfulDetected}
            </Text>
            <Text style={[styles.statLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Harmful Detected
            </Text>
          </View>
          <View style={styles.statItem}>
            <Text style={[styles.statValue, { color: '#4CAF50' }]}>
              {stats.autoScrolled}
            </Text>
            <Text style={[styles.statLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Auto-Scrolled
            </Text>
          </View>
          <View style={styles.statItem}>
            <Text style={[styles.statValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {stats.avgProcessingTime}ms
            </Text>
            <Text style={[styles.statLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Avg Time
            </Text>
          </View>
        </View>
      </View>
      
      {/* Last Result */}
      {lastResult && (
        <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
          <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Last Detection
          </Text>
          <View style={styles.resultRow}>
            <Text style={[styles.resultLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Category:
            </Text>
            <Text style={[styles.resultValue, { 
              color: lastResult.harmful ? '#F44336' : '#4CAF50' 
            }]}>
              {lastResult.category}
            </Text>
          </View>
          <View style={styles.resultRow}>
            <Text style={[styles.resultLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Confidence:
            </Text>
            <Text style={[styles.resultValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {(lastResult.confidence * 100).toFixed(1)}%
            </Text>
          </View>
          <View style={styles.resultRow}>
            <Text style={[styles.resultLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Action:
            </Text>
            <Text style={[styles.resultValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {lastResult.action}
            </Text>
          </View>
          <View style={styles.resultRow}>
            <Text style={[styles.resultLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Processing Time:
            </Text>
            <Text style={[styles.resultValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {lastResult.benchmark.total_time_ms}ms
            </Text>
          </View>
          {lastResult.benchmark.tokens_used && (
            <View style={styles.resultRow}>
              <Text style={[styles.resultLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
                Tokens Used:
              </Text>
              <Text style={[styles.resultValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
                {lastResult.benchmark.tokens_used.total_tokens}
              </Text>
            </View>
          )}
        </View>
      )}
      
      {/* Instructions */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          How to Use
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          1. Grant all permissions (screen capture + accessibility)
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          2. Start monitoring
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          3. Switch to TikTok or other social media
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          4. Scroll through content - harmful posts will be auto-skipped
        </Text>
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
    marginBottom: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
  },
  card: {
    padding: 20,
    borderRadius: 12,
    marginBottom: 16,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 12,
  },
  appStatus: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  appName: {
    fontSize: 24,
    fontWeight: 'bold',
  },
  badge: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
  },
  badgeText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  hint: {
    fontSize: 12,
    fontStyle: 'italic',
  },
  controlRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  controlLabel: {
    fontSize: 14,
    flex: 1,
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  processingText: {
    textAlign: 'center',
    marginTop: 12,
    fontSize: 14,
    fontWeight: '600',
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  statItem: {
    width: '48%',
    marginBottom: 16,
  },
  statValue: {
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
  },
  resultRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  resultLabel: {
    fontSize: 14,
  },
  resultValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  instruction: {
    fontSize: 14,
    marginBottom: 8,
    lineHeight: 20,
  },
});
