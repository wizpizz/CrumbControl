package com.wizpizz.crumbcontrol.hooks

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class ToastHook {

    companion object {
        const val TAG = "CrumbControlHook.ToastHook"
        const val PACKAGE_NAME = "com.wizpizz.crumbcontrol"
        const val PREF_NAME = "crumb_control_prefs"
        const val KEY_BLOCKED_APPS = "blocked_apps"
        
        fun initializeHook(lpparam: XC_LoadPackage.LoadPackageParam) {
            Log.i(TAG, "Initializing Toast Hook for: ${lpparam.packageName}")

            try {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.notification.NotificationManagerService",
                    lpparam.classLoader,
                    "tryShowToast",
                    "com.android.server.notification.toast.ToastRecord", // ToastRecord
                    Boolean::class.java, // rateLimitingEnabled
                    Boolean::class.java, // isWithinQuota
                    Boolean::class.java, // isPackageInForeground
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val toastRecord = param.args[0]
                            
                            // Extract package name from ToastRecord
                            val pkgField = toastRecord.javaClass.getDeclaredField("pkg")
                            pkgField.isAccessible = true
                            val pkg = pkgField.get(toastRecord) as String
                            
                            Log.i(TAG, "Intercepting toast from: $pkg")
                            
                            // Check if this app should be blocked
                            if (shouldBlockToast(pkg)) {
                                Log.i(TAG, "Blocking toast from: $pkg")
                                param.setResult(false)
                                return
                            }
                            
                            Log.d(TAG, "Allowing toast from: $pkg")
                        }
                    }
                )
                Log.i(TAG, "Successfully hooked tryShowToast method")
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to hook tryShowToast method: ${e.message}")
                e.printStackTrace()
                XposedBridge.log(e)
            }
        }
        
        private fun shouldBlockToast(packageName: String): Boolean {
            return try {
                // Use XSharedPreferences to read from the app's preferences
                val prefs = XSharedPreferences(PACKAGE_NAME, PREF_NAME)
                prefs.makeWorldReadable()
                
                val blockedApps = prefs.getStringSet(KEY_BLOCKED_APPS, emptySet()) ?: emptySet()
                val isBlocked = blockedApps.contains(packageName)
                
                Log.d(TAG, "Package $packageName blocked: $isBlocked (total blocked: ${blockedApps.size})")
                isBlocked
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read preferences: ${e.message}")
                // Fall back to checking if it's our test app
                packageName == PACKAGE_NAME
            }
        }
    }
}