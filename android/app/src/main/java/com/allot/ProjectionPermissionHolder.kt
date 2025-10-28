package com.allot

import android.content.Intent

object ProjectionPermissionHolder {
    @Volatile
    var pendingResultCode: Int = -1

    @Volatile
    var pendingDataIntent: Intent? = null

    fun clear() {
        pendingResultCode = -1
        pendingDataIntent = null
    }
}