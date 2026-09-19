package org.phireox.ofa.feature.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.AiProviderConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val config = remember { AiProviderConfig(context) }
    val scope = rememberCoroutineScope()

    var provider by remember { mutableStateOf(config.getProvider()) }
    var apiKey by remember { mutableStateOf(config.getApiKey()) }
    var baseUrl by remember { mutableStateOf(config.getBaseUrl()) }
    var prompt by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var loading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provider configuration", style = MaterialTheme.typography.titleMedium)
                    Text("No AI model is bundled. Your prompts are sent to the provider you choose. The API key is stored locally.")
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = provider,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Provider") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenuBox(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("openai", "gemini", "anthropic", "custom").forEach { p ->
                                DropdownMenuItem(text = { Text(p.replaceFirstChar { it.uppercase() }) }, onClick = { provider = p; expanded = false })
                            }
                        }
                    }
                    if (provider == "custom") {
                        OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") }, modifier = Modifier.fillMaxWidth())
                    }
                    OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { config.save(provider, apiKey, baseUrl) }, modifier = Modifier.fillMaxWidth()) { Text("Save configuration") }
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { (role, text) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(role.uppercase(), style = MaterialTheme.typography.labelSmall)
                            Text(text)
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Ask...") }, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (prompt.isBlank() || loading) return@Button
                        val userMsg = prompt
                        messages = messages + ("user" to userMsg)
                        prompt = ""
                        loading = true
                        scope.launch {
                            val reply = AiChatClient.chat(provider, apiKey, baseUrl, messages)
                            loading = false
                            messages = messages + ("assistant" to reply)
                        }
                    },
                    enabled = apiKey.isNotBlank() && !loading
                ) { Text("Send") }
            }
        }
    }
}
