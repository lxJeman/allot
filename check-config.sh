#!/bin/bash

echo "🔍 Allot Configuration Check"
echo "============================"
echo ""

# Check backend URL in app.json
echo "📋 Backend Configuration:"
BACKEND_URL=$(node -e "const fs = require('fs'); const appJson = JSON.parse(fs.readFileSync('app.json', 'utf8')); console.log(appJson.expo.extra?.backendUrl || 'Not set');" 2>/dev/null)
echo "   Current URL: $BACKEND_URL"
echo ""

# Check if backend is reachable
echo "🌐 Backend Status:"
if command -v curl &> /dev/null; then
    if curl -s --connect-timeout 2 "$BACKEND_URL/health" > /dev/null 2>&1; then
        echo "   ✅ Backend is reachable at $BACKEND_URL"
    else
        echo "   ❌ Backend is NOT reachable at $BACKEND_URL"
        echo "   💡 Make sure the backend is running:"
        echo "      cd rust-backend && cargo run"
    fi
else
    echo "   ⚠️  curl not installed - cannot check backend"
fi
echo ""

# Check if ngrok is running
echo "🔌 Ngrok Status:"
if pgrep -x "ngrok" > /dev/null; then
    echo "   ✅ Ngrok is running"
    if command -v curl &> /dev/null; then
        NGROK_URL=$(curl -s http://localhost:4040/api/tunnels 2>/dev/null | grep -o '"public_url":"https://[^"]*' | head -1 | cut -d'"' -f4)
        if [ ! -z "$NGROK_URL" ]; then
            echo "   📡 Public URL: $NGROK_URL"
        fi
    fi
else
    echo "   ❌ Ngrok is NOT running"
    echo "   💡 Start ngrok with: npm run ngrok"
fi
echo ""

# Check Android device
echo "📱 Device Status:"
if command -v adb &> /dev/null; then
    DEVICE_COUNT=$(adb devices | grep -c "device$")
    if [ "$DEVICE_COUNT" -gt 0 ]; then
        echo "   ✅ $DEVICE_COUNT device(s) connected"
        adb devices | grep "device$" | while read line; do
            DEVICE_ID=$(echo $line | cut -f1)
            MODEL=$(adb -s $DEVICE_ID shell getprop ro.product.model 2>/dev/null | tr -d '\r')
            echo "      - $DEVICE_ID ($MODEL)"
        done
    else
        echo "   ❌ No devices connected"
        echo "   💡 Connect via USB and enable USB debugging"
    fi
else
    echo "   ⚠️  adb not installed - cannot check devices"
fi
echo ""

# Check if APK exists
echo "📦 Build Status:"
if [ -f "android/app/build/outputs/apk/release/app-release.apk" ]; then
    APK_SIZE=$(du -h android/app/build/outputs/apk/release/app-release.apk | cut -f1)
    APK_DATE=$(stat -c %y android/app/build/outputs/apk/release/app-release.apk 2>/dev/null | cut -d' ' -f1,2 | cut -d'.' -f1)
    echo "   ✅ Release APK exists"
    echo "      Size: $APK_SIZE"
    echo "      Built: $APK_DATE"
else
    echo "   ❌ No release APK found"
    echo "   💡 Build with: ./build-standalone.sh"
fi
echo ""

# Summary
echo "📊 Summary:"
echo "   Backend URL: $BACKEND_URL"
if pgrep -x "ngrok" > /dev/null && [ "$DEVICE_COUNT" -gt 0 ]; then
    echo "   Status: ✅ Ready to build and deploy!"
elif pgrep -x "ngrok" > /dev/null; then
    echo "   Status: ⚠️  Ngrok running, but no device connected"
elif [ "$DEVICE_COUNT" -gt 0 ]; then
    echo "   Status: ⚠️  Device connected, but ngrok not running"
else
    echo "   Status: ❌ Need to start backend, ngrok, and connect device"
fi
