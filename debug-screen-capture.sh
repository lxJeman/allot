#!/bin/bash

echo "🔍 Starting Android Debug Log Monitor for Screen Capture"
echo "========================================================"
echo "Looking for ScreenCaptureModule logs..."
echo ""

# Clear existing logs first
adb logcat -c

# Monitor logs with color coding
adb logcat -s "ScreenCaptureModule" | while read line; do
    timestamp=$(date '+%H:%M:%S')
    
    # Color code different types of logs
    if [[ $line == *"🔥 Image available listener triggered!"* ]]; then
        echo -e "\033[1;32m[$timestamp] $line\033[0m"  # Green - Critical success
    elif [[ $line == *"🔥 Got image:"* ]]; then
        echo -e "\033[1;36m[$timestamp] $line\033[0m"  # Cyan - Image received
    elif [[ $line == *"🎯 Processing continuous frame"* ]]; then
        echo -e "\033[1;34m[$timestamp] $line\033[0m"  # Blue - Processing
    elif [[ $line == *"📸 Continuous frame updated:"* ]]; then
        echo -e "\033[1;35m[$timestamp] $line\033[0m"  # Magenta - Frame updated
    elif [[ $line == *"🔄 Forcing virtual display refresh"* ]]; then
        echo -e "\033[1;33m[$timestamp] $line\033[0m"  # Yellow - Refresh attempt
    elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
        echo -e "\033[1;31m[$timestamp] $line\033[0m"  # Red - Errors
    elif [[ $line == *"⚠️"* ]] || [[ $line == *"WARN"* ]]; then
        echo -e "\033[1;93m[$timestamp] $line\033[0m"  # Bright yellow - Warnings
    else
        echo "[$timestamp] $line"  # Normal
    fi
done