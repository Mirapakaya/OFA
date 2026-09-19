package org.phireox.ofa.data.local

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context) {

    private val prefs by lazy { PrefsDataStore(context) }

    suspend fun createBackup(): Result<Uri?> = withContext(Dispatchers.IO) {
        runCatching {
            val notesFile = File(context.filesDir, "ofa_notes/index.json")
            val notesText = if (notesFile.exists()) notesFile.readText() else "{\"notes\":[]}"

            val messagesDir = File(context.filesDir, "ofa_messages")
            val messagesFile = File(messagesDir, "index.json")
            val messagesText = if (messagesFile.exists()) messagesFile.readText() else "{\"conversations\":[]}"
            val messageFiles = JSONObject()
            messagesDir.listFiles { _, name -> name.endsWith(".json") }?.forEach { file ->
                runCatching { messageFiles.put(file.name, file.readText()) }
            }

            val prefsMap = prefs.export()
            val prefsJson = JSONObject()
            prefsMap.forEach { (key, value) ->
                when (value) {
                    is String -> prefsJson.put(key, value)
                    is Boolean -> prefsJson.put(key, value)
                    is Set<*> -> prefsJson.put(key, JSONArray(value.toList()))
                    else -> prefsJson.put(key, value.toString())
                }
            }

            val backup = JSONObject().apply {
                put("version", 1)
                put("createdAt", System.currentTimeMillis())
                put("preferences", prefsJson)
                put("notes", JSONObject(notesText).optJSONArray("notes") ?: JSONArray())
                put("conversations", JSONObject(messagesText).optJSONArray("conversations") ?: JSONArray())
                put("messageFiles", messageFiles)
            }

            saveBackupFile(backup.toString(2))
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun restoreBackup(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val content = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                ?: throw IllegalStateException("Could not read backup file")
            val json = JSONObject(content)
            if (json.optInt("version", 1) > 1) {
                throw IllegalStateException("Unsupported backup version")
            }

            val prefsJson = json.optJSONObject("preferences")
            if (prefsJson != null) {
                val map = mutableMapOf<String, Any>()
                prefsJson.keys().forEach { key ->
                    val value = prefsJson.get(key)
                    if (value is JSONArray) {
                        val set = mutableSetOf<String>()
                        for (i in 0 until value.length()) set.add(value.getString(i))
                        map[key] = set
                    } else {
                        map[key] = value
                    }
                }
                prefs.import(map)
            }

            val notesArray = json.optJSONArray("notes") ?: JSONArray()
            val notesFile = File(context.filesDir, "ofa_notes/index.json")
            notesFile.parentFile?.mkdirs()
            notesFile.writeText(JSONObject().put("notes", notesArray).toString(2))

            val conversationsArray = json.optJSONArray("conversations") ?: JSONArray()
            val messagesDir = File(context.filesDir, "ofa_messages")
            messagesDir.mkdirs()
            File(messagesDir, "index.json").writeText(JSONObject().put("conversations", conversationsArray).toString(2))
            val messageFiles = json.optJSONObject("messageFiles")
            messageFiles?.keys()?.forEach { name ->
                val content = messageFiles.optString(name, "")
                File(messagesDir, name).writeText(content)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun saveBackupFile(content: String): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "ofa_backup_$timestamp.json"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/OFA/Backups")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let { context.contentResolver.openOutputStream(it)?.use { out -> out.write(content.toByteArray()) } }
            uri
        } else {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir, "OFA/Backups")
            dir.mkdirs()
            val file = File(dir, fileName)
            file.writeText(content)
            Uri.fromFile(file)
        }
    }
}
