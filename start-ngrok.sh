#!/bin/bash

echo "🌐 Ngrok Backend Tunnel Setup"
echo "=============================="
echo ""

# Check if ngrok is installed
if ! command -v ngrok &> /dev/null; then
    echo "❌ ngrok is not installed"
    echo ""
    echo "💡 Install ngrok:"
    echo "   1. Download from: https://ngrok.com/download"
    echo "   2. Or use package manager:"
    echo "      - Ubuntu/Debian: snap install ngrok"
    echo "      - macOS: brew install ngrok"
    echo ""
    exit 1
fi

# Check if Rust backend is running
BACKEND_PORT=3000
if ! lsof -i:$BACKEND_PORT &> /dev/null; then
    echo "⚠️  Rust backend is not running on port $BACKEND_PORT"
    echo ""
    echo "💡 Start the backend first:"
    echo "   cd rust-backend"
    echo "   cargo run"
    echo ""
    read -p "Continue anyway? (y/n): " CONTINUE
    if [ "$CONTINUE" != "y" ]; then
        exit 1
    fi
fi

echo "🚀 Starting ngrok tunnel to localhost:$BACKEND_PORT..."
echo ""
echo "📋 This will:"
echo "   1. Create a public HTTPS URL for your backend"
echo "   2. Display the URL (copy it for the build script)"
echo "   3. Keep the tunnel open (press Ctrl+C to stop)"
echo ""
echo "🔥 Starting ngrok..."
echo "=================================="
echo ""

# Start ngrok
ngrok http $BACKEND_PORT

# Note: ngrok will run in foreground until Ctrl+C
echo ""
echo "✅ Ngrok tunnel closed"
