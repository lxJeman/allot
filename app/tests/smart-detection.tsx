import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { NativeModules } from 'react-native';

const { SmartDetectionModule } = NativeModules;

interface TestResult {
  testName: string;
  passed: boolean;
  processingTimeMs?: number;
  error?: string;
  issues?: string[];
  [key: string]: any;
}

interface MotionTestResults {
  basicMotion: {
    pixelDifference: number;
    isSignificant: boolean;
    processingTime: number;
    regionsCount: number;
  };
  contentAwareThresholds: {
    socialMedia: number | string;
    video: number | string;
    static: number | string;
  };
  velocityTracking: {
    frameCount: number;
    lastVelocity: number | string;
    avgProcessingTime: number;
  };
  adaptiveLearning: {
    totalEvaluations: number;
    adaptiveAdjustment: number;
    contentTypeDetected: boolean;
  };
}

interface StateMachineStatus {
  currentState: string;
  timeSinceLastStateChange: number;
  timeSinceLastHarmful: number;
  timeSinceLastAnalysis: number;
  totalTransitions: number;
  averageProcessingTimeMs: number;
  historyUtilization: number;
  mostFrequentState?: string;
  stateInfo: {
    displayName: string;
    description: string;
    defaultAnalysisFrequency: number;
    defaultConfidenceThreshold: number;
    similarityThreshold: number;
    allowsNormalAnalysis: boolean;
    isProtectiveMode: boolean;
    indicatesUserActivity: boolean;
  };
  recentHistory: Array<{
    fromState: string;
    toState: string;
    reason: string;
    ageMs: number;
    processingTimeMs: number;
  }>;
  config: {
    cooldownPeriodMs: number;
    extendedCooldownMs: number;
    safeContentThresholdMs: number;
    maxHistorySize: number;
    enableVerboseLogging: boolean;
  };
}

interface PreFilterPipelineStats {
  totalExecutions: number;
  totalFramesProcessed: number;
  totalFramesSkipped: number;
  skipRate: number;
  averageProcessingTimeMs: number;
  maxProcessingTimeMs: number;
  meetsPerformanceRequirements: boolean;
  pipelineEfficiency: {
    skipRate: number;
    processRate: number;
    averageStagesExecuted: number;
    earlyTerminationRate: number;
  };
  stageStats: Array<{
    stageName: string;
    totalExecutions: number;
    passRate: number;
    averageProcessingTimeMs: number;
    maxProcessingTimeMs: number;
    averageConfidence: number;
    meetsPerformanceRequirements: boolean;
    performanceAlerts: string[];
  }>;
  performanceAlerts: string[];
  activeStages: string[];
}

