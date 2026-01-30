#!/bin/bash

DEVICE_IP="192.168.100.13"
LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🚀 Ultimate React Native Development Script"
echo "==========================================="
echo "💻 Computer IP: $LOCAL_IP"
echo "📱 Device IP: $DEVICE_IP"
echo "🔌 Metro Port: $METRO_PORT"
echo ""

# Function to auto-detect and connect device
auto_connect_device() {
    # Check if already connected
    if adb devices | grep -q "device$"; then
        echo "✅ Device already connected"
        return 0
    fi
    
    # Try last known device first
    if [ -f .last_device ]; then
        LAST_DEVICE=$(cat .last_device)
        echo "🔄 Trying last device: $LAST_DEVICE"
        adb connect "$LAST_DEVICE" >/dev/null 2>&1
        sleep 1
        
        if adb devices | grep -q "device$"; then
            echo "✅ Reconnected to $LAST_DEVICE"
            return 0
        else
            echo "❌ Last device failed, trying auto-detection..."
        fi
    fi
    
    # Auto-detect with nmap
    echo "🔍 Auto-detecting device port..."
    
    # Install nmap if needed
    if ! command -v nmap &> /dev/null; then
        echo "📦 Installing nmap..."
        sudo apt update && sudo apt install -y nmap >/dev/null 2>&1
    fi
    
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
    fi
    
    # Fallback to manual
    echo "❌ Auto-detection failed"
    echo "📱 Check your phone's Wireless debugging settings"
    read -p "Enter current port for $DEVICE_IP: " MANUAL_PORT
    
    if [ -n "$MANUAL_PORT" ]; then
        adb connect "$DEVICE_IP:$MANUAL_PORT"
        if adb devices | grep -q "device$"; then
            echo "✅ Connected to $DEVICE_IP:$MANUAL_PORT"
            echo "$DEVICE_IP:$MANUAL_PORT" > .last_device
            return 0
        fi
    fi
    
    return 1
}

# Function to setup WiFi development
setup_wifi_dev() {
    echo "🔧 Setting up WiFi development..."
    adb reverse tcp:$METRO_PORT tcp:$METRO_PORT
    
    if [ $? -eq 0 ]; then
        echo "✅ Port forwarding configured"
        return 0
    else
        echo "⚠️  Port forwarding failed - manual setup may be needed"
        return 1
    fi
}

# Function to build APK
build_apk() {
    echo "🔨 Building APK..."
    cd android
    ./gradlew assembleDebug --quiet
    
    if [ $? -eq 0 ]; then
        echo "✅ Build successful! Installing..."
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo "🎉 APK installed successfully!"
            cd ..
            return 0
        else
            echo "❌ Install failed"
            cd ..
            return 1
        fi
    else
        echo "❌ Build failed"
        cd ..
        return 1
    fi
}

# Function to start Metro
start_metro() {
    echo "🚀 Starting Metro development server..."
    echo "📡 Server URL: http://$LOCAL_IP:$METRO_PORT"
    echo ""
    echo "📱 If your app shows connection error:"
    echo "1. Shake your device in the app"
    echo "2. Tap 'Settings' → 'Debug server host & port'"
    echo "3. Enter: $LOCAL_IP:$METRO_PORT"
    echo "4. Tap OK and reload"
    echo ""
    echo "🔥 Metro starting... (Press Ctrl+C to stop)"
    echo "=========================================="
    
    export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
    npx expo start --dev-client --lan --port $METRO_PORT
}

# Main workflow
echo "🔌 Step 1: Connecting to device..."
if ! auto_connect_device; then
    echo "❌ Failed to connect device. Exiting."
    exit 1
fi

echo ""
echo "🔧 Step 2: Setting up WiFi development..."
setup_wifi_dev

echo ""
echo "🎯 Choose your development mode:"
echo "1) 🚀 Start Metro server (for live development)"
echo "2) 🔨 Build & install APK only"
echo "3) 🎪 Full setup (build APK + start Metro)"
echo "4) 🔄 Just reconnect device and exit"

read -p "Choose (1-4): " CHOICE

case $CHOICE in
    1)
        start_metro
        ;;
    2)
        build_apk
        ;;
    3)
        if build_apk; then
            echo ""
            echo "✅ APK ready! Starting Metro server..."
            sleep 2
            start_metro
        fi
        ;;
    4)
        echo "✅ Device connected and ready!"
        adb devices
        ;;
    *)
        echo "❌ Invalid choice"
        ;;
esac