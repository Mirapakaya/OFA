package org.phireox.ofa.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.phireox.ofa.data.model.Note
import java.io.File
import java.util.UUID

class NotesRepository(private val context: Context) {

    private val dir: File by lazy {
        File(context.filesDir, "ofa_notes").apply { mkdirs() }
    }

    private val indexFile: File by lazy { File(dir, "index.json") }

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: Flow<List<Note>> = _notes.asStateFlow()

    init {
        loadIndex()
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        loadIndex()
    }

    private fun loadIndex() {
        val list = if (indexFile.exists()) {
            try {
                val json = JSONObject(indexFile.readText())
                val array = json.optJSONArray("notes") ?: JSONArray()
                (0 until array.length()).map { index -> noteFromJson(array.getJSONObject(index)) }
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
        _notes.value = list.sortedByDescending { it.updatedAt }
    }

    private fun saveIndex(notes: List<Note>) {
        val array = JSONArray()
        notes.forEach { array.put(noteToJson(it)) }
        val json = JSONObject().put("notes", array)
        indexFile.writeText(json.toString(2))
    }

    suspend fun save(note: Note) = withContext(Dispatchers.IO) {
        val current = _notes.value.toMutableList()
        current.removeAll { it.id == note.id }
        val toSave = note.copy(updatedAt = System.currentTimeMillis())
        current.add(0, toSave)
        saveIndex(current)
        _notes.value = current.sortedByDescending { it.updatedAt }
    }

    suspend fun delete(noteId: String) = withContext(Dispatchers.IO) {
        val current = _notes.value.filterNot { it.id == noteId }
        saveIndex(current)
        _notes.value = current
    }

    fun search(query: String): List<Note> {
        val q = query.trim().lowercase()
        return _notes.value.filter { note ->
            note.title.lowercase().contains(q) ||
            note.content.lowercase().contains(q) ||
            note.tags.any { it.lowercase().contains(q) } ||
            note.folder.lowercase().contains(q)
        }
    }

    fun byId(id: String): Note? = _notes.value.find { it.id == id }

    fun folders(): List<String> = _notes.value.map { it.folder }.distinct().sorted()

    fun tags(): List<String> = _notes.value.flatMap { it.tags }.distinct().sorted()

    private fun noteToJson(note: Note): JSONObject {
        return JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("folder", note.folder)
            put("tags", JSONArray(note.tags))
            put("createdAt", note.createdAt)
            put("updatedAt", note.updatedAt)
            put("isEncrypted", note.isEncrypted)
        }
    }

    private fun noteFromJson(json: JSONObject): Note {
        return Note(
            id = json.optString("id", UUID.randomUUID().toString()),
            title = json.optString("title", ""),
            content = json.optString("content", ""),
            folder = json.optString("folder", Note.DEFAULT_FOLDER),
            tags = (0 until (json.optJSONArray("tags")?.length() ?: 0)).map { json.optJSONArray("tags")?.getString(it) ?: "" },
            createdAt = json.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = json.optLong("updatedAt", System.currentTimeMillis()),
            isEncrypted = json.optBoolean("isEncrypted", false)
        )
    }

}
