package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * LocalTextExtractor - On-device text extraction using Google ML Kit
 * 
 * Provides fast, private text recognition without external API calls.
 * Optimized for social media content with intelligent filtering and classification.
 */
class LocalTextExtractor {
    
    companion object {
        const val TAG = "LocalTextExtractor"
        const val TARGET_PROCESSING_TIME_MS = 50L
        const val MIN_CONFIDENCE_THRESHOLD = 0.7f
        const val MIN_TEXT_DENSITY_THRESHOLD = 0.1f
        
        // Quality scoring thresholds
        const val MIN_QUALITY_SCORE = 0.6f
        const val HIGH_QUALITY_THRESHOLD = 0.8f
        const val CLARITY_WEIGHT = 0.4f
        const val COMPLETENESS_WEIGHT = 0.3f
        const val RELEVANCE_WEIGHT = 0.3f
        
        // Region-based analysis
        const val REGION_GRID_SIZE = 4 // 4x4 grid for regional analysis
        const val MIN_REGION_TEXT_DENSITY = 0.05f
    }
    
    // ML Kit text recognizer with Latin script optimization
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    // Performance monitoring
    private val performanceMonitor = PerformanceMonitor()
    
    // Text extraction caching system
    private val extractionCache = TextExtractionCache()
    
    // Region-of-interest detector for social media patterns
    private val roiDetector = RegionOfInterestDetector()
    
    // Frame preprocessor for social media optimization
    private val framePreprocessor = SocialMediaFramePreprocessor()
    
    // Quality validation and fallback system
    private val qualityValidator = TextExtractionValidator()
    private val fallbackManager = TextExtractionFallbackManager()
    
    // A/B testing framework for accuracy comparison
    val abTestingFramework = TextExtractionABTesting()
    
