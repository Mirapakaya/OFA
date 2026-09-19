package org.phireox.ofa.feature.diagnostics

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.phireox.ofa.BuildConfig
import org.phireox.ofa.data.model.ToolRegistry
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val items = remember { buildDiagnostics(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(item.value, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

private data class DiagnosticItem(val label: String, val value: String)

private fun buildDiagnostics(context: Context): List<DiagnosticItem> {
    val runtime = Runtime.getRuntime()
    val totalMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()
    val maxMemory = runtime.maxMemory()

    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersion = packageInfo?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            "${it.longVersionCode} (${it.versionName})"
        } else {
            "${it.versionName}"
        }
    } ?: "unknown"

    val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: Build.CPU_ABI

    val storage = runCatching {
        val cacheDir = context.cacheDir
        val filesDir = context.filesDir
        val cacheSize = cacheDir?.let { folderSize(it) } ?: 0L
        val filesSize = filesDir?.let { folderSize(it) } ?: 0L
        "Cache: ${formatBytes(cacheSize)}, Files: ${formatBytes(filesSize)}"
    }.getOrDefault("Unavailable")

    val network = runCatching {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val active = cm?.activeNetworkInfo
        when {
            active == null -> "No network"
            active.isConnected -> "Connected (${if (active.type == ConnectivityManager.TYPE_WIFI) "WiFi" else "Mobile"})"
            else -> "Disconnected"
        }
    }.getOrDefault("Unknown")

    val memory = runCatching {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        "Total: ${formatBytes(info.totalMem)}, Available: ${formatBytes(info.availMem)}, Threshold: ${formatBytes(info.threshold)}"
    }.getOrDefault("Unavailable")

    return listOf(
        DiagnosticItem("App version", appVersion),
        DiagnosticItem("Package", context.packageName),
        DiagnosticItem("Android version", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"),
        DiagnosticItem("Device", "${Build.MANUFACTURER} ${Build.MODEL}"),
        DiagnosticItem("ABI", abi),
        DiagnosticItem("Memory", memory),
        DiagnosticItem("App memory", "Total: ${formatBytes(totalMemory)}, Free: ${formatBytes(freeMemory)}, Max: ${formatBytes(maxMemory)}"),
        DiagnosticItem("Storage", storage),
        DiagnosticItem("Network", network),
        DiagnosticItem("Tools registered", ToolRegistry.tools.size.toString())
    )
}

private fun folderSize(dir: File): Long {
    var size = 0L
    dir.listFiles()?.forEach { file ->
        size += if (file.isDirectory) folderSize(file) else file.length()
    }
    return size
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
        bytes >= 1024 -> "%.2f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