export default function SmartDetectionTest() {
  const [isLoading, setIsLoading] = useState(false);
  const [testResults, setTestResults] = useState<TestResult[]>([]);
  const [motionTestResults, setMotionTestResults] = useState<MotionTestResults | null>(null);
  const [stateMachineStatus, setStateMachineStatus] = useState<StateMachineStatus | null>(null);
  const [bufferStats, setBufferStats] = useState<any>(null);
  const [performanceMetrics, setPerformanceMetrics] = useState<any>(null);
  const [preFilterPipelineStats, setPreFilterPipelineStats] = useState<PreFilterPipelineStats | null>(null);

  useEffect(() => {
    checkModuleAvailability();
    loadInitialData();
  }, []);

  const checkModuleAvailability = () => {
    if (!SmartDetectionModule) {
      Alert.alert('Error', 'SmartDetectionModule not available');
      return false;
    }
    return true;
  };

  const loadInitialData = async () => {
    try {
      if (SmartDetectionModule) {
        const [bufferData, performanceData, stateMachineData, pipelineData] = await Promise.all([
          SmartDetectionModule.getBufferStats().catch(() => null),
          SmartDetectionModule.getPerformanceMetrics().catch(() => null),
          SmartDetectionModule.getStateMachineStatus ? SmartDetectionModule.getStateMachineStatus().catch(() => null) : null,
          SmartDetectionModule.getPreFilterPipelineStats ? SmartDetectionModule.getPreFilterPipelineStats().catch(() => null) : null
        ]);
        setBufferStats(bufferData);
        setPerformanceMetrics(performanceData);
        setStateMachineStatus(stateMachineData);
        setPreFilterPipelineStats(pipelineData);
      }
    } catch (error) {
      console.error('Error loading initial data:', error);
    }
  };

  const runComprehensiveTestSuite = async () => {
    if (!checkModuleAvailability()) return;

    setIsLoading(true);
    setTestResults([]);

    try {
      console.log('🧪 Running comprehensive Smart Detection test suite...');

      const result = await SmartDetectionModule.runTestSuite();
      console.log('✅ Test suite completed:', result);

      if (result.testResults) {
        setTestResults(result.testResults);
      }

      // Refresh data after tests
      await loadInitialData();

      Alert.alert(
        'Test Suite Complete',
        `${result.summary}\n\nAll tests passed: ${result.allTestsPassed ? 'Yes' : 'No'}`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('❌ Test suite failed:', error);
      Alert.alert('Test Error', `Failed to run test suite: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const runMotionDetectionTests = async () => {
    if (!checkModuleAvailability()) return;

    setIsLoading(true);

    try {
      console.log('🎬 Testing Motion Detection Engine...');

      // Clear previous data
      await SmartDetectionModule.clearAllData();

      // Create test images with different patterns for motion testing
      console.log('1️⃣ Creating test images...');
      
      // Fallback base64 images (simple 1x1 pixel images)
      const testImageBase64_1 = '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A';
      const testImageBase64_2 = '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A';
      
      let testImage1, testImage2, testImage3, testImage4;
      
      if (SmartDetectionModule.createTestImage) {
        testImage1 = await SmartDetectionModule.createTestImage(200, 200, '#FF0000', 'SOLID');
        testImage2 = await SmartDetectionModule.createTestImage(200, 200, '#00FF00', 'SOLID');
        testImage3 = await SmartDetectionModule.createTestImage(200, 200, '#FF0011', 'GRADIENT');
        testImage4 = await SmartDetectionModule.createTestImage(200, 200, '#FF0000', 'CHECKERBOARD');
      } else {
        console.log('⚠️ createTestImage not available, using fallback images');
        testImage1 = { base64: testImageBase64_1 };
        testImage2 = { base64: testImageBase64_2 };
        testImage3 = { base64: testImageBase64_1 };
        testImage4 = { base64: testImageBase64_2 };
      }

      console.log('✅ Test images created');

      // Test 1: Basic Motion Detection
      console.log('2️⃣ Testing basic motion detection...');
      const basicMotionResult = await SmartDetectionModule.testMotionDetection(
        testImage1.base64,
        testImage2.base64
      );
      console.log('✅ Basic motion result:', basicMotionResult);

      // Test 2: Advanced Motion Analysis with different content types
      console.log('3️⃣ Testing advanced motion analysis...');
      const socialMediaResult = await SmartDetectionModule.analyzeMotionAdvanced(
        testImage1.base64,
        testImage2.base64,
        'SOCIAL_MEDIA'
      );

      const videoResult = await SmartDetectionModule.analyzeMotionAdvanced(
        testImage1.base64,
        testImage3.base64,
        'VIDEO_CONTENT'
      );

      const staticResult = await SmartDetectionModule.analyzeMotionAdvanced(
        testImage1.base64,
        testImage4.base64,
        'STATIC_CONTENT'
      );

      console.log('✅ Advanced motion results:', { socialMediaResult, videoResult, staticResult });

      // Test 3: Velocity Calculation with sequence
      console.log('4️⃣ Testing velocity calculation...');
      const frames = [testImage1, testImage2, testImage3, testImage4];
      let velocityResults = [];

      for (let i = 1; i < frames.length; i++) {
        const result = await SmartDetectionModule.analyzeMotionAdvanced(
          frames[i].base64,
          frames[i - 1].base64,
          'VIDEO_CONTENT'
        );
        velocityResults.push(result);
      }

      console.log('✅ Velocity results:', velocityResults);

      // Test 4: Enhanced Motion Stats
      console.log('5️⃣ Getting enhanced motion stats...');
      const enhancedStats = await SmartDetectionModule.getEnhancedMotionStats();
      console.log('✅ Enhanced stats:', enhancedStats);

      // Prepare results summary
      const motionTestResults = {
        basicMotion: {
          pixelDifference: basicMotionResult.pixelDifference,
          isSignificant: basicMotionResult.isSignificantMotion,
          processingTime: basicMotionResult.processingTimeMs,
          regionsCount: basicMotionResult.motionRegionsCount
        },
        contentAwareThresholds: {
          socialMedia: socialMediaResult.thresholdEvaluation?.adjustedThreshold || 'N/A',
          video: videoResult.thresholdEvaluation?.adjustedThreshold || 'N/A',
          static: staticResult.thresholdEvaluation?.adjustedThreshold || 'N/A'
        },
        velocityTracking: {
          frameCount: velocityResults.length,
          lastVelocity: velocityResults[velocityResults.length - 1]?.thresholdEvaluation?.motionVelocity || 'N/A',
          avgProcessingTime: velocityResults.reduce((sum, r) => sum + (r.motionResult?.processingTimeMs || 0), 0) / velocityResults.length
        },
        adaptiveLearning: {
          totalEvaluations: enhancedStats.evaluationStats?.totalEvaluations || 0,
          adaptiveAdjustment: enhancedStats.evaluationStats?.adaptiveAdjustment || 0,
          contentTypeDetected: enhancedStats.contentTypeDetected || false
        }
      };

      setMotionTestResults(motionTestResults);

      // Refresh stats
      await loadInitialData();

      Alert.alert(
        'Motion Detection Test Complete',
        `Basic Motion: ${motionTestResults.basicMotion.pixelDifference.toFixed(3)} difference\n` +
        `Processing: ${motionTestResults.basicMotion.processingTime.toFixed(1)}ms\n` +
        `Content-Aware Thresholds: ${Object.keys(motionTestResults.contentAwareThresholds).length} types\n` +
        `Velocity Frames: ${motionTestResults.velocityTracking.frameCount}\n` +
        `Adaptive Learning: ${motionTestResults.adaptiveLearning.totalEvaluations} evaluations`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error in motion detection test:', error);
      Alert.alert('Motion Test Error', `Failed to test motion detection: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const testFrameAnalysis = async () => {
    if (!checkModuleAvailability()) return;

    setIsLoading(true);

    try {
      console.log('🔍 Testing frame analysis...');

      // Create a simple test image (1x1 pixel base64)
      const testImageBase64 = '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A';
      
      // Check if createTestImage is available, if not use fallback
      let testImage;
      if (SmartDetectionModule.createTestImage) {
        console.log('1️⃣ Creating test image...');
        testImage = await SmartDetectionModule.createTestImage(200, 200, '#FF0000', 'GRADIENT');
      } else {
        console.log('1️⃣ Using fallback test image...');
        testImage = { base64: testImageBase64 };
      }

      // 1. Generate hash
      console.log('1️⃣ Generating hash...');
      const hashResult = await SmartDetectionModule.generateHashFromBase64(testImage.base64);
      console.log('✅ Hash generated:', hashResult.hash);

      // 2. Add to analyzer
      console.log('2️⃣ Adding frame to analyzer...');
      await SmartDetectionModule.addFrameToAnalyzer(testImage.base64);
      console.log('✅ Frame added to analyzer');

      // 3. Analyze frame
      console.log('3️⃣ Analyzing frame...');
      const analysisResult = await SmartDetectionModule.analyzeFrame(testImage.base64);
      console.log('✅ Analysis result:', analysisResult);

      // 4. Find similar frames
      console.log('4️⃣ Finding similar frames...');
      const similarFrames = await SmartDetectionModule.findSimilarFrames(testImage.base64, 0.8);
      console.log('✅ Similar frames:', similarFrames);

      // Refresh stats
      await loadInitialData();

      Alert.alert(
        'Frame Analysis Complete',
        `Hash: ${hashResult.hash.substring(0, 8)}...\n` +
        `Processing: ${hashResult.processingTimeMs.toFixed(1)}ms\n` +
        `Should Process: ${analysisResult.shouldProcess ? 'Yes' : 'No'}\n` +
        `Reason: ${analysisResult.reason}\n` +
        `Similar Frames: ${similarFrames.matchCount}`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error in frame analysis:', error);
      Alert.alert('Analysis Error', `Failed to analyze frame: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const testStateMachine = async () => {
    if (!checkModuleAvailability()) return;

    setIsLoading(true);

    try {
      console.log('🔄 Testing State Machine...');

      // Reset state machine for clean test
      if (SmartDetectionModule.resetStateMachine) {
        await SmartDetectionModule.resetStateMachine();
        console.log('✅ State machine reset');
      }

      // Create test image
      const testImageBase64 = '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A';
      
      let testImage;
      if (SmartDetectionModule.createTestImage) {
        testImage = await SmartDetectionModule.createTestImage(200, 200, '#FF0000', 'SOLID');
      } else {
        testImage = { base64: testImageBase64 };
      }

      // Test 1: Normal scanning state
      console.log('1️⃣ Testing normal scanning...');
      if (SmartDetectionModule.testStateMachine) {
        const scanningResult = await SmartDetectionModule.testStateMachine(
          testImage.base64,
          0.5, // Low similarity
          false, // No motion
          false, // Not harmful
          0.8 // High confidence
        );
        console.log('✅ Scanning result:', scanningResult);
      }

      // Test 2: Harmful content detection
      console.log('2️⃣ Testing harmful content detection...');
      if (SmartDetectionModule.testStateMachine) {
        const harmfulResult = await SmartDetectionModule.testStateMachine(
          testImage.base64,
          0.3, // Low similarity
          false, // No motion
          true, // Harmful content
          0.9 // Very high confidence
        );
        console.log('✅ Harmful content result:', harmfulResult);
      }

      // Test 3: Motion detection (scrolling away)
      console.log('3️⃣ Testing motion detection...');
      if (SmartDetectionModule.testStateMachine) {
        const motionResult = await SmartDetectionModule.testStateMachine(
          testImage.base64,
          0.4, // Medium similarity
          true, // Motion detected
          false, // Not harmful
          0.7 // Medium confidence
        );
        console.log('✅ Motion result:', motionResult);
      }

      // Test 4: Force state transitions
      console.log('4️⃣ Testing state transitions...');
      const states = ['HARMFUL_DETECTED', 'SCROLLING_AWAY', 'COOLDOWN', 'SAFE_CONTENT', 'SCANNING'];
      
      for (const state of states) {
        if (SmartDetectionModule.forceStateTransition) {
          await SmartDetectionModule.forceStateTransition(state, `Test transition to ${state}`);
          console.log(`✅ Transitioned to ${state}`);
        }
      }

      // Test 5: Get final status
      console.log('5️⃣ Getting final status...');
      let finalStatus = null;
      if (SmartDetectionModule.getStateMachineStatus) {
        finalStatus = await SmartDetectionModule.getStateMachineStatus();
        console.log('✅ Final status:', finalStatus);
        setStateMachineStatus(finalStatus);
      }

      // Refresh all data
      await loadInitialData();

      Alert.alert(
        'State Machine Test Complete',
        `Current State: ${finalStatus?.currentState || 'Unknown'}\n` +
        `Total Transitions: ${finalStatus?.totalTransitions || 0}\n` +
        `History Utilization: ${finalStatus?.historyUtilization ? (finalStatus.historyUtilization * 100).toFixed(1) : 0}%\n` +
        `Avg Processing: ${finalStatus?.averageProcessingTimeMs?.toFixed(2) || 0}ms\n` +
        `Most Frequent State: ${finalStatus?.mostFrequentState || 'None'}`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error in state machine test:', error);
      Alert.alert('State Machine Test Error', `Failed to test state machine: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const testPreFilterPipeline = async () => {
    if (!checkModuleAvailability()) return;

    setIsLoading(true);

    try {
      console.log('🔍 Testing Pre-filter Pipeline...');

      // Reset pipeline for clean test
      if (SmartDetectionModule.resetPreFilterPipeline) {
        await SmartDetectionModule.resetPreFilterPipeline();
        console.log('✅ Pipeline reset');
      }

      // Create test images
      const testImageBase64 = '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A';
      
      let testImage;
      if (SmartDetectionModule.createTestImage) {
        testImage = await SmartDetectionModule.createTestImage(200, 200, '#FF0000', 'SOLID');
      } else {
        testImage = { base64: testImageBase64 };
      }

      // Test 1: Add real filter stages
      console.log('1️⃣ Adding real filter stages...');
      if (SmartDetectionModule.addSimilarityFilterStage && SmartDetectionModule.addMotionFilterStage) {
        await SmartDetectionModule.addSimilarityFilterStage(0.90); // 90% similarity threshold
        await SmartDetectionModule.addMotionFilterStage(0.25); // 25% motion threshold
        console.log('✅ Real filter stages added');
      } else if (SmartDetectionModule.addTestFilterStage) {
        // Fallback to test stages if real stages not available
        await SmartDetectionModule.addTestFilterStage('SimilarityFilter', 1, 0.9);
        await SmartDetectionModule.addTestFilterStage('MotionFilter', 2, 0.7);
        await SmartDetectionModule.addTestFilterStage('TextFilter', 3, 0.5);
        console.log('✅ Test stages added (fallback)');
      }

      // Test 2: Run pipeline tests
      console.log('2️⃣ Testing pipeline execution...');
      const pipelineResults = [];
      
      if (SmartDetectionModule.testPreFilterPipeline) {
        for (let i = 0; i < 10; i++) {
          const result = await SmartDetectionModule.testPreFilterPipeline(testImage.base64);
          pipelineResults.push(result);
          console.log(`  Test ${i + 1}: ${result.shouldProceed ? 'PROCEED' : 'SKIP'} (${result.processingTimeMs}ms)`);
        }
      }

      // Test 3: Get pipeline statistics
      console.log('3️⃣ Getting pipeline statistics...');
      let pipelineStats = null;
      if (SmartDetectionModule.getPreFilterPipelineStats) {
        pipelineStats = await SmartDetectionModule.getPreFilterPipelineStats();
        console.log('✅ Pipeline stats:', pipelineStats);
        setPreFilterPipelineStats(pipelineStats);
      }

      // Refresh all data
      await loadInitialData();

      // Calculate test summary
      const proceedCount = pipelineResults.filter(r => r.shouldProceed).length;
      const skipCount = pipelineResults.length - proceedCount;
      const avgProcessingTime = pipelineResults.reduce((sum, r) => sum + r.processingTimeMs, 0) / pipelineResults.length;

      Alert.alert(
        'Pre-filter Pipeline Test Complete',
        `Total Tests: ${pipelineResults.length}\n` +
        `Proceeded: ${proceedCount} (${((proceedCount / pipelineResults.length) * 100).toFixed(1)}%)\n` +
        `Skipped: ${skipCount} (${((skipCount / pipelineResults.length) * 100).toFixed(1)}%)\n` +
        `Avg Processing: ${avgProcessingTime.toFixed(2)}ms\n` +
        `Active Stages: ${pipelineStats?.activeStages?.length || 0}\n` +
        `Performance: ${pipelineStats?.meetsPerformanceRequirements ? 'Met' : 'Not Met'}`,
        [{ text: 'OK' }]
      );

    } catch (error) {
      console.error('Error in pre-filter pipeline test:', error);
      Alert.alert('Pipeline Test Error', `Failed to test pre-filter pipeline: ${error instanceof Error ? error.message : 'Unknown error'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const clearAllData = async () => {
    if (!checkModuleAvailability()) return;

    try {
      await SmartDetectionModule.clearAllData();
      setTestResults([]);
      setMotionTestResults(null);
      setStateMachineStatus(null);
      setPreFilterPipelineStats(null);
      await loadInitialData();
      Alert.alert('Success', 'All data cleared');
    } catch (error) {
      console.error('Error clearing data:', error);
      Alert.alert('Error', `Failed to clear data: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>🧪 Smart Detection Test Suite</Text>
        <Text style={styles.subtitle}>
          Comprehensive testing for motion detection and frame analysis
        </Text>
      </View>

      {/* Module Status */}
      <View style={styles.statusCard}>
        <Text style={styles.statusTitle}>Module Status</Text>
        <Text style={[styles.statusText, { color: SmartDetectionModule ? '#4CAF50' : '#F44336' }]}>
          SmartDetectionModule: {SmartDetectionModule ? '✅ Available' : '❌ Not Found'}
        </Text>
      </View>

      {/* Test Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[styles.button, styles.primaryButton]}
          onPress={runComprehensiveTestSuite}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🧪 Running Tests...' : '🧪 Run Full Test Suite'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.motionButton]}
          onPress={runMotionDetectionTests}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🎬 Testing Motion...' : '🎬 Test Motion Detection'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.analysisButton]}
          onPress={testFrameAnalysis}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🔍 Analyzing...' : '🔍 Test Frame Analysis'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.stateMachineButton]}
          onPress={testStateMachine}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🔄 Testing State Machine...' : '🔄 Test State Machine'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.pipelineButton]}
          onPress={testPreFilterPipeline}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>
            {isLoading ? '🔍 Testing Pipeline...' : '🔍 Test Pre-filter Pipeline'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.clearButton]}
          onPress={clearAllData}
          disabled={isLoading || !SmartDetectionModule}
        >
          <Text style={styles.buttonText}>🗑️ Clear All Data</Text>
        </TouchableOpacity>
      </View>

      {/* Loading Indicator */}
      {isLoading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#007AFF" />
          <Text style={styles.loadingText}>Running tests...</Text>
        </View>
      )}

      {/* Pre-filter Pipeline Results */}
      {preFilterPipelineStats && (
        <View style={styles.resultsContainer}>
          <Text style={styles.sectionTitle}>🔍 Pre-filter Pipeline Statistics</Text>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Pipeline Overview</Text>
            <Text style={styles.resultText}>Total Executions: {preFilterPipelineStats.totalExecutions}</Text>
            <Text style={styles.resultText}>Frames Processed: {preFilterPipelineStats.totalFramesProcessed}</Text>
            <Text style={styles.resultText}>Frames Skipped: {preFilterPipelineStats.totalFramesSkipped}</Text>
            <Text style={styles.resultText}>Skip Rate: {preFilterPipelineStats.skipRate.toFixed(1)}%</Text>
            <Text style={styles.resultText}>Avg Processing: {preFilterPipelineStats.averageProcessingTimeMs.toFixed(2)}ms</Text>
            <Text style={styles.resultText}>Max Processing: {preFilterPipelineStats.maxProcessingTimeMs}ms</Text>
            <Text style={[styles.resultText, { color: preFilterPipelineStats.meetsPerformanceRequirements ? '#4CAF50' : '#F44336' }]}>
              Performance: {preFilterPipelineStats.meetsPerformanceRequirements ? 'Requirements Met' : 'Below Target'}
            </Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Pipeline Efficiency</Text>
            <Text style={styles.resultText}>Skip Rate: {preFilterPipelineStats.pipelineEfficiency.skipRate.toFixed(1)}%</Text>
            <Text style={styles.resultText}>Process Rate: {preFilterPipelineStats.pipelineEfficiency.processRate.toFixed(1)}%</Text>
            <Text style={styles.resultText}>Avg Stages Executed: {preFilterPipelineStats.pipelineEfficiency.averageStagesExecuted.toFixed(1)}</Text>
            <Text style={styles.resultText}>Early Termination Rate: {preFilterPipelineStats.pipelineEfficiency.earlyTerminationRate.toFixed(1)}%</Text>
          </View>

          {preFilterPipelineStats.stageStats && preFilterPipelineStats.stageStats.length > 0 && (
            <View style={styles.resultCard}>
              <Text style={styles.resultTitle}>Stage Statistics</Text>
              {preFilterPipelineStats.stageStats.map((stage, index) => (
                <View key={index} style={{ marginBottom: 12, paddingBottom: 8, borderBottomWidth: 1, borderBottomColor: '#eee' }}>
                  <Text style={[styles.resultText, { fontWeight: '600' }]}>{stage.stageName}</Text>
                  <Text style={styles.resultText}>Executions: {stage.totalExecutions}</Text>
                  <Text style={styles.resultText}>Pass Rate: {stage.passRate.toFixed(1)}%</Text>
                  <Text style={styles.resultText}>Avg Processing: {stage.averageProcessingTimeMs.toFixed(2)}ms</Text>
                  <Text style={styles.resultText}>Max Processing: {stage.maxProcessingTimeMs}ms</Text>
                  <Text style={styles.resultText}>Avg Confidence: {stage.averageConfidence.toFixed(1)}%</Text>
                  <Text style={[styles.resultText, { color: stage.meetsPerformanceRequirements ? '#4CAF50' : '#F44336' }]}>
                    Performance: {stage.meetsPerformanceRequirements ? 'Met' : 'Below Target'}
                  </Text>
                  {stage.performanceAlerts && stage.performanceAlerts.length > 0 && (
                    <Text style={[styles.resultText, { color: '#FF9500' }]}>
                      Alerts: {stage.performanceAlerts.length}
                    </Text>
                  )}
                </View>
              ))}
            </View>
          )}

          {preFilterPipelineStats.activeStages && preFilterPipelineStats.activeStages.length > 0 && (
            <View style={styles.resultCard}>
              <Text style={styles.resultTitle}>Active Stages</Text>
              <Text style={styles.resultText}>Stages: {preFilterPipelineStats.activeStages.join(', ')}</Text>
              <Text style={styles.resultText}>Total Count: {preFilterPipelineStats.activeStages.length}</Text>
            </View>
          )}

          {preFilterPipelineStats.performanceAlerts && preFilterPipelineStats.performanceAlerts.length > 0 && (
            <View style={styles.resultCard}>
              <Text style={styles.resultTitle}>Performance Alerts</Text>
              {preFilterPipelineStats.performanceAlerts.map((alert, index) => (
                <Text key={index} style={[styles.resultText, { color: '#FF3B30' }]}>• {alert}</Text>
              ))}
            </View>
          )}
        </View>
      )}

      {/* State Machine Results */}
      {stateMachineStatus && (
        <View style={styles.resultsContainer}>
          <Text style={styles.sectionTitle}>🔄 State Machine Status</Text>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Current State</Text>
            <Text style={styles.resultText}>State: {stateMachineStatus.currentState}</Text>
            <Text style={styles.resultText}>Display Name: {stateMachineStatus.stateInfo.displayName}</Text>
            <Text style={styles.resultText}>Description: {stateMachineStatus.stateInfo.description}</Text>
            <Text style={styles.resultText}>Analysis Frequency: {(stateMachineStatus.stateInfo.defaultAnalysisFrequency * 100).toFixed(0)}%</Text>
            <Text style={styles.resultText}>Confidence Threshold: {(stateMachineStatus.stateInfo.defaultConfidenceThreshold * 100).toFixed(0)}%</Text>
            <Text style={styles.resultText}>Similarity Threshold: {(stateMachineStatus.stateInfo.similarityThreshold * 100).toFixed(0)}%</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>State Properties</Text>
            <Text style={styles.resultText}>Allows Normal Analysis: {stateMachineStatus.stateInfo.allowsNormalAnalysis ? 'Yes' : 'No'}</Text>
            <Text style={styles.resultText}>Protective Mode: {stateMachineStatus.stateInfo.isProtectiveMode ? 'Yes' : 'No'}</Text>
            <Text style={styles.resultText}>Indicates User Activity: {stateMachineStatus.stateInfo.indicatesUserActivity ? 'Yes' : 'No'}</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Timing Information</Text>
            <Text style={styles.resultText}>Time Since State Change: {(stateMachineStatus.timeSinceLastStateChange / 1000).toFixed(1)}s</Text>
            <Text style={styles.resultText}>Time Since Last Harmful: {(stateMachineStatus.timeSinceLastHarmful / 1000).toFixed(1)}s</Text>
            <Text style={styles.resultText}>Time Since Last Analysis: {(stateMachineStatus.timeSinceLastAnalysis / 1000).toFixed(1)}s</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Statistics</Text>
            <Text style={styles.resultText}>Total Transitions: {stateMachineStatus.totalTransitions}</Text>
            <Text style={styles.resultText}>Average Processing: {stateMachineStatus.averageProcessingTimeMs.toFixed(2)}ms</Text>
            <Text style={styles.resultText}>History Utilization: {(stateMachineStatus.historyUtilization * 100).toFixed(1)}%</Text>
            <Text style={styles.resultText}>Most Frequent State: {stateMachineStatus.mostFrequentState || 'None'}</Text>
          </View>

          {stateMachineStatus.recentHistory && stateMachineStatus.recentHistory.length > 0 && (
            <View style={styles.resultCard}>
              <Text style={styles.resultTitle}>Recent Transitions</Text>
              {stateMachineStatus.recentHistory.slice(0, 5).map((transition, index) => (
                <Text key={index} style={styles.resultText}>
                  {transition.fromState} → {transition.toState} ({(transition.ageMs / 1000).toFixed(1)}s ago)
                </Text>
              ))}
            </View>
          )}

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Configuration</Text>
            <Text style={styles.resultText}>Cooldown Period: {(stateMachineStatus.config.cooldownPeriodMs / 1000).toFixed(1)}s</Text>
            <Text style={styles.resultText}>Extended Cooldown: {(stateMachineStatus.config.extendedCooldownMs / 1000).toFixed(1)}s</Text>
            <Text style={styles.resultText}>Safe Content Threshold: {(stateMachineStatus.config.safeContentThresholdMs / 1000).toFixed(1)}s</Text>
            <Text style={styles.resultText}>Max History Size: {stateMachineStatus.config.maxHistorySize}</Text>
            <Text style={styles.resultText}>Verbose Logging: {stateMachineStatus.config.enableVerboseLogging ? 'Enabled' : 'Disabled'}</Text>
          </View>
        </View>
      )}

      {/* Motion Test Results */}
      {motionTestResults && (
        <View style={styles.resultsContainer}>
          <Text style={styles.sectionTitle}>🎬 Motion Detection Results</Text>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Basic Motion Detection</Text>
            <Text style={styles.resultText}>Pixel Difference: {motionTestResults.basicMotion.pixelDifference.toFixed(3)}</Text>
            <Text style={styles.resultText}>Significant Motion: {motionTestResults.basicMotion.isSignificant ? 'Yes' : 'No'}</Text>
            <Text style={styles.resultText}>Processing Time: {motionTestResults.basicMotion.processingTime.toFixed(1)}ms</Text>
            <Text style={styles.resultText}>Motion Regions: {motionTestResults.basicMotion.regionsCount}</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Content-Aware Thresholds</Text>
            <Text style={styles.resultText}>Social Media: {motionTestResults.contentAwareThresholds.socialMedia}</Text>
            <Text style={styles.resultText}>Video Content: {motionTestResults.contentAwareThresholds.video}</Text>
            <Text style={styles.resultText}>Static Content: {motionTestResults.contentAwareThresholds.static}</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Velocity Tracking</Text>
            <Text style={styles.resultText}>Frame Count: {motionTestResults.velocityTracking.frameCount}</Text>
            <Text style={styles.resultText}>Last Velocity: {motionTestResults.velocityTracking.lastVelocity}</Text>
            <Text style={styles.resultText}>Avg Processing: {motionTestResults.velocityTracking.avgProcessingTime.toFixed(1)}ms</Text>
          </View>

          <View style={styles.resultCard}>
            <Text style={styles.resultTitle}>Adaptive Learning</Text>
            <Text style={styles.resultText}>Total Evaluations: {motionTestResults.adaptiveLearning.totalEvaluations}</Text>
            <Text style={styles.resultText}>Adaptive Adjustment: {motionTestResults.adaptiveLearning.adaptiveAdjustment.toFixed(3)}</Text>
            <Text style={styles.resultText}>Content Type Detected: {motionTestResults.adaptiveLearning.contentTypeDetected ? 'Yes' : 'No'}</Text>
          </View>
        </View>
      )}

      {/* Buffer Statistics */}
      {bufferStats && (
        <View style={styles.statsContainer}>
          <Text style={styles.sectionTitle}>📊 Buffer Statistics</Text>
          <View style={styles.statsGrid}>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Frames</Text>
              <Text style={styles.statValue}>{bufferStats.totalFrames}/{bufferStats.maxCapacity}</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Utilization</Text>
              <Text style={styles.statValue}>{bufferStats.utilization.toFixed(1)}%</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Memory</Text>
              <Text style={styles.statValue}>{bufferStats.memoryUsageKB.toFixed(1)}KB</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Avg Similarity</Text>
              <Text style={styles.statValue}>{(bufferStats.averageSimilarity * 100).toFixed(1)}%</Text>
            </View>
          </View>
        </View>
      )}

      {/* Performance Metrics */}
      {performanceMetrics && (
        <View style={styles.statsContainer}>
          <Text style={styles.sectionTitle}>⚡ Performance Metrics</Text>
          <View style={styles.statsGrid}>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Total Operations</Text>
              <Text style={styles.statValue}>{performanceMetrics.totalOperations}</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Avg Processing</Text>
              <Text style={styles.statValue}>{performanceMetrics.averageProcessingTimeMs.toFixed(2)}ms</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Memory Usage</Text>
              <Text style={styles.statValue}>{performanceMetrics.memoryUsageMB}MB</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statLabel}>Requirements</Text>
              <Text style={[styles.statValue, { color: performanceMetrics.meetsPerformanceRequirements ? '#4CAF50' : '#F44336' }]}>
                {performanceMetrics.meetsPerformanceRequirements ? 'Met' : 'Not Met'}
              </Text>
            </View>
          </View>
        </View>
      )}

      {/* Test Results */}
      {testResults.length > 0 && (
        <View style={styles.resultsContainer}>
          <Text style={styles.sectionTitle}>🧪 Test Results</Text>
          {testResults.map((test, index) => (
            <View key={index} style={[styles.testCard, test.passed ? styles.passedCard : styles.failedCard]}>
              <View style={styles.testHeader}>
                <Text style={styles.testName}>{test.testName || 'Unknown Test'}</Text>
                <Text style={[styles.testStatus, test.passed ? styles.passedText : styles.failedText]}>
                  {test.passed ? '✅ PASS' : '❌ FAIL'}
                </Text>
              </View>

              {test.processingTimeMs !== undefined && (
                <Text style={styles.testDetail}>Processing Time: {Number(test.processingTimeMs).toFixed(2)}ms</Text>
              )}

              {test.error && (
                <Text style={styles.errorText}>Error: {test.error}</Text>
              )}

              {test.issues && Array.isArray(test.issues) && test.issues.length > 0 && (
                <View style={styles.issuesContainer}>
                  <Text style={styles.issuesTitle}>Issues:</Text>
                  {test.issues.map((issue, i) => (
                    <Text key={i} style={styles.issueText}>• {String(issue)}</Text>
                  ))}
                </View>
              )}
            </View>
          ))}
        </View>
      )}

      {/* Instructions */}
      <View style={styles.instructionsContainer}>
        <Text style={styles.sectionTitle}>📋 How to Test</Text>
        <Text style={styles.instruction}>1. Run Full Test Suite - Tests all components with synthetic data</Text>
        <Text style={styles.instruction}>2. Test Motion Detection - Comprehensive motion analysis testing</Text>
        <Text style={styles.instruction}>3. Test Frame Analysis - Hash generation and similarity detection</Text>
        <Text style={styles.instruction}>4. Test State Machine - Content state machine and decision logic</Text>
        <Text style={styles.instruction}>5. Test Pre-filter Pipeline - Multi-stage filtering system testing</Text>
        <Text style={styles.instruction}>6. Clear All Data - Reset all buffers and statistics</Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 16,
  },
  header: {
    alignItems: 'center',
    marginBottom: 24,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  statusCard: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statusTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  statusText: {
    fontSize: 16,
    fontWeight: '500',
  },
  buttonContainer: {
    marginBottom: 24,
  },
  button: {
    padding: 16,
    borderRadius: 8,
    marginBottom: 12,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  primaryButton: {
    backgroundColor: '#007AFF',
  },
  motionButton: {
    backgroundColor: '#FF9500',
  },
  analysisButton: {
    backgroundColor: '#34C759',
  },
  stateMachineButton: {
    backgroundColor: '#5856D6',
  },
  pipelineButton: {
    backgroundColor: '#AF52DE',
  },
  clearButton: {
    backgroundColor: '#FF3B30',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  loadingContainer: {
    alignItems: 'center',
    padding: 24,
    backgroundColor: 'white',
    borderRadius: 8,
    marginBottom: 16,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#666',
  },
  resultsContainer: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 16,
  },
  resultCard: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  resultTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  resultText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  statsContainer: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  statItem: {
    width: '48%',
    alignItems: 'center',
    marginBottom: 12,
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 4,
  },
  statValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
  testCard: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 12,
    borderLeftWidth: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  passedCard: {
    borderLeftColor: '#34C759',
  },
  failedCard: {
    borderLeftColor: '#FF3B30',
  },
  testHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  testName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    flex: 1,
  },
  testStatus: {
    fontSize: 14,
    fontWeight: '600',
  },
  passedText: {
    color: '#34C759',
  },
  failedText: {
    color: '#FF3B30',
  },
  testDetail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  errorText: {
    fontSize: 14,
    color: '#FF3B30',
    marginBottom: 8,
  },
  issuesContainer: {
    marginTop: 8,
  },
  issuesTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FF3B30',
    marginBottom: 4,
  },
  issueText: {
    fontSize: 13,
    color: '#FF3B30',
    marginLeft: 8,
  },
  instructionsContainer: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  instruction: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
    lineHeight: 20,
  },
});