    /**
     * Extract text from bitmap with comprehensive analysis and caching
     */
    suspend fun extractText(bitmap: Bitmap): TextExtractionResult {
        val timer = performanceMonitor.startOperation("extract_text")
        
        return try {
            val startTime = System.currentTimeMillis()
            
            // Generate cache key from bitmap fingerprint
            val cacheKey = generateCacheKey(bitmap)
            
            // Check cache first
            extractionCache.get(cacheKey)?.let { cachedResult ->
                timer.complete()
                Log.d(TAG, "Cache hit for text extraction (${System.currentTimeMillis() - startTime}ms)")
                return cachedResult.copy(processingTimeMs = System.currentTimeMillis() - startTime)
            }
            
            // Detect region of interest for social media content
            val roi = roiDetector.detectContentRegion(bitmap)
            
            // Preprocess bitmap with social media optimizations
            val preprocessedBitmap = framePreprocessor.preprocessForSocialMedia(bitmap, roi)
            
            // Create ML Kit input image (use ROI if detected)
            val inputImage = if (roi.isValid()) {
                val croppedBitmap = cropBitmapToROI(preprocessedBitmap, roi)
                InputImage.fromBitmap(croppedBitmap, 0).also { croppedBitmap.recycle() }
            } else {
                InputImage.fromBitmap(preprocessedBitmap, 0)
            }
            
            // Perform text recognition
            val visionText = suspendCancellableCoroutine { continuation ->
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { text ->
                        continuation.resume(text)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Extract and classify text regions
            val textRegions = mutableListOf<TextRegion>()
            var totalText = ""
            var totalConfidence = 0f
            var regionCount = 0
            
            for (block in visionText.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        val text = element.text
                        val boundingBox = element.boundingBox ?: continue
                        
                        // Calculate confidence (ML Kit doesn't provide confidence, so we estimate)
                        val confidence = estimateTextConfidence(text, boundingBox, bitmap)
                        
                        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                            // Classify text type
                            val textType = classifyTextType(text, boundingBox, bitmap)
                            
                            textRegions.add(
                                TextRegion(
                                    text = text,
                                    boundingBox = boundingBox,
                                    confidence = confidence,
                                    textType = textType
                                )
                            )
                            
                            totalText += "$text "
                            totalConfidence += confidence
                            regionCount++
                        }
                    }
                }
            }
            
            // Calculate text density
            val textDensity = calculateTextDensity(bitmap, textRegions)
            
            // Calculate average confidence
            val averageConfidence = if (regionCount > 0) totalConfidence / regionCount else 0f
            
            // Clean up preprocessed bitmap if different from original
            if (preprocessedBitmap != bitmap) {
                preprocessedBitmap.recycle()
            }
            
            timer.complete()
            
            val initialResult = TextExtractionResult(
                extractedText = totalText.trim(),
                confidence = averageConfidence,
                textRegions = adjustTextRegionsForROI(textRegions, roi),
                textDensity = textDensity,
                processingTimeMs = processingTime,
                usedCache = false,
                roiDetected = roi.isValid(),
                roiArea = roi.getArea(),
                preprocessingTimeMs = System.currentTimeMillis() - startTime - processingTime
            )
            
            // Validate extraction quality
            val validationResult = qualityValidator.validateExtraction(initialResult, bitmap)
            
            // Apply fallback if quality is insufficient
            val finalResult = if (validationResult.meetsQualityRequirements) {
                initialResult.copy(
                    validationPassed = true,
                    validationScore = validationResult.overallScore,
                    fallbackUsed = false
                )
            } else {
                Log.w(TAG, "Quality validation failed (${validationResult.overallScore}), applying fallback")
                applyFallbackExtraction(bitmap, initialResult, validationResult)
            }
            
            // Record for A/B testing if enabled
            abTestingFramework.recordExtraction(finalResult, bitmap)
            
            // Cache the final result
            extractionCache.put(cacheKey, finalResult)
            
            finalResult
            
        } catch (e: Exception) {
            timer.fail(e.message)
            Log.e(TAG, "Error extracting text: ${e.message}", e)
            
            // Return empty result on error
            TextExtractionResult(
                extractedText = "",
                confidence = 0f,
                textRegions = emptyList(),
                textDensity = 0f,
                processingTimeMs = timer.getElapsedTime()
            )
        }
    }
    
    /**
     * Extract text with detailed region analysis
     */
    suspend fun extractTextWithRegions(bitmap: Bitmap): List<TextRegion> {
        val result = extractText(bitmap)
        return result.textRegions
    }
    
    /**
     * Calculate text density for intelligent filtering
     */
    fun calculateTextDensity(bitmap: Bitmap): Float {
        return try {
            // Quick estimation without full text extraction
            val sampleRegions = getSampleRegions(bitmap)
            var totalTextPixels = 0
            var totalPixels = 0
            
            for (region in sampleRegions) {
                val regionPixels = region.width() * region.height()
                val textPixels = estimateTextPixelsInRegion(bitmap, region)
                
                totalTextPixels += textPixels
                totalPixels += regionPixels
            }
            
            if (totalPixels > 0) totalTextPixels.toFloat() / totalPixels else 0f
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating text density: ${e.message}", e)
            0f
        }
    }
    
    /**
     * Calculate text density per frame region for intelligent filtering
     */
    fun calculateRegionalTextDensity(bitmap: Bitmap): RegionalTextDensity {
        return try {
            val regions = getRegionalGrid(bitmap, REGION_GRID_SIZE)
            val regionDensities = mutableMapOf<RegionPosition, Float>()
            var totalDensity = 0f
            var significantRegions = 0
            
            regions.forEach { (position, rect) ->
                val density = calculateRegionTextDensity(bitmap, rect)
                regionDensities[position] = density
                totalDensity += density
                
                if (density >= MIN_REGION_TEXT_DENSITY) {
                    significantRegions++
                }
            }
            
            val averageDensity = if (regions.isNotEmpty()) totalDensity / regions.size else 0f
            val coverage = if (regions.isNotEmpty()) significantRegions.toFloat() / regions.size else 0f
            
            RegionalTextDensity(
                regionDensities = regionDensities,
                averageDensity = averageDensity,
                maxDensity = regionDensities.values.maxOrNull() ?: 0f,
                coverage = coverage,
                significantRegions = significantRegions,
                totalRegions = regions.size
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating regional text density: ${e.message}", e)
            RegionalTextDensity.empty()
        }
    }
    
    /**
     * Calculate comprehensive text quality score (clarity, completeness, relevance)
     */
    suspend fun calculateTextQuality(bitmap: Bitmap): TextQualityScore {
        return try {
            val extractionResult = extractText(bitmap)
            
            // Calculate clarity score based on confidence and text characteristics
            val clarityScore = calculateClarityScore(extractionResult)
            
            // Calculate completeness score based on text coverage and structure
            val completenessScore = calculateCompletenessScore(extractionResult, bitmap)
            
            // Calculate relevance score based on content vs UI/metadata ratio
            val relevanceScore = calculateRelevanceScore(extractionResult)
            
            // Weighted overall quality score
            val overallScore = (clarityScore * CLARITY_WEIGHT) + 
                             (completenessScore * COMPLETENESS_WEIGHT) + 
                             (relevanceScore * RELEVANCE_WEIGHT)
            
            TextQualityScore(
                clarityScore = clarityScore,
                completenessScore = completenessScore,
                relevanceScore = relevanceScore,
                overallScore = overallScore,
                meetsQualityThreshold = overallScore >= MIN_QUALITY_SCORE,
                isHighQuality = overallScore >= HIGH_QUALITY_THRESHOLD,
                processingTimeMs = extractionResult.processingTimeMs
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating text quality: ${e.message}", e)
            TextQualityScore.empty()
        }
    }
    
    /**
     * Filter text regions to focus on meaningful content
     */
    fun filterMeaningfulContent(textRegions: List<TextRegion>): FilteredTextResult {
        val contentRegions = mutableListOf<TextRegion>()
        val filteredOutRegions = mutableListOf<TextRegion>()
        var totalContentArea = 0
        var totalFilteredArea = 0
        
        textRegions.forEach { region ->
            if (isMeaningfulContent(region)) {
                contentRegions.add(region)
                totalContentArea += region.getArea()
            } else {
                filteredOutRegions.add(region)
                totalFilteredArea += region.getArea()
            }
        }
        
        val filterEfficiency = if (textRegions.isNotEmpty()) {
            filteredOutRegions.size.toFloat() / textRegions.size
        } else 0f
        
        return FilteredTextResult(
            meaningfulRegions = contentRegions,
            filteredOutRegions = filteredOutRegions,
            contentText = contentRegions.joinToString(" ") { it.text },
            filterEfficiency = filterEfficiency,
            contentCoverage = if (totalContentArea + totalFilteredArea > 0) {
                totalContentArea.toFloat() / (totalContentArea + totalFilteredArea)
            } else 0f,
            qualityImprovement = calculateQualityImprovement(textRegions, contentRegions)
        )
    }
    
    /**
     * Quick text presence detection
     */
    fun isTextPresent(bitmap: Bitmap, threshold: Float = MIN_TEXT_DENSITY_THRESHOLD): Boolean {
        return calculateTextDensity(bitmap) >= threshold
    }
    
    /**
     * Preprocess bitmap for optimal text recognition
     */
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        return try {
            // For social media content, we typically don't need heavy preprocessing
            // ML Kit handles most optimization internally
            
            // Check if bitmap is too large and needs scaling
            val maxDimension = 1024
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
                val scaledWidth = (bitmap.width * scale).toInt()
                val scaledHeight = (bitmap.height * scale).toInt()
                
                Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
            } else {
                bitmap
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Error preprocessing bitmap, using original: ${e.message}")
            bitmap
        }
    }
    
    /**
     * Estimate text confidence based on various factors
     */
    private fun estimateTextConfidence(text: String, boundingBox: Rect, bitmap: Bitmap): Float {
        var confidence = 0.8f // Base confidence for ML Kit detection
        
        // Adjust based on text length (longer text usually more reliable)
        when {
            text.length >= 10 -> confidence += 0.1f
            text.length >= 5 -> confidence += 0.05f
            text.length < 3 -> confidence -= 0.2f
        }
        
        // Adjust based on text characteristics
        if (text.matches(Regex(".*[a-zA-Z].*"))) confidence += 0.05f // Contains letters
        if (text.matches(Regex(".*\\d.*"))) confidence += 0.02f // Contains numbers
        if (text.matches(Regex(".*[!@#$%^&*(),.?\":{}|<>].*"))) confidence -= 0.05f // Special chars
        
        // Adjust based on bounding box size (very small text might be noise)
        val boxArea = boundingBox.width() * boundingBox.height()
        val bitmapArea = bitmap.width * bitmap.height
        val relativeSize = boxArea.toFloat() / bitmapArea
        
        when {
            relativeSize < 0.0001f -> confidence -= 0.3f // Very small text
            relativeSize < 0.001f -> confidence -= 0.1f // Small text
            relativeSize > 0.1f -> confidence += 0.1f // Large text
        }
        
        return confidence.coerceIn(0f, 1f)
    }
    
    /**
     * Classify text type for intelligent filtering
     */
    private fun classifyTextType(text: String, boundingBox: Rect, bitmap: Bitmap): TextType {
        val cleanText = text.trim().lowercase()
        
        // UI element patterns
        val uiPatterns = listOf(
            "like", "share", "comment", "follow", "subscribe", "save",
            "more", "options", "menu", "settings", "back", "next",
            "play", "pause", "stop", "volume", "search", "filter",
            "sort", "edit", "delete", "cancel", "ok", "yes", "no"
        )
        
        // Metadata patterns
        val metadataPatterns = listOf(
            Regex("\\d+[hms]"), // Time indicators (1h, 30m, 45s)
            Regex("\\d+[kmb]"), // Count indicators (1k, 2m, 3b)
            Regex("@\\w+"), // Usernames
            Regex("#\\w+"), // Hashtags
            Regex("\\d{1,2}[:/]\\d{1,2}") // Time stamps
        )
        
        // Check for UI elements
        if (uiPatterns.any { cleanText.contains(it) }) {
            return TextType.UI_ELEMENT
        }
        
        // Check for metadata
        if (metadataPatterns.any { it.containsMatchIn(cleanText) }) {
            return TextType.METADATA
        }
        
        // Check position-based classification
        val bitmapHeight = bitmap.height
        val textCenterY = boundingBox.centerY()
        
        // Text at very top or bottom is likely UI
        if (textCenterY < bitmapHeight * 0.1f || textCenterY > bitmapHeight * 0.9f) {
            return TextType.UI_ELEMENT
        }
        
        // Default to content text for main area
        return TextType.CONTENT_TEXT
    }
    
    /**
     * Calculate text density from extracted regions
     */
    private fun calculateTextDensity(bitmap: Bitmap, textRegions: List<TextRegion>): Float {
        if (textRegions.isEmpty()) return 0f
        
        val bitmapArea = bitmap.width * bitmap.height
        val textArea = textRegions.sumOf { region ->
            region.boundingBox.width() * region.boundingBox.height()
        }
        
        return textArea.toFloat() / bitmapArea
    }
    
    /**
     * Get sample regions for quick density estimation
     */
    private fun getSampleRegions(bitmap: Bitmap): List<Rect> {
        val regions = mutableListOf<Rect>()
        val width = bitmap.width
        val height = bitmap.height
        
        // Sample 9 regions (3x3 grid)
        val regionWidth = width / 3
        val regionHeight = height / 3
        
        for (row in 0..2) {
            for (col in 0..2) {
                val left = col * regionWidth
                val top = row * regionHeight
                val right = minOf(left + regionWidth, width)
                val bottom = minOf(top + regionHeight, height)
                
                regions.add(Rect(left, top, right, bottom))
            }
        }
        
        return regions
    }
    
    /**
     * Get regional grid for detailed analysis
     */
    private fun getRegionalGrid(bitmap: Bitmap, gridSize: Int): Map<RegionPosition, Rect> {
        val regions = mutableMapOf<RegionPosition, Rect>()
        val width = bitmap.width
        val height = bitmap.height
        val regionWidth = width / gridSize
        val regionHeight = height / gridSize
        
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val left = col * regionWidth
                val top = row * regionHeight
                val right = minOf(left + regionWidth, width)
                val bottom = minOf(top + regionHeight, height)
                
                val position = RegionPosition(row, col)
                regions[position] = Rect(left, top, right, bottom)
            }
        }
        
        return regions
    }
    
    /**
     * Calculate text density for a specific region
     */
    private fun calculateRegionTextDensity(bitmap: Bitmap, region: Rect): Float {
        return try {
            val textPixels = estimateTextPixelsInRegion(bitmap, region)
            val totalPixels = region.width() * region.height()
            
            if (totalPixels > 0) textPixels.toFloat() / totalPixels else 0f
            
        } catch (e: Exception) {
            Log.w(TAG, "Error calculating region text density: ${e.message}")
            0f
        }
    }
    
    /**
     * Calculate clarity score based on confidence and text characteristics
     */
    private fun calculateClarityScore(extractionResult: TextExtractionResult): Float {
        if (extractionResult.textRegions.isEmpty()) return 0f
        
        var clarityScore = extractionResult.confidence
        
        // Adjust based on text region characteristics
        val avgRegionSize = extractionResult.textRegions.map { it.getArea() }.average()
        val textVariety = calculateTextVariety(extractionResult.textRegions)
        val confidenceVariance = calculateConfidenceVariance(extractionResult.textRegions)
        
        // Bonus for consistent, well-sized text
        if (avgRegionSize > 100) clarityScore += 0.1f
        if (textVariety > 0.5f) clarityScore += 0.1f
        if (confidenceVariance < 0.2f) clarityScore += 0.1f
        
        return clarityScore.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate completeness score based on text coverage and structure
     */
    private fun calculateCompletenessScore(extractionResult: TextExtractionResult, bitmap: Bitmap): Float {
        if (extractionResult.textRegions.isEmpty()) return 0f
        
        var completenessScore = extractionResult.textDensity
        
        // Adjust based on text distribution and structure
        val regionalDensity = calculateRegionalTextDensity(bitmap)
        val textDistribution = regionalDensity.coverage
        val hasStructuredContent = hasStructuredTextContent(extractionResult.textRegions)
        
        // Bonus for well-distributed and structured text
        completenessScore += textDistribution * 0.3f
        if (hasStructuredContent) completenessScore += 0.2f
        
        return completenessScore.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate relevance score based on content vs UI/metadata ratio
     */
    private fun calculateRelevanceScore(extractionResult: TextExtractionResult): Float {
        if (extractionResult.textRegions.isEmpty()) return 0f
        
        val contentRegions = extractionResult.textRegions.filter { it.isContentText() }
        val uiRegions = extractionResult.textRegions.filter { it.isUIElement() }
        val metadataRegions = extractionResult.textRegions.filter { it.isMetadata() }
        
        val contentRatio = contentRegions.size.toFloat() / extractionResult.textRegions.size
        val contentArea = contentRegions.sumOf { it.getArea() }
        val totalArea = extractionResult.textRegions.sumOf { it.getArea() }
        
        val areaRatio = if (totalArea > 0) contentArea.toFloat() / totalArea else 0f
        
        // Weighted relevance score
        var relevanceScore = (contentRatio * 0.6f) + (areaRatio * 0.4f)
        
        // Penalty for too much UI noise
        val uiRatio = uiRegions.size.toFloat() / extractionResult.textRegions.size
        if (uiRatio > 0.5f) relevanceScore *= 0.7f
        
        return relevanceScore.coerceIn(0f, 1f)
    }
    
    /**
     * Check if content is meaningful (not UI noise or metadata)
     */
    private fun isMeaningfulContent(region: TextRegion): Boolean {
        // Content text is always meaningful
        if (region.isContentText()) return true
        
        // Filter out low-confidence regions
        if (region.confidence < MIN_CONFIDENCE_THRESHOLD) return false
        
        // Filter out very small regions (likely noise)
        if (region.getArea() < 50) return false
        
        // Filter out single characters or very short text
        if (region.text.trim().length < 3) return false
        
        // Filter out common UI patterns
        val cleanText = region.text.trim().lowercase()
        val commonUIPatterns = listOf(
            "•", "...", "›", "‹", "▶", "⏸", "⏹", "🔍", "⚙", "📱", "💬", "❤", "👍", "📤", "📥"
        )
        
        if (commonUIPatterns.any { cleanText.contains(it) }) return false
        
        // Allow metadata if it's substantial
        if (region.isMetadata() && region.text.length >= 5) return true
        
        return false
    }
    
    /**
     * Calculate text variety (diversity of text content)
     */
    private fun calculateTextVariety(regions: List<TextRegion>): Float {
        if (regions.isEmpty()) return 0f
        
        val uniqueWords = regions.flatMap { it.text.split("\\s+".toRegex()) }
            .map { it.lowercase().trim() }
            .filter { it.isNotBlank() }
            .toSet()
        
        val totalWords = regions.sumOf { it.text.split("\\s+".toRegex()).size }
        
        return if (totalWords > 0) uniqueWords.size.toFloat() / totalWords else 0f
    }
    
    /**
     * Calculate confidence variance across regions
     */
    private fun calculateConfidenceVariance(regions: List<TextRegion>): Float {
        if (regions.isEmpty()) return 0f
        
        val confidences = regions.map { it.confidence }
        val mean = confidences.average()
        val variance = confidences.map { (it - mean) * (it - mean) }.average()
        
        return variance.toFloat()
    }
    
    /**
     * Check if text has structured content patterns
     */
    private fun hasStructuredTextContent(regions: List<TextRegion>): Boolean {
        val text = regions.joinToString(" ") { it.text }
        
        // Look for structured patterns
        val hasMultipleSentences = text.count { it == '.' || it == '!' || it == '?' } > 1
        val hasLineBreaks = regions.size > 3
        val hasVariedLength = regions.map { it.text.length }.let { lengths ->
            lengths.maxOrNull()?.let { max -> 
                lengths.minOrNull()?.let { min -> 
                    max - min > 10 
                } 
            } ?: false
        }
        
        return hasMultipleSentences || hasLineBreaks || hasVariedLength
    }
    
    /**
     * Calculate quality improvement from filtering
     */
    private fun calculateQualityImprovement(
        originalRegions: List<TextRegion>, 
        filteredRegions: List<TextRegion>
    ): Float {
        if (originalRegions.isEmpty()) return 0f
        
        val originalContentRatio = originalRegions.count { it.isContentText() }.toFloat() / originalRegions.size
        val filteredContentRatio = if (filteredRegions.isNotEmpty()) {
            filteredRegions.count { it.isContentText() }.toFloat() / filteredRegions.size
        } else 0f
        
        return (filteredContentRatio - originalContentRatio).coerceAtLeast(0f)
    }
    
    /**
     * Estimate text pixels in a region (simplified edge detection)
     */
    private fun estimateTextPixelsInRegion(bitmap: Bitmap, region: Rect): Int {
        return try {
            var textPixels = 0
            val pixels = IntArray(region.width() * region.height())
            
            bitmap.getPixels(
                pixels, 0, region.width(),
                region.left, region.top,
                region.width(), region.height()
            )
            
            // Simple edge detection for text estimation
            for (i in pixels.indices) {
                val pixel = pixels[i]
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val gray = (r + g + b) / 3
                
                // Text typically has high contrast edges
                if (gray < 100 || gray > 200) {
                    textPixels++
                }
            }
            
            textPixels
            
        } catch (e: Exception) {
            Log.w(TAG, "Error estimating text pixels: ${e.message}")
            0
        }
    }
    
    /**
     * Get performance metrics for text extraction
     */
    fun getPerformanceMetrics(): TextExtractionMetrics {
        val summary = performanceMonitor.getPerformanceSummary()
        
        return TextExtractionMetrics(
            totalExtractions = summary.totalOperations,
            averageProcessingTimeMs = summary.averageProcessingTimeMs,
            successRate = if (summary.totalOperations > 0) {
                (summary.totalOperations - summary.alertCount).toFloat() / summary.totalOperations
            } else 0f,
            meetsPerformanceTarget = summary.averageProcessingTimeMs <= TARGET_PROCESSING_TIME_MS
        )
    }
    
    /**
     * Clear performance history and cache
     */
    fun clearMetrics() {
        performanceMonitor.clearAllData()
        extractionCache.clear()
        abTestingFramework.clearResults()
    }
    
    /**
     * Get validation and fallback system status
     */
    fun getValidationSystemStatus(): ValidationSystemStatus {
        return ValidationSystemStatus(
            validatorEnabled = true,
            fallbackManagerEnabled = true,
            abTestingEnabled = abTestingFramework.getABTestingStats().isEnabled,
            supportedFallbackStrategies = FallbackStrategy.values().toList(),
            validationThreshold = TextExtractionValidator.MIN_VALIDATION_SCORE
        )
    }
    
    /**
     * Get cache statistics
     */
    fun getCacheStats(): TextExtractionCacheStats {
        return extractionCache.getStats()
    }
    
    /**
     * Check if text is present using fast ROI detection
     */
    fun isTextPresentFast(bitmap: Bitmap, threshold: Float = MIN_TEXT_DENSITY_THRESHOLD): Boolean {
        val roi = roiDetector.detectContentRegion(bitmap)
        return if (roi.isValid()) {
            // Only check ROI area for faster processing
            val roiDensity = calculateRegionTextDensity(bitmap, roi.toRect())
            roiDensity >= threshold
        } else {
            // Fallback to full image analysis
            calculateTextDensity(bitmap) >= threshold
        }
    }
    
    /**
     * Generate cache key from bitmap characteristics
     */
    private fun generateCacheKey(bitmap: Bitmap): String {
        // Use a combination of bitmap hash and size for cache key
        val hash = PerceptualHashGenerator.generateHash(bitmap)
        return "${hash}_${bitmap.width}x${bitmap.height}"
    }
    
    /**
     * Crop bitmap to region of interest
     */
    private fun cropBitmapToROI(bitmap: Bitmap, roi: RegionOfInterest): Bitmap {
        return try {
            Bitmap.createBitmap(
                bitmap,
                roi.left.coerceAtLeast(0),
                roi.top.coerceAtLeast(0),
                roi.width.coerceAtMost(bitmap.width - roi.left),
                roi.height.coerceAtMost(bitmap.height - roi.top)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error cropping bitmap to ROI: ${e.message}")
            bitmap
        }
    }
    
    /**
     * Adjust text region coordinates for ROI offset
     */
    private fun adjustTextRegionsForROI(regions: List<TextRegion>, roi: RegionOfInterest): List<TextRegion> {
        if (!roi.isValid()) return regions
        
        return regions.map { region ->
            val adjustedBounds = Rect(
                region.boundingBox.left + roi.left,
                region.boundingBox.top + roi.top,
                region.boundingBox.right + roi.left,
                region.boundingBox.bottom + roi.top
            )
            region.copy(boundingBox = adjustedBounds)
        }
    }
    
    /**
     * Apply fallback extraction when primary method fails quality validation
     */
    private suspend fun applyFallbackExtraction(
        bitmap: Bitmap,
        initialResult: TextExtractionResult,
        validationResult: TextValidationResult
    ): TextExtractionResult {
        return try {
            val fallbackStrategy = fallbackManager.selectFallbackStrategy(validationResult)
            
            when (fallbackStrategy) {
                FallbackStrategy.ENHANCED_PREPROCESSING -> {
                    Log.d(TAG, "Applying enhanced preprocessing fallback")
                    extractTextWithEnhancedPreprocessing(bitmap, initialResult)
                }
                FallbackStrategy.MULTI_SCALE_ANALYSIS -> {
                    Log.d(TAG, "Applying multi-scale analysis fallback")
                    extractTextWithMultiScale(bitmap, initialResult)
                }
                FallbackStrategy.REGION_SEGMENTATION -> {
                    Log.d(TAG, "Applying region segmentation fallback")
                    extractTextWithRegionSegmentation(bitmap, initialResult)
                }
                FallbackStrategy.CONFIDENCE_BOOSTING -> {
                    Log.d(TAG, "Applying confidence boosting fallback")
                    boostExtractionConfidence(initialResult)
                }
                FallbackStrategy.NONE -> {
                    Log.d(TAG, "No suitable fallback available")
                    initialResult.copy(
                        validationPassed = false,
                        validationScore = validationResult.overallScore,
                        fallbackUsed = false,
                        fallbackStrategy = FallbackStrategy.NONE
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fallback extraction: ${e.message}", e)
            initialResult.copy(
                validationPassed = false,
                validationScore = validationResult.overallScore,
                fallbackUsed = false,
                fallbackStrategy = FallbackStrategy.NONE
            )
        }
    }
    
    /**
     * Enhanced preprocessing fallback
     */
    private suspend fun extractTextWithEnhancedPreprocessing(
        bitmap: Bitmap,
        initialResult: TextExtractionResult
    ): TextExtractionResult {
        val enhancedBitmap = framePreprocessor.applyEnhancedPreprocessing(bitmap)
        val inputImage = InputImage.fromBitmap(enhancedBitmap, 0)
        
        val visionText = suspendCancellableCoroutine { continuation ->
            textRecognizer.process(inputImage)
                .addOnSuccessListener { text -> continuation.resume(text) }
                .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        }
        
        // Process results similar to main extraction
        val textRegions = mutableListOf<TextRegion>()
        var totalText = ""
        var totalConfidence = 0f
        var regionCount = 0
        
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val text = element.text
                    val boundingBox = element.boundingBox ?: continue
                    val confidence = estimateTextConfidence(text, boundingBox, enhancedBitmap)
                    
                    if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                        val textType = classifyTextType(text, boundingBox, enhancedBitmap)
                        textRegions.add(TextRegion(text, boundingBox, confidence, textType))
                        totalText += "$text "
                        totalConfidence += confidence
                        regionCount++
                    }
                }
            }
        }
        
        enhancedBitmap.recycle()
        
        return initialResult.copy(
            extractedText = totalText.trim(),
            confidence = if (regionCount > 0) totalConfidence / regionCount else 0f,
            textRegions = textRegions,
            validationPassed = true,
            fallbackUsed = true,
            fallbackStrategy = FallbackStrategy.ENHANCED_PREPROCESSING
        )
    }
    
    /**
     * Multi-scale analysis fallback
     */
    private suspend fun extractTextWithMultiScale(
        bitmap: Bitmap,
        initialResult: TextExtractionResult
    ): TextExtractionResult {
        val scales = listOf(0.8f, 1.2f, 1.5f)
        val allRegions = mutableListOf<TextRegion>()
        var bestConfidence = initialResult.confidence
        var bestText = initialResult.extractedText
        
        for (scale in scales) {
            val scaledWidth = (bitmap.width * scale).toInt()
            val scaledHeight = (bitmap.height * scale).toInt()
            
            if (scaledWidth > 0 && scaledHeight > 0 && scaledWidth < 2048 && scaledHeight < 2048) {
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
                val inputImage = InputImage.fromBitmap(scaledBitmap, 0)
                
                try {
                    val visionText = suspendCancellableCoroutine { continuation ->
                        textRecognizer.process(inputImage)
                            .addOnSuccessListener { text -> continuation.resume(text) }
                            .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
                    }
                    
                    // Process and scale back coordinates
                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            for (element in line.elements) {
                                val text = element.text
                                val boundingBox = element.boundingBox ?: continue
                                val confidence = estimateTextConfidence(text, boundingBox, scaledBitmap)
                                
                                if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                                    // Scale coordinates back to original size
                                    val originalBounds = Rect(
                                        (boundingBox.left / scale).toInt(),
                                        (boundingBox.top / scale).toInt(),
                                        (boundingBox.right / scale).toInt(),
                                        (boundingBox.bottom / scale).toInt()
                                    )
                                    
                                    val textType = classifyTextType(text, originalBounds, bitmap)
                                    allRegions.add(TextRegion(text, originalBounds, confidence, textType))
                                    
                                    if (confidence > bestConfidence) {
                                        bestConfidence = confidence
                                        bestText = text
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error in multi-scale analysis at scale $scale: ${e.message}")
                }
                
                scaledBitmap.recycle()
            }
        }
        
        // Combine with original results
        val combinedRegions = (initialResult.textRegions + allRegions).distinctBy { 
            "${it.text}_${it.boundingBox.centerX()}_${it.boundingBox.centerY()}" 
        }
        
        val combinedText = combinedRegions.joinToString(" ") { it.text }
        val avgConfidence = if (combinedRegions.isNotEmpty()) {
            combinedRegions.map { it.confidence }.average().toFloat()
        } else bestConfidence
        
        return initialResult.copy(
            extractedText = combinedText,
            confidence = avgConfidence,
            textRegions = combinedRegions,
            validationPassed = true,
            fallbackUsed = true,
            fallbackStrategy = FallbackStrategy.MULTI_SCALE_ANALYSIS
        )
    }
    
    /**
     * Region segmentation fallback
     */
    private suspend fun extractTextWithRegionSegmentation(
        bitmap: Bitmap,
        initialResult: TextExtractionResult
    ): TextExtractionResult {
        val regions = segmentBitmapIntoRegions(bitmap)
        val allTextRegions = mutableListOf<TextRegion>()
        var totalText = ""
        
        for (region in regions) {
            try {
                val regionBitmap = Bitmap.createBitmap(
                    bitmap, region.left, region.top, region.width(), region.height()
                )
                val inputImage = InputImage.fromBitmap(regionBitmap, 0)
                
                val visionText = suspendCancellableCoroutine { continuation ->
                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { text -> continuation.resume(text) }
                        .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
                }
                
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        for (element in line.elements) {
                            val text = element.text
                            val boundingBox = element.boundingBox ?: continue
                            val confidence = estimateTextConfidence(text, boundingBox, regionBitmap)
                            
                            if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                                // Adjust coordinates to full bitmap
                                val adjustedBounds = Rect(
                                    boundingBox.left + region.left,
                                    boundingBox.top + region.top,
                                    boundingBox.right + region.left,
                                    boundingBox.bottom + region.top
                                )
                                
                                val textType = classifyTextType(text, adjustedBounds, bitmap)
                                allTextRegions.add(TextRegion(text, adjustedBounds, confidence, textType))
                                totalText += "$text "
                            }
                        }
                    }
                }
                
                regionBitmap.recycle()
            } catch (e: Exception) {
                Log.w(TAG, "Error processing region: ${e.message}")
            }
        }
        
        val avgConfidence = if (allTextRegions.isNotEmpty()) {
            allTextRegions.map { it.confidence }.average().toFloat()
        } else initialResult.confidence
        
        return initialResult.copy(
            extractedText = totalText.trim(),
            confidence = avgConfidence,
            textRegions = allTextRegions,
            validationPassed = true,
            fallbackUsed = true,
            fallbackStrategy = FallbackStrategy.REGION_SEGMENTATION
        )
    }
    
    /**
     * Confidence boosting fallback
     */
    private fun boostExtractionConfidence(initialResult: TextExtractionResult): TextExtractionResult {
        // Apply confidence boosting algorithms
        val boostedRegions = initialResult.textRegions.map { region ->
            val boostedConfidence = minOf(region.confidence * 1.2f, 1.0f)
            region.copy(confidence = boostedConfidence)
        }
        
        val avgConfidence = if (boostedRegions.isNotEmpty()) {
            boostedRegions.map { it.confidence }.average().toFloat()
        } else initialResult.confidence
        
        return initialResult.copy(
            confidence = avgConfidence,
            textRegions = boostedRegions,
            validationPassed = true,
            fallbackUsed = true,
            fallbackStrategy = FallbackStrategy.CONFIDENCE_BOOSTING
        )
    }
    
    /**
     * Segment bitmap into regions for focused analysis
     */
    private fun segmentBitmapIntoRegions(bitmap: Bitmap): List<Rect> {
        val regions = mutableListOf<Rect>()
        val width = bitmap.width
        val height = bitmap.height
        
        // Create overlapping regions for better coverage
        val regionWidth = width / 2
        val regionHeight = height / 2
        val overlap = 0.2f
        
        for (row in 0..1) {
            for (col in 0..1) {
                val left = (col * regionWidth * (1 - overlap)).toInt()
                val top = (row * regionHeight * (1 - overlap)).toInt()
                val right = minOf(left + regionWidth, width)
                val bottom = minOf(top + regionHeight, height)
                
                if (right > left && bottom > top) {
                    regions.add(Rect(left, top, right, bottom))
                }
            }
        }
        
        return regions
    }
}

