#!/bin/bash

echo "Setting up Android development environment..."

# Install OpenJDK 17
echo "Installing OpenJDK 17..."
sudo apt update
sudo apt install -y openjdk-17-jdk

# Install Node.js 18 if not present
if ! command -v node &> /dev/null; then
    echo "Installing Node.js..."
    curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
    sudo apt install -y nodejs
fi

# Install Android SDK command line tools
echo "Installing Android SDK..."
sudo apt install -y android-sdk

# Set up environment variables
echo "Setting up environment variables..."

# Add to bashrc
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

echo "Environment setup complete!"
echo "Please run: source ~/.bashrc"
echo "Then verify with: java -version"