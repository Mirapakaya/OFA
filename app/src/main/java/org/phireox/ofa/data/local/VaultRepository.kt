package org.phireox.ofa.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import org.json.JSONArray
import org.json.JSONObject
import org.phireox.ofa.core.security.VaultCrypto
import org.phireox.ofa.data.model.VaultItem
import org.phireox.ofa.data.model.VaultItemType
import java.io.File

class VaultRepository(context: Context) {

    private val file = File(context.filesDir, "ofa_vault.dat")

    fun load(): List<VaultItem> {
        if (!file.exists()) return emptyList()
        val encrypted = file.readText()
        if (encrypted.isBlank()) return emptyList()
        return try {
            val json = VaultCrypto.decrypt(encrypted)
            parse(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(items: List<VaultItem>) {
        val json = serialize(items)
        val encrypted = VaultCrypto.encrypt(json)
        file.writeText(encrypted)
    }

    private fun serialize(items: List<VaultItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("username", item.username)
                put("secret", item.secret)
                put("url", item.url)
                put("notes", item.notes)
                put("type", item.type.name)
                put("createdAt", item.createdAt)
            })
        }
        return array.toString()
    }

    private fun parse(json: String): List<VaultItem> {
        val array = JSONArray(json)
        val list = mutableListOf<VaultItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                VaultItem(
                    id = obj.getString("id"),
                    name = obj.optString("name"),
                    username = obj.optString("username"),
                    secret = obj.optString("secret"),
                    url = obj.optString("url"),
                    notes = obj.optString("notes"),
                    type = runCatching { VaultItemType.valueOf(obj.optString("type", "PASSWORD")) }.getOrDefault(VaultItemType.PASSWORD),
                    createdAt = obj.optLong("createdAt")
                )
            )
        }
        return list
    }
}