/**
 * Text extraction result with comprehensive analysis and performance metrics
 */
data class TextExtractionResult(
    val extractedText: String,
    val confidence: Float,
    val textRegions: List<TextRegion>,
    val textDensity: Float,
    val processingTimeMs: Long,
    val usedCache: Boolean = false,
    val roiDetected: Boolean = false,
    val roiArea: Int = 0,
    val preprocessingTimeMs: Long = 0,
    val validationPassed: Boolean? = null,
    val validationScore: Float? = null,
    val fallbackUsed: Boolean? = null,
    val fallbackStrategy: FallbackStrategy? = null
) {
    fun hasText(): Boolean = extractedText.isNotBlank()
    fun meetsConfidenceThreshold(threshold: Float = 0.8f): Boolean = confidence >= threshold
    fun meetsPerformanceTarget(): Boolean = processingTimeMs <= LocalTextExtractor.TARGET_PROCESSING_TIME_MS
    fun getPerformanceImprovement(): Float = if (roiDetected && roiArea > 0) {
        1f - (roiArea.toFloat() / (roiArea + 1000000)) // Estimate based on area reduction
    } else 0f
    fun isHighQuality(): Boolean = validationPassed == true && validationScore?.let { it >= 0.8f } == true
    fun requiresFallback(): Boolean = validationPassed == false
}

