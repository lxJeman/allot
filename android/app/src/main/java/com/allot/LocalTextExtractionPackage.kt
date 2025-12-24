package com.allot

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

/**
 * LocalTextExtractionPackage - React Native package for LocalTextExtractionModule
 * 
 * Registers the LocalTextExtractionModule with React Native bridge
 */
class LocalTextExtractionPackage : ReactPackage {

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(LocalTextExtractionModule(reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}