#!/bin/bash

# Test Smart Capture Logic - Verify the skip logic is working
# This script specifically tests if captures are being skipped correctly

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
WHITE='\033[1;37m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${WHITE}🧪 Smart Capture Logic Test${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"

# Check device connection
if ! adb devices | grep -q "device$"; then
    echo -e "${RED}❌ No device connected. Connect your Android device first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Device connected${NC}"

# Start the app
echo -e "${BLUE}🚀 Starting Allot app...${NC}"
adb shell am start -n "com.allot/.MainActivity" > /dev/null 2>&1
sleep 3

# Clear logcat for clean test
adb logcat -c

# Enable debug logging
echo -e "${BLUE}🔍 Enabling debug logging...${NC}"
adb shell am broadcast -a com.allot.DEBUG_LOGGING --ez enabled true > /dev/null 2>&1

echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${YELLOW}📋 SMART CAPTURE LOGIC TEST:${NC}"
echo -e "${WHITE}This test will monitor ONLY the skip/process decisions${NC}"
echo -e "${WHITE}1. Start capture in the app (Smart Capture ON)${NC}"
echo -e "${WHITE}2. Stay in your app - should see SKIPPED${NC}"
echo -e "${WHITE}3. Switch to TikTok - should see PROCESSING${NC}"
echo -e "${WHITE}4. Switch back to your app - should see SKIPPED${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${PURPLE}🔍 Monitoring smart capture decisions...${NC}"
echo -e "${WHITE}Press Ctrl+C to stop${NC}"
echo ""

# Monitor ONLY the smart capture logic
adb logcat -s AppDetectionModule:* AllotAccessibility:* ReactNativeJS:* | while IFS= read -r line; do
    timestamp=$(echo "$line" | grep -o '[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\.[0-9][0-9][0-9]')
    
    # Smart Capture Decision Logic
    if [[ $line == *"shouldProcessCapture check"* ]]; then
        echo -e "${timestamp} ${BLUE}🤔 CHECKING IF SHOULD PROCESS...${NC}"
    elif [[ $line == *"App:"* ]] && [[ $line == *"Monitored:"* ]]; then
        if [[ $line == *"Monitored: true"* ]]; then
            echo -e "${timestamp} ${GREEN}   📱 App: $(echo "$line" | sed 's/.*App: \([^(]*\).*/\1/') → MONITORED ✅${NC}"
        else
            echo -e "${timestamp} ${YELLOW}   📱 App: $(echo "$line" | sed 's/.*App: \([^(]*\).*/\1/') → NOT MONITORED ❌${NC}"
        fi
    elif [[ $line == *"Decision:"* ]]; then
        if [[ $line == *"PROCESS"* ]]; then
            echo -e "${timestamp} ${GREEN}   ✅ DECISION: WILL PROCESS${NC}"
        else
            echo -e "${timestamp} ${RED}   ❌ DECISION: WILL SKIP${NC}"
        fi
    
    # Actual Actions
    elif [[ $line == *"Starting automatic processing for monitored app"* ]]; then
        echo -e "${timestamp} ${GREEN}🔄 PROCESSING STARTED (CORRECT)${NC}"
    elif [[ $line == *"Skipping capture - not in monitored app"* ]]; then
        echo -e "${timestamp} ${BLUE}⏭️ CAPTURE SKIPPED (CORRECT)${NC}"
    
    # Backend Calls (These should NOT happen when skipping)
    elif [[ $line == *"Starting Local ML Kit text extraction"* ]]; then
        echo -e "${timestamp} ${PURPLE}🤖 ML KIT STARTED${NC}"
    elif [[ $line == *"Sending extracted text to backend"* ]]; then
        echo -e "${timestamp} ${RED}🚨 BACKEND CALL MADE - CHECK IF THIS SHOULD HAPPEN!${NC}"
    
    # App Changes
    elif [[ $line == *"ENTERED MONITORED APP"* ]]; then
        echo -e "${timestamp} ${GREEN}🎯 ENTERED MONITORED APP${NC}"
    elif [[ $line == *"LEFT MONITORED APP"* ]]; then
        echo -e "${timestamp} ${RED}🚪 LEFT MONITORED APP${NC}"
    
    # Errors
    elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
        echo -e "${timestamp} ${RED}❌ ERROR: ${line#*: }${NC}"
    fi
done