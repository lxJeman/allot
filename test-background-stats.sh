#!/bin/bash

echo "📊 Testing Background Stats Polling"
echo "===================================="
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
echo "2. Make sure 'Background Operation' is ON (enabled by default)"
echo "3. Tap 'Start Live Capture' and grant permissions"
echo "4. Watch the stats update in real-time"
echo "5. Minimize the app or switch to other apps"
echo "6. Return to the app - stats should continue updating"
echo "7. Open Instagram, TikTok, or other text-heavy apps"
echo "8. Watch the 'Total Captures' and 'Text Extracted' numbers grow"
echo ""

echo "📊 Expected behavior:"
echo "- Stats should update every 2 seconds"
echo "- Numbers should increase even when app is minimized"
echo "- Performance should remain consistent"
echo ""

echo "📱 Monitoring both UI stats and service logs:"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor both the service logs and any React Native logs
adb logcat -s LocalTextExtractionService:* ReactNativeJS:* | grep -E "(📊|Background stats|LOCAL ML TEXT EXTRACTION RESULT)"