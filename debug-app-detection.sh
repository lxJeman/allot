#!/bin/bash

# Debug App Detection - Advanced monitoring and control script
# This script provides comprehensive app detection debugging capabilities

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m'

PACKAGE_NAME="com.allot"

# Function to show usage
show_usage() {
    echo -e "${WHITE}Usage: $0 [COMMAND]${NC}"
    echo ""
    echo -e "${CYAN}Commands:${NC}"
    echo -e "  ${GREEN}monitor${NC}     - Start real-time log monitoring"
    echo -e "  ${GREEN}debug-on${NC}    - Enable debug logging and start monitoring"
    echo -e "  ${GREEN}debug-off${NC}   - Disable debug logging"
    echo -e "  ${GREEN}status${NC}      - Check current app and service status"
    echo -e "  ${GREEN}apps${NC}        - List monitored apps"
    echo -e "  ${GREEN}current${NC}     - Get current app info"
    echo -e "  ${GREEN}test${NC}        - Run app detection test"
    echo -e "  ${GREEN}help${NC}        - Show this help"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo -e "  $0 debug-on    # Enable debug logging and start monitoring"
    echo -e "  $0 current     # Check what app is currently detected"
    echo -e "  $0 test        # Test app detection by switching apps"
}

# Function to check device connection
check_device() {
    if ! adb devices | grep -q "device$"; then
        echo -e "${RED}❌ No Android device connected${NC}"
        echo -e "${YELLOW}💡 Connect device and enable USB debugging${NC}"
        return 1
    fi
    return 0
}

# Function to check if app is running
check_app_running() {
    if ! adb shell "ps | grep $PACKAGE_NAME" > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  App is not running. Starting app...${NC}"
        adb shell am start -n "$PACKAGE_NAME/.MainActivity" > /dev/null 2>&1
        sleep 2
    fi
}

# Function to enable debug logging
enable_debug_logging() {
    echo -e "${CYAN}🔍 Enabling debug logging...${NC}"
    check_app_running
    
    # Try to call the React Native method via ADB
    adb shell am broadcast -a com.allot.DEBUG_LOGGING --ez enabled true > /dev/null 2>&1
    
    # Also log directly to indicate debug mode
    adb shell "echo 'Debug logging enabled via script' | su -c 'log -t AppDetectionModule'"
    
    echo -e "${GREEN}✅ Debug logging enabled${NC}"
}

# Function to disable debug logging
disable_debug_logging() {
    echo -e "${CYAN}🔇 Disabling debug logging...${NC}"
    check_app_running
    
    adb shell am broadcast -a com.allot.DEBUG_LOGGING --ez enabled false > /dev/null 2>&1
    echo -e "${GREEN}✅ Debug logging disabled${NC}"
}

# Function to get current app status
get_current_app() {
    echo -e "${CYAN}📱 Getting current app info...${NC}"
    check_app_running
    
    # Get current foreground app
    current_app=$(adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp' | head -1 | sed 's/.*{.*\/\([^}]*\)}.*/\1/' | cut -d' ' -f1)
    
    if [ -n "$current_app" ]; then
        echo -e "${WHITE}Current App: ${GREEN}$current_app${NC}"
        
        # Check if it's monitored
        case "$current_app" in
            *tiktok*|*musically*|*instagram*|*facebook*|*twitter*|*reddit*|*snapchat*|*whatsapp*|*discord*|*pinterest*|*linkedin*)
                echo -e "${WHITE}Status: ${GREEN}🎯 MONITORED${NC}"
                ;;
            *)
                echo -e "${WHITE}Status: ${YELLOW}📱 Not monitored${NC}"
                ;;
        esac
    else
        echo -e "${RED}❌ Could not detect current app${NC}"
    fi
}

# Function to list monitored apps
list_monitored_apps() {
    echo -e "${CYAN}📋 Monitored Apps:${NC}"
    echo -e "${WHITE}Social Media Apps that trigger screen capture:${NC}"
    echo ""
    echo -e "  🎵 ${GREEN}TikTok${NC} (com.zhiliaoapp.musically)"
    echo -e "  🎵 ${GREEN}TikTok Lite${NC} (com.zhiliaoapp.musically.go)"
    echo -e "  📸 ${GREEN}Instagram${NC} (com.instagram.android)"
    echo -e "  📸 ${GREEN}Instagram Lite${NC} (com.instagram.lite)"
    echo -e "  👥 ${GREEN}Facebook${NC} (com.facebook.katana)"
    echo -e "  👥 ${GREEN}Facebook Lite${NC} (com.facebook.lite)"
    echo -e "  🐦 ${GREEN}Twitter${NC} (com.twitter.android)"
    echo -e "  🤖 ${GREEN}Reddit${NC} (com.reddit.frontpage)"
    echo -e "  👻 ${GREEN}Snapchat${NC} (com.snapchat.android)"
    echo -e "  💬 ${GREEN}WhatsApp${NC} (com.whatsapp)"
    echo -e "  🎮 ${GREEN}Discord${NC} (com.discord)"
    echo -e "  📌 ${GREEN}Pinterest${NC} (com.pinterest)"
    echo -e "  💼 ${GREEN}LinkedIn${NC} (com.linkedin.android)"
}

