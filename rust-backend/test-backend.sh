#!/bin/bash

# Test script for Phase 3 AI Detection Backend

echo "🧪 Testing Allot AI Detection Backend"
echo "======================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BACKEND_URL="http://192.168.100.47:3000"

# Test 1: Health Check
echo "📡 Test 1: Health Check"
echo "----------------------"
response=$(curl -s -w "\n%{http_code}" "$BACKEND_URL/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ Health check passed${NC}"
    echo "Response: $body"
else
    echo -e "${RED}❌ Health check failed (HTTP $http_code)${NC}"
    echo "Response: $body"
    exit 1
fi

echo ""
echo ""

# Test 2: Analyze Endpoint (with sample base64 image)
echo "🧠 Test 2: Analyze Endpoint"
echo "-------------------------"

# Create a simple test image (1x1 white pixel)
# This is a valid PNG in base64
TEST_IMAGE="iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="

# Create JSON payload
payload=$(cat <<EOF
{
  "image": "$TEST_IMAGE",
  "width": 1,
  "height": 1,
  "timestamp": $(date +%s)000
}
EOF
)

echo "Sending test image to backend..."
echo ""

response=$(curl -s -w "\n%{http_code}" -X POST "$BACKEND_URL/analyze" \
  -H "Content-Type: application/json" \
  -d "$payload")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" = "200" ]; then
    echo -e "${GREEN}✅ Analysis endpoint passed${NC}"
    echo ""
    echo "Response:"
    echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    
    # Extract key metrics
    category=$(echo "$body" | grep -o '"category":"[^"]*"' | cut -d'"' -f4)
    confidence=$(echo "$body" | grep -o '"confidence":[0-9.]*' | cut -d':' -f2)
    action=$(echo "$body" | grep -o '"action":"[^"]*"' | cut -d'"' -f4)
    total_time=$(echo "$body" | grep -o '"total_time_ms":[0-9]*' | cut -d':' -f2)
    
    echo ""
    echo "📊 Key Metrics:"
    echo "  Category: $category"
    echo "  Confidence: $confidence"
    echo "  Action: $action"
    echo "  Total Time: ${total_time}ms"
else
    echo -e "${RED}❌ Analysis endpoint failed (HTTP $http_code)${NC}"
    echo "Response: $body"
    exit 1
fi

echo ""
echo ""
echo -e "${GREEN}🎉 All tests passed!${NC}"
echo ""
echo "Backend is ready for Phase 3 integration!"
