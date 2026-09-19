package org.phireox.ofa.feature.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.phireox.ofa.data.local.NotesRepository
import org.phireox.ofa.data.model.Note

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    val repository = NotesRepository(application.applicationContext)
    val notes: Flow<List<Note>> = repository.notes

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) { repository.refresh() }
    }

    fun save(note: Note) {
        viewModelScope.launch(Dispatchers.IO) { repository.save(note) }
    }

    fun delete(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.delete(noteId) }
    }
}