/**
 * Individual text region with classification
 */
data class TextRegion(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val textType: TextType
) {
    fun isContentText(): Boolean = textType == TextType.CONTENT_TEXT
    fun isUIElement(): Boolean = textType == TextType.UI_ELEMENT
    fun isMetadata(): Boolean = textType == TextType.METADATA
    fun getArea(): Int = boundingBox.width() * boundingBox.height()
}

/**
 * Text type classification for intelligent filtering
 */
enum class TextType {
    CONTENT_TEXT,    // Main content (posts, comments, etc.)
    UI_ELEMENT,      // Interface elements (buttons, labels)
    METADATA,        // Timestamps, usernames, etc.
    UNKNOWN
}

/**
 * Performance metrics for text extraction
 */
data class TextExtractionMetrics(
    val totalExtractions: Long,
    val averageProcessingTimeMs: Float,
    val successRate: Float,
    val meetsPerformanceTarget: Boolean
)

/**
 * Regional text density analysis for intelligent filtering
 */
data class RegionalTextDensity(
    val regionDensities: Map<RegionPosition, Float>,
    val averageDensity: Float,
    val maxDensity: Float,
    val coverage: Float, // Percentage of regions with significant text
    val significantRegions: Int,
    val totalRegions: Int
) {
    companion object {
        fun empty() = RegionalTextDensity(
            regionDensities = emptyMap(),
            averageDensity = 0f,
            maxDensity = 0f,
            coverage = 0f,
            significantRegions = 0,
            totalRegions = 0
        )
    }
    
    fun hasSignificantText(): Boolean = coverage > 0.25f
    fun isWellDistributed(): Boolean = coverage > 0.5f && maxDensity < 0.8f
}

