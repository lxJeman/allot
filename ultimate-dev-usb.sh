#!/bin/bash

LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🚀 Ultimate React Native Development Script (USB)"
echo "================================================="
echo "💻 Computer IP: $LOCAL_IP"
echo "🔌 Metro Port: $METRO_PORT"
echo "📱 Connection: USB Debugging"
echo ""

# Function to check USB device connection
check_usb_device() {
    DEVICE_COUNT=$(adb devices | grep -c "device$")
    
    if [ "$DEVICE_COUNT" -eq 0 ]; then
        echo "❌ No USB device connected"
        echo ""
        echo "💡 Please:"
        echo "1. Connect your Android device via USB"
        echo "2. Enable Developer Options (Settings → About → tap Build Number 7 times)"
        echo "3. Enable USB Debugging in Developer Options"
        echo "4. Accept the debugging prompt on your device"
        return 1
    elif [ "$DEVICE_COUNT" -eq 1 ]; then
        DEVICE_ID=$(adb devices | grep "device$" | cut -f1)
        echo "✅ USB device connected: $DEVICE_ID"
        return 0
    else
        echo "⚠️  Multiple devices connected:"
        adb devices
        echo ""
        echo "💡 Please disconnect extra devices or use 'adb -s DEVICE_ID' for specific device"
        return 1
    fi
}

# Function to setup USB development
setup_usb_dev() {
    echo "🔧 Setting up USB development..."
    
    # Setup port forwarding for Metro
    adb reverse tcp:$METRO_PORT tcp:$METRO_PORT
    
    if [ $? -eq 0 ]; then
        echo "✅ Port forwarding configured (device:$METRO_PORT → computer:$METRO_PORT)"
        return 0
    else
        echo "⚠️  Port forwarding failed - Metro may need manual configuration"
        return 1
    fi
}

# Function to build APK
build_apk() {
    echo ""
    echo "🔨 Building APK..."
    echo "🧹 Cleaning cached bundles..."
    rm -f android/app/src/main/assets/index.android.bundle
    rm -rf android/app/build/intermediates/assets/
    
    echo "🔨 Building debug APK..."
    cd android
    ./gradlew assembleDebug
    
    if [ $? -eq 0 ]; then
        echo "✅ Build successful! Installing APK..."
        
        # Check device connection before installing
        if ! adb devices | grep -q "device$"; then
            echo "❌ Device disconnected during build"
            cd ..
            return 1
        fi
        
        # Install APK with force reinstall
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo "🎉 APK installed successfully!"
            cd ..
            return 0
        else
            echo "❌ APK installation failed"
            cd ..
            return 1
        fi
    else
        echo "❌ Build failed"
        cd ..
        return 1
    fi
}

# Function to start Metro server
start_metro() {
    echo ""
    echo "🚀 Starting Metro development server..."
    echo "📡 Server URL: http://$LOCAL_IP:$METRO_PORT"
    echo "🔌 USB port forwarding: Active"
    echo ""
    echo "📱 Your app should automatically connect via USB"
    echo "   If connection fails, the app will show connection instructions"
    echo ""
    echo "🔥 Metro starting... (Press Ctrl+C to stop)"
    echo "=========================================="
    
    # Set environment variable for Metro
    export REACT_NATIVE_PACKAGER_HOSTNAME=$LOCAL_IP
    
    # Start Metro with LAN mode for better compatibility
    npx expo start --dev-client --lan --port $METRO_PORT
}

# Function for quick development cycle
quick_dev_cycle() {
    echo ""
    echo "⚡ Quick Development Cycle"
    echo "========================"
    echo "🔄 This will:"
    echo "   1. Build APK (incremental, faster)"
    echo "   2. Install on device"
    echo "   3. Start Metro for live development"
    echo ""
    
    # Quick build (no clean)
    echo "🚀 Quick building..."
    cd android
    ./gradlew assembleDebug --quiet
    
    if [ $? -eq 0 ]; then
        echo "✅ Quick build complete! Installing..."
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo "🎉 APK installed! Starting Metro..."
            cd ..
            sleep 1
            start_metro
        else
            echo "❌ Install failed"
            cd ..
            return 1
        fi
    else
        echo "❌ Quick build failed, trying full build..."
        cd ..
        build_apk && start_metro
    fi
}

# Function to just start Metro (for when APK is already installed)
metro_only() {
    echo ""
    echo "🚀 Starting Metro server only..."
    echo "💡 Make sure your development APK is already installed"
    start_metro
}

# Function to show device info
show_device_info() {
    echo ""
    echo "📱 Device Information:"
    echo "====================="
    adb devices -l
    echo ""
    echo "📋 Device Properties:"
    echo "Model: $(adb shell getprop ro.product.model 2>/dev/null || echo 'Unknown')"
    echo "Android: $(adb shell getprop ro.build.version.release 2>/dev/null || echo 'Unknown')"
    echo "API Level: $(adb shell getprop ro.build.version.sdk 2>/dev/null || echo 'Unknown')"
    echo ""
}

# Main execution
echo "🔌 Step 1: Checking USB device connection..."
if ! check_usb_device; then
    echo ""
    echo "❌ Please connect your device via USB and try again"
    exit 1
fi

echo ""
echo "🔧 Step 2: Setting up USB development..."
setup_usb_dev

echo ""
echo "🎯 Choose your development mode:"
echo "1) 🚀 Start Metro server only (APK already installed)"
echo "2) 🔨 Build & install APK only"
echo "3) 🎪 Full setup (clean build APK + start Metro)"
echo "4) ⚡ Quick cycle (fast build APK + start Metro)"
echo "5) 📱 Show device info"
echo "6) 🔄 Just setup USB and exit"

read -p "Choose (1-6): " CHOICE

case $CHOICE in
    1)
        metro_only
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
        quick_dev_cycle
        ;;
    5)
        show_device_info
        ;;
    6)
        echo "✅ USB development setup complete!"
        echo "📱 Device ready for development"
        adb devices
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac