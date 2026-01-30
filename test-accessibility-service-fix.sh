#!/bin/bash

# Test Accessibility Service Fix
# This script tests the complete accessibility service fix

echo "🔧 Testing Accessibility Service Fix"
echo "=================================="

# Build and install the app
echo "📱 Building and installing app..."
./quick-build.sh

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "🔍 Testing App Detection System..."
echo "=================================="

# Test 1: Check if accessibility service is properly declared
echo "1️⃣ Checking accessibility service declaration..."
adb shell dumpsys package com.allot | grep -A 5 "AllotAccessibilityService"

# Test 2: Check if service is enabled
echo ""
echo "2️⃣ Checking if accessibility service is enabled..."
adb shell settings get secure enabled_accessibility_services | grep com.allot

# Test 3: Monitor app detection logs
echo ""
echo "3️⃣ Starting log monitoring (press Ctrl+C to stop)..."
echo "   - Open the app"
echo "   - Try enabling Smart Capture"
echo "   - Check if you see the 'Enable App Detection' button"
echo "   - Switch between apps to test detection"
echo ""

# Clear logs and start monitoring
adb logcat -c
adb logcat | grep -E "(AppDetectionModule|AllotAccessibility|ScreenCapture.*Smart|shouldProcessCapture)"