// Quick test to verify config reads app.json correctly
const fs = require('fs');

console.log('🔍 Testing Config System');
console.log('========================\n');

// Read app.json
const appJson = JSON.parse(fs.readFileSync('app.json', 'utf8'));
console.log('📋 app.json extra.backendUrl:', appJson.expo.extra?.backendUrl);

// Simulate what the config does
const backendUrl = appJson.expo.extra?.backendUrl || 'http://192.168.100.55:3000';
console.log('✅ Config would use:', backendUrl);

if (backendUrl.includes('ngrok')) {
    console.log('✅ Ngrok URL detected - production build will use this!');
} else {
    console.log('⚠️  Using local IP - make sure app.json has ngrok URL for production');
}
