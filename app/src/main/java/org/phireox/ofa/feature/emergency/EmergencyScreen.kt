package org.phireox.ofa.feature.emergency

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var countdown by remember { mutableStateOf(0) }
    var sosMessage by remember { mutableStateOf("I need help. Here is my location: ") }
    var flashlightOn by remember { mutableStateOf(false) }
    var sirenPlaying by remember { mutableStateOf(false) }
    val vibrator = remember { getVibrator(context) }

    DisposableEffect(Unit) {
        onDispose {
            setFlashlight(context, false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SOS", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            countdown = 3
                            scope.launch {
                                while (countdown > 0) {
                                    delay(1000)
                                    countdown--
                                }
                                triggerSos(context, sosMessage)
                                vibrateCompat(vibrator, 500)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null)
                        Text(if (countdown > 0) "Hold on... $countdown" else "Send SOS", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sosMessage,
                        onValueChange = { sosMessage = it },
                        label = { Text("Emergency message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                    onClick = {
                        flashlightOn = !flashlightOn
                        setFlashlight(context, flashlightOn)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(if (flashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff, contentDescription = null)
                    Text(if (flashlightOn) "Flashlight On" else "Flashlight")
                }
                FilledTonalButton(
                    onClick = {
                        sirenPlaying = !sirenPlaying
                        if (sirenPlaying) {
                            try {
                                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                val ringtone = RingtoneManager.getRingtone(context, notification)
                                ringtone.play()
                                scope.launch {
                                    delay(3000)
                                    if (ringtone.isPlaying) ringtone.stop()
                                    sirenPlaying = false
                                }
                            } catch (_: Exception) {
                                sirenPlaying = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.NotificationImportant, contentDescription = null)
                    Text(if (sirenPlaying) "Siren..." else "Siren")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Emergency call", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    FilledTonalButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        context.startActivity(android.content.Intent.createChooser(intent, "Call emergency"))
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Phone, contentDescription = null)
                        Text("Dial 112")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Medical information", style = MaterialTheme.typography.titleMedium)
                    Text("Blood group, allergies and important notes can be stored locally in Settings.")
                }
            }
        }
    }
}

private fun getVibrator(context: Context): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
}

private fun vibrateCompat(vibrator: Vibrator?, durationMs: Long) {
    vibrator?.let { v ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(durationMs)
        }
    }
}

private fun setFlashlight(context: Context, on: Boolean) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
    try {
        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.setTorchMode(cameraId, on)
        }
    } catch (_: CameraAccessException) {
    }
}

private fun triggerSos(context: Context, message: String) {
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, message)
    }
    context.startActivity(android.content.Intent.createChooser(intent, "SOS"))
}
