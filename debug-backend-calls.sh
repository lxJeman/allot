#!/bin/bash

# Debug Backend Calls - Track exactly when backend calls happen vs skip decisions
# This script monitors both frontend decisions and backend requests to find the disconnect

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
WHITE='\033[1;37m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${WHITE}🔍 Backend Call Debug Monitor${NC}"
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
echo -e "${YELLOW}🔍 BACKEND CALL DEBUG:${NC}"
echo -e "${WHITE}This will show the exact disconnect between skip decisions and backend calls${NC}"
echo -e "${WHITE}1. Start capture with Smart Capture ON${NC}"
echo -e "${WHITE}2. Stay in your app - should see SKIP but NO backend calls${NC}"
echo -e "${WHITE}3. Switch to TikTok - should see PROCESS and backend calls${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${PURPLE}🔍 Monitoring frontend decisions vs backend calls...${NC}"
echo -e "${WHITE}Press Ctrl+C to stop${NC}"
echo ""

# Track state
skip_decision=false
backend_call_expected=false

# Monitor with enhanced logic tracking
adb logcat -s AppDetectionModule:* AllotAccessibility:* ReactNativeJS:* | while IFS= read -r line; do
    timestamp=$(echo "$line" | grep -o '[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\.[0-9][0-9][0-9]')
    
    # Track decision flow
    if [[ $line == *"DECISION: WILL SKIP"* ]]; then
        echo -e "${timestamp} ${RED}❌ FRONTEND DECISION: WILL SKIP${NC}"
        skip_decision=true
        backend_call_expected=false
    elif [[ $line == *"DECISION: WILL PROCESS"* ]]; then
        echo -e "${timestamp} ${GREEN}✅ FRONTEND DECISION: WILL PROCESS${NC}"
        skip_decision=false
        backend_call_expected=true
    
    # Track processCapture calls
    elif [[ $line == *"processCapture ENTRY"* ]]; then
        if [[ $skip_decision == true ]]; then
            echo -e "${timestamp} ${RED}🚨 CRITICAL: processCapture called after SKIP decision!${NC}"
        else
            echo -e "${timestamp} ${BLUE}🚪 processCapture ENTRY (expected)${NC}"
        fi
    elif [[ $line == *"processCapture called from"* ]]; then
        echo -e "${timestamp} ${PURPLE}📍 Called from: ${line#*from: }${NC}"
    
    # Track the critical double-check
    elif [[ $line == *"CRITICAL DOUBLE-CHECK"* ]]; then
        echo -e "${timestamp} ${CYAN}🔍 DOUBLE-CHECK STARTING...${NC}"
    elif [[ $line == *"BLOCKED: Smart capture enabled"* ]]; then
        echo -e "${timestamp} ${GREEN}✅ DOUBLE-CHECK BLOCKED (correct)${NC}"
    elif [[ $line == *"PROCEEDING: In monitored app"* ]]; then
        echo -e "${timestamp} ${GREEN}✅ DOUBLE-CHECK PROCEEDING (correct)${NC}"
    
    # Track ML Kit and Backend calls
    elif [[ $line == *"Starting Local ML Kit text extraction"* ]]; then
        if [[ $skip_decision == true ]]; then
            echo -e "${timestamp} ${RED}🚨 CRITICAL ERROR: ML Kit started after SKIP decision!${NC}"
        else
            echo -e "${timestamp} ${BLUE}🤖 ML Kit started (expected for monitored app)${NC}"
        fi
    elif [[ $line == *"Sending extracted text to backend"* ]]; then
        if [[ $skip_decision == true ]]; then
            echo -e "${timestamp} ${RED}🚨 CRITICAL ERROR: Backend call made after SKIP decision!${NC}"
        else
            echo -e "${timestamp} ${GREEN}🧠 Backend call (expected for monitored app)${NC}"
        fi
    
    # Track app changes
    elif [[ $line == *"ENTERED MONITORED APP"* ]]; then
        echo -e "${timestamp} ${GREEN}🎯 ENTERED MONITORED APP${NC}"
    elif [[ $line == *"LEFT MONITORED APP"* ]]; then
        echo -e "${timestamp} ${RED}🚪 LEFT MONITORED APP${NC}"
    
    # Track capture events
    elif [[ $line == *"CAPTURE SKIPPED"* ]]; then
        echo -e "${timestamp} ${BLUE}⏭️ CAPTURE SKIPPED${NC}"
    elif [[ $line == *"PROCESSING STARTED"* ]]; then
        echo -e "${timestamp} ${GREEN}🔄 PROCESSING STARTED${NC}"
    
    # Track errors
    elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
        echo -e "${timestamp} ${RED}❌ ERROR: ${line#*: }${NC}"
    fi
done