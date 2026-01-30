#!/bin/bash

echo "🚀 Setting up React Native development environment..."

# Update system
echo "📦 Updating system packages..."
sudo apt update

# Install Node.js 20
echo "🔄 Installing Node.js 20..."
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Install OpenJDK 17
echo "☕ Installing OpenJDK 17..."
sudo apt install -y openjdk-17-jdk

# Install Android SDK
echo "📱 Installing Android SDK..."
sudo apt install -y android-sdk

# Set up environment variables
echo "🔧 Setting up environment variables..."
cat >> ~/.bashrc << 'EOF'

# Android Development Environment
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/usr/lib/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$JAVA_HOME/bin
EOF

# Source the new environment
source ~/.bashrc

# Verify installations
echo "✅ Verifying installations..."
echo "Node.js version: $(node --version)"
echo "npm version: $(npm --version)"
echo "Java version: $(java -version 2>&1 | head -n 1)"

# Install project dependencies
echo "📦 Installing project dependencies..."
npm install

# Install Expo CLI globally (with proper permissions)
echo "🌐 Installing Expo CLI..."
sudo npm install -g @expo/cli

echo "🎉 Setup complete!"
echo ""
echo "Next steps:"
echo "1. Run: source ~/.bashrc"
echo "2. Connect your Android device or start an emulator"
echo "3. Run: npx expo start"
echo "4. Or try: npx expo run:android"