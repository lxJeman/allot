#!/bin/bash

# Configuration
DEVICE_IP="192.168.100.13"
DEVICE_GUID="adb-895XTSKN8HXSDAOJ-fk0aNP"

echo "🔄 Auto-connecting to Android device and building APK..."

# Function to find and connect to device
connect_device() {
    echo "🔍 Scanning for device ports..."
    
    # Try to find the device by scanning common ports or using the GUID
    for port in {30000..50000}; do
        if timeout 1 bash -c "</dev/tcp/$DEVICE_IP/$port" 2>/dev/null; then
            echo "📡 Found open port: $port"
            adb connect "$DEVICE_IP:$port" 2>/dev/null
            if adb devices | grep -q "$DEVICE_IP:$port.*device"; then
                echo "✅ Connected to $DEVICE_IP:$port"
                return 0
            fi
        fi
    done
    
    # Alternative: Try to reconnect using known GUID
    echo "🔄 Trying to reconnect using device GUID..."
    adb reconnect
    
    return 1
}

# Function to ensure device is connected
ensure_connected() {
    # Check if device is already connected
    if adb devices | grep -q "device$"; then
        echo "✅ Device already connected"
        return 0
    fi
    
    echo "📱 No device connected, attempting to connect..."
    
    # Try to connect
    if ! connect_device; then
        echo "❌ Failed to auto-connect. Please manually connect your device:"
        echo "1. Check your phone's Wireless debugging settings"
        echo "2. Note the IP:PORT"
        echo "3. Run: adb connect IP:PORT"
        return 1
    fi
}

# Function to build and install
build_and_install() {
    echo "🔨 Building and installing APK..."
    
    # Ensure device is connected before building
    if ! ensure_connected; then
        exit 1
    fi
    
    # Build the APK
    echo "🧹 Cleaning cached bundles..."
    rm -f android/app/src/main/assets/index.android.bundle
    rm -rf android/app/build/intermediates/assets/
    
    echo "🔨 Building APK..."
    cd android
    ./gradlew assembleDebug
    
    if [ $? -eq 0 ]; then
        echo "✅ Build successful! Installing APK..."
        
        # Check connection again before installing
        if ! adb devices | grep -q "device$"; then
            echo "🔄 Device disconnected, reconnecting..."
            ensure_connected
        fi
        
        # Install APK
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo "🚀 APK installed successfully!"
            echo "📱 You can now test the app on your device"
        else
            echo "❌ Failed to install APK"
            echo "💡 Try running: adb install -r android/app/build/outputs/apk/debug/app-debug.apk"
        fi
    else
        echo "❌ Build failed"
    fi
    
    cd ..
}

# Main execution
build_and_install