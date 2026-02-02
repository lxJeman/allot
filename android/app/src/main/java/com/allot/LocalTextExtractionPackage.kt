package com.allot

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

/**
 * LocalTextExtractionPackage - React Native package for native modules
 * 
 * Registers native modules with React Native bridge:
 * - LocalTextExtractionModule: ML Kit text extraction
 * - HttpBridgeModule: Native HTTP client (replaces broken React Native fetch)
 */
class LocalTextExtractionPackage : ReactPackage {

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(
            LocalTextExtractionModule(reactContext),
            HttpBridgeModule(reactContext)
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}