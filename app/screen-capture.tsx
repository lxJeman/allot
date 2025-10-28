import React, { useEffect, useState } from 'react';
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

const { ScreenCaptureModule, ScreenPermissionModule } = NativeModules;

interface CaptureStats {
  totalCaptures: number;
  lastCaptureTime: number;
  isCapturing: boolean;
  captureInterval: number;
}

interface CaptureData {
  base64: string;
  filePath: string;
  width: number;
  height: number;
  timestamp: number;
}

export default function ScreenCaptureScreen() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  
  const [stats, setStats] = useState<CaptureStats>({
    totalCaptures: 0,
    lastCaptureTime: 0,
    isCapturing: false,
    captureInterval: 100, // 100ms default
  });
  
  const [lastCapture, setLastCapture] = useState<CaptureData | null>(null);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [resultCode, setResultCode] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    // Check if already capturing
    checkCaptureStatus();

    // Listen for screen capture events with async processing
    const captureListener = DeviceEventEmitter.addListener('onScreenCaptured', async (data: CaptureData) => {
      console.log('📸 Screen captured:', data.width + 'x' + data.height);
      setLastCapture(data);
      setStats(prev => ({
        ...prev,
        totalCaptures: prev.totalCaptures + 1,
        lastCaptureTime: data.timestamp,
      }));

      // Process this capture asynchronously
      if (!isProcessing) {
        processCapture(data);
      }
    });

    // Listen for permission results
    const permissionListener = DeviceEventEmitter.addListener('onScreenCapturePermissionResult', (result) => {
      console.log('🔐 Permission result:', result);
      setPermissionGranted(result.granted);
      if (result.granted && result.resultCode) {
        setResultCode(result.resultCode);
      }
    });

    return () => {
      captureListener.remove();
      permissionListener.remove();
    };
  }, []);

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
      console.log('🎬 Starting screen capture...');
      // The native module now handles the permission data internally
      await ScreenCaptureModule.startScreenCapture();
      setStats(prev => ({ ...prev, isCapturing: true }));
      Alert.alert('Success', 'Screen capture started! Check the notification bar.');
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
      console.log('🛑 Stopping screen capture...');
      await ScreenCaptureModule.stopScreenCapture();
      setStats(prev => ({ ...prev, isCapturing: false }));
      Alert.alert('Success', 'Screen capture stopped');
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

  const processCapture = async (captureData: CaptureData) => {
    if (isProcessing) {
      console.log('⏳ Already processing, skipping capture');
      return;
    }

    setIsProcessing(true);
    try {
      console.log('🚀 Processing capture...');
      const response = await fetch('http://192.168.100.47:3000/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          image: captureData.base64,
          width: captureData.width,
          height: captureData.height,
          timestamp: captureData.timestamp,
        }),
      });

      const result = await response.json();
      console.log('📊 Analysis result:', result.analysis.category, 'confidence:', result.analysis.confidence);
      
      // Handle the analysis result
      if (result.analysis.harmful && result.analysis.action === 'scroll') {
        console.log('⚠️ Harmful content detected - would trigger scroll');
        // TODO: Trigger scroll action
      } else if (result.analysis.harmful && result.analysis.action === 'blur') {
        console.log('⚠️ Harmful content detected - would trigger blur');
        // TODO: Trigger blur action
      } else {
        console.log('✅ Content safe - continuing');
      }

    } catch (error) {
      console.error('❌ Error processing capture:', error);
      // Continue processing even if backend fails
    } finally {
      setIsProcessing(false);
      // Small delay before next capture can be processed
      setTimeout(() => {
        // Ready for next capture
      }, 100);
    }
  };

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
          value={stats.isCapturing ? 'CAPTURING' : 'STOPPED'} 
          color={stats.isCapturing ? '#4CAF50' : '#F44336'} 
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
          value={isProcessing ? 'ANALYZING' : 'READY'} 
          color={isProcessing ? '#FF9800' : '#4CAF50'} 
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

        {permissionGranted && !stats.isCapturing && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#4CAF50' }]}
            onPress={startCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🎬 Start Capture</Text>
          </TouchableOpacity>
        )}

        {stats.isCapturing && (
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#F44336' }]}
            onPress={stopCapture}
            disabled={loading}
          >
            <Text style={styles.buttonText}>🛑 Stop Capture</Text>
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