package com.allot.detection

import org.json.JSONObject

/**
 * FrameFingerprint - Lightweight visual hash representing screen capture frame characteristics
 * 
 * This data class stores a perceptual hash of a frame along with metadata for fast similarity
 * comparison and temporal tracking. Used by the Smart Content Detection System to identify
 * duplicate or similar content without expensive re-analysis.
 * 
 * Performance Requirements:
 * - Generation: <5ms per frame
 * - Comparison: <1ms per check
 * - Memory: <100 bytes per fingerprint
 */
data class FrameFingerprint(
    /**
     * 64-bit perceptual hash generated using dHash algorithm
     * Represents visual characteristics of the frame for fast comparison
     */
    val hash: Long,
    
    /**
     * Timestamp when the frame was captured (System.currentTimeMillis())
     * Used for temporal analysis and buffer expiration
     */
    val timestamp: Long,
    
    /**
     * Original frame width in pixels
     * Used for validation and scaling calculations
     */
    val width: Int,
    
    /**
     * Original frame height in pixels
     * Used for validation and scaling calculations
     */
    val height: Int,
    
    /**
     * Estimated text density (0.0 to 1.0)
     * Higher values indicate more text content, used for analysis prioritization
     * Default: 0f (no text density calculated)
     */
    val textDensity: Float = 0f,
    
    /**
     * Optional metadata for debugging and analysis
     * Can store additional frame characteristics or processing info
     */
    val metadata: Map<String, String> = emptyMap()
) {
    
    /**
     * Calculate age of this fingerprint in milliseconds
     * @return Age in milliseconds from current time
     */
    fun getAgeMs(): Long = System.currentTimeMillis() - timestamp
    
    /**
     * Check if this fingerprint has expired based on given threshold
     * @param maxAgeMs Maximum age in milliseconds before expiration
     * @return true if fingerprint is expired
     */
    fun isExpired(maxAgeMs: Long): Boolean = getAgeMs() > maxAgeMs
    
    /**
     * Get frame aspect ratio
     * @return Width/height ratio, or 1.0 if height is 0
     */
    fun getAspectRatio(): Float = if (height > 0) width.toFloat() / height else 1.0f
    
    /**
     * Get total pixel count
     * @return Total pixels in the frame
     */
    fun getPixelCount(): Long = width.toLong() * height
    
    /**
     * Convert fingerprint to JSON for serialization/debugging
     * @return JSONObject representation
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("hash", hash)
            put("timestamp", timestamp)
            put("width", width)
            put("height", height)
            put("textDensity", textDensity.toDouble())
            put("ageMs", getAgeMs())
            put("aspectRatio", getAspectRatio().toDouble())
            put("pixelCount", getPixelCount())
            
            // Add metadata if present
            if (metadata.isNotEmpty()) {
                val metadataJson = JSONObject()
                metadata.forEach { (key, value) ->
                    metadataJson.put(key, value)
                }
                put("metadata", metadataJson)
            }
        }
    }
    
    /**
     * Create a compact string representation for logging
     * @return Compact string with key information
     */
    fun toCompactString(): String {
        return "FP[hash=${hash.toString(16).takeLast(8)}, " +
                "age=${getAgeMs()}ms, " +
                "size=${width}x${height}, " +
                "text=${String.format("%.2f", textDensity)}]"
    }
    
    companion object {
        /**
         * Create FrameFingerprint from JSON
         * @param json JSONObject containing fingerprint data
         * @return FrameFingerprint instance or null if parsing fails
         */
        fun fromJson(json: JSONObject): FrameFingerprint? {
            return try {
                val metadata = mutableMapOf<String, String>()
                
                // Parse metadata if present
                if (json.has("metadata")) {
                    val metadataJson = json.getJSONObject("metadata")
                    metadataJson.keys().forEach { key ->
                        metadata[key] = metadataJson.getString(key)
                    }
                }
                
                FrameFingerprint(
                    hash = json.getLong("hash"),
                    timestamp = json.getLong("timestamp"),
                    width = json.getInt("width"),
                    height = json.getInt("height"),
                    textDensity = json.optDouble("textDensity", 0.0).toFloat(),
                    metadata = metadata
                )
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Create an invalid/empty fingerprint for error cases
         * @return FrameFingerprint with zero values
         */
        fun createEmpty(): FrameFingerprint {
            return FrameFingerprint(
                hash = 0L,
                timestamp = 0L,
                width = 0,
                height = 0,
                textDensity = 0f,
                metadata = mapOf("type" to "empty")
            )
        }
        
        /**
         * Validate fingerprint data integrity
         * @param fingerprint FrameFingerprint to validate
         * @return true if fingerprint appears valid
         */
        fun isValid(fingerprint: FrameFingerprint): Boolean {
            return fingerprint.width > 0 &&
                    fingerprint.height > 0 &&
                    fingerprint.timestamp > 0 &&
                    fingerprint.textDensity >= 0f &&
                    fingerprint.textDensity <= 1f
        }
    }
}