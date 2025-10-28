import React from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import { Colors } from '../../constants/theme';
import { useColorScheme } from '../../hooks/use-color-scheme';

export default function CaptureTab() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';

  return (
    <View style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.content}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Screen Capture
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Real-time screen analysis system
        </Text>
        
        <TouchableOpacity
          style={[styles.button, { backgroundColor: Colors[colorScheme ?? 'light'].tint }]}
          onPress={() => router.push('/screen-capture')}
        >
          <Text style={styles.buttonText}>Open Screen Capture</Text>
        </TouchableOpacity>
        
        <TouchableOpacity
          style={[styles.button, { backgroundColor: '#FF9800' }]}
          onPress={() => router.push('/permissions')}
        >
          <Text style={styles.buttonText}>Manage Permissions</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 18,
    textAlign: 'center',
    marginBottom: 40,
  },
  button: {
    paddingVertical: 15,
    paddingHorizontal: 30,
    borderRadius: 12,
    marginBottom: 16,
    minWidth: 200,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});