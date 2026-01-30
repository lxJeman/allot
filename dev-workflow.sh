#!/bin/bash

DEVICE_IP="192.168.100.13"
LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🚀 React Native Development Workflow"
echo "===================================="
echo "💻 Computer IP: $LOCAL_IP"
echo "📱 Device IP: $DEVICE_IP"
echo "🔌 Metro Port: $METRO_PORT"
echo ""

# Function to connect device
connect_device() {
    if adb devices | grep -q "device$"; then
        echo "✅ Device already connected"
        return 0
    fi
    
    # Try last known device
    if [ -f .last_device ]; then
        LAST_DEVICE=$(cat .last_device)
        echo "🔄 Trying last device: $LAST_DEVICE"
        adb connect "$LAST_DEVICE" >/dev/null 2>&1
        sleep 1
        
        if adb devices | grep -q "device$"; then
            echo "✅ Reconnected to $LAST_DEVICE"
            return 0
        fi
    fi
    
    # Ask for current port
    echo "📱 Check your phone's Wireless debugging settings"
    read -p "Enter current port for $DEVICE_IP: " PORT
    
    if [ -n "$PORT" ]; then
        adb connect "$DEVICE_IP:$PORT"
        if adb devices | grep -q "device$"; then
            echo "✅ Connected to $DEVICE_IP:$PORT"
            echo "$DEVICE_IP:$PORT" > .last_device
            return 0
        fi
    fi
    
    return 1
}

# Function to setup WiFi development
setup_wifi_dev() {
    echo ""
    echo "🔧 Setting up WiFi development..."
    
    # Set up port forwarding
    adb reverse tcp:$METRO_PORT tcp:$METRO_PORT
    
    if [ $? -eq 0 ]; then
        echo "✅ Port forwarding set up successfully!"
        echo ""
        echo "🎯 Your app is now configured for WiFi development"
        return 0
    else
        echo "⚠️  Port forwarding failed - manual setup needed"
        return 1
    fi
}

# Function to start Metro
start_metro() {
    echo ""
    echo "🚀 Starting Metro development server..."
    echo "📡 Server URL: http://$LOCAL_IP:$METRO_PORT"
    echo ""
    echo "📱 On your device:"
    echo "1. Open the app"
    echo "2. If it shows connection error, shake the device"
    echo "3. Tap 'Settings' → 'Debug server host & port'"
    echo "4. Enter: $LOCAL_IP:$METRO_PORT"
    echo "5. Tap OK and reload"
    echo ""
    echo "🔥 Starting Metro... (Press Ctrl+C to stop)"
    echo "==========================================="
    
    export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
    npx expo start --dev-client --host $LOCAL_IP --port $METRO_PORT
}

# Main workflow
echo "Step 1: Connect device"
if ! connect_device; then
    echo "❌ Failed to connect device"
    exit 1
fi

echo ""
echo "Step 2: Setup WiFi development"
setup_wifi_dev

echo ""
echo "Step 3: Choose your action:"
echo "1) Start Metro server for live development"
echo "2) Build and install APK"
echo "3) Both (build APK first, then start Metro)"

read -p "Choose (1/2/3): " CHOICE

case $CHOICE in
    1)
        start_metro
        ;;
    2)
        echo "🔨 Building and installing APK..."
        ./quick-build.sh
        ;;
    3)
        echo "🔨 Building and installing APK first..."
        ./quick-build.sh
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ APK installed! Now starting Metro..."
            sleep 2
            start_metro
        fi
        ;;
    *)
        echo "❌ Invalid choice"
        ;;
esac