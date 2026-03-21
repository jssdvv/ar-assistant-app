package com.jssdvv.ara.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jssdvv.ara.core.presentation.App
import com.jssdvv.ara.core.presentation.rememberAppState
import com.jssdvv.ara.core.presentation.theme.Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Theme {
                val appState = rememberAppState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = { App(appState) }
                )
            }
        }
    }
}

// TODO: Fix all strings in the app
// TODO: Create common icons for the app and fix the use of them
// TODO: Fix colors
// TODO: normalize spacings
// TODO: normalize shapes