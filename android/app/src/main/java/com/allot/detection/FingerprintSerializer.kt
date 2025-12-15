package com.allot.detection

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * FingerprintSerializer - Handles serialization and caching of FrameFingerprint data
 * 
 * Provides efficient serialization for debugging, caching, and persistence of fingerprint data.
 * Supports both SharedPreferences for small datasets and file-based storage for larger collections.
 * 
 * Performance Requirements:
 * - Serialization: <10ms for collections up to 100 fingerprints
 * - Deserialization: <20ms for collections up to 100 fingerprints
 * - Memory efficient: Streaming for large datasets
 */
object FingerprintSerializer {
    
    private const val PREFS_NAME = "fingerprint_cache"
    private const val CACHE_DIR = "fingerprint_data"
    private const val MAX_PREFS_SIZE = 50 // Maximum fingerprints to store in SharedPreferences
    
    /**
     * Serialize a single fingerprint to JSON string
     * 
     * @param fingerprint FrameFingerprint to serialize
     * @return JSON string representation
     */
    fun serializeFingerprint(fingerprint: FrameFingerprint): String {
        return fingerprint.toJson().toString()
    }
    
    /**
     * Deserialize a single fingerprint from JSON string
     * 
     * @param json JSON string representation
     * @return FrameFingerprint or null if parsing fails
     */
    fun deserializeFingerprint(json: String): FrameFingerprint? {
        return try {
            val jsonObject = JSONObject(json)
            FrameFingerprint.fromJson(jsonObject)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Serialize a collection of fingerprints to JSON array string
     * 
     * @param fingerprints Collection of fingerprints to serialize
     * @return JSON array string
     */
    fun serializeFingerprintCollection(fingerprints: Collection<FrameFingerprint>): String {
        val jsonArray = JSONArray()
        fingerprints.forEach { fingerprint ->
            jsonArray.put(fingerprint.toJson())
        }
        return jsonArray.toString()
    }
    
    /**
     * Deserialize a collection of fingerprints from JSON array string
     * 
     * @param json JSON array string
     * @return List of FrameFingerprint (invalid entries are skipped)
     */
    fun deserializeFingerprintCollection(json: String): List<FrameFingerprint> {
        return try {
            val jsonArray = JSONArray(json)
            val fingerprints = mutableListOf<FrameFingerprint>()
            
            for (i in 0 until jsonArray.length()) {
                val fingerprintJson = jsonArray.getJSONObject(i)
                FrameFingerprint.fromJson(fingerprintJson)?.let { fingerprint ->
                    fingerprints.add(fingerprint)
                }
            }
            
            fingerprints
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Save fingerprints to SharedPreferences (for small collections)
     * 
     * @param context Android context
     * @param key Storage key
     * @param fingerprints Collection to save (limited to MAX_PREFS_SIZE)
     * @return true if saved successfully
     */
    fun saveFingerprintsToPrefs(
        context: Context,
        key: String,
        fingerprints: Collection<FrameFingerprint>
    ): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val limitedFingerprints = fingerprints.take(MAX_PREFS_SIZE)
            val json = serializeFingerprintCollection(limitedFingerprints)
            
            prefs.edit()
                .putString(key, json)
                .putLong("${key}_timestamp", System.currentTimeMillis())
                .putInt("${key}_count", limitedFingerprints.size)
                .apply()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Load fingerprints from SharedPreferences
     * 
     * @param context Android context
     * @param key Storage key
     * @return List of FrameFingerprint or empty list if not found/error
     */
    fun loadFingerprintsFromPrefs(context: Context, key: String): List<FrameFingerprint> {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(key, null) ?: return emptyList()
            deserializeFingerprintCollection(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Save fingerprints to file (for larger collections)
     * 
     * @param context Android context
     * @param filename File name (without extension)
     * @param fingerprints Collection to save
     * @return true if saved successfully
     */
    fun saveFingerprintsToFile(
        context: Context,
        filename: String,
        fingerprints: Collection<FrameFingerprint>
    ): Boolean {
        return try {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            
            val file = File(cacheDir, "$filename.json")
            val json = serializeFingerprintCollection(fingerprints)
            
            FileWriter(file).use { writer ->
                writer.write(json)
            }
            
            true
        } catch (e: IOException) {
            false
        }
    }
    
    /**
     * Load fingerprints from file
     * 
     * @param context Android context
     * @param filename File name (without extension)
     * @return List of FrameFingerprint or empty list if not found/error
     */
    fun loadFingerprintsFromFile(context: Context, filename: String): List<FrameFingerprint> {
        return try {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            val file = File(cacheDir, "$filename.json")
            
            if (!file.exists()) return emptyList()
            
            val json = file.readText()
            deserializeFingerprintCollection(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get cache metadata from SharedPreferences
     * 
     * @param context Android context
     * @param key Storage key
     * @return CacheMetadata or null if not found
     */
    fun getCacheMetadata(context: Context, key: String): CacheMetadata? {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val timestamp = prefs.getLong("${key}_timestamp", 0L)
            val count = prefs.getInt("${key}_count", 0)
            
            if (timestamp > 0) {
                CacheMetadata(
                    key = key,
                    timestamp = timestamp,
                    count = count,
                    ageMs = System.currentTimeMillis() - timestamp
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Clear cached fingerprints
     * 
     * @param context Android context
     * @param key Storage key (null to clear all)
     */
    fun clearCache(context: Context, key: String? = null) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            
            if (key != null) {
                // Clear specific key
                editor.remove(key)
                editor.remove("${key}_timestamp")
                editor.remove("${key}_count")
            } else {
                // Clear all
                editor.clear()
            }
            
            editor.apply()
            
            // Also clear file cache
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            if (cacheDir.exists()) {
                if (key != null) {
                    val file = File(cacheDir, "$key.json")
                    file.delete()
                } else {
                    cacheDir.listFiles()?.forEach { it.delete() }
                }
            }
        } catch (e: Exception) {
            // Ignore errors during cleanup
        }
    }
    
    /**
     * Get all cached keys
     * 
     * @param context Android context
     * @return Set of cached keys
     */
    fun getCachedKeys(context: Context): Set<String> {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.all.keys
                .filter { !it.endsWith("_timestamp") && !it.endsWith("_count") }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    /**
     * Export fingerprints to debug format
     * Creates a human-readable format for debugging and analysis
     * 
     * @param fingerprints Collection to export
     * @param includeMetadata Whether to include detailed metadata
     * @return Debug string representation
     */
    fun exportToDebugFormat(
        fingerprints: Collection<FrameFingerprint>,
        includeMetadata: Boolean = true
    ): String {
        val sb = StringBuilder()
        sb.appendLine("=== FrameFingerprint Debug Export ===")
        sb.appendLine("Count: ${fingerprints.size}")
        sb.appendLine("Generated: ${System.currentTimeMillis()}")
        sb.appendLine()
        
        if (fingerprints.isNotEmpty()) {
            val sortedFingerprints = fingerprints.sortedBy { it.timestamp }
            val timeSpan = sortedFingerprints.last().timestamp - sortedFingerprints.first().timestamp
            val avgSimilarity = FingerprintUtils.calculateAverageSimilarity(fingerprints)
            
            sb.appendLine("Time Span: ${timeSpan}ms")
            sb.appendLine("Average Similarity: ${String.format("%.3f", avgSimilarity)}")
            sb.appendLine()
            
            sortedFingerprints.forEachIndexed { index, fp ->
                sb.appendLine("[$index] ${fp.toCompactString()}")
                
                if (includeMetadata && fp.metadata.isNotEmpty()) {
                    fp.metadata.forEach { (key, value) ->
                        sb.appendLine("    $key: $value")
                    }
                }
                
                if (index > 0) {
                    val similarity = FingerprintUtils.calculateSimilarity(
                        sortedFingerprints[index - 1], 
                        fp
                    )
                    sb.appendLine("    Similarity to previous: ${String.format("%.3f", similarity)}")
                }
                
                sb.appendLine()
            }
        }
        
        return sb.toString()
    }
}

/**
 * Metadata about cached fingerprint data
 */
data class CacheMetadata(
    val key: String,
    val timestamp: Long,
    val count: Int,
    val ageMs: Long
) {
    fun isExpired(maxAgeMs: Long): Boolean = ageMs > maxAgeMs
    
    override fun toString(): String {
        return "CacheMetadata(key='$key', count=$count, age=${ageMs}ms)"
    }
}