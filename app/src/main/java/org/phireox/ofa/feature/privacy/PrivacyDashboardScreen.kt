package org.phireox.ofa.feature.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.phireox.ofa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDashboardScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.privacy_dashboard)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Local-first by design", style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.local_processing_explanation))
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Processed on this device", style = MaterialTheme.typography.titleMedium)
                    Text("• PDF processing, image processing, SVG tools\n" +
                        "• Text, document and code utilities\n" +
                        "• CSV/data tools, calculators, QR/barcode generation\n" +
                        "• Local notes, business documents, engineering calculators\n" +
                        "• Privacy scanners, file operations, metadata tools")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Uses external network services", style = MaterialTheme.typography.titleMedium)
                    Text("• Temporary mail, temporary phone/SMS\n" +
                        "• Online meeting rooms, internet file sharing\n" +
                        "• Live weather, external AI, social/public-media retrieval\n" +
                        "• Payment verification and subscription status checks")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("What can leave the device", style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.network_feature_explanation) +
                        " Data sent to a provider is governed by that provider's privacy policy.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Your controls", style = MaterialTheme.typography.titleMedium)
                    Text("You can delete all OFA data, including preferences, favorites, recent tools, notes, vault items and cached files, from Settings → Delete all OFA data.")
                }
            }
        }
    }
}
