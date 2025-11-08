package com.allot

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import com.facebook.react.bridge.*

class BatteryOptimizationModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "BatteryOptimization"
    }

    override fun getName(): String = "BatteryOptimizationModule"

    @ReactMethod
    fun isIgnoringBatteryOptimizations(promise: Promise) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = reactApplicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                val packageName = reactApplicationContext.packageName
                val isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
                promise.resolve(isIgnoring)
            } else {
                promise.resolve(true) // Not applicable on older versions
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking battery optimization: ${e.message}")
            promise.reject("ERROR", e.message)
        }
    }

    @ReactMethod
    fun requestIgnoreBatteryOptimizations(promise: Promise) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:${reactApplicationContext.packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                reactApplicationContext.startActivity(intent)
                promise.resolve(true)
            } else {
                promise.resolve(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting battery optimization: ${e.message}")
            // Fallback to settings page
            openBatteryOptimizationSettings(promise)
        }
    }

    @ReactMethod
    fun openBatteryOptimizationSettings(promise: Promise) {
        try {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            reactApplicationContext.startActivity(intent)
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening battery settings: ${e.message}")
            promise.reject("ERROR", e.message)
        }
    }

    @ReactMethod
    fun getDeviceInfo(promise: Promise) {
        try {
            val info = Arguments.createMap().apply {
                putString("manufacturer", Build.MANUFACTURER)
                putString("brand", Build.BRAND)
                putString("model", Build.MODEL)
                putString("device", Build.DEVICE)
                putBoolean("isMiui", isMiui())
                putBoolean("isXiaomi", isXiaomi())
                putBoolean("isHuawei", isHuawei())
                putBoolean("isOppo", isOppo())
                putBoolean("isSamsung", isSamsung())
            }
            promise.resolve(info)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting device info: ${e.message}")
            promise.reject("ERROR", e.message)
        }
    }

    @ReactMethod
    fun openManufacturerSettings(promise: Promise) {
        try {
            val opened = when {
                isMiui() -> openMiuiAutostart()
                isHuawei() -> openHuaweiProtectedApps()
                isOppo() -> openOppoAutostart()
                isSamsung() -> openSamsungBatterySettings()
                else -> false
            }
            
            if (!opened) {
                // Fallback to general battery settings
                openBatteryOptimizationSettings(promise)
            } else {
                promise.resolve(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening manufacturer settings: ${e.message}")
            promise.reject("ERROR", e.message)
        }
    }

    private fun isMiui(): Boolean {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = systemProperties.getMethod("get", String::class.java)
            val miuiVersion = getMethod.invoke(null, "ro.miui.ui.version.name") as? String
            miuiVersion != null && miuiVersion.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun isXiaomi(): Boolean {
        return Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
               Build.BRAND.equals("Xiaomi", ignoreCase = true) ||
               Build.BRAND.equals("Redmi", ignoreCase = true)
    }

    private fun isHuawei(): Boolean {
        return Build.MANUFACTURER.equals("Huawei", ignoreCase = true) ||
               Build.BRAND.equals("Huawei", ignoreCase = true) ||
               Build.BRAND.equals("Honor", ignoreCase = true)
    }

    private fun isOppo(): Boolean {
        return Build.MANUFACTURER.equals("Oppo", ignoreCase = true) ||
               Build.BRAND.equals("Oppo", ignoreCase = true) ||
               Build.BRAND.equals("OnePlus", ignoreCase = true) ||
               Build.BRAND.equals("Realme", ignoreCase = true)
    }

    private fun isSamsung(): Boolean {
        return Build.MANUFACTURER.equals("Samsung", ignoreCase = true) ||
               Build.BRAND.equals("Samsung", ignoreCase = true)
    }

    private fun openMiuiAutostart(): Boolean {
        return try {
            // Try MIUI autostart settings
            val intents = listOf(
                Intent().apply {
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                },
                Intent().apply {
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.powercenter.PowerSettings"
                    )
                },
                Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                    putExtra("extra_pkgname", reactApplicationContext.packageName)
                }
            )

            for (intent in intents) {
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    reactApplicationContext.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    continue
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error opening MIUI settings: ${e.message}")
            false
        }
    }

    private fun openHuaweiProtectedApps(): Boolean {
        return try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            reactApplicationContext.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Huawei settings: ${e.message}")
            false
        }
    }

    private fun openOppoAutostart(): Boolean {
        return try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            reactApplicationContext.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Oppo settings: ${e.message}")
            false
        }
    }

    private fun openSamsungBatterySettings(): Boolean {
        return try {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:${reactApplicationContext.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            reactApplicationContext.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening Samsung settings: ${e.message}")
            false
        }
    }
}
