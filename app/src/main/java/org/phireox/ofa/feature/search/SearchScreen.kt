package org.phireox.ofa.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.phireox.ofa.core.ui.components.ToolCard
import org.phireox.ofa.data.local.PrefsDataStore
import org.phireox.ofa.data.model.ToolRegistry

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(onBack: () -> Unit, onToolClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember { PrefsDataStore(context) }
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val results = remember(query) { ToolRegistry.search(query) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    label = { Text("Search tools") }
                )
            }
            items(results, key = { it.id }) { tool ->
                ToolCard(
                    tool = tool,
                    isFavorite = favorites.contains(tool.id),
                    onClick = { onToolClick(tool.id) },
                    onToggleFavorite = { },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}
