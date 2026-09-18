package org.phireox.ofa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.phireox.ofa.core.theme.OFATheme
import org.phireox.ofa.feature.main.OFAApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OFATheme {
                OFAApp()
            }
        }
    }
}
