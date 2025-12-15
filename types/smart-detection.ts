/**
 * TypeScript interfaces for Smart Detection Module
 */

export interface SmartDetectionModule {
  generateHashFromBase64(base64Image: string): Promise<{
    hash: string;
    processingTimeMs: number;
    fingerprint: string;
    compactString: string;
  }>;

  addFrameToAnalyzer(base64Image: string): Promise<{
    fingerprint: string;
    success: boolean;
  }>;

  findSimilarFrames(base64Image: string, threshold: number): Promise<{
    matches: Array<{
      fingerprint: string;
      similarity: number;
      ageMs: number;
    }>;
    matchCount: number;
    targetFingerprint: string;
  }>;

  analyzeFrame(base64Image: string): Promise<{
    shouldProcess: boolean;
    reason: string;
    confidence: number;
    processingTimeMs: number;
    mostSimilarMatch?: {
      fingerprint: string;
      similarity: number;
      ageMs: number;
    };
  }>;

  getBufferStats(): Promise<{
    totalFrames: number;
    maxCapacity: number;
    utilization: number;
    oldestFrameAge: number;
    averageSimilarity: number;
    memoryUsageKB: number;
    totalAdded: number;
    totalExpired: number;
    totalSearches: number;
    averageSearchTimeMs: number;
  }>;

  getPerformanceMetrics(): Promise<{
    uptimeMs: number;
    totalOperations: number;
    averageProcessingTimeMs: number;
    operationSummaries: Array<{
      operationName: string;
      totalExecutions: number;
      averageTimeMs: number;
      successRate: number;
    }>;
    alertCount: number;
    memoryUsageMB: number;
    analyzerTotalOperations: number;
    analyzerAverageTimeMs: number;
    meetsPerformanceRequirements: boolean;
  }>;

  runTestSuite(): Promise<{
    testResults: Array<{
      testName: string;
      passed: boolean;
      processingTimeMs?: number;
      hash?: string;
      similarity?: number;
      error?: string;
      issues: string[];
    }>;
    allTestsPassed: boolean;
    summary: string;
  }>;

  clearAllData(): Promise<boolean>;
}

declare global {
  interface NativeModules {
    SmartDetectionModule: SmartDetectionModule;
  }
}