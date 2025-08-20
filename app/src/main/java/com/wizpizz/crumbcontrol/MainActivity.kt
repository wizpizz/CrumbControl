package com.wizpizz.crumbcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wizpizz.crumbcontrol.data.PreferencesManager
import com.wizpizz.crumbcontrol.repository.AppRepository
import com.wizpizz.crumbcontrol.ui.screens.AppSelectionScreen
import com.wizpizz.crumbcontrol.ui.theme.CrumbControlTheme
import com.wizpizz.crumbcontrol.viewmodel.AppSelectionViewModel

class MainActivity : ComponentActivity() {
    
    private lateinit var appRepository: AppRepository
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize repositories
        appRepository = AppRepository(this)
        preferencesManager = PreferencesManager(this)
        
        enableEdgeToEdge()
        
        setContent {
            CrumbControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: AppSelectionViewModel = viewModel(
                        factory = AppSelectionViewModel.Factory(
                            appRepository = appRepository,
                            preferencesManager = preferencesManager
                        )
                    )
                    
                    AppSelectionScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}