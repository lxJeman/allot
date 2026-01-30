#!/bin/bash

echo "🔧 Setting up wireless debugging..."

echo "📱 Make sure on your Android device you have:"
echo "1. Enabled Developer Options (Settings → About Phone → tap Build Number 7 times)"
echo "2. Enabled 'Wireless debugging' in Developer Options"
echo "3. Noted the IP address and port from Wireless debugging settings"
echo ""

echo "🔍 Current ADB devices:"
adb devices
echo ""

echo "📡 Attempting to connect to your device..."
echo "If this fails, please provide the correct IP:PORT from your phone's Wireless debugging settings"
echo ""

# Try to connect
read -p "Enter your device IP:PORT (e.g., 192.168.100.13:12345): " DEVICE_ADDRESS

if [ -n "$DEVICE_ADDRESS" ]; then
    echo "🔌 Connecting to $DEVICE_ADDRESS..."
    adb connect "$DEVICE_ADDRESS"
    
    echo ""
    echo "📋 Checking connected devices:"
    adb devices
    
    if adb devices | grep -q "$DEVICE_ADDRESS"; then
        echo "✅ Successfully connected!"
        echo "🚀 Now you can install the APK with: ./build-and-install.sh"
    else
        echo "❌ Connection failed. Please check:"
        echo "1. Both devices are on the same WiFi network"
        echo "2. The IP:PORT is correct from your phone's Wireless debugging settings"
        echo "3. Wireless debugging is enabled on your phone"
        echo ""
        echo "💡 Alternative: Try pairing first if you haven't:"
        echo "   adb pair IP:PORT"
        echo "   (Use the pairing code shown on your phone)"
    fi
else
    echo "❌ No IP:PORT provided"
fi