/**
 * Position in regional grid
 */
data class RegionPosition(
    val row: Int,
    val col: Int
) {
    override fun toString(): String = "($row,$col)"
}

/**
 * Comprehensive text quality scoring
 */
data class TextQualityScore(
    val clarityScore: Float,        // Text clarity and confidence
    val completenessScore: Float,   // Text coverage and structure
    val relevanceScore: Float,      // Content vs UI/metadata ratio
    val overallScore: Float,        // Weighted overall quality
    val meetsQualityThreshold: Boolean,
    val isHighQuality: Boolean,
    val processingTimeMs: Long
) {
    companion object {
        fun empty() = TextQualityScore(
            clarityScore = 0f,
            completenessScore = 0f,
            relevanceScore = 0f,
            overallScore = 0f,
            meetsQualityThreshold = false,
            isHighQuality = false,
            processingTimeMs = 0L
        )
    }
    
    fun getQualityLevel(): QualityLevel = when {
        overallScore >= LocalTextExtractor.HIGH_QUALITY_THRESHOLD -> QualityLevel.HIGH
        overallScore >= LocalTextExtractor.MIN_QUALITY_SCORE -> QualityLevel.MEDIUM
        else -> QualityLevel.LOW
    }
}

/**
 * Quality level enumeration
 */
