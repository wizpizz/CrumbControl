package com.wizpizz.crumbcontrol

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wizpizz.crumbcontrol.ui.theme.CrumbControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrumbControlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "asdf",
                        modifier = Modifier.padding(innerPadding)
                    )
                    Button(
                        onClick = {
                            Toast.makeText(
                                this,
                                "Button clicked!",
                                Toast.LENGTH_SHORT
                            ).show()  // Replace with your toast function
                        },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(text = "Click Me")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}