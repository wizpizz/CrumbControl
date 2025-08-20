package com.wizpizz.crumbcontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wizpizz.crumbcontrol.data.AppInfo
import com.wizpizz.crumbcontrol.data.PreferencesManager
import com.wizpizz.crumbcontrol.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppSelectionViewModel(
    private val appRepository: AppRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppSelectionUiState())
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    init {
        loadApps()
        loadPreferences()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val allApps = appRepository.getAllInstalledApps()
                _apps.value = allApps
                updateFilteredApps()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load apps: ${e.message}"
                )
            }
        }
    }

    private fun loadPreferences() {
        val blockedApps = preferencesManager.getBlockedApps()
        val showSystemApps = preferencesManager.getShowSystemApps()
        
        _uiState.value = _uiState.value.copy(
            blockedApps = blockedApps,
            showSystemApps = showSystemApps
        )
        updateFilteredApps()
    }

    private fun updateFilteredApps() {
        val currentState = _uiState.value
        val allApps = _apps.value
        
        val filtered = if (currentState.showSystemApps) {
            allApps
        } else {
            allApps.filter { !it.isSystemApp }
        }
        
        val searchFiltered = if (currentState.searchQuery.isBlank()) {
            filtered
        } else {
            filtered.filter { 
                it.appName.contains(currentState.searchQuery, ignoreCase = true) ||
                it.packageName.contains(currentState.searchQuery, ignoreCase = true)
            }
        }
        
        _uiState.value = currentState.copy(
            filteredApps = searchFiltered,
            isLoading = false,
            error = null
        )
    }

    fun toggleAppBlocked(packageName: String) {
        val currentBlocked = _uiState.value.blockedApps.toMutableSet()
        if (currentBlocked.contains(packageName)) {
            currentBlocked.remove(packageName)
            preferencesManager.removeBlockedApp(packageName)
        } else {
            currentBlocked.add(packageName)
            preferencesManager.addBlockedApp(packageName)
        }
        
        _uiState.value = _uiState.value.copy(blockedApps = currentBlocked)
    }

    fun toggleShowSystemApps() {
        val newValue = !_uiState.value.showSystemApps
        preferencesManager.setShowSystemApps(newValue)
        _uiState.value = _uiState.value.copy(showSystemApps = newValue)
        updateFilteredApps()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        updateFilteredApps()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    class Factory(
        private val appRepository: AppRepository,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppSelectionViewModel::class.java)) {
                return AppSelectionViewModel(appRepository, preferencesManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class AppSelectionUiState(
    val filteredApps: List<AppInfo> = emptyList(),
    val blockedApps: Set<String> = emptySet(),
    val showSystemApps: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)