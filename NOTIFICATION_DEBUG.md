# 🔔 Notification Issues - Deep Dive Analysis & Fixes

## 🎯 **ISSUES IDENTIFIED & FIXED**

### ❌ **Issue 1: Bundled Version Not Updating**
**Root Cause**: Cached JavaScript bundle in `android/app/src/main/assets/index.android.bundle`
**Solution**: 
- Removed cached bundle files
- Updated build script to clean cache before building
- APK now includes latest JavaScript changes

### ❌ **Issue 2: Notification Toggle Not Available**
**Root Causes**:
1. **Missing Android Manifest Permissions**:
   - `POST_NOTIFICATIONS` (Android 13+)
   - `FOREGROUND_SERVICE_SPECIAL_USE`
   - `USE_FULL_SCREEN_INTENT`

2. **Android 13+ Runtime Permission Requirements**:
   - Android 13+ requires explicit runtime permission request
   - Previous implementation only checked status, didn't request permission

## 🛠️ **FIXES IMPLEMENTED**

### ✅ **1. Android Manifest Updates**
```xml
<!-- Added to AndroidManifest.xml -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE"/>
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT"/>
```

### ✅ **2. Enhanced NotificationModule**
- **`requestNotificationPermission()`** - Handles Android 13+ permission requests
- **`openNotificationSettings()`** - Opens correct settings page per Android version
- **Version-specific handling**:
  - Android 13+: Directs to permission request
  - Android 8-12: Opens app notification settings
  - Android 5-7: Opens app info page
  - Below Android 5: Opens general settings

### ✅ **3. Improved Build Process**
- **`build-and-install.sh`** script cleans cache and rebuilds
- Removes cached bundles before building
- Ensures latest JavaScript changes are included

### ✅ **4. Persistent Notification System**
- **Two notification channels**:
  - `allot_foreground_service` - Regular notifications
  - `allot_persistent_service` - Always-on notifications
- **`setOngoing(true)`** - Makes notifications undismissable
- **Low priority** - Non-intrusive but always visible

## 🧪 **TESTING INSTRUCTIONS**

### **For Bundled Version (Production-like)**:
```bash
./build-and-install.sh
```

### **Test Notification Permission**:
1. Open app → Permissions screen
2. Notification card should show "Required" status
3. Tap "Grant Permission"
4. Tap "Open Settings" - should open notification settings
5. **Enable notifications** for Allot app
6. Return to app - status should update to "Granted"

### **Test Persistent Notification**:
1. Ensure notifications are enabled
2. Tap "🔔 Create Persistent Notification"
3. Check status bar - should see "Allot Active" notification
4. **Try to swipe away** - should stay (cannot be dismissed)
5. Tap notification - should open app
6. Use "Remove Persistent Notification" to remove it

## 🔍 **DEBUGGING COMMANDS**

### **Check if notification permission is in manifest**:
```bash
aapt dump permissions android/app/build/outputs/apk/debug/app-debug.apk | grep -i notification
```

### **Check app notification status on device**:
```bash
adb shell cmd notification allowed_listeners
adb shell dumpsys notification | grep -A5 -B5 "com.allot"
```

### **View app permissions**:
```bash
adb shell dumpsys package com.allot | grep -A10 -B10 permission
```

### **Check Android version on device**:
```bash
adb shell getprop ro.build.version.sdk
```

## 🎯 **EXPECTED BEHAVIOR**

### **Android 13+ (API 33+)**:
- App should request POST_NOTIFICATIONS permission
- User sees system permission dialog or settings page
- Toggle should be available in notification settings

### **Android 8-12 (API 26-32)**:
- Notifications enabled by default
- User can toggle in app notification settings
- No runtime permission required

### **Android 5-7 (API 21-25)**:
- Notifications enabled by default
- Toggle available in app info page
- No notification channels

## 🚀 **VERIFICATION**

The fixes address both issues:
1. **Bundled version** now includes latest changes (cache clearing)
2. **Notification toggle** should be available (proper permissions + Android version handling)

Test the bundled version after running `./build-and-install.sh` to verify all changes are included!