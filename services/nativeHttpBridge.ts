/**
 * Native HTTP Bridge - TypeScript interface for HttpBridgeModule
 * 
 * This replaces React Native's broken fetch API with a native Android HTTP client
 * that actually works and doesn't hang indefinitely.
 */

import { NativeModules } from 'react-native';

interface HttpResponse {
  success: boolean;
  status: number;
  data?: string;
  error?: string;
  responseTime: number;
}

interface HttpBridgeInterface {
  post(
    url: string,
    headers: Record<string, string> | null,
    body: string,
    timeoutMs: number
  ): Promise<HttpResponse>;
  
  get(
    url: string,
    headers: Record<string, string> | null,
    timeoutMs: number
  ): Promise<HttpResponse>;
}

const { HttpBridge } = NativeModules as { HttpBridge: HttpBridgeInterface };

if (!HttpBridge) {
  throw new Error('HttpBridge native module not found. Make sure the native module is properly registered.');
}

/**
 * Native HTTP client that replaces React Native's broken fetch
 */
export class NativeHttpClient {
  private defaultTimeout = 2000; // 2 seconds - much faster than the broken fetch

  /**
   * Make a POST request using native HTTP client
   */
  async post(
    url: string,
    options: {
      headers?: Record<string, string>;
      body?: any;
      timeout?: number;
    } = {}
  ): Promise<HttpResponse> {
    const {
      headers = { 'Content-Type': 'application/json' },
      body,
      timeout = this.defaultTimeout
    } = options;

    const bodyString = typeof body === 'string' ? body : JSON.stringify(body);
    
    console.log(`🚀 [NativeHttp] POST ${url} (timeout: ${timeout}ms)`);
    console.log(`📦 [NativeHttp] Body: ${bodyString.length} chars`);
    
    const startTime = Date.now();
    
    try {
      const response = await HttpBridge.post(url, headers, bodyString, timeout);
      const totalTime = Date.now() - startTime;
      
      console.log(`✅ [NativeHttp] Response in ${totalTime}ms: HTTP ${response.status}`);
      
      return response;
    } catch (error) {
      const totalTime = Date.now() - startTime;
      console.error(`❌ [NativeHttp] Request failed after ${totalTime}ms:`, error);
      throw error;
    }
  }

  /**
   * Make a GET request using native HTTP client
   */
  async get(
    url: string,
    options: {
      headers?: Record<string, string>;
      timeout?: number;
    } = {}
  ): Promise<HttpResponse> {
    const {
      headers,
      timeout = this.defaultTimeout
    } = options;
    
    console.log(`🚀 [NativeHttp] GET ${url} (timeout: ${timeout}ms)`);
    
    const startTime = Date.now();
    
    try {
      const response = await HttpBridge.get(url, headers || null, timeout);
      const totalTime = Date.now() - startTime;
      
      console.log(`✅ [NativeHttp] Response in ${totalTime}ms: HTTP ${response.status}`);
      
      return response;
    } catch (error) {
      const totalTime = Date.now() - startTime;
      console.error(`❌ [NativeHttp] Request failed after ${totalTime}ms:`, error);
      throw error;
    }
  }

  /**
   * Set default timeout for all requests
   */
  setDefaultTimeout(timeoutMs: number) {
    this.defaultTimeout = timeoutMs;
    console.log(`⏱️ [NativeHttp] Default timeout set to ${timeoutMs}ms`);
  }
}

// Export singleton instance
export const nativeHttpClient = new NativeHttpClient();

// Export for direct access to the bridge if needed
export { HttpBridge };