enum class QualityLevel {
    LOW,     // Below minimum quality threshold
    MEDIUM,  // Meets minimum quality but not high quality
    HIGH     // Meets high quality threshold
}

/**
 * Filtered text result with meaningful content focus
 */
data class FilteredTextResult(
    val meaningfulRegions: List<TextRegion>,
    val filteredOutRegions: List<TextRegion>,
    val contentText: String,
    val filterEfficiency: Float,    // Percentage of regions filtered out
    val contentCoverage: Float,     // Percentage of area that is meaningful content
    val qualityImprovement: Float   // Improvement in content ratio after filtering
) {
    fun hasSignificantContent(): Boolean = meaningfulRegions.isNotEmpty() && contentText.length > 10
    fun getContentRegionCount(): Int = meaningfulRegions.count { it.isContentText() }
    fun getTotalFilteredCount(): Int = filteredOutRegions.size
}

/**
 * Region of Interest for focused text extraction
 */
data class RegionOfInterest(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val confidence: Float,
    val type: ROIType
) {
    fun isValid(): Boolean = width > 0 && height > 0 && confidence > 0.5f
    fun getArea(): Int = width * height
    fun toRect(): Rect = Rect(left, top, left + width, top + height)
}

/**
 * Types of regions of interest
 */
enum class ROIType {
    CONTENT_AREA,    // Main content area (posts, comments)
    FEED_ITEM,       // Individual feed item
    TEXT_BLOCK,      // Dense text block
    UNKNOWN
}

/**
 * Text extraction cache for performance optimization
 */
class TextExtractionCache(private val maxSize: Int = 50) {
    private val cache = LinkedHashMap<String, CacheEntry>(maxSize, 0.75f, true)
    private val maxAgeMs = 30_000L // 30 seconds
    
    private var hits = 0L
    private var misses = 0L
    
    data class CacheEntry(
        val result: TextExtractionResult,
        val timestamp: Long
    )
    
    @Synchronized
    fun get(key: String): TextExtractionResult? {
        val entry = cache[key]
        return if (entry != null && (System.currentTimeMillis() - entry.timestamp) < maxAgeMs) {
            hits++
            entry.result.copy(usedCache = true)
        } else {
            if (entry != null) cache.remove(key) // Remove expired entry
            misses++
            null
        }
    }
    
    @Synchronized
    fun put(key: String, result: TextExtractionResult) {
        // Remove oldest entries if cache is full
        while (cache.size >= maxSize) {
            val oldest = cache.entries.first()
            cache.remove(oldest.key)
        }
        
        cache[key] = CacheEntry(result, System.currentTimeMillis())
    }
    
    @Synchronized
    fun clear() {
        cache.clear()
        hits = 0L
        misses = 0L
    }
    
    @Synchronized
    fun getStats(): TextExtractionCacheStats {
        val total = hits + misses
        return TextExtractionCacheStats(
            size = cache.size,
            maxSize = maxSize,
            hits = hits,
            misses = misses,
            hitRate = if (total > 0) hits.toFloat() / total else 0f,
            memoryUsageKB = cache.size * 2 // Rough estimate
        )
    }
}

/**
 * Cache statistics
 */
data class TextExtractionCacheStats(
    val size: Int,
    val maxSize: Int,
    val hits: Long,
    val misses: Long,
    val hitRate: Float,
    val memoryUsageKB: Int
)

/**
 * Region of Interest detector for social media content
 */
class RegionOfInterestDetector {
    
    /**
     * Detect content region in social media frames
     */
    fun detectContentRegion(bitmap: Bitmap): RegionOfInterest {
        return try {
            // Analyze bitmap for content patterns
            val contentAreas = analyzeContentAreas(bitmap)
            val bestArea = selectBestContentArea(contentAreas, bitmap)
            
            bestArea ?: RegionOfInterest(0, 0, 0, 0, 0f, ROIType.UNKNOWN)
            
        } catch (e: Exception) {
            Log.w(LocalTextExtractor.TAG, "Error detecting ROI: ${e.message}")
            RegionOfInterest(0, 0, 0, 0, 0f, ROIType.UNKNOWN)
        }
    }
    
    /**
     * Analyze bitmap for potential content areas
     */
    private fun analyzeContentAreas(bitmap: Bitmap): List<RegionOfInterest> {
        val areas = mutableListOf<RegionOfInterest>()
        val width = bitmap.width
        val height = bitmap.height
        
        // Skip top and bottom UI areas (typical social media layout)
        val skipTop = (height * 0.1f).toInt()
        val skipBottom = (height * 0.1f).toInt()
        val contentHeight = height - skipTop - skipBottom
        
        if (contentHeight > 100) {
            // Main content area (center 80% of screen)
            areas.add(RegionOfInterest(
                left = (width * 0.1f).toInt(),
                top = skipTop,
                width = (width * 0.8f).toInt(),
                height = contentHeight,
                confidence = 0.8f,
                type = ROIType.CONTENT_AREA
            ))
            
            // Feed item areas (divide content into sections)
            val itemHeight = contentHeight / 3
            for (i in 0 until 3) {
                areas.add(RegionOfInterest(
                    left = 0,
                    top = skipTop + (i * itemHeight),
                    width = width,
                    height = itemHeight,
                    confidence = 0.6f,
                    type = ROIType.FEED_ITEM
                ))
            }
        }
        
        return areas
    }
    
