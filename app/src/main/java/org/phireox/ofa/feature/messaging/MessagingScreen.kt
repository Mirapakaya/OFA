package org.phireox.ofa.feature.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.MessagingRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { MessagingRepository(context) }
    val conversations by repository.conversations.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var newConversation by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Local E2EE Messaging") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Offline-first encrypted messages", style = MaterialTheme.typography.titleMedium)
                    Text("Messages are encrypted with the password you choose and stored locally. Real-time E2EE messaging with contacts requires a configured provider or peer protocol and is not available in this offline build.")
                }
            }
            if (selected == null) {
                OutlinedTextField(
                    value = newConversation,
                    onValueChange = { newConversation = it },
                    label = { Text("New conversation name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    scope.launch { repository.createConversation(newConversation) }
                    newConversation = ""
                }, modifier = Modifier.fillMaxWidth()) { Text("Create conversation") }
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(conversations, key = { it }) { name ->
                        Card(onClick = { selected = name }, modifier = Modifier.fillMaxWidth()) {
                            Text(name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            } else {
                val messages = remember(selected) { repository.messages(selected!!) }
                Button(onClick = { selected = null }) { Text("Back") }
                Text(selected!!, style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password for decryption") }, modifier = Modifier.fillMaxWidth())
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(messages, key = { it.id }) { msg ->
                        val decrypted = remember(msg, password) { repository.decryptMessage(msg, password) }
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(decrypted, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    scope.launch { repository.sendMessage(selected!!, message, password) }
                    message = ""
                }, modifier = Modifier.fillMaxWidth()) { Text("Send encrypted") }
            }
        }
    }
}
