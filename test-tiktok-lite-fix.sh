#!/bin/bash

echo "🧪 Testing TikTok Lite Detection Fix"
echo "=================================="
echo ""

echo "📱 Current running processes:"
adb shell ps | grep -E "(allot|go)" | head -5

echo ""
echo "🔍 Checking if Allot service is running:"
adb shell ps | grep com.allot

echo ""
echo "📋 Instructions:"
echo "1. Open the Allot app"
echo "2. Start screen capture"
echo "3. Close the Allot app (press home button)"
echo "4. Open TikTok Lite"
echo "5. Check the logs below - it should now detect TikTok Lite and start capturing"
echo ""

echo "📊 Live logs (press Ctrl+C to stop):"
echo "======================================"
adb logcat -s ScreenCaptureService AllotAccessibility | grep -E "(TikTok|go|MONITORING|Not capturing)"