# 📱 App Detection System Guide

## Overview
This system detects when specific social media apps are opened and can trigger screen capture only for those apps to save resources.

## 🎯 Monitored Apps
The system currently monitors these social media apps:
- **TikTok** (`com.zhiliaoapp.musically`)
- **TikTok Lite** (`com.zhiliaoapp.musically.go`)
- **Instagram** (`com.instagram.android`)
- **Instagram Lite** (`com.instagram.lite`)
- **Facebook** (`com.facebook.katana`)
- **Facebook Lite** (`com.facebook.lite`)
- **Twitter** (`com.twitter.android`)
- **Reddit** (`com.reddit.frontpage`)
- **Snapchat** (`com.snapchat.android`)
- **WhatsApp** (`com.whatsapp`)
- **Discord** (`com.discord`)
- **Pinterest** (`com.pinterest`)
- **LinkedIn** (`com.linkedin.android`)

## 🛠️ Debug Scripts

### 1. `debug-app-detection.sh` - Main debugging tool
```bash
# Enable debug logging and start monitoring
./debug-app-detection.sh debug-on

# Just monitor logs
./debug-app-detection.sh monitor

# Check current status
./debug-app-detection.sh status

# List monitored apps
./debug-app-detection.sh apps

# Get current app info
./debug-app-detection.sh current

# Run interactive test
./debug-app-detection.sh test

# Disable debug logging
./debug-app-detection.sh debug-off
```

### 2. `monitor-app-detection.sh` - Simple log monitor
```bash
# Start real-time log monitoring
./monitor-app-detection.sh
```

### 3. `test-app-detection-simple.sh` - Quick test
```bash
# Run a quick test to verify detection is working
./test-app-detection-simple.sh
```

## 🔍 How to Debug App Detection

### Step 1: Check if everything is set up
```bash
./debug-app-detection.sh status
```

### Step 2: Enable debug logging and monitor
```bash
./debug-app-detection.sh debug-on
```

### Step 3: Test by switching apps
1. Open a monitored app (like TikTok or Instagram)
2. Switch to a non-monitored app (like Settings)
3. Switch back to a monitored app
4. Watch the logs for detection events

### Step 4: Look for these log messages
- `🎯 ENTERED MONITORED APP` - When you open a social media app
- `🚪 LEFT MONITORED APP` - When you leave a social media app
- `⚡ Screen capture should START/STOP` - Capture control messages
- `🔄 APP CHANGE` - Any app switch (when debug logging is on)

## 📋 Log Message Types

| Icon | Type | Description |
|------|------|-------------|
| 🎯 | MONITORED ENTRY | Entered a social media app - capture should start |
| 🚪 | MONITORED EXIT | Left a social media app - capture should stop |
| 🔄 | APP CHANGE | Any app switch (debug mode only) |
| ⚡ | CAPTURE EVENT | Screen capture start/stop instruction |
| 🔍 | DEBUG | Debug logging status changes |
| ❌ | ERROR | Error messages |
| ✅ | SUCCESS | Successful operations |

## 🚀 Integration with Screen Capture

The app detection system sends events that your screen capture system can listen to:

### React Native Integration
```javascript
import { NativeEventEmitter, NativeModules } from 'react-native';

const { AppDetectionModule } = NativeModules;
const eventEmitter = new NativeEventEmitter(AppDetectionModule);

// Listen for app changes
eventEmitter.addListener('onAppChanged', (event) => {
  console.log('App changed:', event.appName);
  console.log('Is monitored:', event.isMonitored);
  
  if (event.isMonitored) {
    // Start screen capture
    startScreenCapture();
  } else {
    // Stop screen capture
    stopScreenCapture();
  }
});

// Get current app status
AppDetectionModule.getCurrentApp()
  .then(result => {
    console.log('Current app:', result.appName);
    console.log('Is monitored:', result.isMonitored);
  });
```

### Native Android Integration
The `AllotAccessibilityService` provides callbacks:
```kotlin
AllotAccessibilityService.getInstance()?.onAppChanged = { packageName, isMonitored ->
    if (isMonitored) {
        // Start screen capture for social media apps
        startScreenCapture()
    } else {
        // Stop screen capture for other apps
        stopScreenCapture()
    }
}
```

## 🔧 Troubleshooting

### App detection not working?
1. Check if accessibility service is enabled:
   ```bash
   ./debug-app-detection.sh status
   ```
2. Enable accessibility service in Android Settings > Accessibility
3. Make sure the app is running

### Not seeing log messages?
1. Enable debug logging:
   ```bash
   ./debug-app-detection.sh debug-on
   ```
2. Check device connection:
   ```bash
   adb devices
   ```

### False detections?
1. Check the monitored apps list:
   ```bash
   ./debug-app-detection.sh apps
   ```
2. Monitor logs to see what's being detected:
   ```bash
   ./debug-app-detection.sh monitor
   ```

## 📊 Performance Benefits

By only capturing screens when social media apps are active:
- **Reduced CPU usage** - No unnecessary screen captures
- **Lower battery drain** - Capture only when needed
- **Less storage usage** - Fewer screenshots to process
- **Better performance** - System resources available for other tasks

## 🎛️ Customization

To add or remove monitored apps, edit the `MONITORED_APPS` set in `AllotAccessibilityService.kt`:

```kotlin
private val MONITORED_APPS = setOf(
    "com.zhiliaoapp.musically",    // TikTok
    "com.instagram.android",       // Instagram
    // Add your apps here
    "com.example.newapp"           // New app to monitor
)
```

Don't forget to update the `APP_DISPLAY_NAMES` map for better logging:

```kotlin
private val APP_DISPLAY_NAMES = mapOf(
    "com.example.newapp" to "New App Name"
)
```