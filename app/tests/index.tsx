/**
 * Test Suite Hub - Central location for all testing functionality
 * 
 * Provides access to various test modules and utilities for the Allot app
 */

import React, { useEffect, useState } from 'react';
import {
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Alert,
  NativeModules,
} from 'react-native';
import { Link, router } from 'expo-router';
import { Colors } from '../../constants/theme';
import { useColorScheme } from '../../hooks/use-color-scheme';

const { SmartDetectionModule } = NativeModules;

interface TestModule {
  id: string;
  title: string;
  description: string;
  route: string;
  icon: string;
  status: 'available' | 'coming-soon' | 'experimental';
  category: 'detection' | 'performance' | 'integration' | 'ui';
}

const testModules: TestModule[] = [
  {
    id: 'smart-detection',
    title: 'Smart Detection System',
    description: 'Test frame fingerprinting, motion detection, similarity analysis, and buffer management',
    route: '/tests/smart-detection',
    icon: '🧠',
    status: 'available',
    category: 'detection',
  },
  {
    id: 'local-text-extraction',
    title: 'Local Text Extraction',
    description: 'Real-time screen capture with on-device ML Kit text extraction (faster & private)',
    route: '/tests/local-text-extraction',
    icon: '🔍',
    status: 'available',
    category: 'detection',
  },
  {
    id: 'performance',
    title: 'Performance Benchmarks',
    description: 'Comprehensive performance testing and optimization validation',
    route: '/tests/performance',
    icon: '⚡',
    status: 'coming-soon',
    category: 'performance',
  },
  {
    id: 'integration',
    title: 'Integration Tests',
    description: 'End-to-end testing of screen capture and content analysis pipeline',
    route: '/tests/integration',
    icon: '🔗',
    status: 'coming-soon',
    category: 'integration',
  },

  {
    id: 'state-machine',
    title: 'Content State Machine',
    description: 'Test adaptive behavior and state transitions for content analysis',
    route: '/tests/state-machine',
    icon: '🔄',
    status: 'coming-soon',
    category: 'detection',
  },
  {
    id: 'ui-components',
    title: 'UI Component Tests',
    description: 'Test React Native components and user interface elements',
    route: '/tests/ui-components',
    icon: '🎨',
    status: 'experimental',
    category: 'ui',
  },
];

