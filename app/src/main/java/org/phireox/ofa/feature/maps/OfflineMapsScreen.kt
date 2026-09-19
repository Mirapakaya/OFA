package org.phireox.ofa.feature.maps

import android.net.Uri
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineMapsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var reader by remember { mutableStateOf<MbtilesReader?>(null) }
    var zoom by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("No MBTiles imported") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val imported = MbtilesReader.import(context, uri)
        reader = imported
        if (imported != null && imported.isOpen()) {
            val min = imported.minZoom()
            val max = imported.maxZoom()
            zoom = min.toFloat().coerceIn(min.toFloat(), max.toFloat())
            status = "Imported: zoom $min..$max"
        } else {
            status = "Failed to import MBTiles"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Maps") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Offline-first maps", style = MaterialTheme.typography.titleMedium)
                        Text("Import an MBTiles file (SQLite-based offline map pack). The viewer renders tiles locally without internet.")
                        Button(onClick = { picker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("Import MBTiles") }
                        Text(status, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                val currentReader = reader
                if (currentReader != null && currentReader.isOpen()) {
                    val min = currentReader.minZoom()
                    val max = currentReader.maxZoom()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Zoom: ${zoom.toInt()} (range $min..$max)")
                        Slider(value = zoom, onValueChange = { zoom = it }, valueRange = min.toFloat()..max.toFloat(), steps = max - min)
                        val zoomInt = zoom.toInt()
                        val currentTiles = remember(zoomInt) { currentReader.listTiles(zoomInt) }
                        Text("Tiles at zoom $zoomInt: ${currentTiles.size}")
                        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(currentTiles, key = { "${it.x}-${it.y}" }) { tile ->
                                val bitmap = remember(tile, zoomInt) {
                                    currentReader.getTile(zoomInt, tile.x, tile.y)?.data?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }?.asImageBitmap()
                                }
                                if (bitmap != null) {
                                    Image(bitmap = bitmap, contentDescription = "map tile", modifier = Modifier.size(128.dp))
                                } else {
                                    Text("...", modifier = Modifier.size(128.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
