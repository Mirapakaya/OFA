package org.phireox.ofa.feature.backup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.BackupManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backupManager = remember { BackupManager(context) }
    var status by remember { mutableStateOf("") }
    var isWorking by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            isWorking = true
            backupManager.restoreBackup(uri).fold(
                onSuccess = { status = "Restore completed." },
                onFailure = { status = "Restore failed: ${it.localizedMessage}" }
            )
            isWorking = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Local backup", style = MaterialTheme.typography.titleMedium)
                    Text("Export your preferences and notes to a JSON file in Downloads/OFA/Backups. No data leaves this device.")
                    Button(
                        onClick = {
                            scope.launch {
                                isWorking = true
                                backupManager.createBackup().fold(
                                    onSuccess = { uri -> status = if (uri != null) "Backup saved." else "Backup failed." },
                                    onFailure = { status = "Backup failed: ${it.localizedMessage}" }
                                )
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Backup now") }
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Restore", style = MaterialTheme.typography.titleMedium)
                    Text("Pick an OFA backup JSON file to restore preferences and notes.")
                    Button(
                        onClick = { pickerLauncher.launch(arrayOf("application/json")) },
                        enabled = !isWorking,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Restore from file") }
                }
            }
            if (isWorking) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            if (status.isNotBlank()) Text(status, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
