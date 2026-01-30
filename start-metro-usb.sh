#!/bin/bash

LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)

echo "🚀 Starting Metro Server (USB)"
echo "=============================="
echo "📡 Server: http://$LOCAL_IP:8081"
echo "🔌 Connection: USB Debugging"

# Check USB device
if ! adb devices | grep -q "device$"; then
    echo "❌ No USB device connected!"
    echo "Please connect your Android device via USB"
    exit 1
fi

DEVICE_ID=$(adb devices | grep "device$" | cut -f1)
echo "✅ USB device: $DEVICE_ID"

# Setup port forwarding
echo "🔧 Setting up USB port forwarding..."
adb reverse tcp:8081 tcp:8081

if [ $? -eq 0 ]; then
    echo "✅ Port forwarding active"
else
    echo "⚠️  Port forwarding failed"
fi

echo ""
echo "📱 Your app should connect automatically via USB"
echo "🔥 Metro starting... (Ctrl+C to stop)"
echo "====================================="

export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
npx expo start --dev-client --lan --port 8081