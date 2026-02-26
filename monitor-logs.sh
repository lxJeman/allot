#!/bin/bash

# Simple production log monitor - shows only app logs
APP_PACKAGE="com.allot"

# Check device
if ! adb devices | grep -q "device$"; then
    echo "❌ No device connected"
    exit 1
fi

# Check if app is installed
if ! adb shell pm list packages | grep -q "$APP_PACKAGE"; then
    echo "❌ App not installed: $APP_PACKAGE"
    exit 1
fi

# Get PID
PID=$(adb shell pidof $APP_PACKAGE 2>/dev/null | tr -d '\r')

if [ -z "$PID" ]; then
    echo "⚠️  App not running - launch it and run this script again"
    exit 1
fi

echo "📱 Monitoring logs for $APP_PACKAGE (PID: $PID)"
echo "Press Ctrl+C to stop"
echo "─────────────────────────────────────────────────────"

# Clear old logs and start monitoring
adb logcat -c
adb logcat --pid=$PID -v time
