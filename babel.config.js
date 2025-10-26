module.exports = function(api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'],
    plugins: [
      [
        'module-resolver',
        {
          alias: {
            // This maps '@/*' to the project root './'
            // This assumes your project structure is what the tsconfig suggests:
            // '@/components/haptic-tab' resolves to './components/haptic-tab'
            '@': './',
          },
        },
      ],
    ],
  };
};