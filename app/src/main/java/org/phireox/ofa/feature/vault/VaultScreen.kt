package org.phireox.ofa.feature.vault

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.phireox.ofa.data.model.VaultItem
import org.phireox.ofa.data.model.VaultItemType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: VaultViewModel = viewModel(factory = androidx.lifecycle.viewmodel.ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application))
    val clipboard = LocalClipboardManager.current
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<VaultItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vault") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.items, key = { it.id }) { item ->
                val isPassword = item.type == VaultItemType.PASSWORD
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.delete(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                        if (item.username.isNotBlank()) Text(item.username, style = MaterialTheme.typography.bodyMedium)
                        if (isPassword) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("••••••", modifier = Modifier.weight(1f))
                                TextButton(onClick = { clipboard.setText(AnnotatedString(item.secret)) }) { Text("Copy") }
                            }
                        } else {
                            val code = viewModel.totpCodes[item.id] ?: "------"
                            Text(code, style = MaterialTheme.typography.headlineMedium)
                            Text("Refreshes in ${viewModel.remainingSeconds.value}s")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { editing = item; showDialog = true }) { Text("Edit") }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        VaultItemDialog(
            item = editing,
            onDismiss = { showDialog = false },
            onSave = {
                viewModel.save(it)
                showDialog = false
            }
        )
    }
}

@Composable
private fun VaultItemDialog(item: VaultItem?, onDismiss: () -> Unit, onSave: (VaultItem) -> Unit) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var username by remember { mutableStateOf(item?.username ?: "") }
    var secret by remember { mutableStateOf(item?.secret ?: "") }
    var url by remember { mutableStateOf(item?.url ?: "") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var type by remember { mutableStateOf(item?.type ?: VaultItemType.PASSWORD) }
    var showSecret by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add item" else "Edit item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == VaultItemType.PASSWORD, onClick = { type = VaultItemType.PASSWORD })
                    Text("Password")
                    RadioButton(selected = type == VaultItemType.TOTP, onClick = { type = VaultItemType.TOTP })
                    Text("2FA")
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username / Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text(if (type == VaultItemType.PASSWORD) "Password" else "Secret (base32)") },
                    visualTransformation = if (showSecret) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { Switch(checked = showSecret, onCheckedChange = { showSecret = it }) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    VaultItem(
                        id = item?.id ?: java.util.UUID.randomUUID().toString(),
                        name = name,
                        username = username,
                        secret = secret,
                        url = url,
                        notes = notes,
                        type = type
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
