package com.allot

import android.graphics.Bitmap
import android.net.Uri

/**
 * Sealed class for different image input types
 * Allows the OCR engine to accept multiple formats without forcing conversion
 */
sealed class ImageInput {
    data class BitmapInput(val bitmap: Bitmap) : ImageInput()
    data class UriInput(val uri: Uri) : ImageInput()
    data class Base64Input(val base64: String) : ImageInput()
}