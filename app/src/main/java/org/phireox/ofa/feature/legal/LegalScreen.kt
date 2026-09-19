package org.phireox.ofa.feature.legal

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(onBack: () -> Unit) {
    val sections = listOf(
        "Privacy Policy" to PRIVACY_POLICY,
        "Terms of Service" to TERMS_OF_SERVICE,
        "Terms & Conditions" to TERMS_AND_CONDITIONS,
        "EULA" to EULA,
        "Refund Information" to REFUND_INFO,
        "Subscription Terms" to SUBSCRIPTION_TERMS,
        "Third-Party Services" to THIRD_PARTY_SERVICES,
        "External Provider Disclosure" to PROVIDER_DISCLOSURE,
        "Open-Source Licenses" to OPEN_SOURCE_LICENSES,
        "Copyright/Trademark Notice" to COPYRIGHT_NOTICE,
        "Data Deletion" to DATA_DELETION,
        "Network Feature Disclosure" to NETWORK_DISCLOSURE,
        "AI Disclosure" to AI_DISCLOSURE,
        "Emergency Disclaimer" to EMERGENCY_DISCLAIMER
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legal & Privacy") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sections) { (title, body) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

private val PRIVACY_POLICY = """
OFA ("One For All") is built around a local-first architecture. Most tools process data directly on your Android device. OFA does not operate general cloud storage, user databases, or analytics warehouses for local tools. When you use network-dependent features (for example, temporary mail, temporary phone, online meetings, weather, or external AI), data is sent to the chosen third-party provider, not to OFA. We do not sell your data. Sensitive data such as passwords, vault items, and biometric-backed notes are stored on-device using Android Keystore.
""".trimIndent()

private val TERMS_OF_SERVICE = """
By using OFA you agree to use the tools lawfully and responsibly. OFA is provided "as is" without warranties. Local tools run on your device; network tools rely on external providers and are subject to those providers' terms. Business, financial, engineering, and medical-related tools are informational only and do not replace professional advice.
""".trimIndent()

private val TERMS_AND_CONDITIONS = """
These Terms & Conditions govern use of the OFA application. You retain ownership of content you create with local tools. Do not use OFA to violate applicable laws, platform policies, or third-party rights. Premium subscriptions are handled through the relevant billing provider.
""".trimIndent()

private val EULA = """
This End User License Agreement grants you a limited, non-exclusive license to use OFA on devices you own or control. You may not reverse-engineer, modify, or redistribute the application except as permitted by law.
""".trimIndent()

private val REFUND_INFO = """
Refund handling follows the policies of the store or payment provider through which the purchase was made (e.g., Google Play). Contact the respective support channel for refund requests.
""".trimIndent()

private val SUBSCRIPTION_TERMS = """
OFA offers optional Premium subscriptions. Pricing and billing are managed by the store/payment provider. Subscriptions auto-renew according to the provider's terms. You can cancel or manage subscriptions through the provider's account settings.
""".trimIndent()

private val THIRD_PARTY_SERVICES = """
OFA integrates open-source and third-party libraries (e.g., PDFBox-Android, ZXing, Razorpay, AndroidX) under their respective licenses. Network features may use external providers selected by the user. OFA does not control those providers.
""".trimIndent()

private val PROVIDER_DISCLOSURE = """
Network features require external providers. Before using temporary mail, temporary SMS, meetings, weather, AI, or social/public media tools, you choose or configure a provider. Data is sent directly to that provider under its terms. OFA infrastructure does not receive, store, or process that data.
""".trimIndent()

private val OPEN_SOURCE_LICENSES = """
OFA uses open-source components including but not limited to: Android Jetpack, Kotlin Coroutines, Compose, PDFBox-Android (Apache 2.0), ZXing (Apache 2.0), and Razorpay SDK. Their respective licenses and notices apply.
""".trimIndent()

private val COPYRIGHT_NOTICE = """
OFA and its original code are proprietary. Third-party marks and libraries belong to their respective owners. OFA is not affiliated with or endorsed by My Hero Academia, Shueisha, or any other rights holder.
""".trimIndent()

private val DATA_DELETION = """
You can delete all OFA data from Settings. This removes preferences, favorites, recent tools, notes, vault items, and locally cached files. Subscription records remain with the billing provider and must be managed there.
""".trimIndent()

private val NETWORK_DISCLOSURE = """
Local tools work offline. Network tools require an internet connection and an external provider. OFA shows which tools require a network connection. We do not mask network activity as offline processing.
""".trimIndent()

private val AI_DISCLOSURE = """
AI features clearly distinguish deterministic, AI-generated, and uncertain results. Local AI runs on-device where feasible. External AI sends data to the configured provider only with explicit user action.
""".trimIndent()

private val EMERGENCY_DISCLAIMER = """
OFA's emergency/SOS tools are aids, not replacements for official emergency services. Call local emergency numbers directly when possible. SOS actions depend on device capabilities, permissions, and network availability. OFA cannot guarantee delivery of emergency messages.
""".trimIndent()
