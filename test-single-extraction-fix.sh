#!/bin/bash

echo "🔍 ═══════════════════════════════════════"
echo "🔍 TESTING SINGLE EXTRACTION FIX"
echo "🔍 ═══════════════════════════════════════"
echo ""

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    echo "Please connect your device and enable USB debugging"
    exit 1
fi

echo "📱 Android device detected"
echo "✅ App installed with updated LocalTextExtractionModule"
echo ""

# Clear logs first
adb logcat -c

echo "📋 Instructions:"
echo "1. Open the Allot app on your device"
echo "2. Go to Tests tab → Local Text Extraction"
echo "3. Press 'Test Single Extraction'"
echo "4. Grant screen capture permission when prompted"
echo "5. Watch the logs below for the extraction results"
echo ""
echo "🔍 Expected behavior:"
echo "   ✅ Screen capture permission granted"
echo "   ✅ Screen capture started successfully"
echo "   ✅ Frame captured with dimensions"
echo "   ✅ Text extraction completed with results"
echo "   ✅ Alert shows extracted text and confidence"
echo ""
echo "🔍 Monitoring logs for single extraction test..."
echo "════════════════════════════════════════"

# Monitor logs for the single extraction test
adb logcat | grep -E "(LocalTextExtractionModule|ScreenCaptureModule|🧪|📸|✅|❌|Single extraction|Frame captured|Text extraction|captureNextFrame)" --line-buffered | while read line; do
    timestamp=$(date '+%H:%M:%S')
    echo "📋 [$timestamp] $line"
done