package com.allot

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class ScreenPermissionPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(
            ScreenPermissionModule(reactContext),
            NotificationModule(reactContext),
            AccessibilityControlModule(reactContext),
            ContentBlockingModule(reactContext),
            AppDetectionModule(reactContext),
            BatteryOptimizationModule(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}