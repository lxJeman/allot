#!/bin/bash

PHONE="192.168.100.13"

echo "🔍 Auto-detecting Android device port..."

# First, clean up any offline devices
echo "🧹 Cleaning up offline devices..."
adb devices | grep "offline" | cut -f1 | while read device; do
    adb disconnect "$device" >/dev/null 2>&1
done

echo "📱 Scanning $PHONE for open ports..."

# Check if nmap is installed
if ! command -v nmap &> /dev/null; then
    echo "❌ nmap not found. Installing..."
    sudo apt update && sudo apt install -y nmap
fi

# Scan for open ports in the typical ADB range
echo "🔍 Scanning ports 30000-49999..."
PORT=$(nmap -sT $PHONE -p30000-49999 2>/dev/null | grep -E "^[0-9]+/tcp.*open" | cut -d'/' -f1 | head -n 1)

if [ -n "$PORT" ]; then
    echo "✅ Detected port: $PORT"
    echo "🔌 Connecting to ${PHONE}:$PORT..."
    
    # Disconnect any existing connection to this IP first
    adb disconnect $PHONE >/dev/null 2>&1
    
    # Connect to the new port
    adb connect ${PHONE}:$PORT
    
    # Wait a moment for connection to establish
    sleep 2
    
    # Check if connected successfully
    if adb devices | grep -q "${PHONE}:$PORT.*device"; then
        echo "🎉 Successfully connected!"
        echo "${PHONE}:$PORT" > .last_device
        adb devices
        exit 0
    else
        echo "❌ Connection failed - device may be offline"
        echo "🔄 Try again or check your phone's wireless debugging settings"
        exit 1
    fi
else
    echo "❌ No open ports found in range 30000-49999"
    echo ""
    echo "💡 Troubleshooting:"
    echo "1. Make sure Wireless debugging is enabled on your phone"
    echo "2. Check that both devices are on the same WiFi network"
    echo "3. Try disabling and re-enabling Wireless debugging"
    echo "4. Check if your phone's IP is still $PHONE"
    echo ""
    echo "🔧 Manual connection:"
    read -p "Enter the current port manually (or press Enter to skip): " MANUAL_PORT
    
    if [ -n "$MANUAL_PORT" ]; then
        echo "🔌 Trying manual connection to ${PHONE}:$MANUAL_PORT..."
        adb connect ${PHONE}:$MANUAL_PORT
        
        if adb devices | grep -q "device$"; then
            echo "✅ Manual connection successful!"
            echo "${PHONE}:$MANUAL_PORT" > .last_device
            adb devices
            exit 0
        else
            echo "❌ Manual connection failed"
        fi
    fi
    
    exit 1
fi