package org.phireox.ofa.feature.tool

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.PrefsDataStore
import org.phireox.ofa.data.model.ToolType
import org.phireox.ofa.feature.tool.form.ToolFormRenderer

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
    val snackbarHostState = remember { SnackbarHostState() }
    val prefs = remember { PrefsDataStore(context) }
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()
    val isFavorite = favorites.contains(toolId)

    LaunchedEffect(state.output) {
        if (state.output.isNotBlank()) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.tool?.title ?: "Tool") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { scope.launch { prefs.toggleFavorite(toolId) } }) {
                        Icon(
                            if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (isFavorite) "Remove favorite" else "Add favorite"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.tool?.let { tool ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        tool.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (tool.requiresNetwork) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("External service required", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("This feature requires an external provider. No provider is configured. Data would be sent to the provider, not to OFA.")
                        }
                    }
                } else {
                    ToolFormRenderer(tool = tool, viewModel = viewModel)

                    if (tool.toolType == ToolType.QR_GENERATOR) {
                        AnimatedVisibility(visible = state.qrBitmap != null, enter = fadeIn(), exit = fadeOut()) {
                            state.qrBitmap?.let { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    }

                    state.error?.let {
                        Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    }

                    AnimatedVisibility(
                        visible = state.output.isNotBlank() && !state.loading,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Result", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(state.output)
                                Spacer(Modifier.height(8.dp))
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
