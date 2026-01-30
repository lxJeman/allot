#!/bin/bash

echo "🔄 Upgrading Node.js to version 20..."

# Remove old Node.js
sudo apt remove -y nodejs npm

# Install Node.js 20 using NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Verify installation
echo "✅ Node.js version:"
node --version
echo "✅ npm version:"
npm --version

echo "🎉 Node.js upgrade complete!"