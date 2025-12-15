import { Stack } from 'expo-router';
import React from 'react';
import { useColorScheme } from '../../hooks/use-color-scheme';
import { Colors } from '../../constants/theme';

export default function TestsLayout() {
  const colorScheme = useColorScheme();

  return (
    <Stack
      screenOptions={{
        headerStyle: {
          backgroundColor: Colors[colorScheme ?? 'light'].background,
        },
        headerTintColor: Colors[colorScheme ?? 'light'].text,
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      }}>
      <Stack.Screen
        name="index"
        options={{
          title: 'Test Suite',
          headerLargeTitle: true,
        }}
      />
      <Stack.Screen
        name="smart-detection"
        options={{
          title: 'Smart Detection Test',
          presentation: 'card',
        }}
      />
      <Stack.Screen
        name="performance"
        options={{
          title: 'Performance Test',
          presentation: 'card',
        }}
      />
      <Stack.Screen
        name="integration"
        options={{
          title: 'Integration Test',
          presentation: 'card',
        }}
      />
    </Stack>
  );
}