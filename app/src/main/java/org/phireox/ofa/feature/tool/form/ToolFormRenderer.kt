package org.phireox.ofa.feature.tool.form

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.phireox.ofa.data.model.Tool
import org.phireox.ofa.data.model.ToolType
import org.phireox.ofa.feature.tool.ToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolFormRenderer(tool: Tool, viewModel: ToolViewModel) {
    val specs = remember(tool.id) { ToolFormRegistry.forTool(tool.id) }
    val values = remember { mutableStateMapOf<String, String>() }

    val fileFields = specs.filter { it.type == FieldType.FILE || it.type == FieldType.FILES }
    val hasFileField = fileFields.isNotEmpty()
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val mimeType = remember(tool.toolType) {
        when (tool.toolType) {
            ToolType.PDF_PROCESSOR -> arrayOf("application/pdf")
            ToolType.IMAGE_PROCESSOR -> arrayOf("image/*")
            else -> arrayOf("*/*")
        }
    }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { selectedUri = it }
    }
    val filesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris?.let { selectedUris = it }
    }

    // Initialise defaults once
    remember(tool.id) {
        specs.forEach { values[it.key] = it.defaultValue }
        true
    }

    val onSubmit: () -> Unit = {
        val params = specs.associate { it.key to (values[it.key] ?: it.defaultValue) }
        if (hasFileField) {
            if (selectedUris.isNotEmpty()) {
                viewModel.processFiles(selectedUris, params)
            } else if (selectedUri != null) {
                viewModel.processFile(selectedUri!!, params)
            }
        } else {
            viewModel.process(params)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
    ) {
        specs.forEachIndexed { index, field ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300, index * 30)) + slideInVertically(tween(300, index * 30)) { it / 4 }
            ) {
                when (field.type) {
                    FieldType.FILE -> {
                        Surface(
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(field.label, style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { fileLauncher.launch(mimeType) }, modifier = Modifier.fillMaxWidth()) {
                                    Text(if (selectedUri == null) "Select file" else "Change file")
                                }
                                selectedUri?.let {
                                    Spacer(Modifier.height(4.dp))
                                    Text(it.toString(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    FieldType.FILES -> {
                        Surface(
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(field.label, style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { filesLauncher.launch(mimeType) }, modifier = Modifier.fillMaxWidth()) {
                                    Text(if (selectedUris.isEmpty()) "Select files" else "${selectedUris.size} selected")
                                }
                                if (selectedUris.isNotEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(selectedUris.joinToString { it.lastPathSegment ?: "" }, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    FieldType.BOOLEAN -> {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(field.label, modifier = Modifier.weight(1f))
                            Switch(
                                checked = (values[field.key] ?: field.defaultValue).toBooleanStrictOrNull() ?: false,
                                onCheckedChange = { values[field.key] = it.toString() }
                            )
                        }
                    }
                    FieldType.DROPDOWN -> {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedTextField(
                                value = values[field.key] ?: field.defaultValue,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(field.label) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                field.options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            values[field.key] = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        val isNumber = field.type == FieldType.NUMBER || field.type == FieldType.INTEGER
                        val keyboardType = when (field.type) {
                            FieldType.INTEGER -> KeyboardType.Number
                            FieldType.NUMBER -> KeyboardType.Decimal
                            FieldType.DATE -> KeyboardType.Text
                            else -> KeyboardType.Text
                        }
                        OutlinedTextField(
                            value = values[field.key] ?: field.defaultValue,
                            onValueChange = { values[field.key] = it },
                            label = { Text(field.label) },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
                            minLines = if (field.type == FieldType.MULTILINE) 4 else 1,
                            maxLines = if (field.type == FieldType.MULTILINE) 8 else 1,
                            singleLine = field.type != FieldType.MULTILINE,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = true, enter = fadeIn()) {
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) {
                Text(if (tool.toolType == ToolType.QR_GENERATOR) "Generate" else if (hasFileField) "Process file" else "Run ${tool.title}")
            }
        }
    }
}
