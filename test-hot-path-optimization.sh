#!/bin/bash

echo "⚡ Testing Hot Path Optimization"
echo "================================"
echo ""

echo "📱 Building and installing the app..."
cd android
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo ""
echo "🚀 Starting the app..."
adb shell am start -n com.allot/.MainActivity

echo ""
echo "🎯 Test Instructions:"
echo "1. Open the app and go to 'Local Text Extraction' tab"
echo "2. Make sure 'Background Operation' is ON"
echo "3. Tap 'Start Live Capture' and grant permissions"
echo "4. Watch the logs for performance improvements"
echo ""

echo "⚡ Expected Hot Path Benefits:"
echo "- Look for 'HOT PATH' vs 'COLD PATH' in logs"
echo "- Direct bitmap processing should be ~100-200ms faster"
echo "- No Base64 encoding/decoding overhead"
echo "- Better memory efficiency"
echo ""

echo "📊 Performance Comparison:"
echo "- COLD PATH: Screenshot → Bitmap → Base64 → Bitmap → ML Kit"
echo "- HOT PATH:  Screenshot → Bitmap → ML Kit (direct)"
echo ""

echo "📱 Monitoring performance logs:"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor logs specifically for hot path vs cold path performance
adb logcat -s LocalTextExtractionService:* | grep -E "(HOT PATH|COLD PATH|Direct bitmap|Base64|Processing Time|Total Time)"