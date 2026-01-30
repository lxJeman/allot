#!/bin/bash

echo "🚀 Setting up Expo development environment..."

# Check if Node.js 20+ is installed
NODE_VERSION=$(node --version 2>/dev/null | cut -d'v' -f2 | cut -d'.' -f1)
if [ -z "$NODE_VERSION" ] || [ "$NODE_VERSION" -lt 20 ]; then
    echo "❌ Node.js 20+ required. Please run ./complete-setup.sh first"
    exit 1
fi

echo "✅ Node.js version: $(node --version)"

# Install dependencies
echo "📦 Installing project dependencies..."
npm install

# Install Expo CLI globally
echo "🌐 Installing Expo CLI..."
sudo npm install -g @expo/cli

# Install EAS CLI for building
echo "🔨 Installing EAS CLI..."
sudo npm install -g eas-cli

echo "🎉 Expo setup complete!"
echo ""
echo "Development options:"
echo "1. Start development server: npx expo start"
echo "2. Run on Android: npx expo run:android"
echo "3. Build APK with EAS: eas build --platform android --profile preview"
echo ""
echo "For the original build script to work, you still need Java/Android SDK."
echo "But for development, Expo CLI is usually easier!"