package com.wizpizz.crumbcontrol.hooks

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class ToastHook {

    companion object {
        const val TAG = "CrumbControlHook.ToastHook"
        const val TEST_APP_TO_BLOCK = "com.wizpizz.crumbcontrol"
        
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
                            
                            // Block toast from specified app
                            if (pkg == TEST_APP_TO_BLOCK) {
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
    }
}