package com.wizpizz.crumbcontrol.hooks

import android.util.Log
import de.robv.android.xposed.IXposedHookZygoteInit

class ToastHookZygote : IXposedHookZygoteInit {
    companion object {
        const val TAG = "CrumbControlHook.ToastHookZygote"
    }
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        Log.i(TAG, "Initializing Toast Hook in Zygote")

    }


}