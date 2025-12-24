#!/bin/bash

echo "🔍 ═══════════════════════════════════════"
echo "🔍 DEBUGGING SCREEN CAPTURE ISSUE"
echo "🔍 ═══════════════════════════════════════"
echo ""

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    echo "Please connect your device and enable USB debugging"
    exit 1
fi

echo "📱 Android device detected"
echo ""

echo "🔍 Checking current app state..."
echo ""

# Clear logs first
adb logcat -c

echo "📋 Instructions:"
echo "1. Open the Allot app on your device"
echo "2. Go to Tests tab → Local Text Extraction"
echo "3. Try 'Test Single Extraction'"
echo "4. Watch the logs below for the exact error"
echo ""
echo "🔍 Monitoring logs for screen capture issues..."
echo "════════════════════════════════════════"

# Monitor logs for screen capture and permission issues
adb logcat | grep -E "(ScreenCapture|LocalTextExtraction|Permission|MediaProjection|failed to capture|Error|Exception|startScreenCapture|captureNextFrame)" --line-buffered | while read line; do
    timestamp=$(date '+%H:%M:%S')
    echo "📋 [$timestamp] $line"
done