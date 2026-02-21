#!/bin/bash

LOCAL_IP=$(ip route get 1 | awk '{print $7}' | head -1)
METRO_PORT=8081

echo "🚀 Allot Build & Development Script"
echo "===================================="
echo "💻 Computer IP: $LOCAL_IP"
echo "🔌 Metro Port: $METRO_PORT"
echo ""

# Function to get backend URL
get_backend_url() {
    if [ ! -z "$1" ]; then
        echo "$1"
    else
        # Read from app.json
        BACKEND_URL=$(node -e "const fs = require('fs'); const appJson = JSON.parse(fs.readFileSync('app.json', 'utf8')); console.log(appJson.expo.extra?.backendUrl || 'http://192.168.100.55:3000');" 2>/dev/null)
        echo "$BACKEND_URL"
    fi
}

# Function to update backend URL in app.json
update_backend_url() {
    local BACKEND_URL="$1"
    echo "🔧 Updating backend URL to: $BACKEND_URL"
    node -e "
const fs = require('fs');
const appJson = JSON.parse(fs.readFileSync('app.json', 'utf8'));
appJson.expo.extra = appJson.expo.extra || {};
appJson.expo.extra.backendUrl = '$BACKEND_URL';
fs.writeFileSync('app.json', JSON.stringify(appJson, null, 2));
console.log('✅ Backend URL updated');
"
}

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

# Function to build production APK (standalone with bundled JS)
build_production_apk() {
    local BACKEND_URL=$(get_backend_url "$1")
    
    echo ""
    echo "🏭 PRODUCTION BUILD MODE"
    echo "========================"
    echo "📋 Configuration:"
    echo "   Backend URL: $BACKEND_URL"
    echo "   Build Type: Release APK (standalone, no Metro needed)"
    echo "   Native Code: Included (all architectures)"
    echo ""
    
    # Update backend URL
    update_backend_url "$BACKEND_URL"
    
    echo "🔨 Building production release APK..."
    echo "⚠️  This may take 5-10 minutes (compiling native code)..."
    echo ""
    
    cd android
    
    # Build release APK with all architectures (no filters)
    echo "🔧 Building universal APK with all native libraries..."
    ./gradlew assembleRelease --no-daemon
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ Production build successful!"
        echo ""
        echo "📦 APK Location:"
        echo "   android/app/build/outputs/apk/release/app-release.apk"
        
        if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
            APK_SIZE=$(du -h app/build/outputs/apk/release/app-release.apk | cut -f1)
            echo "   Size: $APK_SIZE"
            echo ""
            
            # Offer to install
            if adb devices | grep -q "device$"; then
                echo "📱 Device detected! Install production APK? (y/n)"
                read -p "Install: " INSTALL_CHOICE
                
                if [ "$INSTALL_CHOICE" = "y" ]; then
                    echo "📲 Installing production APK..."
                    cd ..
                    adb install -r android/app/build/outputs/apk/release/app-release.apk
                    
                    if [ $? -eq 0 ]; then
                        echo "✅ Production APK installed!"
                        echo ""
                        echo "🎉 App is ready to use (no Metro server needed)"
                        echo "   Backend: $BACKEND_URL"
                    else
                        echo "❌ Installation failed"
                    fi
                    return 0
                else
                    cd ..
                fi
            else
                echo "💡 To install, connect device and run:"
                echo "   adb install -r android/app/build/outputs/apk/release/app-release.apk"
                cd ..
            fi
        else
            echo "⚠️  APK not found at expected location"
            cd ..
            return 1
        fi
    else
        echo ""
        echo "❌ Production build failed!"
        echo "💡 Check error messages above"
        cd ..
        return 1
    fi
}

# Function to build APK
# Function to build development APK (for use with Metro)
build_apk() {
    echo ""
    echo "🔨 DEVELOPMENT BUILD MODE"
    echo "========================="
    echo "📋 Building debug APK (requires Metro server)..."
    echo ""
    
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
            echo "🎉 Development APK installed!"
            echo "💡 Start Metro server to run the app"
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
echo "🎯 Choose build mode:"
echo ""
echo "DEVELOPMENT MODE (requires Metro server):"
echo "  1) 🚀 Start Metro server only (APK already installed)"
echo "  2) 🔨 Build & install debug APK only"
echo "  3) 🎪 Full dev setup (build debug APK + start Metro)"
echo "  4) ⚡ Quick dev cycle (fast build + start Metro)"
echo ""
echo "PRODUCTION MODE (standalone, no Metro needed):"
echo "  5) 🏭 Build production APK with local backend"
echo "  6) 🌐 Build production APK with ngrok backend"
echo ""
echo "UTILITIES:"
echo "  7) 📱 Show device info"
echo "  8) 🔄 Setup USB and exit"
echo ""

read -p "Choose (1-8): " CHOICE

case $CHOICE in
    1)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        setup_usb_dev
        metro_only
        ;;
    2)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        setup_usb_dev
        build_apk
        ;;
    3)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        setup_usb_dev
        if build_apk; then
            echo ""
            echo "✅ APK ready! Starting Metro server..."
            sleep 2
            start_metro
        fi
        ;;
    4)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        setup_usb_dev
        quick_dev_cycle
        ;;
    5)
        echo ""
        read -p "Enter local backend URL (or press Enter for default http://192.168.100.55:3000): " BACKEND_URL
        if [ -z "$BACKEND_URL" ]; then
            BACKEND_URL="http://192.168.100.55:3000"
        fi
        build_production_apk "$BACKEND_URL"
        ;;
    6)
        echo ""
        echo "💡 Make sure ngrok is running! Check your ngrok terminal for the URL."
        echo "   Example: https://abc123.ngrok-free.dev"
        echo ""
        read -p "Enter ngrok URL: " NGROK_URL
        if [ -z "$NGROK_URL" ]; then
            echo "❌ No URL provided"
            exit 1
        fi
        build_production_apk "$NGROK_URL"
        ;;
    7)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        show_device_info
        ;;
    8)
        if ! check_usb_device; then
            echo "❌ Please connect device via USB"
            exit 1
        fi
        setup_usb_dev
        echo "✅ USB development setup complete!"
        echo "📱 Device ready for development"
        adb devices
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac