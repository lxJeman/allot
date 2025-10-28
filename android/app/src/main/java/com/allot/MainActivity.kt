package com.allot

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import expo.modules.ReactActivityDelegateWrapper

class MainActivity : ReactActivity() {
  private var screenPermissionModule: ScreenPermissionModule? = null
  private var notificationModule: NotificationModule? = null
  private var screenCaptureModule: ScreenCaptureModule? = null

  companion object {
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    const val SCREEN_CAPTURE_REQUEST_CODE = 1000
    const val TAG = "MainActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);
    super.onCreate(null)
  }

  override fun onResume() {
    super.onResume()
    // Get reference to our native modules for activity result handling
    try {
      val reactInstanceManager = reactNativeHost.reactInstanceManager
      val reactContext = reactInstanceManager.currentReactContext
      reactContext?.let { context ->
        screenPermissionModule = context.getNativeModule(ScreenPermissionModule::class.java)
        notificationModule = context.getNativeModule(NotificationModule::class.java)
        screenCaptureModule = context.getNativeModule(ScreenCaptureModule::class.java)
      }
    } catch (e: Exception) {
      // Module not ready yet, will be available later
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    // Activity results are now handled automatically by ActivityEventListener modules
    // ScreenPermissionModule implements ActivityEventListener and receives results automatically
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    when (requestCode) {
      NOTIFICATION_PERMISSION_REQUEST_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Log.d(TAG, "POST_NOTIFICATIONS permission granted by user")
        } else {
          Log.d(TAG, "POST_NOTIFICATIONS permission denied by user")
        }
        // The React Native side will check the permission status after the request
      }
    }
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "main"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate {
    return ReactActivityDelegateWrapper(
          this,
          BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
          object : DefaultReactActivityDelegate(
              this,
              mainComponentName,
              fabricEnabled
          ){})
  }

  /**
    * Align the back button behavior with Android S
    * where moving root activities to background instead of finishing activities.
    * @see <a href="https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
    */
  override fun invokeDefaultOnBackPressed() {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
          if (!moveTaskToBack(false)) {
              // For non-root activities, use the default implementation to finish them.
              super.invokeDefaultOnBackPressed()
          }
          return
      }

      // Use the default back button implementation on Android S
      // because it's doing more than [Activity.moveTaskToBack] in fact.
      super.invokeDefaultOnBackPressed()
  }
}
