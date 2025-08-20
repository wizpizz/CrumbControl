package com.wizpizz.crumbcontrol.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.wizpizz.crumbcontrol.data.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    
    private val packageManager: PackageManager = context.packageManager
    
    suspend fun getAllInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            packages.mapNotNull { appInfo ->
                try {
                    val packageName = appInfo.packageName
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = packageManager.getApplicationIcon(appInfo)
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    
                    AppInfo(
                        packageName = packageName,
                        appName = appName,
                        icon = icon,
                        isSystemApp = isSystemApp
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getUserApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getAllInstalledApps().filter { !it.isSystemApp }
    }
    
    suspend fun getSystemApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getAllInstalledApps().filter { it.isSystemApp }
    }
}