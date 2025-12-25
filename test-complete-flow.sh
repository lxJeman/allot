#!/bin/bash

echo "🔍 Complete Text Extraction Flow Test"
echo "====================================="
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
echo "4. Open apps with text content (Instagram, Settings, Chrome, etc.)"
echo "5. Watch the logs for extracted text content"
echo ""

echo "📊 What to Look For:"
echo "✅ HOT PATH: Direct bitmap processing (faster)"
echo "❌ COLD PATH: Base64 fallback (if hot path fails)"
echo "�  EXTRACTED TEXT: Actual text content from screen"
echo "⚡ PERFORMANCE: Processing times and optimizations"
echo ""

echo "🔍 Expected Flow:"
echo "1. 📸 Direct bitmap captured: 720x1600 (HOT PATH)"
echo "2. 🔍 Processing with ML Kit..."
echo "3. ✅ ML Kit success: found X blocks"
echo "4. 📝 Block: 'actual extracted text here'"
echo "5. ✅ Direct bitmap extraction result: 'combined text'"
echo "6. ⚡ Path: HOT (direct bitmap, no Base64 conversion)"
echo ""

echo "🐛 Troubleshooting:"
echo "- If you see 'Failed to run text recognizer': ML Kit initialization issue"
echo "- If you see 'No bitmap returned': Screen capture connection issue"
echo "- If you see 'COLD PATH': Fallback to Base64 (still works, just slower)"
echo ""

echo "📱 Monitoring complete extraction flow:"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor comprehensive logs showing the complete flow
adb logcat -s LocalTextExtractionService:* ReactNativeJS:* | grep -E "(📸|🔍|📝|✅|❌|⚡|HOT PATH|COLD PATH|Block:|extraction result:|Extracted Text:|Background stats)"