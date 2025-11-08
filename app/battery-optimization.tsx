/**
 * Battery Optimization Setup Screen
 * 
 * Guides users through whitelisting the app from battery optimization,
 * especially important for Xiaomi/MIUI devices.
 */

import React, { useEffect, useState } from 'react';
import {
  Alert,
  NativeModules,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { Colors } from '../constants/theme';
import { useColorScheme } from '../hooks/use-color-scheme';

const { BatteryOptimizationModule } = NativeModules;

interface DeviceInfo {
  manufacturer: string;
  brand: string;
  model: string;
  device: string;
  isMiui: boolean;
  isXiaomi: boolean;
  isHuawei: boolean;
  isOppo: boolean;
  isSamsung: boolean;
}

export default function BatteryOptimizationScreen() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  
  const [isWhitelisted, setIsWhitelisted] = useState(false);
  const [deviceInfo, setDeviceInfo] = useState<DeviceInfo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkStatus();
  }, []);

  const checkStatus = async () => {
    try {
      setLoading(true);
      const [whitelisted, info] = await Promise.all([
        BatteryOptimizationModule.isIgnoringBatteryOptimizations(),
        BatteryOptimizationModule.getDeviceInfo(),
      ]);
      
      setIsWhitelisted(whitelisted);
      setDeviceInfo(info);
      
      console.log('Device info:', info);
      console.log('Whitelisted:', whitelisted);
    } catch (error) {
      console.error('Error checking status:', error);
    } finally {
      setLoading(false);
    }
  };

  const requestWhitelist = async () => {
    try {
      await BatteryOptimizationModule.requestIgnoreBatteryOptimizations();
      
      // Check status after user returns
      setTimeout(() => {
        checkStatus();
      }, 1000);
    } catch (error) {
      console.error('Error requesting whitelist:', error);
      Alert.alert('Error', 'Failed to open battery optimization settings');
    }
  };

  const openManufacturerSettings = async () => {
    try {
      await BatteryOptimizationModule.openManufacturerSettings();
      
      // Show instructions
      if (deviceInfo?.isMiui) {
        Alert.alert(
          'MIUI Settings',
          '1. Find "Allot" in the list\n2. Enable "Autostart"\n3. Go back and check battery optimization',
          [{ text: 'OK' }]
        );
      }
      
      setTimeout(() => {
        checkStatus();
      }, 1000);
    } catch (error) {
      console.error('Error opening manufacturer settings:', error);
    }
  };

  const getManufacturerName = (): string => {
    if (!deviceInfo) return 'Your Device';
    if (deviceInfo.isMiui) return 'MIUI/Xiaomi';
    if (deviceInfo.isHuawei) return 'Huawei';
    if (deviceInfo.isOppo) return 'Oppo/OnePlus';
    if (deviceInfo.isSamsung) return 'Samsung';
    return deviceInfo.manufacturer;
  };

  const getInstructions = (): string[] => {
    if (!deviceInfo) return [];
    
    if (deviceInfo.isMiui) {
      return [
        'Open "Security" app',
        'Go to "Battery & Performance"',
        'Tap "Choose apps"',
        'Find "Allot" and select "No restrictions"',
        'Go to "Autostart" and enable for Allot',
        'Restart the app',
      ];
    }
    
    if (deviceInfo.isHuawei) {
      return [
        'Open "Settings"',
        'Go to "Battery" → "App Launch"',
        'Find "Allot"',
        'Toggle to "Manage manually"',
        'Enable all three options',
      ];
    }
    
    if (deviceInfo.isOppo) {
      return [
        'Open "Settings"',
        'Go to "Battery" → "App Battery Management"',
        'Find "Allot"',
        'Select "Don\'t optimize"',
        'Enable "Autostart"',
      ];
    }
    
    if (deviceInfo.isSamsung) {
      return [
        'Open "Settings"',
        'Go to "Apps" → "Allot"',
        'Tap "Battery"',
        'Select "Unrestricted"',
      ];
    }
    
    return [
      'Open "Settings"',
      'Go to "Battery" → "Battery Optimization"',
      'Find "Allot"',
      'Select "Don\'t optimize"',
    ];
  };

  if (loading) {
    return (
      <View style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Loading...
        </Text>
      </View>
    );
  }

  return (
    <ScrollView style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.header}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Battery Optimization
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Required for background monitoring
        </Text>
      </View>

      {/* Status Card */}
      <View style={[
        styles.card,
        {
          backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5',
          borderColor: isWhitelisted ? '#4CAF50' : '#FF9800',
          borderWidth: 2,
        }
      ]}>
        <View style={styles.statusHeader}>
          <Text style={[styles.statusIcon, { fontSize: 48 }]}>
            {isWhitelisted ? '✅' : '⚠️'}
          </Text>
          <View style={styles.statusText}>
            <Text style={[styles.statusTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {isWhitelisted ? 'Optimized' : 'Action Required'}
            </Text>
            <Text style={[styles.statusSubtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              {isWhitelisted
                ? 'App can run in background'
                : 'App may stop when minimized'}
            </Text>
          </View>
        </View>
      </View>

      {/* Device Info */}
      {deviceInfo && (
        <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
          <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Device Information
          </Text>
          <View style={styles.infoRow}>
            <Text style={[styles.infoLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Manufacturer:
            </Text>
            <Text style={[styles.infoValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {getManufacturerName()}
            </Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={[styles.infoLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Model:
            </Text>
            <Text style={[styles.infoValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {deviceInfo.model}
            </Text>
          </View>
          {deviceInfo.isMiui && (
            <View style={[styles.warningBox, { backgroundColor: '#FFF3CD' }]}>
              <Text style={styles.warningText}>
                ⚠️ MIUI detected - Additional setup required for reliable background operation
              </Text>
            </View>
          )}
        </View>
      )}

      {/* Actions */}
      {!isWhitelisted && (
        <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
          <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Setup Steps
          </Text>
          
          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#4CAF50' }]}
            onPress={requestWhitelist}
          >
            <Text style={styles.buttonText}>
              1️⃣ Disable Battery Optimization
            </Text>
          </TouchableOpacity>

          {(deviceInfo?.isMiui || deviceInfo?.isHuawei || deviceInfo?.isOppo) && (
            <TouchableOpacity
              style={[styles.button, { backgroundColor: '#FF9800', marginTop: 12 }]}
              onPress={openManufacturerSettings}
            >
              <Text style={styles.buttonText}>
                2️⃣ Enable Autostart ({getManufacturerName()})
              </Text>
            </TouchableOpacity>
          )}

          <TouchableOpacity
            style={[styles.button, { backgroundColor: '#2196F3', marginTop: 12 }]}
            onPress={checkStatus}
          >
            <Text style={styles.buttonText}>
              ✓ Check Status
            </Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Instructions */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Manual Instructions for {getManufacturerName()}
        </Text>
        {getInstructions().map((instruction, index) => (
          <View key={index} style={styles.instructionRow}>
            <Text style={[styles.instructionNumber, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              {index + 1}.
            </Text>
            <Text style={[styles.instructionText, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {instruction}
            </Text>
          </View>
        ))}
      </View>

      {/* Why This Matters */}
      <View style={[styles.card, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Why This Matters
        </Text>
        <Text style={[styles.explanation, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Without battery optimization whitelist, Android will:
        </Text>
        <Text style={[styles.bulletPoint, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          • Stop the app when you minimize it
        </Text>
        <Text style={[styles.bulletPoint, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          • Prevent background monitoring
        </Text>
        <Text style={[styles.bulletPoint, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          • Block network requests in background
        </Text>
        <Text style={[styles.explanation, { color: isDark ? '#9BA1A6' : '#687076', marginTop: 12 }]}>
          Whitelisting allows Allot to:
        </Text>
        <Text style={[styles.bulletPoint, { color: '#4CAF50' }]}>
          ✓ Monitor content continuously
        </Text>
        <Text style={[styles.bulletPoint, { color: '#4CAF50' }]}>
          ✓ Detect harmful content in real-time
        </Text>
        <Text style={[styles.bulletPoint, { color: '#4CAF50' }]}>
          ✓ Auto-scroll past harmful posts
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
  statusHeader: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusIcon: {
    marginRight: 16,
  },
  statusText: {
    flex: 1,
  },
  statusTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  statusSubtitle: {
    fontSize: 14,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  infoLabel: {
    fontSize: 14,
  },
  infoValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  warningBox: {
    padding: 12,
    borderRadius: 8,
    marginTop: 12,
  },
  warningText: {
    fontSize: 12,
    color: '#856404',
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
  instructionRow: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  instructionNumber: {
    fontSize: 14,
    marginRight: 8,
    fontWeight: '600',
  },
  instructionText: {
    fontSize: 14,
    flex: 1,
    lineHeight: 20,
  },
  explanation: {
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 8,
  },
  bulletPoint: {
    fontSize: 14,
    lineHeight: 24,
    marginLeft: 8,
  },
});
