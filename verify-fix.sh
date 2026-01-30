#!/bin/bash

# Replace with your phone's IP
PHONE_IP="192.168.100.13"

# Kill any existing ADB server
adb kill-server
adb start-server

echo "Detecting current Wireless ADB port..."

# List paired devices and find the GUID matching your phone IP
PORT=$(adb pair $PHONE_IP 2>&1 | grep -oP '\d{4,5}' | tail -1)

if [ -z "$PORT" ]; then
    echo "Failed to detect port. Make sure Wireless Debugging is ON and phone is on the same Wi-Fi."
    exit 1
fi

echo "Connecting to $PHONE_IP on port $PORT ..."
adb connect $PHONE_IP:$PORT

echo ""
echo "Current connected devices:"
adb devices