# Function to run app detection test
run_test() {
    echo -e "${CYAN}🧪 Running App Detection Test${NC}"
    echo -e "${WHITE}═══════════════════════════════════════${NC}"
    
    check_app_running
    enable_debug_logging
    
    echo -e "${YELLOW}📋 Test Instructions:${NC}"
    echo -e "1. Switch to a monitored app (TikTok, Instagram, etc.)"
    echo -e "2. Switch to a non-monitored app (Settings, Chrome, etc.)"
    echo -e "3. Switch back to a monitored app"
    echo -e "4. Press Ctrl+C when done"
    echo ""
    echo -e "${WHITE}Monitoring logs...${NC}"
    echo -e "${WHITE}═══════════════════════════════════════${NC}"
    
    # Start monitoring with test-specific formatting
    adb logcat -s AppDetectionModule:* AllotAccessibility:* | while IFS= read -r line; do
        if [[ $line == *"ENTERED MONITORED APP"* ]]; then
            echo -e "${GREEN}✅ TEST PASS: ${line#*: }${NC}"
        elif [[ $line == *"LEFT MONITORED APP"* ]]; then
            echo -e "${BLUE}ℹ️  TEST INFO: ${line#*: }${NC}"
        elif [[ $line == *"APP CHANGE"* ]]; then
            echo -e "${PURPLE}🔄 APP SWITCH: ${line#*: }${NC}"
        elif [[ $line == *"Screen capture should"* ]]; then
            echo -e "${YELLOW}⚡ CAPTURE EVENT: ${line#*: }${NC}"
        else
            echo -e "${WHITE}${line}${NC}"
        fi
    done
}

# Function to monitor logs
monitor_logs() {
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${WHITE}           📱 APP DETECTION MONITOR 📱${NC}"
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}Package: ${WHITE}$PACKAGE_NAME${NC}"
    echo -e "${CYAN}Time: ${WHITE}$(date)${NC}"
    echo -e "${WHITE}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${YELLOW}💡 Switch between apps to see detection in action${NC}"
    echo -e "${WHITE}Press Ctrl+C to stop monitoring${NC}"
    echo ""
    
    # Monitor with enhanced formatting
    adb logcat -s AppDetectionModule:* AllotAccessibility:* | while IFS= read -r line; do
        timestamp=$(echo "$line" | grep -o '[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\.[0-9][0-9][0-9]')
        
        if [[ $line == *"ENTERED MONITORED APP"* ]]; then
            echo -e "${timestamp} ${GREEN}🎯 ${line#*: }${NC}"
        elif [[ $line == *"LEFT MONITORED APP"* ]]; then
            echo -e "${timestamp} ${RED}🚪 ${line#*: }${NC}"
        elif [[ $line == *"APP CHANGE"* ]]; then
            echo -e "${timestamp} ${BLUE}🔄 ${line#*: }${NC}"
        elif [[ $line == *"Screen capture should"* ]]; then
            echo -e "${timestamp} ${YELLOW}⚡ ${line#*: }${NC}"
        elif [[ $line == *"DEBUG LOGGING"* ]]; then
            echo -e "${timestamp} ${CYAN}🔍 ${line#*: }${NC}"
        elif [[ $line == *"ERROR"* ]] || [[ $line == *"❌"* ]]; then
            echo -e "${timestamp} ${RED}❌ ${line#*: }${NC}"
        else
            echo -e "${timestamp} ${WHITE}${line#*: }${NC}"
        fi
    done
}

# Function to check service status
check_status() {
    echo -e "${CYAN}🔍 Checking App Detection Status${NC}"
    echo -e "${WHITE}═══════════════════════════════════════${NC}"
    
    # Check if device is connected
    if ! check_device; then
        return 1
    fi
    
    # Check if app is running
    if adb shell "ps | grep $PACKAGE_NAME" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ App is running${NC}"
    else
        echo -e "${RED}❌ App is not running${NC}"
    fi
    
    # Check accessibility service
    accessibility_services=$(adb shell settings get secure enabled_accessibility_services)
    if [[ $accessibility_services == *"$PACKAGE_NAME"* ]]; then
        echo -e "${GREEN}✅ Accessibility service is enabled${NC}"
    else
        echo -e "${RED}❌ Accessibility service is not enabled${NC}"
        echo -e "${YELLOW}💡 Enable it in Settings > Accessibility${NC}"
    fi
    
    # Get current app
    echo ""
    get_current_app
}

# Main script logic
case "${1:-help}" in
    "monitor")
        check_device && monitor_logs
        ;;
    "debug-on")
        if check_device; then
            enable_debug_logging
            echo -e "${CYAN}Starting monitoring...${NC}"
            monitor_logs
        fi
        ;;
    "debug-off")
        check_device && disable_debug_logging
        ;;
    "status")
        check_status
        ;;
    "apps")
        list_monitored_apps
        ;;
    "current")
        check_device && get_current_app
        ;;
    "test")
        check_device && run_test
        ;;
    "help"|*)
        show_usage
        ;;
esac