#!/bin/bash

DEVICE_IP="192.168.100.13"

echo "⚡ Quick Build & Install"
echo "======================="

# Try to use last known device first
if [ -f .last_device ]; then
    LAST_DEVICE=$(cat .last_device)
    echo "🔄 Trying last known device: $LAST_DEVICE"
    
    # Check if it's still connected
    if adb devices | grep -q "$LAST_DEVICE.*device"; then
        echo "✅ Device still connected!"
    else
        echo "🔌 Reconnecting to $LAST_DEVICE..."
        adb connect "$LAST_DEVICE" >/dev/null 2>&1
        sleep 1
        
        if adb devices | grep -q "$LAST_DEVICE.*device"; then
            echo "✅ Reconnected successfully!"
        else
            echo "❌ Failed to reconnect to last device"
            rm .last_device
        fi
    fi
fi

# If no device connected, try auto-detection
if ! adb devices | grep -q "device$"; then
    echo "🔍 Auto-detecting device port..."
    
    if command -v nmap &> /dev/null; then
        PORT=$(nmap -sT $DEVICE_IP -p30000-49999 2>/dev/null | awk -F/ '/tcp open/{print $1}' | head -n 1)
        
        if [ -n "$PORT" ]; then
            echo "✅ Detected port: $PORT"
            adb connect "$DEVICE_IP:$PORT"
            if adb devices | grep -q "device$"; then
                echo "🎉 Auto-connected to $DEVICE_IP:$PORT"
                echo "$DEVICE_IP:$PORT" > .last_device
            fi
        fi
    fi
    
    # If auto-detection failed, ask manually
    if ! adb devices | grep -q "device$"; then
        echo "📱 Enter the current port from your phone's Wireless debugging:"
        read -p "Port for $DEVICE_IP: " PORT
        
        if [ -n "$PORT" ]; then
            adb connect "$DEVICE_IP:$PORT"
            if adb devices | grep -q "device$"; then
                echo "✅ Connected to $DEVICE_IP:$PORT"
                echo "$DEVICE_IP:$PORT" > .last_device
            else
                echo "❌ Failed to connect"
                exit 1
            fi
        else
            echo "❌ No port provided"
            exit 1
        fi
    fi
fi

# Quick build (skip cleaning for faster builds)
echo "🚀 Quick building..."
cd android
./gradlew assembleDebug --quiet

if [ $? -eq 0 ]; then
    echo "✅ Build complete! Installing..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "🎉 Installed successfully!"
    else
        echo "❌ Install failed"
    fi
else
    echo "❌ Build failed"
fi

cd ..