#!/bin/bash

DEVICE_IP="192.168.100.13"

echo "🚀 Smart Build & Install Script"
echo "================================"

# Function to auto-detect and connect device
reconnect_device() {
    echo "🔄 Attempting to reconnect device..."
    
    # First try adb reconnect
    adb reconnect 2>/dev/null
    sleep 2
    
    # Check if reconnected
    if adb devices | grep -q "device$"; then
        echo "✅ Device reconnected successfully"
        return 0
    fi
    
    # Try auto-detection with nmap
    echo "🔍 Auto-detecting device port..."
    if command -v nmap &> /dev/null; then
        echo "📡 Scanning $DEVICE_IP for open ports..."
        PORT=$(nmap -sT $DEVICE_IP -p30000-49999 2>/dev/null | awk -F/ '/tcp open/{print $1}' | head -n 1)
        
        if [ -n "$PORT" ]; then
            echo "✅ Detected port: $PORT"
            adb connect "$DEVICE_IP:$PORT"
            if adb devices | grep -q "device$"; then
                echo "🎉 Auto-connected to $DEVICE_IP:$PORT"
                echo "$DEVICE_IP:$PORT" > .last_device
                return 0
            fi
        else
            echo "❌ No open ports detected"
        fi
    else
        echo "⚠️  nmap not found - installing..."
        sudo apt update && sudo apt install -y nmap >/dev/null 2>&1
        # Retry after installation
        PORT=$(nmap -sT $DEVICE_IP -p30000-49999 2>/dev/null | awk -F/ '/tcp open/{print $1}' | head -n 1)
        if [ -n "$PORT" ]; then
            echo "✅ Detected port: $PORT"
            adb connect "$DEVICE_IP:$PORT"
            if adb devices | grep -q "device$"; then
                echo "🎉 Auto-connected to $DEVICE_IP:$PORT"
                echo "$DEVICE_IP:$PORT" > .last_device
                return 0
            fi
        fi
    fi
    
    # Fallback to manual input
    echo "❌ Auto-detection failed"
    echo "📱 Please check your phone's Wireless debugging settings for the current port"
    read -p "Enter current port for $DEVICE_IP: " PORT
    
    if [ -n "$PORT" ]; then
        adb connect "$DEVICE_IP:$PORT"
        if adb devices | grep -q "device$"; then
            echo "✅ Connected successfully to $DEVICE_IP:$PORT"
            echo "$DEVICE_IP:$PORT" > .last_device
            return 0
        fi
    fi
    
    return 1
}

# Check device connection
echo "📱 Checking device connection..."
if ! adb devices | grep -q "device$"; then
    echo "⚠️  No device connected"
    if ! reconnect_device; then
        echo "❌ Failed to connect device. Exiting."
        exit 1
    fi
else
    echo "✅ Device connected"
fi

# Build and install
echo "🔨 Building Android APK..."
echo "🧹 Cleaning cached bundles..."
rm -f android/app/src/main/assets/index.android.bundle
rm -rf android/app/build/intermediates/assets/

echo "🔨 Building APK..."
cd android
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful! Installing APK..."
    
    # Check connection before installing
    if ! adb devices | grep -q "device$"; then
        echo "🔄 Device disconnected during build, reconnecting..."
        cd ..
        if ! reconnect_device; then
            echo "❌ Failed to reconnect. Please run manually:"
            echo "adb install android/app/build/outputs/apk/debug/app-debug.apk"
            exit 1
        fi
        cd android
    fi
    
    # Install with force reinstall
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "🎉 APK installed successfully!"
        echo "📱 Check your device - the app should be installed"
        
        # Save successful connection info
        CURRENT_DEVICE=$(adb devices | grep "device$" | cut -f1)
        echo "💾 Saving device info: $CURRENT_DEVICE"
        echo "$CURRENT_DEVICE" > .last_device
        
    else
        echo "❌ Failed to install APK"
        echo "🔧 Troubleshooting:"
        echo "1. Check if device is still connected: adb devices"
        echo "2. Try manual install: adb install -r android/app/build/outputs/apk/debug/app-debug.apk"
    fi
else
    echo "❌ Build failed"
fi

cd ..