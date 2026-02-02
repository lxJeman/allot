package com.allot

import android.util.Log
import com.facebook.react.bridge.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class HttpBridgeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    
    companion object {
        private const val TAG = "HttpBridge"
        private const val MODULE_NAME = "HttpBridge"
    }

    override fun getName(): String = MODULE_NAME

    @ReactMethod
    fun post(
        url: String,
        headers: ReadableMap?,
        body: String,
        timeoutMs: Int,
        promise: Promise
    ) {
        Log.d(TAG, "🌐 Making POST request to: $url")
        Log.d(TAG, "📦 Body length: ${body.length} chars")
        Log.d(TAG, "⏱️ Timeout: ${timeoutMs}ms")
        
        // Use coroutine for async operation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startTime = System.currentTimeMillis()
                
                val connection = URL(url).openConnection() as HttpURLConnection
                
                // Set timeouts (much shorter than the original 10s/30s)
                connection.connectTimeout = timeoutMs
                connection.readTimeout = timeoutMs
                
                // Set method and headers
                connection.requestMethod = "POST"
                connection.doOutput = true
                
                // Add default headers
                connection.setRequestProperty("Content-Type", "application/json")
                
                // Add custom headers if provided
                headers?.let { headerMap ->
                    val iterator = headerMap.keySetIterator()
                    while (iterator.hasNextKey()) {
                        val key = iterator.nextKey()
                        val value = headerMap.getString(key)
                        if (value != null) {
                            connection.setRequestProperty(key, value)
                        }
                    }
                }
                
                Log.d(TAG, "📡 Sending request...")
                
                // Send body
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(body)
                    writer.flush()
                }
                
                // Get response
                val responseCode = connection.responseCode
                val responseTime = System.currentTimeMillis() - startTime
                
                Log.d(TAG, "📨 Response received: HTTP $responseCode in ${responseTime}ms")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        reader.readText()
                    }
                    
                    Log.d(TAG, "✅ Success: ${response.length} chars received")
                    
                    // Return success response
                    val result = Arguments.createMap().apply {
                        putInt("status", responseCode)
                        putString("data", response)
                        putDouble("responseTime", responseTime.toDouble())
                        putBoolean("success", true)
                    }
                    
                    promise.resolve(result)
                    
                } else {
                    // Handle error response
                    val errorResponse = try {
                        BufferedReader(InputStreamReader(connection.errorStream)).use { reader ->
                            reader.readText()
                        }
                    } catch (e: Exception) {
                        "No error details available"
                    }
                    
                    Log.e(TAG, "❌ HTTP Error $responseCode: $errorResponse")
                    
                    val result = Arguments.createMap().apply {
                        putInt("status", responseCode)
                        putString("error", "HTTP $responseCode: $errorResponse")
                        putDouble("responseTime", responseTime.toDouble())
                        putBoolean("success", false)
                    }
                    
                    promise.resolve(result) // Still resolve, let JS handle the error
                }
                
            } catch (e: Exception) {
                val errorTime = System.currentTimeMillis() - System.currentTimeMillis()
                Log.e(TAG, "❌ Network error: ${e.message}")
                
                val result = Arguments.createMap().apply {
                    putString("error", e.message ?: "Unknown network error")
                    putDouble("responseTime", errorTime.toDouble())
                    putBoolean("success", false)
                    putInt("status", -1)
                }
                
                promise.resolve(result) // Resolve with error info instead of rejecting
            }
        }
    }
    
    @ReactMethod
    fun get(
        url: String,
        headers: ReadableMap?,
        timeoutMs: Int,
        promise: Promise
    ) {
        Log.d(TAG, "🌐 Making GET request to: $url")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startTime = System.currentTimeMillis()
                
                val connection = URL(url).openConnection() as HttpURLConnection
                
                // Set timeouts
                connection.connectTimeout = timeoutMs
                connection.readTimeout = timeoutMs
                
                // Set method
                connection.requestMethod = "GET"
                
                // Add custom headers if provided
                headers?.let { headerMap ->
                    val iterator = headerMap.keySetIterator()
                    while (iterator.hasNextKey()) {
                        val key = iterator.nextKey()
                        val value = headerMap.getString(key)
                        if (value != null) {
                            connection.setRequestProperty(key, value)
                        }
                    }
                }
                
                // Get response
                val responseCode = connection.responseCode
                val responseTime = System.currentTimeMillis() - startTime
                
                Log.d(TAG, "📨 GET Response: HTTP $responseCode in ${responseTime}ms")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        reader.readText()
                    }
                    
                    val result = Arguments.createMap().apply {
                        putInt("status", responseCode)
                        putString("data", response)
                        putDouble("responseTime", responseTime.toDouble())
                        putBoolean("success", true)
                    }
                    
                    promise.resolve(result)
                    
                } else {
                    val errorResponse = try {
                        BufferedReader(InputStreamReader(connection.errorStream)).use { reader ->
                            reader.readText()
                        }
                    } catch (e: Exception) {
                        "No error details available"
                    }
                    
                    val result = Arguments.createMap().apply {
                        putInt("status", responseCode)
                        putString("error", "HTTP $responseCode: $errorResponse")
                        putDouble("responseTime", responseTime.toDouble())
                        putBoolean("success", false)
                    }
                    
                    promise.resolve(result)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ GET Network error: ${e.message}")
                
                val result = Arguments.createMap().apply {
                    putString("error", e.message ?: "Unknown network error")
                    putBoolean("success", false)
                    putInt("status", -1)
                }
                
                promise.resolve(result)
            }
        }
    }
}