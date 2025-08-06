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
                XposedHelpers.findAndHookMethod(
                    "com.android.server.notification.NotificationManagerService",
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
                                Log.i(TAG, "Blocking toast from: $pkg. Original text: ${param.args[2]}")
                                param.setResult(false)
                                return
                            }
                            Log.i(TAG, "Allowing toast from: $pkg. Original text: ${param.args[2]}")
                        }
                    }
                )
            } catch (e: NoSuchMethodError) {
                Log.e(TAG, "Failed to hook enqueueToast method: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in Toast Hook: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}