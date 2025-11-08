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

const { ScreenPermissionModule, NotificationModule, AccessibilityControlModule, ContentBlockingModule } = NativeModules;

interface PermissionStatus {
  screenCapture: boolean;
  accessibility: boolean;
  overlay: boolean;
  notifications: boolean;
}

export default function PermissionsScreen() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  const [permissions, setPermissions] = useState<PermissionStatus>({
    screenCapture: false,
    accessibility: false,
    overlay: false,
    notifications: false,
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Debug: Check if all native modules are available
    console.log('🔧 Checking native modules availability...');
    console.log('🔧 ScreenPermissionModule:', !!ScreenPermissionModule);
    console.log('🔧 NotificationModule:', !!NotificationModule);
    console.log('🔧 AccessibilityControlModule:', !!AccessibilityControlModule);
    console.log('🔧 ContentBlockingModule:', !!ContentBlockingModule);

    if (!ScreenPermissionModule) {
      Alert.alert('Module Error', 'ScreenPermissionModule not found');
      return;
    }

    checkAllPermissions();
  }, []);

  const checkAllPermissions = async () => {
    try {
      console.log('🔍 Checking all permissions...');
      const status = await ScreenPermissionModule.getAllPermissionsStatus();
      console.log('✅ Permissions status received:', status);

      // Also check notification permission
      const notificationEnabled = await NotificationModule.checkNotificationPermission();
      console.log('🔔 Notification permission:', notificationEnabled);

      setPermissions({
        ...status,
        notifications: notificationEnabled
      });
    } catch (error) {
      console.error('❌ Error checking permissions:', error);
      Alert.alert('Error', 'Failed to check permissions');
    }
  };

  const requestScreenCapture = async () => {
    setLoading(true);
    try {
      console.log('📱 Requesting screen capture permission...');
      const granted = await ScreenPermissionModule.requestScreenCapturePermission();
      console.log('📱 Screen capture result:', granted);
      setPermissions(prev => ({ ...prev, screenCapture: granted }));

      if (granted) {
        console.log('✅ Screen capture permission granted');
        Alert.alert('Success', 'Screen capture permission granted!');
      } else {
        console.log('❌ Screen capture permission denied');
        Alert.alert('Permission Denied', 'Screen capture permission was denied');
      }
    } catch (error) {
      console.error('❌ Error requesting screen capture:', error);
      Alert.alert('Error', `Failed to request screen capture: ${error instanceof Error ? error.message : String(error)}`);
    } finally {
      setLoading(false);
    }
  };

  const requestAccessibility = async () => {
    setLoading(true);
    try {
      console.log('♿ Requesting accessibility permission...');
      await ScreenPermissionModule.requestAccessibilityPermission();
      console.log('♿ Accessibility settings opened, waiting for user...');
      // Check status after user returns from settings
      setTimeout(() => {
        console.log('♿ Checking accessibility status after settings...');
        checkAllPermissions();
      }, 1000);
    } catch (error) {
      console.error('❌ Error requesting accessibility:', error);
      Alert.alert('Error', `Failed to request accessibility: ${error instanceof Error ? error.message : String(error)}`);
    } finally {
      setLoading(false);
    }
  };

  const requestOverlay = async () => {
    setLoading(true);
    try {
      console.log('🖼️ Requesting overlay permission...');
      const granted = await ScreenPermissionModule.requestOverlayPermission();
      console.log('🖼️ Overlay permission result:', granted);
      setPermissions(prev => ({ ...prev, overlay: granted }));

      if (granted) {
        console.log('✅ Overlay permission granted');
        Alert.alert('Success', 'Overlay permission granted!');
      } else {
        console.log('❌ Overlay permission denied or not available on this device');
        Alert.alert('Permission Info', 'Overlay permission not available on this device. This is normal for some Android devices. You can still use accessibility overlays!');
      }
    } catch (error) {
      console.error('❌ Error requesting overlay:', error);
      Alert.alert('Device Info', 'Display over other apps not supported on this device. Accessibility overlay will be used instead.');
    } finally {
      setLoading(false);
    }
  };

  const requestNotifications = async () => {
    setLoading(true);
    try {
      console.log('🔔 Requesting notification permission...');

      // First check if notifications are already enabled
      const isEnabled = await NotificationModule.checkNotificationPermission();
      console.log('🔔 Current notification status:', isEnabled);

      if (isEnabled) {
        console.log('✅ Notifications already enabled');
        Alert.alert('Already Enabled', 'Notifications are already enabled for this app');
        setPermissions(prev => ({ ...prev, notifications: true }));
      } else {
        console.log('❌ Notifications disabled - requesting permission');

        // Show explanation first, then request permission
        Alert.alert(
          'Enable Notifications',
          'Allot needs notification permission to show monitoring status and alerts. This will either show a permission dialog or open settings.',
          [
            { text: 'Cancel', style: 'cancel' },
            {
              text: 'Grant Permission',
              onPress: async () => {
                try {
                  console.log('🔔 Requesting notification permission...');
                  await NotificationModule.requestNotificationPermission();
                  console.log('🔔 Permission request completed');

                  // Check permissions again after user returns from settings or grants permission
                  setTimeout(async () => {
                    console.log('🔔 Checking notification status after permission request...');
                    await checkAllPermissions();

                    // Check if permission was granted
                    const newStatus = await NotificationModule.checkNotificationPermission();
                    if (newStatus) {
                      Alert.alert('Success!', 'Notification permission granted. You can now receive monitoring alerts.');
                    } else {
                      Alert.alert(
                        'Permission Required',
                        'Notifications are still disabled. Please enable them in your device settings to receive monitoring alerts.',
                        [
                          { text: 'OK', style: 'default' },
                          {
                            text: 'Open Settings',
                            onPress: async () => {
                              try {
                                await NotificationModule.openNotificationSettings();
                              } catch (error) {
                                console.error('Error opening notification settings:', error);
                              }
                            }
                          }
                        ]
                      );
                    }
                  }, 1500);
                } catch (error) {
                  console.error('❌ Error requesting notification permission:', error);
                  Alert.alert('Error', 'Failed to request notification permission. Please enable notifications manually in your device settings.');
                }
              }
            }
          ]
        );
      }
    } catch (error) {
      console.error('❌ Error checking notification permission:', error);
      Alert.alert('Error', `Failed to check notification permission: ${error instanceof Error ? error.message : String(error)}`);
    } finally {
      setLoading(false);
    }
  };

  const testNotification = async () => {
    try {
      console.log('🔔 Testing notification...');

      // First check if notifications are enabled
      const notificationEnabled = await NotificationModule.checkNotificationPermission();
      console.log('🔔 Notifications enabled:', notificationEnabled);

      if (!notificationEnabled) {
        console.log('❌ Notifications are disabled by user');
        Alert.alert('Notifications Disabled', 'Please enable notifications in device settings');
        return;
      }

      const result = await NotificationModule.showForegroundNotification(
        'Allot Active',
        'Monitoring screen activity - This should appear in status bar'
      );
      console.log('🔔 Notification result:', result);

      // Don't show alert immediately, let user see the actual notification
      setTimeout(() => {
        Alert.alert('Check Status Bar', 'Look for the notification in your status bar (pull down from top)');
      }, 500);
    } catch (error) {
      console.error('❌ Error showing notification:', error);
      Alert.alert('Error', `Failed to show notification: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const hideNotification = async () => {
    try {
      console.log('🔔 Hiding notification...');
      const result = await NotificationModule.hideForegroundNotification();
      console.log('🔔 Hide notification result:', result);
      Alert.alert('Success', 'Notification hidden from status bar');
    } catch (error) {
      console.error('❌ Error hiding notification:', error);
      Alert.alert('Error', `Failed to hide notification: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const testAccessibilityOverlay = async () => {
    try {
      console.log('🎭 Testing accessibility overlay...');
      const isRunning = await AccessibilityControlModule.isAccessibilityServiceRunning();
      console.log('🎭 Accessibility service running:', isRunning);

      if (!isRunning) {
        console.log('❌ Accessibility service not running');
        Alert.alert('Service Not Running', 'Please enable the accessibility service first');
        return;
      }

      const result = await AccessibilityControlModule.showOverlay('Allot Monitoring Active');
      console.log('Show overlay result:', result);
      Alert.alert('Success', 'Accessibility overlay shown!');
    } catch (error) {
      console.error('❌ Error showing accessibility overlay:', error);
      Alert.alert('Error', `Failed to show overlay: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const hideAccessibilityOverlay = async () => {
    try {
      console.log('🎭 Hiding accessibility overlay...');
      const result = await AccessibilityControlModule.hideOverlay();
      console.log('🎭 Hide overlay result:', result);
      Alert.alert('Success', 'Accessibility overlay hidden!');
    } catch (error) {
      console.error('❌ Error hiding accessibility overlay:', error);
      Alert.alert('Error', `Failed to hide overlay: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const testContentBlocking = async () => {
    try {
      console.log('🚫 Testing content blocking (RenderEffect + Accessibility)...');

      const isAccessibilityRunning = await AccessibilityControlModule.isAccessibilityServiceRunning();
      console.log('🚫 Accessibility service running:', isAccessibilityRunning);

      if (!isAccessibilityRunning) {
        console.log('❌ Accessibility service required for content blocking');
        Alert.alert('Accessibility Required', 'Please enable accessibility service for content blocking to work properly');
        return;
      }

      const result = await ContentBlockingModule.showContentWarning(
        'This content has been detected as harmful based on your configured settings.\n\nScrolling to next video...',
        3 // Auto-hide after 3 seconds
      );
      console.log('🚫 Content blocking result:', result);
      Alert.alert('Content Blocked!', 'Check your screen - content should be blurred with warning overlay. Will auto-hide in 3 seconds.');
    } catch (error) {
      console.error('❌ Error testing content blocking:', error);
      Alert.alert('Error', `Failed to test content blocking: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const hideContentBlocking = async () => {
    try {
      console.log('🚫 Manually hiding content blocking...');
      const result = await ContentBlockingModule.hideContentWarning();
      console.log('🚫 Hide content blocking result:', result);
      Alert.alert('Success', 'Content blocking removed!');
    } catch (error) {
      console.error('❌ Error hiding content blocking:', error);
      Alert.alert('Error', `Failed to hide content blocking: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const showPersistentNotification = async () => {
    try {
      console.log('🔔 Creating persistent notification...');
      const notificationEnabled = await NotificationModule.checkNotificationPermission();
      if (!notificationEnabled) {
        Alert.alert('Notifications Disabled', 'Please enable notifications first');
        return;
      }

      const result = await NotificationModule.showPersistentNotification(
        'Allot Active',
        'Content monitoring is active • Tap to open app'
      );
      console.log('🔔 Persistent notification result:', result);
      Alert.alert('Success', 'Persistent notification created! Check your status bar - this notification cannot be dismissed.');
    } catch (error) {
      console.error('❌ Error creating persistent notification:', error);
      Alert.alert('Error', `Failed to create persistent notification: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const hidePersistentNotification = async () => {
    try {
      console.log('🔔 Hiding persistent notification...');
      const result = await NotificationModule.hidePersistentNotification();
      console.log('🔔 Hide persistent notification result:', result);
      Alert.alert('Success', 'Persistent notification removed!');
    } catch (error) {
      console.error('❌ Error hiding persistent notification:', error);
      Alert.alert('Error', `Failed to hide persistent notification: ${error instanceof Error ? error.message : String(error)}`);
    }
  };

  const PermissionCard = ({
    title,
    description,
    granted,
    onRequest
  }: {
    title: string;
    description: string;
    granted: boolean;
    onRequest: () => void;
  }) => (
    <View style={[
      styles.card,
      {
        backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5',
        borderColor: granted ? '#4CAF50' : '#FF9800'
      }
    ]}>
      <View style={styles.cardHeader}>
        <Text style={[styles.cardTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          {title}
        </Text>
        <View style={[
          styles.statusBadge,
          { backgroundColor: granted ? '#4CAF50' : '#FF9800' }
        ]}>
          <Text style={styles.statusText}>
            {granted ? 'Granted' : 'Required'}
          </Text>
        </View>
      </View>
      <Text style={[styles.cardDescription, { color: isDark ? '#9BA1A6' : '#687076' }]}>
        {description}
      </Text>
      {!granted && (
        <TouchableOpacity
          style={[styles.button, { backgroundColor: Colors[colorScheme ?? 'light'].tint }]}
          onPress={onRequest}
          disabled={loading}
        >
          <Text style={styles.buttonText}>Grant Permission</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  return (
    <ScrollView style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.header}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Allot Permissions
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Grant the following permissions to enable Allot's monitoring features
        </Text>
      </View>

      <PermissionCard
        title="Screen Capture"
        description="Required to capture and analyze screen content for monitoring purposes"
        granted={permissions.screenCapture}
        onRequest={requestScreenCapture}
      />

      <PermissionCard
        title="Accessibility Service"
        description="Needed to detect scroll events and user interactions"
        granted={permissions.accessibility}
        onRequest={requestAccessibility}
      />

      <PermissionCard
        title="Display Over Other Apps"
        description="Allows showing status indicators and controls over other applications"
        granted={permissions.overlay}
        onRequest={requestOverlay}
      />

      <PermissionCard
        title="Notifications"
        description="Required to show persistent monitoring status and alerts in the notification bar"
        granted={permissions.notifications}
        onRequest={requestNotifications}
      />

      <View style={styles.testSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Test Features
        </Text>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#4CAF50' }]}
          onPress={testNotification}
        >
          <Text style={styles.buttonText}>Show Notification</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#F44336' }]}
          onPress={hideNotification}
        >
          <Text style={styles.buttonText}>Hide Notification</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#2196F3' }]}
          onPress={checkAllPermissions}
        >
          <Text style={styles.buttonText}>Refresh Status</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#9C27B0' }]}
          onPress={testAccessibilityOverlay}
        >
          <Text style={styles.buttonText}>Show Accessibility Overlay</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#FF5722' }]}
          onPress={hideAccessibilityOverlay}
        >
          <Text style={styles.buttonText}>Hide Accessibility Overlay</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#E91E63' }]}
          onPress={testContentBlocking}
        >
          <Text style={styles.buttonText}>🚫 Test Content Blocking (Blur + Overlay)</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#795548' }]}
          onPress={hideContentBlocking}
        >
          <Text style={styles.buttonText}>Remove Content Blocking</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#607D8B' }]}
          onPress={showPersistentNotification}
        >
          <Text style={styles.buttonText}>🔔 Create Persistent Notification</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#455A64' }]}
          onPress={hidePersistentNotification}
        >
          <Text style={styles.buttonText}>Remove Persistent Notification</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.testButton, { backgroundColor: '#FF9800' }]}
          onPress={async () => {
            try {
              await NotificationModule.openNotificationSettings();
            } catch (error) {
              console.error('Error opening notification settings:', error);
              Alert.alert('Error', 'Failed to open notification settings');
            }
          }}
        >
          <Text style={styles.buttonText}>⚙️ Open Notification Settings</Text>
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
  card: {
    padding: 20,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '600',
    flex: 1,
  },
  statusBadge: {
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  cardDescription: {
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 16,
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
  testSection: {
    marginTop: 30,
    paddingTop: 20,
    borderTopWidth: 1,
    borderTopColor: '#E0E0E0',
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    marginBottom: 16,
  },
  testButton: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 12,
  },
});