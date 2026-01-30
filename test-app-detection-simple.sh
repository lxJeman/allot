#!/bin/bash

# Simple App Detection Test
# Quick test to verify app detection is working correctly

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
WHITE='\033[1;37m'
NC='\033[0m'

echo -e "${WHITE}🧪 Quick App Detection Test${NC}"
echo -e "${WHITE}═══════════════════════════════════════${NC}"

# Check device connection
if ! adb devices | grep -q "device$"; then
    echo -e "${YELLOW}❌ No device connected. Connect your Android device first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Device connected${NC}"

# Start the app if not running
echo -e "${BLUE}🚀 Starting app...${NC}"
adb shell am start -n "com.allot/.MainActivity" > /dev/null 2>&1
sleep 2

echo -e "${BLUE}📱 Getting current app info...${NC}"

# Clear logcat buffer
adb logcat -c

# Enable debug logging and get current app in background
adb shell am broadcast -a com.allot.DEBUG_LOGGING --ez enabled true > /dev/null 2>&1 &

# Wait a moment for the broadcast
sleep 1

# Get current app
current_app=$(adb shell dumpsys window windows | grep -E 'mCurrentFocus' | head -1 | sed 's/.*{.*\/\([^}]*\)}.*/\1/' | cut -d' ' -f1)

echo -e "${WHITE}Current detected app: ${GREEN}$current_app${NC}"

# Check recent logs for app detection
echo -e "${BLUE}📋 Recent app detection logs:${NC}"
echo -e "${WHITE}═══════════════════════════════════════${NC}"

# Show last 20 lines of relevant logs
adb logcat -d -s AppDetectionModule:* AllotAccessibility:* | tail -20 | while IFS= read -r line; do
    if [[ $line == *"MONITORED"* ]]; then
        echo -e "${GREEN}$line${NC}"
    elif [[ $line == *"APP CHANGE"* ]]; then
        echo -e "${BLUE}$line${NC}"
    else
        echo -e "${WHITE}$line${NC}"
    fi
done

echo -e "${WHITE}═══════════════════════════════════════${NC}"
echo -e "${YELLOW}💡 To see live monitoring, run: ./debug-app-detection.sh monitor${NC}"
echo -e "${YELLOW}💡 To enable debug mode: ./debug-app-detection.sh debug-on${NC}"