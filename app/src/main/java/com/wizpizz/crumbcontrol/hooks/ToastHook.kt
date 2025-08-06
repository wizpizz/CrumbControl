package com.wizpizz.crumbcontrol.hooks

import android.os.IBinder
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class ToastHook {

    companion object {
        const val TAG = "CrumbControlHook.ToastHook"
        const val TEST_APP_TO_BLOCK = "com.wizpizz.crumbcontrol"
        
        fun initializeHook(lpparam: XC_LoadPackage.LoadPackageParam) {
            Log.i(TAG, "Initializing Toast Hook: ${lpparam.packageName}")

            try {
                // Hook the anonymous inner class that contains enqueueToast
                XposedHelpers.findAndHookMethod(
                    "com.android.server.notification.NotificationManagerService\$1", // Anonymous inner class
                    lpparam.classLoader,
                    "enqueueToast",
                    String::class.java, // pkg
                    IBinder::class.java, // token
                    "android.app.ITransientNotification", // callback
                    Int::class.java, // duration
                    Boolean::class.java, // isUiContext
                    Int::class.java, // displayId
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val pkg = param.args[0] as String
                            Log.i(TAG, "Intercepting toast enqueue: $pkg")
                            if (pkg == TEST_APP_TO_BLOCK) {
                                Log.i(TAG, "Blocking toast from: $pkg")
                                param.setResult(false)
                                return
                            }
                            Log.i(TAG, "Allowing toast from: $pkg")
                        }
                    }
                )
                Log.i(TAG, "Successfully hooked enqueueToast method")
            } catch (e: NoSuchMethodError) {
                Log.e(TAG, "Failed to hook enqueueToast: ${e.message}")

                // Try alternative approach - hook the interface directly
                try {
                    XposedHelpers.findAndHookMethod(
                        "android.app.INotificationManager\$Stub\$Proxy",
                        lpparam.classLoader,
                        "enqueueToast",
                        String::class.java, // pkg
                        IBinder::class.java, // token
                        "android.app.ITransientNotification", // callback
                        Int::class.java, // duration
                        Boolean::class.java, // isUiContext
                        Int::class.java, // displayId
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                val pkg = param.args[0] as String
                                Log.i(TAG, "Intercepting toast via proxy: $pkg")
                                if (pkg == TEST_APP_TO_BLOCK) {
                                    Log.i(TAG, "Blocking toast from: $pkg")
                                    param.setResult(false)
                                    return
                                }
                                Log.i(TAG, "Allowing toast from: $pkg")
                            }
                        }
                    )
                    Log.i(TAG, "Successfully hooked INotificationManager proxy")
                } catch (e2: Exception) {
                    Log.e(TAG, "Failed to hook proxy too: ${e2.message}")
                }
            }
        }
    }
}