export default function TestSuiteHub() {
  const colorScheme = useColorScheme();
  const isDark = colorScheme === 'dark';
  
  const [systemStatus, setSystemStatus] = useState<{
    smartDetectionAvailable: boolean;
    performanceMetrics: any;
    lastTestRun: string | null;
  }>({
    smartDetectionAvailable: false,
    performanceMetrics: null,
    lastTestRun: null,
  });

  useEffect(() => {
    checkSystemStatus();
  }, []);

  const checkSystemStatus = async () => {
    try {
      // Check if Smart Detection module is available
      if (SmartDetectionModule) {
        const metrics = await SmartDetectionModule.getPerformanceMetrics();
        setSystemStatus({
          smartDetectionAvailable: true,
          performanceMetrics: metrics,
          lastTestRun: new Date().toLocaleString(),
        });
      }
    } catch (error) {
      console.log('Smart Detection module not available:', error);
      setSystemStatus(prev => ({
        ...prev,
        smartDetectionAvailable: false,
      }));
    }
  };

  const runQuickHealthCheck = async () => {
    try {
      if (!SmartDetectionModule) {
        Alert.alert('Not Available', 'Smart Detection module is not available');
        return;
      }

      Alert.alert('Running Health Check', 'Testing core functionality...');
      
      const result = await SmartDetectionModule.runTestSuite();
      
      Alert.alert(
        'Health Check Complete',
        `${result.summary}\n\n${result.allTestsPassed ? '✅ System Healthy' : '⚠️ Issues Detected'}`,
        [
          { text: 'View Details', onPress: () => router.push('/tests/smart-detection') },
          { text: 'OK' }
        ]
      );
    } catch (error) {
      Alert.alert('Health Check Failed', `Error: ${error.message}`);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'available': return '#4CAF50';
      case 'coming-soon': return '#FF9800';
      case 'experimental': return '#9C27B0';
      default: return '#757575';
    }
  };

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'detection': return '🔍';
      case 'performance': return '📊';
      case 'integration': return '🔧';
      case 'ui': return '🎨';
      default: return '🧪';
    }
  };

  const TestModuleCard = ({ module }: { module: TestModule }) => (
    <TouchableOpacity
      style={[
        styles.moduleCard,
        { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' },
        module.status !== 'available' && styles.disabledCard
      ]}
      onPress={() => {
        if (module.status === 'available') {
          router.push(module.route as any);
        } else {
          Alert.alert('Coming Soon', `${module.title} will be available in a future update.`);
        }
      }}
      disabled={module.status !== 'available'}
    >
      <View style={styles.moduleHeader}>
        <View style={styles.moduleIconContainer}>
          <Text style={styles.moduleIcon}>{module.icon}</Text>
          <Text style={styles.categoryIcon}>{getCategoryIcon(module.category)}</Text>
        </View>
        <View style={styles.moduleInfo}>
          <Text style={[styles.moduleTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
            {module.title}
          </Text>
          <Text style={[styles.moduleDescription, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            {module.description}
          </Text>
        </View>
        <View style={[styles.statusBadge, { backgroundColor: getStatusColor(module.status) }]}>
          <Text style={styles.statusText}>
            {module.status === 'available' ? 'Ready' : 
             module.status === 'coming-soon' ? 'Soon' : 'Beta'}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  const SystemStatusCard = () => (
    <View style={[styles.statusCard, { backgroundColor: isDark ? '#2A2A2A' : '#F5F5F5' }]}>
      <Text style={[styles.statusTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
        System Status
      </Text>
      
      <View style={styles.statusRow}>
        <Text style={[styles.statusLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Smart Detection:
        </Text>
        <Text style={[styles.statusValue, { 
          color: systemStatus.smartDetectionAvailable ? '#4CAF50' : '#F44336' 
        }]}>
          {systemStatus.smartDetectionAvailable ? '✅ Available' : '❌ Not Available'}
        </Text>
      </View>

      {systemStatus.performanceMetrics && (
        <>
          <View style={styles.statusRow}>
            <Text style={[styles.statusLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Total Operations:
            </Text>
            <Text style={[styles.statusValue, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
              {systemStatus.performanceMetrics.totalOperations}
            </Text>
          </View>

          <View style={styles.statusRow}>
            <Text style={[styles.statusLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Avg Processing:
            </Text>
            <Text style={[styles.statusValue, { 
              color: systemStatus.performanceMetrics.averageProcessingTimeMs < 5 ? '#4CAF50' : '#FF9800'
            }]}>
              {systemStatus.performanceMetrics.averageProcessingTimeMs.toFixed(2)}ms
            </Text>
          </View>

          <View style={styles.statusRow}>
            <Text style={[styles.statusLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
              Performance:
            </Text>
            <Text style={[styles.statusValue, { 
              color: systemStatus.performanceMetrics.meetsPerformanceRequirements ? '#4CAF50' : '#F44336'
            }]}>
              {systemStatus.performanceMetrics.meetsPerformanceRequirements ? '✅ Meets Requirements' : '⚠️ Below Target'}
            </Text>
          </View>
        </>
      )}

      {systemStatus.lastTestRun && (
        <View style={styles.statusRow}>
          <Text style={[styles.statusLabel, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            Last Check:
          </Text>
          <Text style={[styles.statusValue, { color: isDark ? '#9BA1A6' : '#687076' }]}>
            {systemStatus.lastTestRun}
          </Text>
        </View>
      )}
    </View>
  );

  return (
    <ScrollView style={[styles.container, { backgroundColor: isDark ? '#151718' : '#ffffff' }]}>
      <View style={styles.header}>
        <Text style={[styles.title, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Test Suite Hub
        </Text>
        <Text style={[styles.subtitle, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          Comprehensive testing tools for Allot's detection systems
        </Text>
      </View>

      {/* Quick Actions */}
      <View style={styles.quickActions}>
        <TouchableOpacity
          style={[styles.quickActionButton, { backgroundColor: '#4CAF50' }]}
          onPress={runQuickHealthCheck}
        >
          <Text style={styles.quickActionText}>🏥 Quick Health Check</Text>
        </TouchableOpacity>
        
        <TouchableOpacity
          style={[styles.quickActionButton, { backgroundColor: '#2196F3' }]}
          onPress={checkSystemStatus}
        >
          <Text style={styles.quickActionText}>🔄 Refresh Status</Text>
        </TouchableOpacity>
      </View>

      {/* System Status */}
      <SystemStatusCard />

      {/* Test Modules by Category */}
      <View style={styles.section}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Detection System Tests
        </Text>
        {testModules
          .filter(module => module.category === 'detection')
          .map(module => (
            <TestModuleCard key={module.id} module={module} />
          ))}
      </View>

      <View style={styles.section}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Performance & Integration
        </Text>
        {testModules
          .filter(module => module.category === 'performance' || module.category === 'integration')
          .map(module => (
            <TestModuleCard key={module.id} module={module} />
          ))}
      </View>

      <View style={styles.section}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          UI & Experimental
        </Text>
        {testModules
          .filter(module => module.category === 'ui')
          .map(module => (
            <TestModuleCard key={module.id} module={module} />
          ))}
      </View>

      {/* Instructions */}
      <View style={styles.instructionsSection}>
        <Text style={[styles.sectionTitle, { color: isDark ? '#ECEDEE' : '#11181C' }]}>
          Getting Started
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          1. Run a Quick Health Check to verify system functionality
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          2. Start with Smart Detection System tests (available now)
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          3. More test modules will be added as features are implemented
        </Text>
        <Text style={[styles.instruction, { color: isDark ? '#9BA1A6' : '#687076' }]}>
          4. Check system status regularly to monitor performance
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    marginBottom: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    lineHeight: 24,
  },
  quickActions: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 24,
  },
  quickActionButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  quickActionText: {
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
  },
  statusCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 24,
  },
  statusTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 12,
  },
  statusRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  statusLabel: {
    fontSize: 14,
  },
  statusValue: {
    fontSize: 14,
    fontWeight: '600',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    marginBottom: 16,
  },
  moduleCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 12,
  },
  disabledCard: {
    opacity: 0.6,
  },
  moduleHeader: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  moduleIconContainer: {
    marginRight: 16,
    alignItems: 'center',
  },
  moduleIcon: {
    fontSize: 24,
    marginBottom: 4,
  },
  categoryIcon: {
    fontSize: 12,
  },
  moduleInfo: {
    flex: 1,
  },
  moduleTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  moduleDescription: {
    fontSize: 14,
    lineHeight: 20,
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
  },
  statusText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  instructionsSection: {
    marginTop: 16,
    marginBottom: 32,
  },
  instruction: {
    fontSize: 14,
    marginBottom: 8,
    lineHeight: 20,
  },
});