import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Animated, StyleSheet, Text, View } from 'react-native';
import { useColorScheme } from '../hooks/use-color-scheme';

interface LoadingScreenProps {
  onReady?: () => void;
}

export function LoadingScreen({ onReady }: LoadingScreenProps) {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  const [loadingText, setLoadingText] = useState('Initializing...');
  const [progress, setProgress] = useState(0);
  const fadeAnim = useState(new Animated.Value(0))[0];
  const progressAnim = useState(new Animated.Value(0))[0];

  useEffect(() => {
    // Fade in animation
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration: 300,
      useNativeDriver: true,
    }).start();

    // Simulate the actual loading process that happens during app startup
    const loadingSteps = [
      { text: 'Loading JavaScript bundle...', duration: 500, progress: 20 },
      { text: 'Initializing native modules...', duration: 800, progress: 40 },
      { text: 'Setting up navigation...', duration: 400, progress: 60 },
      { text: 'Loading components...', duration: 600, progress: 80 },
      { text: 'Finalizing setup...', duration: 300, progress: 100 },
    ];

    let currentStep = 0;
    let totalElapsed = 0;

    const processStep = () => {
      if (currentStep < loadingSteps.length) {
        const step = loadingSteps[currentStep];
        setLoadingText(step.text);
        
        // Animate progress
        Animated.timing(progressAnim, {
          toValue: step.progress,
          duration: step.duration,
          useNativeDriver: false,
        }).start();

        setProgress(step.progress);
        
        setTimeout(() => {
          currentStep++;
          totalElapsed += step.duration;
          processStep();
        }, step.duration);
      } else {
        // All steps complete, but ensure minimum loading time for UX
        const minLoadingTime = 2000; // 2 seconds minimum
        const remainingTime = Math.max(0, minLoadingTime - totalElapsed);
        
        setTimeout(() => {
          setLoadingText('Ready!');
          onReady?.();
        }, remainingTime);
      }
    };

    // Start the loading process
    processStep();
  }, [fadeAnim, progressAnim, onReady]);

  return (
    <Animated.View 
      style={[
        styles.container, 
        { 
          backgroundColor: isDark ? '#151718' : '#ffffff',
          opacity: fadeAnim 
        }
      ]}
    >
      <View style={styles.content}>
        <View style={styles.logoContainer}>
          <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            Allot
          </Text>
          <Text style={[styles.tagline, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            Control What You Watch
          </Text>
        </View>
        
        <View style={styles.loadingContainer}>
          <ActivityIndicator 
            size="large" 
            color={isDark ? '#ffffff' : '#0a7ea4'} 
          />
          
          <Text style={[styles.text, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            {loadingText}
          </Text>
          
          {/* Progress bar */}
          <View style={[styles.progressBarContainer, { backgroundColor: isDark ? '#2A2A2A' : '#E0E0E0' }]}>
            <Animated.View 
              style={[
                styles.progressBar, 
                { 
                  backgroundColor: isDark ? '#ffffff' : '#0a7ea4',
                  width: progressAnim.interpolate({
                    inputRange: [0, 100],
                    outputRange: ['0%', '100%'],
                  })
                }
              ]} 
            />
          </View>
          
          <Text style={[styles.progressText, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            {progress}%
          </Text>
        </View>
      </View>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  content: {
    alignItems: 'center',
    gap: 32,
  },
  logoContainer: {
    alignItems: 'center',
    gap: 8,
  },
  title: {
    fontSize: 48,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  tagline: {
    fontSize: 16,
    textAlign: 'center',
    opacity: 0.8,
    fontStyle: 'italic',
  },
  loadingContainer: {
    alignItems: 'center',
    gap: 16,
    minWidth: 200,
  },
  text: {
    fontSize: 16,
    fontWeight: '500',
    textAlign: 'center',
  },
  progressBarContainer: {
    width: 200,
    height: 4,
    borderRadius: 2,
    overflow: 'hidden',
  },
  progressBar: {
    height: '100%',
    borderRadius: 2,
  },
  progressText: {
    fontSize: 12,
    textAlign: 'center',
    opacity: 0.7,
  },
});