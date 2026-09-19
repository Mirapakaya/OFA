package org.phireox.ofa.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.phireox.ofa.data.local.ProviderConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val config = remember { ProviderConfig(context) }

    var weather by remember { mutableStateOf(config.get(ProviderConfig.KEY_WEATHER_PROVIDER)) }
    var aiUrl by remember { mutableStateOf(config.get(ProviderConfig.KEY_AI_BASE_URL)) }
    var tempMail by remember { mutableStateOf(config.get(ProviderConfig.KEY_TEMP_MAIL_PROVIDER)) }
    var tempPhone by remember { mutableStateOf(config.get(ProviderConfig.KEY_TEMP_PHONE_PROVIDER)) }
    var meeting by remember { mutableStateOf(config.get(ProviderConfig.KEY_MEETING_PROVIDER)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Configure external providers for network-dependent features. Leave blank to use built-in defaults or keep the feature disabled.", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = weather, onValueChange = { weather = it }, label = { Text("Weather provider URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = aiUrl, onValueChange = { aiUrl = it }, label = { Text("Custom AI base URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tempMail, onValueChange = { tempMail = it }, label = { Text("Temporary mail provider URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tempPhone, onValueChange = { tempPhone = it }, label = { Text("Temporary phone provider URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = meeting, onValueChange = { meeting = it }, label = { Text("Meeting room provider URL") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                config.set(ProviderConfig.KEY_WEATHER_PROVIDER, weather)
                config.set(ProviderConfig.KEY_AI_BASE_URL, aiUrl)
                config.set(ProviderConfig.KEY_TEMP_MAIL_PROVIDER, tempMail)
                config.set(ProviderConfig.KEY_TEMP_PHONE_PROVIDER, tempPhone)
                config.set(ProviderConfig.KEY_MEETING_PROVIDER, meeting)
            }, modifier = Modifier.fillMaxWidth()) { Text("Save providers") }
        }
    }
}
