package org.phireox.ofa.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.phireox.ofa.core.ui.components.CategoryCard
import org.phireox.ofa.core.ui.components.ToolCard
import org.phireox.ofa.data.local.PrefsDataStore
import org.phireox.ofa.data.model.ToolCategory
import org.phireox.ofa.data.model.ToolRegistry

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onToolClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotesClick: () -> Unit = {},
    onEmergencyClick: () -> Unit = {},
    onCompassClick: () -> Unit = {},
    onOfflineMapsClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onMessagingClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { PrefsDataStore(context) }
    val favorites by prefs.favorites.collectAsState(initial = emptySet())
    val recent by prefs.recentTools.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OFA") },
                actions = {
                    IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, contentDescription = "Search") }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text("One For All", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Notes",
                subtitle = "Local notes & checklists",
                icon = Icons.Filled.Notes,
                onClick = onNotesClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Emergency",
                subtitle = "SOS, flashlight & siren",
                icon = Icons.Filled.Warning,
                onClick = onEmergencyClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Compass",
                subtitle = "Heading & direction",
                icon = Icons.Filled.Explore,
                onClick = onCompassClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Offline Maps",
                subtitle = "Saved maps & coverage",
                icon = Icons.Filled.Map,
                onClick = onOfflineMapsClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Weather",
                subtitle = "Forecasts & conditions",
                icon = Icons.Filled.WbSunny,
                onClick = onWeatherClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            org.phireox.ofa.core.ui.components.QuickAccessCard(
                title = "Messaging",
                subtitle = "Local E2EE chat",
                icon = Icons.Filled.Chat,
                onClick = onMessagingClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            if (favorites.isNotEmpty()) {
                Text("Favorites", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
                LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favorites.toList(), key = { it }) { id ->
                        ToolRegistry.byId(id)?.let { tool ->
                            ToolCard(
                                tool = tool,
                                isFavorite = true,
                                onClick = { onToolClick(tool.id) },
                                onToggleFavorite = { scope.launch { prefs.toggleFavorite(tool.id) } },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
            Text("Recently used", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recent, key = { it }) { id ->
                    ToolRegistry.byId(id)?.let { tool ->
                        ToolCard(
                            tool = tool,
                            isFavorite = favorites.contains(tool.id),
                            onClick = { onToolClick(tool.id) },
                            onToggleFavorite = { scope.launch { prefs.toggleFavorite(tool.id) } },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
            Text("Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ToolCategory.entries.toList(), key = { it.key }) { cat ->
                    val count = ToolRegistry.byCategory(cat).size
                    CategoryCard(category = cat, title = stringResource(id = cat.titleRes), count = count, modifier = Modifier.animateItemPlacement()) { onCategoryClick(cat.key) }
                }
            }
        }
    }
}
