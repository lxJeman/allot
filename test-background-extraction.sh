#!/bin/bash

echo "🧪 Testing Background Text Extraction"
echo "======================================"
echo ""

echo "📱 Building and installing the app..."
cd android
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo ""
echo "🚀 Starting the app..."
adb shell am start -n com.allot/.MainActivity

echo ""
echo "⏳ Waiting for app to load..."
sleep 5

echo ""
echo "🎯 Instructions for testing:"
echo "1. Open the app and go to 'Local Text Extraction' tab"
echo "2. Enable 'Background Operation' toggle"
echo "3. Tap 'Start Live Capture'"
echo "4. Grant screen capture permission"
echo "5. Minimize the app or switch to another app"
echo "6. Check that the notification shows 'Background Text Extraction Active'"
echo "7. Open other apps with text content"
echo "8. Check logs to see extraction continues in background"
echo ""

echo "📊 Monitoring logs (press Ctrl+C to stop):"
echo "Looking for LocalTextExtractionService logs..."
adb logcat -s LocalTextExtractionService:* ScreenCaptureService:*