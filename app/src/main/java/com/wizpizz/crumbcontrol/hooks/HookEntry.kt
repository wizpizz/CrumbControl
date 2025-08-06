package com.wizpizz.crumbcontrol.hooks

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.wizpizz.crumbcontrol.hooks.ToastHook


class HookEntry: IXposedHookLoadPackage {

    companion object {
        const val TAG = "CrumbControlHook.HookEntry"
        const val MODULE_PACKAGE = "com.wizpizz.crumbcontrol"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "system" && lpparam.packageName != "android") {
            Log.w(TAG, "Unsupported package selected in module scope: ${lpparam.packageName}")
            return
        }
        ToastHook.initializeHook(lpparam)

    }
}