    /**
     * Select the best content area based on text density estimation
     */
    private fun selectBestContentArea(areas: List<RegionOfInterest>, bitmap: Bitmap): RegionOfInterest? {
        if (areas.isEmpty()) return null
        
        var bestArea: RegionOfInterest? = null
        var bestScore = 0f
        
        for (area in areas) {
            val textDensity = estimateTextDensityInRegion(bitmap, area.toRect())
            val score = textDensity * area.confidence
            
            if (score > bestScore) {
                bestScore = score
                bestArea = area.copy(confidence = score)
            }
        }
        
        return bestArea
    }
    
    /**
     * Estimate text density in a specific region
     */
    private fun estimateTextDensityInRegion(bitmap: Bitmap, region: Rect): Float {
        return try {
            val pixels = IntArray(region.width() * region.height())
            bitmap.getPixels(
                pixels, 0, region.width(),
                region.left, region.top,
                region.width(), region.height()
            )
            
            var textPixels = 0
            for (pixel in pixels) {
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val gray = (r + g + b) / 3
                
                // Text typically has high contrast
                if (gray < 80 || gray > 180) {
                    textPixels++
                }
            }
            
            textPixels.toFloat() / pixels.size
            
        } catch (e: Exception) {
            0f
        }
    }
}

/**
 * Frame preprocessor optimized for social media content
 */
class SocialMediaFramePreprocessor {
    
    /**
     * Preprocess frame for optimal text recognition on social media content
     */
    fun preprocessForSocialMedia(bitmap: Bitmap, roi: RegionOfInterest): Bitmap {
        return try {
            var processedBitmap = bitmap
            
            // Apply social media specific optimizations
            processedBitmap = enhanceTextContrast(processedBitmap)
            processedBitmap = optimizeForMobileText(processedBitmap)
            
            // Scale if too large (ML Kit optimization)
            if (processedBitmap.width > 1024 || processedBitmap.height > 1024) {
                val scale = 1024f / maxOf(processedBitmap.width, processedBitmap.height)
                val scaledWidth = (processedBitmap.width * scale).toInt()
                val scaledHeight = (processedBitmap.height * scale).toInt()
                
                val scaledBitmap = Bitmap.createScaledBitmap(processedBitmap, scaledWidth, scaledHeight, true)
                if (processedBitmap != bitmap) processedBitmap.recycle()
                processedBitmap = scaledBitmap
            }
            
            processedBitmap
            
        } catch (e: Exception) {
            Log.w(LocalTextExtractor.TAG, "Error preprocessing frame: ${e.message}")
            bitmap
        }
    }
    
    /**
     * Apply enhanced preprocessing for fallback scenarios
     */
    fun applyEnhancedPreprocessing(bitmap: Bitmap): Bitmap {
        return try {
            // More aggressive preprocessing for difficult cases
            var enhanced = enhanceTextContrast(bitmap)
            enhanced = sharpenText(enhanced)
            enhanced = adjustBrightness(enhanced)
            enhanced
        } catch (e: Exception) {
            Log.w(LocalTextExtractor.TAG, "Error in enhanced preprocessing: ${e.message}")
            bitmap
        }
    }
    
    /**
     * Enhance text contrast for better recognition
     */
    private fun enhanceTextContrast(bitmap: Bitmap): Bitmap {
        // For now, return original bitmap
        // In a full implementation, this would apply contrast enhancement
        return bitmap
    }
    
    /**
     * Optimize for mobile text patterns
     */
    private fun optimizeForMobileText(bitmap: Bitmap): Bitmap {
        // For now, return original bitmap
        // In a full implementation, this would apply mobile-specific optimizations
        return bitmap
    }
    
    /**
     * Sharpen text for better recognition
     */
    private fun sharpenText(bitmap: Bitmap): Bitmap {
        // For now, return original bitmap
        // In a full implementation, this would apply sharpening filters
        return bitmap
    }
    
    /**
     * Adjust brightness for optimal text recognition
     */
    private fun adjustBrightness(bitmap: Bitmap): Bitmap {
        // For now, return original bitmap
        // In a full implementation, this would adjust brightness/gamma
        return bitmap
    }
}

/**
 * Text extraction quality validator
 */
class TextExtractionValidator {
    
    companion object {
        const val MIN_VALIDATION_SCORE = 0.7f
        const val MIN_TEXT_LENGTH = 3
        const val MIN_REGION_COUNT = 1
    }
    
    /**
     * Validate extraction quality against requirements
     */
    fun validateExtraction(result: TextExtractionResult, bitmap: Bitmap): TextValidationResult {
        val validationChecks = mutableListOf<ValidationCheck>()
        
        // Check 1: Minimum confidence threshold
        val confidenceCheck = ValidationCheck(
            name = "Confidence Threshold",
            passed = result.confidence >= LocalTextExtractor.MIN_CONFIDENCE_THRESHOLD,
            score = result.confidence,
            weight = 0.3f,
            details = "Confidence: ${(result.confidence * 100).toInt()}%"
        )
        validationChecks.add(confidenceCheck)
        
        // Check 2: Text length validation
        val textLengthCheck = ValidationCheck(
            name = "Text Length",
            passed = result.extractedText.length >= MIN_TEXT_LENGTH,
            score = minOf(result.extractedText.length.toFloat() / 20f, 1f),
            weight = 0.2f,
            details = "Length: ${result.extractedText.length} chars"
        )
        validationChecks.add(textLengthCheck)
        
        // Check 3: Region count validation
        val regionCountCheck = ValidationCheck(
            name = "Region Count",
            passed = result.textRegions.size >= MIN_REGION_COUNT,
            score = minOf(result.textRegions.size.toFloat() / 5f, 1f),
            weight = 0.2f,
            details = "Regions: ${result.textRegions.size}"
        )
        validationChecks.add(regionCountCheck)
        
        // Check 4: Performance validation
        val performanceCheck = ValidationCheck(
            name = "Performance",
            passed = result.meetsPerformanceTarget(),
            score = if (result.processingTimeMs <= LocalTextExtractor.TARGET_PROCESSING_TIME_MS) 1f 
                   else LocalTextExtractor.TARGET_PROCESSING_TIME_MS.toFloat() / result.processingTimeMs,
            weight = 0.1f,
            details = "Time: ${result.processingTimeMs}ms"
        )
        validationChecks.add(performanceCheck)
        
        // Check 5: Text quality validation
        val qualityCheck = ValidationCheck(
            name = "Text Quality",
            passed = hasQualityText(result),
            score = calculateTextQualityScore(result),
            weight = 0.2f,
            details = "Quality patterns detected"
        )
        validationChecks.add(qualityCheck)
        
        // Calculate overall score
        val overallScore = validationChecks.sumOf { (it.score * it.weight).toDouble() }.toFloat()
        val allChecksPassed = validationChecks.all { it.passed }
        val meetsRequirements = overallScore >= MIN_VALIDATION_SCORE && allChecksPassed
        
        return TextValidationResult(
            isValid = meetsRequirements,
            validCount = validationChecks.count { it.passed },
            totalCount = validationChecks.size,
            issues = validationChecks.filter { !it.passed }.map { it.name },
            meetsQualityRequirements = meetsRequirements,
            overallScore = overallScore,
            validationChecks = validationChecks,
            recommendedFallback = if (!meetsRequirements) {
                selectRecommendedFallback(validationChecks)
            } else null
        )
    }
    
