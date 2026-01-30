#!/bin/bash

# Monitor App Detection - Real-time ADB Log Monitoring
# This script monitors app detection logs from your Android device

# Colors for better readability
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# App package name
PACKAGE_NAME="com.allot"

echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${WHITE}           📱 APP DETECTION MONITOR STARTED 📱${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}Package: ${WHITE}$PACKAGE_NAME${NC}"
echo -e "${CYAN}Time: ${WHITE}$(date)${NC}"
echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Function to check if device is connected
check_device() {
    if ! adb devices | grep -q "device$"; then
        echo -e "${RED}❌ No Android device connected via ADB${NC}"
        echo -e "${YELLOW}💡 Make sure:${NC}"
        echo -e "   1. Device is connected via USB or WiFi"
        echo -e "   2. USB Debugging is enabled"
        echo -e "   3. Device is authorized (check device screen)"
        echo ""
        echo -e "${CYAN}Available devices:${NC}"
        adb devices
        exit 1
    fi
}

# Function to start app detection logging
start_logging() {
    echo -e "${GREEN}🚀 Starting app detection logging...${NC}"
    echo -e "${YELLOW}💡 To enable debug logging, use the app or run:${NC}"
    echo -e "   adb shell am broadcast -a com.allot.START_DEBUG_LOGGING"
    echo ""
    echo -e "${PURPLE}📋 Monitoring these log tags:${NC}"
    echo -e "   • AppDetectionModule"
    echo -e "   • AllotAccessibility"
    echo ""
    echo -e "${WHITE}Press Ctrl+C to stop monitoring${NC}"
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

# Function to format log output
format_log() {
    while IFS= read -r line; do
        timestamp=$(echo "$line" | grep -o '[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\.[0-9][0-9][0-9]')
        
        if [[ $line == *"ENTERED MONITORED APP"* ]]; then
            echo -e "${timestamp} ${GREEN}🎯 ${line#*: }${NC}"
        elif [[ $line == *"LEFT MONITORED APP"* ]]; then
            echo -e "${timestamp} ${RED}🚪 ${line#*: }${NC}"
        elif [[ $line == *"APP CHANGE"* ]]; then
            echo -e "${timestamp} ${BLUE}🔄 ${line#*: }${NC}"
        elif [[ $line == *"MONITORED:"* ]]; then
            echo -e "${timestamp} ${PURPLE}📱 ${line#*: }${NC}"
        elif [[ $line == *"Screen capture should"* ]]; then
            echo -e "${timestamp} ${YELLOW}⚡ ${line#*: }${NC}"
        elif [[ $line == *"DEBUG LOGGING"* ]]; then
            echo -e "${timestamp} ${CYAN}🔍 ${line#*: }${NC}"
        elif [[ $line == *"SERVICE CONNECTED"* ]]; then
            echo -e "${timestamp} ${GREEN}✅ ${line#*: }${NC}"
        elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
            echo -e "${timestamp} ${RED}❌ ${line#*: }${NC}"
        else
            echo -e "${timestamp} ${WHITE}${line#*: }${NC}"
        fi
    done
}

# Function to cleanup on exit
cleanup() {
    echo ""
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}📊 Monitoring stopped at $(date)${NC}"
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Main execution
check_device
start_logging

# Start monitoring logs with filtering
adb logcat -s AppDetectionModule:* AllotAccessibility:* | format_log