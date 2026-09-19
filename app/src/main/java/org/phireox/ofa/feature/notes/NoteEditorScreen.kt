package org.phireox.ofa.feature.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.phireox.ofa.data.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(noteId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context.applicationContext as android.app.Application))
    val scope = rememberCoroutineScope()
    val note = remember { viewModel.repository.byId(noteId) }
    val title = remember { mutableStateOf(note?.title ?: "") }
    val content = remember { mutableStateOf(note?.content ?: "") }
    val folder = remember { mutableStateOf(note?.folder ?: Note.DEFAULT_FOLDER) }
    val tags = remember { mutableStateOf(note?.tags?.joinToString(", ") ?: "") }

    LaunchedEffect(noteId) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId.isBlank()) "New note" else "Edit note") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = title.value, onValueChange = { title.value = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = folder.value, onValueChange = { folder.value = it }, label = { Text("Folder") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = tags.value, onValueChange = { tags.value = it }, label = { Text("Tags (comma separated)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(
                value = content.value,
                onValueChange = { content.value = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                minLines = 10,
                maxLines = 20
            )
            Button(
                onClick = {
                    scope.launch {
                        viewModel.save(
                            Note(
                                id = noteId.ifBlank { java.util.UUID.randomUUID().toString() },
                                title = title.value,
                                content = content.value,
                                folder = folder.value.ifBlank { Note.DEFAULT_FOLDER },
                                tags = tags.value.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            )
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
