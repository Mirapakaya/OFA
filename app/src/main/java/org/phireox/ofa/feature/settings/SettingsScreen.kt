package org.phireox.ofa.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.PrefsDataStore
import org.phireox.ofa.data.local.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onSubscriptionClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { PrefsDataStore(context) }
    val scope = rememberCoroutineScope()
    val themeMode by prefs.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val dynamic by prefs.dynamicColor.collectAsState(initial = true)
    val premium by prefs.isPremium.collectAsState(initial = false)
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { expanded = true }) {
                Text("Theme: ${themeMode.name.lowercase().replaceFirstChar { it.uppercase() }}")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("System") }, onClick = { scope.launch { prefs.setThemeMode(ThemeMode.SYSTEM) }; expanded = false })
                DropdownMenuItem(text = { Text("Light") }, onClick = { scope.launch { prefs.setThemeMode(ThemeMode.LIGHT) }; expanded = false })
                DropdownMenuItem(text = { Text("Dark") }, onClick = { scope.launch { prefs.setThemeMode(ThemeMode.DARK) }; expanded = false })
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dynamic color")
                Switch(checked = dynamic, onCheckedChange = { scope.launch { prefs.setDynamicColor(it) } })
            }
            Text("Premium", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${if (premium) "Premium" else "Free"}")
            TextButton(onClick = onSubscriptionClick) { Text("Manage subscription") }
            Text("Privacy", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { scope.launch { prefs.clearAll() } }) { Text("Delete all OFA data") }
        }
    }
}
