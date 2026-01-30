#!/bin/bash

echo "🔧 Fixing Android SDK setup and accepting licenses..."

# Create Android SDK directory in home folder (better than system-wide)
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
mkdir -p "$ANDROID_HOME"

# Download and install command line tools
echo "📥 Downloading Android command line tools..."
cd "$ANDROID_HOME"
wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip -q commandlinetools-linux-11076708_latest.zip
rm commandlinetools-linux-11076708_latest.zip

# Move to proper location
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true

# Update PATH
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

# Accept all licenses
echo "✅ Accepting Android SDK licenses..."
yes | sdkmanager --licenses

# Install required SDK components
echo "📦 Installing required SDK components..."
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "ndk;27.1.12297006"

# Update environment variables permanently
echo "🔧 Updating environment variables..."
cat >> ~/.bashrc << EOF

# Android SDK (updated)
export ANDROID_HOME="\$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="\$ANDROID_HOME"
export PATH="\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin"
export PATH="\$PATH:\$ANDROID_HOME/platform-tools"
export PATH="\$PATH:\$ANDROID_HOME/tools"
EOF

echo "✅ Android SDK setup complete!"
echo "Please run: source ~/.bashrc"
echo "Then try building again: ./build-and-install.sh"