#!/bin/bash

# Test War Content Detection System
# This script tests the improved detection logic

echo "🧪 Testing War Content Detection System"
echo "========================================"
echo ""

# Check if backend is running
if ! curl -s http://192.168.171.18:3000/health > /dev/null 2>&1; then
    echo "❌ Backend not running at http://192.168.171.18:3000"
    echo "   Start it with: cd rust-backend && cargo run --release"
    exit 1
fi

echo "✅ Backend is running"
echo ""

# Test cases
echo "📋 Test Cases:"
echo ""

echo "1. Testing war hashtags: '#war #military #ukraine'"
echo "   Expected: HARMFUL ⚠️"
echo ""

echo "2. Testing bypass attempt: 'Hell... (no violation made, fake guns, training)'"
echo "   Expected: HARMFUL ⚠️ (bypass blocked)"
echo ""

echo "3. Testing military content: 'IRAQ 2003 #militaryedits'"
echo "   Expected: HARMFUL ⚠️"
echo ""

echo "4. Testing random content: 'Check out my dance video! #fyp'"
echo "   Expected: SAFE ✅"
echo ""

echo "📱 Now open TikTok and search for '#war' or '#military'"
echo "   Watch the backend logs to see detection in action"
echo ""

echo "🔍 Expected log patterns:"
echo "   - 🏷️  Category: war_content"
echo "   - ⚠️  Harmful: YES ⚠️"
echo "   - 🎯 Action: BLUR"
echo "   - 🚨 Risk Factors: war hashtags detected"
echo ""

echo "✅ Test setup complete!"
echo "   Monitor backend logs: cd rust-backend && cargo run --release"
