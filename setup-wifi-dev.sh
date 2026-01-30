#!/bin/bash

LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🌐 WiFi Development Setup"
echo "========================="
echo "💻 Your computer IP: $LOCAL_IP"
echo "🔌 Metro port: $METRO_PORT"
echo ""

# Check device connection
if ! adb devices | grep -q "device$"; then
    echo "❌ No device connected!"
    echo "Please run: ./quick-build.sh first"
    exit 1
fi

echo "✅ Device connected"
echo ""

# Set up port forwarding
echo "🔧 Setting up port forwarding..."
adb reverse tcp:$METRO_PORT tcp:$METRO_PORT

if [ $? -eq 0 ]; then
    echo "✅ Port forwarding successful!"
    echo ""
    echo "🚀 Now you can start Metro server with:"
    echo "   npx expo start --dev-client"
    echo ""
    echo "📱 Or if that doesn't work, manually configure your device:"
    echo "1. Shake your device or press menu button in the app"
    echo "2. Tap 'Settings' or 'Dev Settings'"
    echo "3. Tap 'Debug server host & port for device'"
    echo "4. Enter: $LOCAL_IP:$METRO_PORT"
    echo "5. Tap OK and reload"
else
    echo "❌ Port forwarding failed"
    echo ""
    echo "📱 Manual setup required:"
    echo "1. Open your app on the device"
    echo "2. Shake the device or press hardware menu button"
    echo "3. Tap 'Dev Settings' or 'Debug'"
    echo "4. Tap 'Debug server host & port for device'"
    echo "5. Enter: $LOCAL_IP:$METRO_PORT"
    echo "6. Tap 'OK'"
    echo ""
    echo "Then start Metro server with:"
    echo "   REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP npx expo start --dev-client"
fi