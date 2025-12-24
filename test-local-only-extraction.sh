#!/bin/bash

echo "🧪 Testing Local-Only Text Extraction (No Rust Backend)"
echo "======================================================="
echo ""

# Function to check if Rust backend is receiving requests
check_rust_backend() {
    echo "🔍 Checking if Rust backend is receiving requests..."
    
    # Count recent log entries (last 10 seconds)
    recent_logs=$(adb logcat -d | grep "📸.*Received screen capture" | tail -5)
    
    if [ -z "$recent_logs" ]; then
        echo "✅ No recent requests to Rust backend - LOCAL ONLY mode working!"
        return 0
    else
        echo "❌ Rust backend is still receiving requests:"
        echo "$recent_logs"
        return 1
    fi
}

# Function to check local text extraction logs
check_local_extraction() {
    echo "🔍 Checking local text extraction logs..."
    
    # Look for LocalTextExtractionService logs
    local_logs=$(adb logcat -d | grep "LocalTextExtractionService" | tail -10)
    
    if [ -n "$local_logs" ]; then
        echo "✅ Local text extraction is active:"
        echo "$local_logs" | tail -3
        return 0
    else
        echo "❌ No local text extraction logs found"
        return 1
    fi
}

# Function to check native backend status
check_native_backend_status() {
    echo "🔍 Checking native backend status..."
    
    # Look for native backend disable logs
    backend_logs=$(adb logcat -d | grep "Native backend.*DISABLED\|Disabled native backend" | tail -3)
    
    if [ -n "$backend_logs" ]; then
        echo "✅ Native backend properly disabled:"
        echo "$backend_logs"
        return 0
    else
        echo "⚠️ No native backend disable logs found"
        return 1
    fi
}

echo "📱 Starting local text extraction test..."
echo ""

# Clear logcat
adb logcat -c

# Start local text extraction via ADB
echo "🚀 Starting local text extraction..."
adb shell am broadcast -a com.allot.START_LOCAL_EXTRACTION \
    --ei captureInterval 1000 \
    --ez enableValidation true \
    --ez enableCaching false \
    --ez enableROIOptimization false

echo "⏳ Waiting 3 seconds for service to start..."
sleep 3

echo ""
echo "📊 Initial Status Check:"
echo "------------------------"
check_native_backend_status
echo ""
check_local_extraction
echo ""
check_rust_backend
echo ""

echo "⏳ Running for 15 seconds to monitor behavior..."
echo ""

# Monitor for 15 seconds
for i in {1..15}; do
    echo "⏱️  Second $i/15..."
    sleep 1
    
    # Check every 5 seconds
    if [ $((i % 5)) -eq 0 ]; then
        echo ""
        echo "📊 Status Check at ${i}s:"
        echo "-------------------------"
        check_rust_backend
        echo ""
    fi
done

echo ""
echo "🏁 Final Status Check:"
echo "======================"
check_native_backend_status
echo ""
check_local_extraction
echo ""
check_rust_backend
echo ""

# Stop local text extraction
echo "🛑 Stopping local text extraction..."
adb shell am broadcast -a com.allot.STOP_LOCAL_EXTRACTION

echo ""
echo "✅ Test completed!"
echo ""
echo "📋 Summary:"
echo "----------"
echo "✅ If you see 'LOCAL ONLY mode working!' - the fix is successful"
echo "❌ If you see Rust backend requests - the issue persists"
echo ""
echo "🔍 You can also check the Rust backend terminal - it should show NO new requests during this test"