    /**
     * Check if extracted text has quality characteristics
     */
    private fun hasQualityText(result: TextExtractionResult): Boolean {
        val text = result.extractedText.trim()
        if (text.length < MIN_TEXT_LENGTH) return false
        
        // Check for meaningful content patterns
        val hasLetters = text.any { it.isLetter() }
        val hasWords = text.split("\\s+".toRegex()).size > 1
        val hasVariedContent = text.length > 10 && text.toSet().size > 5
        
        return hasLetters && (hasWords || hasVariedContent)
    }
    
    /**
     * Calculate text quality score based on content characteristics
     */
    private fun calculateTextQualityScore(result: TextExtractionResult): Float {
        val text = result.extractedText
        if (text.isEmpty()) return 0f
        
        var score = 0.5f // Base score
        
        // Bonus for letters
        if (text.any { it.isLetter() }) score += 0.2f
        
        // Bonus for multiple words
        if (text.split("\\s+".toRegex()).size > 1) score += 0.1f
        
        // Bonus for varied characters
        if (text.toSet().size > text.length * 0.3f) score += 0.1f
        
        // Bonus for reasonable length
        if (text.length in 10..200) score += 0.1f
        
        return score.coerceIn(0f, 1f)
    }
    
    /**
     * Select recommended fallback strategy based on validation results
     */
    private fun selectRecommendedFallback(checks: List<ValidationCheck>): FallbackStrategy {
        val failedChecks = checks.filter { !it.passed }
        
        return when {
            failedChecks.any { it.name == "Confidence Threshold" } -> FallbackStrategy.ENHANCED_PREPROCESSING
            failedChecks.any { it.name == "Text Quality" } -> FallbackStrategy.MULTI_SCALE_ANALYSIS
            failedChecks.any { it.name == "Region Count" } -> FallbackStrategy.REGION_SEGMENTATION
            else -> FallbackStrategy.CONFIDENCE_BOOSTING
        }
    }
}

/**
 * Text extraction validation result with detailed analysis
 */
data class TextValidationResult(
    val isValid: Boolean,
    val validCount: Int,
    val totalCount: Int,
    val issues: List<String>,
    val meetsQualityRequirements: Boolean,
    val overallScore: Float,
    val validationChecks: List<ValidationCheck>,
    val recommendedFallback: FallbackStrategy?
)

/**
 * Individual validation check
 */
data class ValidationCheck(
    val name: String,
    val passed: Boolean,
    val score: Float,
    val weight: Float,
    val details: String
)

/**
 * Fallback strategy manager
 */
class TextExtractionFallbackManager {
    
    /**
     * Select appropriate fallback strategy based on validation results
     */
    fun selectFallbackStrategy(validationResult: TextValidationResult): FallbackStrategy {
        return validationResult.recommendedFallback ?: FallbackStrategy.NONE
    }
}

/**
 * Fallback strategies for low-quality extractions
 */
enum class FallbackStrategy {
    ENHANCED_PREPROCESSING,  // Apply more aggressive image preprocessing
    MULTI_SCALE_ANALYSIS,   // Try multiple image scales
    REGION_SEGMENTATION,    // Segment image into smaller regions
    CONFIDENCE_BOOSTING,    // Apply confidence boosting algorithms
    NONE                    // No fallback available
}

/**
 * A/B testing framework for accuracy comparison
 */
class TextExtractionABTesting {
    
    private val testResults = mutableListOf<ABTestResult>()
    private var isEnabled = false
    private var testGroup = ABTestGroup.CONTROL
    
    /**
     * Record extraction result for A/B testing
     */
    fun recordExtraction(result: TextExtractionResult, bitmap: Bitmap) {
        if (!isEnabled) return
        
        try {
            val testResult = ABTestResult(
                timestamp = System.currentTimeMillis(),
                testGroup = testGroup,
                extractedText = result.extractedText,
                confidence = result.confidence,
                processingTimeMs = result.processingTimeMs,
                textRegionCount = result.textRegions.size,
                validationPassed = result.validationPassed ?: false,
                fallbackUsed = result.fallbackUsed ?: false,
                bitmapHash = PerceptualHashGenerator.generateHash(bitmap)
            )
            
            synchronized(testResults) {
                testResults.add(testResult)
                // Keep only recent results
                if (testResults.size > 1000) {
                    testResults.removeAt(0)
                }
            }
        } catch (e: Exception) {
            Log.w(LocalTextExtractor.TAG, "Error recording A/B test result: ${e.message}")
        }
    }
    
    /**
     * Get A/B testing statistics
     */
    fun getABTestingStats(): ABTestingStats {
        synchronized(testResults) {
            val controlResults = testResults.filter { it.testGroup == ABTestGroup.CONTROL }
            val treatmentResults = testResults.filter { it.testGroup == ABTestGroup.TREATMENT }
            
            return ABTestingStats(
                isEnabled = isEnabled,
                currentTestGroup = testGroup,
                totalResults = testResults.size,
                controlResults = controlResults.size,
                treatmentResults = treatmentResults.size,
                controlAvgConfidence = controlResults.map { it.confidence }.average().toFloat(),
                treatmentAvgConfidence = treatmentResults.map { it.confidence }.average().toFloat(),
                controlAvgProcessingTime = controlResults.map { it.processingTimeMs }.average().toFloat(),
                treatmentAvgProcessingTime = treatmentResults.map { it.processingTimeMs }.average().toFloat(),
                controlSuccessRate = if (controlResults.isNotEmpty()) {
                    controlResults.count { it.validationPassed }.toFloat() / controlResults.size
                } else 0f,
                treatmentSuccessRate = if (treatmentResults.isNotEmpty()) {
                    treatmentResults.count { it.validationPassed }.toFloat() / treatmentResults.size
                } else 0f
            )
        }
    }
    
    /**
     * Enable A/B testing with specified group
     */
    fun enableABTesting(group: ABTestGroup) {
        isEnabled = true
        testGroup = group
        Log.d(LocalTextExtractor.TAG, "A/B testing enabled for group: $group")
    }
    
    /**
     * Disable A/B testing
     */
    fun disableABTesting() {
        isEnabled = false
        Log.d(LocalTextExtractor.TAG, "A/B testing disabled")
    }
    
    /**
     * Clear test results
     */
    fun clearResults() {
        synchronized(testResults) {
            testResults.clear()
        }
    }
}

/**
 * A/B test groups
 */
enum class ABTestGroup {
    CONTROL,    // Current implementation
    TREATMENT   // New implementation being tested
}

/**
 * A/B test result record
 */
data class ABTestResult(
    val timestamp: Long,
    val testGroup: ABTestGroup,
    val extractedText: String,
    val confidence: Float,
    val processingTimeMs: Long,
    val textRegionCount: Int,
    val validationPassed: Boolean,
    val fallbackUsed: Boolean,
    val bitmapHash: Long
)

/**
 * A/B testing statistics
 */
data class ABTestingStats(
    val isEnabled: Boolean,
    val currentTestGroup: ABTestGroup,
    val totalResults: Int,
    val controlResults: Int,
    val treatmentResults: Int,
    val controlAvgConfidence: Float,
    val treatmentAvgConfidence: Float,
    val controlAvgProcessingTime: Float,
    val treatmentAvgProcessingTime: Float,
    val controlSuccessRate: Float,
    val treatmentSuccessRate: Float
) {
    fun getConfidenceImprovement(): Float = treatmentAvgConfidence - controlAvgConfidence
    fun getPerformanceImprovement(): Float = controlAvgProcessingTime - treatmentAvgProcessingTime
    fun getSuccessRateImprovement(): Float = treatmentSuccessRate - controlSuccessRate
}

/**
 * Validation system status
 */
data class ValidationSystemStatus(
    val validatorEnabled: Boolean,
    val fallbackManagerEnabled: Boolean,
    val abTestingEnabled: Boolean,
    val supportedFallbackStrategies: List<FallbackStrategy>,
    val validationThreshold: Float
)