package com.wizpizz.crumbcontrol.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "crumb_control_prefs"
        private const val KEY_BLOCKED_APPS = "blocked_apps"
        private const val KEY_SHOW_SYSTEM_APPS = "show_system_apps"
    }
    
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE)
    
    fun getBlockedApps(): Set<String> {
        return sharedPrefs.getStringSet(KEY_BLOCKED_APPS, emptySet()) ?: emptySet()
    }
    
    fun setBlockedApps(blockedApps: Set<String>) {
        sharedPrefs.edit()
            .putStringSet(KEY_BLOCKED_APPS, blockedApps)
            .apply()
        
        // Ensure the preferences file is world readable for Xposed
        makeWorldReadable()
    }
    
    fun addBlockedApp(packageName: String) {
        val currentBlocked = getBlockedApps().toMutableSet()
        currentBlocked.add(packageName)
        setBlockedApps(currentBlocked)
    }
    
    fun removeBlockedApp(packageName: String) {
        val currentBlocked = getBlockedApps().toMutableSet()
        currentBlocked.remove(packageName)
        setBlockedApps(currentBlocked)
    }
    
    fun isAppBlocked(packageName: String): Boolean {
        return getBlockedApps().contains(packageName)
    }
    
    fun getShowSystemApps(): Boolean {
        return sharedPrefs.getBoolean(KEY_SHOW_SYSTEM_APPS, false)
    }
    
    fun setShowSystemApps(show: Boolean) {
        sharedPrefs.edit()
            .putBoolean(KEY_SHOW_SYSTEM_APPS, show)
            .apply()
        makeWorldReadable()
    }
    
    private fun makeWorldReadable() {
        try {
            // Try to make the preferences file world readable
            val prefsDir = sharedPrefs.javaClass.getDeclaredMethod("getFile")
            prefsDir.isAccessible = true
            val prefsFile = prefsDir.invoke(sharedPrefs) as java.io.File
            prefsFile.setReadable(true, false)
        } catch (e: Exception) {
            // If we can't make it world readable, log it but continue
            android.util.Log.w("PreferencesManager", "Could not make preferences world readable: ${e.message}")
        }
    }
}