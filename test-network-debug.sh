#!/bin/bash

echo "🔍 NETWORK DEBUGGING TEST"
echo "========================="
echo ""

echo "📱 Testing backend connectivity from device:"
adb shell "curl -s -m 10 http://192.168.100.47:3000/health || echo 'CURL FAILED'"

echo ""
echo "🌐 Testing backend from host machine:"
curl -s -m 5 http://192.168.100.47:3000/health || echo "HOST CURL FAILED"

echo ""
echo "📊 Device network info:"
adb shell "ip route show | head -3"

echo ""
echo "🔍 Live logs with network debugging:"
echo "===================================="
adb logcat -c
adb logcat -s ScreenCaptureService | grep -E "(Network|Backend|Frame captured|Analysis complete|OCR complete)" --line-buffered