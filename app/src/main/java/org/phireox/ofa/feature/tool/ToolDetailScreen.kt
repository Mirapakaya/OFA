package org.phireox.ofa.feature.tool

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.phireox.ofa.data.model.ToolType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen(
    toolId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ToolViewModel = viewModel(
        key = toolId,
        factory = ToolViewModelFactory(toolId, context.applicationContext as android.app.Application)
    )
    val state = viewModel.state.value
    val scrollState = rememberScrollState()
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.tool?.title ?: "Tool") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.tool?.let { tool ->
                Text(tool.description, style = MaterialTheme.typography.bodyMedium)

                if (tool.requiresNetwork) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("External service required", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("This feature requires an external provider. No provider is configured. Data would be sent to the provider, not to OFA.")
                        }
                    }
                } else {
                    when (tool.toolType) {
                        ToolType.QR_GENERATOR -> {
                            OutlinedTextField(
                                value = state.input,
                                onValueChange = viewModel::updateInput,
                                label = { Text("QR content") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(onClick = { viewModel.process() }, modifier = Modifier.fillMaxWidth()) { Text("Generate QR") }
                            state.qrBitmap?.let { bitmap ->
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                        ToolType.CALCULATOR -> {
                            Text("Enter values as key=value separated by commas, then tap Calculate.")
                            OutlinedTextField(
                                value = state.input,
                                onValueChange = viewModel::updateInput,
                                label = { Text("Parameters") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                            Button(onClick = { viewModel.process(parseParams(state.input)) }, modifier = Modifier.fillMaxWidth()) { Text("Calculate") }
                        }
                        ToolType.FILE_PROCESSOR, ToolType.PDF_PROCESSOR, ToolType.IMAGE_PROCESSOR -> {
                            val mime = when (tool.toolType) {
                                ToolType.PDF_PROCESSOR -> arrayOf("application/pdf")
                                ToolType.IMAGE_PROCESSOR -> arrayOf("image/*")
                                else -> arrayOf("*/*")
                            }
                            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                                uri?.let { viewModel.processFile(it, parseParams(state.input)) }
                            }
                            Text("Pick a file to process. Optional params as key=value,comma separated.")
                            OutlinedTextField(
                                value = state.input,
                                onValueChange = viewModel::updateInput,
                                label = { Text("Parameters") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                            Button(onClick = { launcher.launch(mime) }, modifier = Modifier.fillMaxWidth()) { Text("Pick File") }
                        }
                        else -> {
                            OutlinedTextField(
                                value = state.input,
                                onValueChange = viewModel::updateInput,
                                label = { Text("Input") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                            Button(onClick = { viewModel.process() }, modifier = Modifier.fillMaxWidth()) { Text("Process") }
                        }
                    }

                    if (state.loading) {
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    }

                    state.error?.let {
                        Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    }

                    if (state.output.isNotBlank()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Result", style = MaterialTheme.typography.titleMedium)
                                Text(state.output)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilledTonalButton(onClick = { clipboard.setText(AnnotatedString(state.output)) }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                                        Text("Copy")
                                    }
                                    FilledTonalButton(onClick = {
                                        val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, state.output)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(send, "Share"))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = null)
                                        Text("Share")
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: Text("Tool not found")
        }
    }
}

private fun parseParams(input: String): Map<String, String> {
    return input.split(",")
        .map { it.trim() }
        .filter { "=" in it }
        .associate { it.substringBefore("=") to it.substringAfter("=") }
}
