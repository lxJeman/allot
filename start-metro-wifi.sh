#!/bin/bash

# Get local IP address
LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🌐 Setting up Metro server for WiFi development"
echo "==============================================="
echo "💻 Computer IP: $LOCAL_IP"
echo "📱 Device IP: 192.168.100.13"
echo "🔌 Metro Port: $METRO_PORT"
echo ""

# Function to configure device for Metro connection
configure_device() {
    echo "📱 Configuring device for Metro connection..."
    
    # Check if device is connected
    if ! adb devices | grep -q "device$"; then
        echo "❌ No device connected. Please connect your device first."
        echo "Run: ./quick-build.sh"
        return 1
    fi
    
    # Set Metro server host on device
    echo "🔧 Setting Metro server host to $LOCAL_IP:$METRO_PORT"
    adb shell input keyevent KEYCODE_MENU
    sleep 1
    
    # Alternative method: Use adb to set the server
    adb reverse tcp:$METRO_PORT tcp:$METRO_PORT
    
    if [ $? -eq 0 ]; then
        echo "✅ Port forwarding set up successfully"
    else
        echo "⚠️  Port forwarding failed, will use manual method"
    fi
    
    echo ""
    echo "📱 On your device, you may need to:"
    echo "1. Shake the device or press hardware menu button"
    echo "2. Tap 'Dev Settings' or 'Debug'"
    echo "3. Tap 'Debug server host & port for device'"
    echo "4. Enter: $LOCAL_IP:$METRO_PORT"
    echo "5. Tap 'OK' and reload the app"
}

# Start Metro server
start_metro() {
    echo "🚀 Starting Metro development server..."
    echo "📡 Server will be available at: http://$LOCAL_IP:$METRO_PORT"
    echo ""
    echo "🔥 Metro server starting... (Press Ctrl+C to stop)"
    echo "=================================================="
    
    # Set environment variables for Metro
    export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
    
    # Start Metro with explicit host binding
    npx expo start --dev-client --host $LOCAL_IP --port $METRO_PORT
}

# Main execution
echo "🔧 Step 1: Configure device connection"
configure_device

echo ""
echo "🚀 Step 2: Starting Metro server"
echo "Press Enter to continue or Ctrl+C to cancel..."
read

start_metro