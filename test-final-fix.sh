#!/bin/bash

echo "🎯 FINAL FIX TEST - TikTok Lite Background Capture"
echo "================================================="
echo ""

echo "📱 Checking if TikTok Lite is installed:"
if adb shell pm list packages | grep -q "com.zhiliaoapp.musically.go"; then
    echo "✅ TikTok Lite found: com.zhiliaoapp.musically.go"
else
    echo "❌ TikTok Lite not found. Please install TikTok Lite first."
    exit 1
fi

echo ""
echo "🔍 Checking Allot service status:"
if adb shell ps | grep -q "com.allot"; then
    echo "✅ Allot service is running"
else
    echo "❌ Allot service not running. Please start the app first."
    exit 1
fi

echo ""
echo "📋 TEST PROCEDURE:"
echo "=================="
echo "1. ✅ Open Allot app"
echo "2. ✅ Start screen capture"
echo "3. ✅ Press HOME button (don't close app completely)"
echo "4. ✅ Open TikTok Lite"
echo "5. ✅ Watch the logs below - should show 'MONITORING: TikTok Lite'"
echo ""

echo "🚀 Starting live log monitoring..."
echo "Press Ctrl+C to stop"
echo "=================================="

# Monitor logs for TikTok Lite detection and capture
adb logcat -c  # Clear logs
adb logcat -s ScreenCaptureService AllotAccessibility | grep -E "(TikTok|MONITORING|Not capturing|musically\.go)" --line-buffered