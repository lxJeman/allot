#!/bin/bash

# Test Smart Capture Integration
# Tests the integration between app detection and screen capture

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
WHITE='\033[1;37m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${WHITE}🧪 Smart Capture Integration Test${NC}"
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
echo -e "${YELLOW}📋 INTEGRATION TEST INSTRUCTIONS:${NC}"
echo -e "${WHITE}1. Open the Screen Capture tab in the app${NC}"
echo -e "${WHITE}2. Make sure 'Smart Capture' is ON (green)${NC}"
echo -e "${WHITE}3. Start capture${NC}"
echo -e "${WHITE}4. Switch to TikTok or Instagram${NC}"
echo -e "${WHITE}5. Switch to Settings or Chrome${NC}"
echo -e "${WHITE}6. Switch back to TikTok${NC}"
echo -e "${WHITE}7. Watch the logs below${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${PURPLE}🔍 Monitoring integration logs...${NC}"
echo -e "${WHITE}Press Ctrl+C to stop${NC}"
echo ""

# Monitor logs with enhanced formatting for integration testing
adb logcat -s AppDetectionModule:* AllotAccessibility:* ReactNativeJS:* | while IFS= read -r line; do
    timestamp=$(echo "$line" | grep -o '[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\.[0-9][0-9][0-9]')
    
    # App Detection Events
    if [[ $line == *"ENTERED MONITORED APP"* ]]; then
        echo -e "${timestamp} ${GREEN}🎯 MONITORED APP ENTERED → CAPTURE SHOULD BE ACTIVE${NC}"
    elif [[ $line == *"LEFT MONITORED APP"* ]]; then
        echo -e "${timestamp} ${RED}🚪 LEFT MONITORED APP → CAPTURE SHOULD PAUSE${NC}"
    elif [[ $line == *"APP CHANGE"* ]]; then
        echo -e "${timestamp} ${BLUE}🔄 ${line#*: }${NC}"
    
    # Screen Capture Events
    elif [[ $line == *"Screen captured"* ]]; then
        echo -e "${timestamp} ${PURPLE}📸 SCREEN CAPTURED${NC}"
    elif [[ $line == *"Starting automatic processing"* ]]; then
        echo -e "${timestamp} ${YELLOW}🔄 PROCESSING CAPTURE (MONITORED APP)${NC}"
    elif [[ $line == *"Skipping capture - not in monitored app"* ]]; then
        echo -e "${timestamp} ${BLUE}⏭️ SKIPPED CAPTURE (NOT MONITORED APP)${NC}"
    elif [[ $line == *"shouldProcessCapture"* ]]; then
        echo -e "${timestamp} ${PURPLE}🤔 CHECKING IF SHOULD PROCESS${NC}"
    
    # ML Processing Events
    elif [[ $line == *"ML Kit extraction complete"* ]]; then
        echo -e "${timestamp} ${GREEN}📝 TEXT EXTRACTED${NC}"
    elif [[ $line == *"Complete analysis"* ]]; then
        echo -e "${timestamp} ${GREEN}🧠 ANALYSIS COMPLETE${NC}"
    elif [[ $line == *"HARMFUL CONTENT DETECTED"* ]]; then
        echo -e "${timestamp} ${RED}🚨 HARMFUL CONTENT DETECTED${NC}"
    
    # Debug/Status Events
    elif [[ $line == *"DEBUG LOGGING"* ]]; then
        echo -e "${timestamp} ${BLUE}🔍 ${line#*: }${NC}"
    elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
        echo -e "${timestamp} ${RED}❌ ${line#*: }${NC}"
    
    # Other important events
    elif [[ $line == *"Smart Capture"* ]]; then
        echo -e "${timestamp} ${PURPLE}🎯 ${line#*: }${NC}"
    else
        # Show other relevant logs in white
        if [[ $line == *"AppDetectionModule"* ]] || [[ $line == *"AllotAccessibility"* ]]; then
            echo -e "${timestamp} ${WHITE}${line#*: }${NC}"
        fi
    fi
done