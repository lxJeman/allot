#!/bin/bash

echo "🔍 Verifying Local Text Extraction Fix"
echo "====================================="
echo ""

# Function to test local extraction
test_local_extraction() {
    echo "🧪 Testing local text extraction..."
    
    # Clear logs
    adb logcat -c
    
    # Start local extraction
    echo "🚀 Starting local text extraction..."
    adb shell am broadcast -a com.allot.START_LOCAL_EXTRACTION \
        --ei captureInterval 2000 \
        --ez enableValidation true \
        --ez enableCaching false \
        --ez enableROIOptimization false
    
    # Wait for startup
    sleep 3
    
    # Check for 10 seconds
    echo "⏳ Monitoring for 10 seconds..."
    sleep 10
    
    # Analyze logs
    echo ""
    echo "📊 Analysis Results:"
    echo "==================="
    
    # Check if native backend was disabled
    backend_disabled=$(adb logcat -d | grep -c "Native backend.*DISABLED\|Disabled native backend")
    if [ "$backend_disabled" -gt 0 ]; then
        echo "✅ Native backend properly disabled ($backend_disabled occurrences)"
    else
        echo "❌ Native backend disable not found"
    fi
    
    # Check for local extraction activity
    local_activity=$(adb logcat -d | grep -c "LocalTextExtractionService")
    if [ "$local_activity" -gt 5 ]; then
        echo "✅ Local text extraction active ($local_activity log entries)"
    else
        echo "❌ Insufficient local extraction activity ($local_activity entries)"
    fi
    
    # Check for Rust backend requests (should be 0)
    rust_requests=$(adb logcat -d | grep -c "📸.*Received screen capture")
    if [ "$rust_requests" -eq 0 ]; then
        echo "✅ No Rust backend requests (perfect!)"
    else
        echo "❌ Found $rust_requests Rust backend requests (fix failed)"
    fi
    
    # Stop extraction
    echo ""
    echo "🛑 Stopping local text extraction..."
    adb shell am broadcast -a com.allot.STOP_LOCAL_EXTRACTION
    
    echo ""
    if [ "$backend_disabled" -gt 0 ] && [ "$local_activity" -gt 5 ] && [ "$rust_requests" -eq 0 ]; then
        echo "🎉 FIX VERIFICATION: SUCCESS!"
        echo "   ✅ Native backend disabled"
        echo "   ✅ Local extraction working"
        echo "   ✅ No Rust backend interference"
        return 0
    else
        echo "❌ FIX VERIFICATION: FAILED!"
        echo "   Backend disabled: $backend_disabled > 0"
        echo "   Local activity: $local_activity > 5"
        echo "   Rust requests: $rust_requests = 0"
        return 1
    fi
}

# Run the test
test_local_extraction

echo ""
echo "🔍 Additional verification:"
echo "=========================="
echo "1. Check your Rust backend terminal - it should show NO new requests"
echo "2. The app should now work continuously without stopping after 10-12 seconds"
echo "3. All text extraction is now happening locally on-device"