import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { useState } from 'react';

import { LoadingScreen } from '../components/loading-screen';
import { useColorScheme } from '../hooks/use-color-scheme';

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync();

export const unstable_settings = {
  anchor: '(tabs)',
};

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const [appIsReady, setAppIsReady] = useState(false);

  const handleLoadingComplete = async () => {
    try {
      // Hide the native splash screen
      await SplashScreen.hideAsync();
      // Mark app as ready
      setAppIsReady(true);
    } catch (error) {
      console.warn('Error hiding splash screen:', error);
      setAppIsReady(true);
    }
  };

  // Show loading screen while app is initializing
  if (!appIsReady) {
    return <LoadingScreen onReady={handleLoadingComplete} />;
  }

  return (
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      <Stack>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="modal" options={{ presentation: 'modal', title: 'Modal' }} />
      </Stack>
      <StatusBar style="auto" />
    </ThemeProvider>
  );
}
