import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { NativeModules } from 'react-native';

const { ScreenCaptureModule } = NativeModules;

interface BitmapMemoryStats {
  totalMemoryMB: number;
  poolSize: number;
  maxPoolSize: number;
  maxMemoryMB: number;
  hasLastBitmap: boolean;
  lastBitmapWidth?: number;
  lastBitmapHeight?: number;
  lastBitmapSizeMB?: number;
}

export const BitmapMemoryMonitor: React.FC = () => {
  const [stats, setStats] = useState<BitmapMemoryStats | null>(null);
  const [isMonitoring, setIsMonitoring] = useState(false);
  const [refreshInterval, setRefreshInterval] = useState<NodeJS.Timeout | null>(null);

  const fetchStats = async () => {
    try {
      const memoryStats = await ScreenCaptureModule.getBitmapMemoryStats();
      setStats(memoryStats);
    } catch (error) {
      console.error('Failed to fetch bitmap memory stats:', error);
    }
  };

  const startMonitoring = () => {
    if (isMonitoring) return;
    
    setIsMonitoring(true);
    fetchStats(); // Initial fetch
    
    const interval = setInterval(fetchStats, 2000); // Update every 2 seconds
    setRefreshInterval(interval);
  };

  const stopMonitoring = () => {
    setIsMonitoring(false);
    if (refreshInterval) {
      clearInterval(refreshInterval);
      setRefreshInterval(null);
    }
  };

  const forceCleanup = async () => {
    try {
      const result = await ScreenCaptureModule.forceBitmapCleanup();
      Alert.alert(
        'Cleanup Complete',
        `Freed: ${result.freedMemoryKB.toFixed(1)}KB\nRemaining: ${result.remainingMemoryMB.toFixed(1)}MB`
      );
      fetchStats(); // Refresh stats after cleanup
    } catch (error) {
      console.error('Failed to force cleanup:', error);
      Alert.alert('Error', 'Failed to force bitmap cleanup');
    }
  };

  useEffect(() => {
    return () => {
      if (refreshInterval) {
        clearInterval(refreshInterval);
      }
    };
  }, [refreshInterval]);

  const getMemoryColor = (memoryMB: number, maxMB: number) => {
    const percentage = (memoryMB / maxMB) * 100;
    if (percentage > 80) return '#ff4444'; // Red
    if (percentage > 60) return '#ffaa00'; // Orange
    return '#44ff44'; // Green
  };

  const getPoolColor = (poolSize: number, maxSize: number) => {
    const percentage = (poolSize / maxSize) * 100;
    if (percentage > 80) return '#ffaa00'; // Orange
    return '#44ff44'; // Green
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>🖼️ Bitmap Memory Monitor</Text>
      
      <View style={styles.buttonRow}>
        <TouchableOpacity
          style={[styles.button, isMonitoring ? styles.stopButton : styles.startButton]}
          onPress={isMonitoring ? stopMonitoring : startMonitoring}
        >
          <Text style={styles.buttonText}>
            {isMonitoring ? '⏹️ Stop' : '▶️ Start'} Monitor
          </Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.cleanupButton} onPress={forceCleanup}>
          <Text style={styles.buttonText}>🧹 Force Cleanup</Text>
        </TouchableOpacity>
      </View>

      {stats && (
        <View style={styles.statsContainer}>
          <View style={styles.statRow}>
            <Text style={styles.statLabel}>Total Memory:</Text>
            <Text style={[
              styles.statValue,
              { color: getMemoryColor(stats.totalMemoryMB, stats.maxMemoryMB) }
            ]}>
              {stats.totalMemoryMB.toFixed(1)}MB / {stats.maxMemoryMB.toFixed(0)}MB
            </Text>
          </View>

          <View style={styles.statRow}>
            <Text style={styles.statLabel}>Pool Usage:</Text>
            <Text style={[
              styles.statValue,
              { color: getPoolColor(stats.poolSize, stats.maxPoolSize) }
            ]}>
              {stats.poolSize} / {stats.maxPoolSize} bitmaps
            </Text>
          </View>

          <View style={styles.statRow}>
            <Text style={styles.statLabel}>Last Bitmap:</Text>
            <Text style={styles.statValue}>
              {stats.hasLastBitmap ? '✅ Active' : '❌ None'}
            </Text>
          </View>

          {stats.hasLastBitmap && stats.lastBitmapWidth && (
            <>
              <View style={styles.statRow}>
                <Text style={styles.statLabel}>Dimensions:</Text>
                <Text style={styles.statValue}>
                  {stats.lastBitmapWidth}x{stats.lastBitmapHeight}
                </Text>
              </View>

              <View style={styles.statRow}>
                <Text style={styles.statLabel}>Size:</Text>
                <Text style={styles.statValue}>
                  {stats.lastBitmapSizeMB?.toFixed(1)}MB
                </Text>
              </View>
            </>
          )}

          <View style={styles.memoryBar}>
            <View 
              style={[
                styles.memoryFill,
                { 
                  width: `${Math.min((stats.totalMemoryMB / stats.maxMemoryMB) * 100, 100)}%`,
                  backgroundColor: getMemoryColor(stats.totalMemoryMB, stats.maxMemoryMB)
                }
              ]} 
            />
          </View>
          
          <Text style={styles.memoryBarLabel}>
            Memory Usage: {((stats.totalMemoryMB / stats.maxMemoryMB) * 100).toFixed(1)}%
          </Text>
        </View>
      )}

      <View style={styles.legend}>
        <Text style={styles.legendTitle}>🚨 Memory Leak Indicators:</Text>
        <Text style={styles.legendItem}>• Memory constantly growing = Leak</Text>
        <Text style={styles.legendItem}>• Pool always full = Poor recycling</Text>
        <Text style={styles.legendItem}>• Red memory bar = Critical usage</Text>
        <Text style={styles.legendItem}>• Memory not decreasing after cleanup = Leak</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
    margin: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 16,
    color: '#333',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  button: {
    flex: 1,
    padding: 12,
    borderRadius: 6,
    marginHorizontal: 4,
  },
  startButton: {
    backgroundColor: '#4CAF50',
  },
  stopButton: {
    backgroundColor: '#f44336',
  },
  cleanupButton: {
    flex: 1,
    padding: 12,
    borderRadius: 6,
    marginHorizontal: 4,
    backgroundColor: '#FF9800',
  },
  buttonText: {
    color: 'white',
    textAlign: 'center',
    fontWeight: 'bold',
  },
  statsContainer: {
    backgroundColor: 'white',
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
  },
  statRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  statLabel: {
    fontSize: 14,
    color: '#666',
    fontWeight: '500',
  },
  statValue: {
    fontSize: 14,
    fontWeight: 'bold',
  },
  memoryBar: {
    height: 20,
    backgroundColor: '#e0e0e0',
    borderRadius: 10,
    marginTop: 12,
    marginBottom: 4,
    overflow: 'hidden',
  },
  memoryFill: {
    height: '100%',
    borderRadius: 10,
  },
  memoryBarLabel: {
    fontSize: 12,
    textAlign: 'center',
    color: '#666',
    marginBottom: 8,
  },
  legend: {
    backgroundColor: '#fff3cd',
    padding: 12,
    borderRadius: 6,
    borderLeftWidth: 4,
    borderLeftColor: '#ffc107',
  },
  legendTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#856404',
  },
  legendItem: {
    fontSize: 12,
    color: '#856404',
    marginBottom: 4,
  },
});