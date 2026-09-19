package org.phireox.ofa.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class MessagingRepository(private val context: Context) {

    private val dir: File by lazy { File(context.filesDir, "ofa_messages").apply { mkdirs() } }
    private val indexFile: File by lazy { File(dir, "index.json") }

    private val _conversations = MutableStateFlow<List<String>>(emptyList())
    val conversations: StateFlow<List<String>> = _conversations.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        if (!indexFile.exists()) return
        try {
            val json = JSONObject(indexFile.readText())
            val array = json.optJSONArray("conversations") ?: JSONArray()
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) list.add(array.getString(i))
            _conversations.value = list
        } catch (e: Exception) {
            _conversations.value = emptyList()
        }
    }

    private fun saveConversations() {
        val array = JSONArray(_conversations.value)
        indexFile.writeText(JSONObject().put("conversations", array).toString(2))
    }

    suspend fun createConversation(name: String) = withContext(Dispatchers.IO) {
        if (name.isBlank()) return@withContext
        val current = _conversations.value.toMutableList()
        if (!current.contains(name)) {
            current.add(name)
            _conversations.value = current
            saveConversations()
        }
    }

    suspend fun deleteConversation(name: String) = withContext(Dispatchers.IO) {
        val current = _conversations.value.toMutableList()
        if (current.remove(name)) {
            _conversations.value = current
            saveConversations()
            File(dir, "$name.json").delete()
        }
    }

    fun messages(name: String): List<Message> {
        val file = File(dir, "$name.json")
        if (!file.exists()) return emptyList()
        return try {
            val json = JSONObject(file.readText())
            val array = json.optJSONArray("messages") ?: JSONArray()
            (0 until array.length()).map { messageFromJson(array.getJSONObject(it)) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(name: String, text: String, password: String) = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext
        val key = deriveKey(password)
        val encrypted = encrypt(text.toByteArray(Charsets.UTF_8), key)
        val list = messages(name).toMutableList()
        list.add(Message(id = UUID.randomUUID().toString(), content = encrypted, createdAt = System.currentTimeMillis()))
        saveMessages(name, list)
    }

    fun decryptMessage(message: Message, password: String): String {
        return try {
            val key = deriveKey(password)
            String(decrypt(message.content, key), Charsets.UTF_8)
        } catch (e: Exception) {
            "[unable to decrypt]"
        }
    }

    private fun saveMessages(name: String, messages: List<Message>) {
        val array = JSONArray()
        messages.forEach { array.put(messageToJson(it)) }
        File(dir, "$name.json").writeText(JSONObject().put("messages", array).toString(2))
    }

    private fun messageToJson(message: Message): JSONObject {
        return JSONObject().apply {
            put("id", message.id)
            put("content", message.content)
            put("createdAt", message.createdAt)
        }
    }

    private fun messageFromJson(json: JSONObject): Message {
        return Message(
            id = json.optString("id", UUID.randomUUID().toString()),
            content = json.optString("content", ""),
            createdAt = json.optLong("createdAt", System.currentTimeMillis())
        )
    }

    private fun deriveKey(password: String): SecretKey {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(bytes, "AES")
    }

    private fun encrypt(plaintext: ByteArray, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext)
        val combined = iv + encrypted
        return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
    }

    private fun decrypt(base64: String, key: SecretKey): ByteArray {
        val combined = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted)
    }

    data class Message(val id: String, val content: String, val createdAt: Long)
}
