import Constants from 'expo-constants';

/**
 * Configuration constants for the app
 * Backend URL can be overridden via environment variables or app.json extra config
 */

// Get backend URL from environment or app.json extra config
const getBackendUrl = (): string => {
  // Priority 1: Environment variable (for EAS builds)
  if (process.env.BACKEND_URL) {
    console.log('📋 [Config] Using backend URL from environment:', process.env.BACKEND_URL);
    return process.env.BACKEND_URL;
  }
  
  // Priority 2: Expo Constants extra config (from app.json)
  if (Constants.expoConfig?.extra?.backendUrl) {
    console.log('📋 [Config] Using backend URL from app.json:', Constants.expoConfig.extra.backendUrl);
    return Constants.expoConfig.extra.backendUrl;
  }
  
  // Priority 3: Default local development URL
  console.log('📋 [Config] Using default backend URL: http://192.168.100.55:3000');
  return 'http://192.168.100.55:3000';
};

export const Config = {
  BACKEND_URL: getBackendUrl(),
  
  // Detection settings
  DETECTION: {
    MIN_CONFIDENCE: 0.80,
    CACHE_EXPIRY_HOURS: 24,
    REQUEST_TIMEOUT_MS: 2000,
  },
  
  // Capture settings
  CAPTURE: {
    INTERVAL_MS: 100,
    MIN_TEXT_LENGTH: 10,
  },
} as const;

// Log configuration on startup
console.log('📋 [Config] App Configuration:', {
  backendUrl: Config.BACKEND_URL,
  environment: __DEV__ ? 'development' : 'production',
  isDev: __DEV__,
});

// Log extra config for debugging
console.log('📋 [Config] Expo Constants:', {
  expoConfig: Constants.expoConfig?.extra,
  manifest: Constants.manifest2?.extra,
});
