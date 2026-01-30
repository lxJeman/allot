#!/bin/bash

DEVICE_IP="192.168.100.13"
LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)

echo "🚀 Starting Metro Server with Auto-Connect"
echo "=========================================="
echo "📡 Server: http://$LOCAL_IP:8081"

# Auto-connect device if not connected
if ! adb devices | grep -q "device$"; then
    echo "🔍 Auto-detecting device..."
    
    if command -v nmap &> /dev/null; then
        PORT=$(nmap -sT $DEVICE_IP -p30000-49999 2>/dev/null | awk -F/ '/tcp open/{print $1}' | head -n 1)
        
        if [ -n "$PORT" ]; then
            echo "✅ Detected port: $PORT"
            adb connect "$DEVICE_IP:$PORT" >/dev/null 2>&1
            
            if adb devices | grep -q "device$"; then
                echo "🎉 Auto-connected!"
                # Setup port forwarding
                adb reverse tcp:8081 tcp:8081
            fi
        fi
    fi
fi

# Setup port forwarding if device is connected
if adb devices | grep -q "device$"; then
    echo "🔧 Setting up port forwarding..."
    adb reverse tcp:8081 tcp:8081
fi

echo ""
echo "📱 If your app can't connect:"
echo "1. Shake your device in the app"
echo "2. Tap 'Settings' → 'Debug server host & port'"
echo "3. Enter: $LOCAL_IP:8081"
echo "4. Tap OK and reload"
echo ""
echo "🔥 Metro starting... (Ctrl+C to stop)"
echo "====================================="

export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
npx expo start --dev-client --lan --port 8081