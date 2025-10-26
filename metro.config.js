// Learn more https://docs.expo.io/guides/customizing-metro
const { getDefaultConfig } = require('expo/metro-config');
const path = require('path'); // Add path module

/** @type {import('expo/metro-config').MetroConfig} */
const config = getDefaultConfig(__dirname);

// This is the part you need to add to handle the alias
config.resolver.extraNodeModules = {
  // Map the alias '@' to the project root directory
  // This is a common setup to make '@/' resolve to files in the root folder,
  // which then points to your 'components' folder.
  '@': path.resolve(__dirname, './'), 
};

module.exports = config;