package org.phireox.ofa.data.model

import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val folder: String = DEFAULT_FOLDER,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = false
) {
    companion object {
        const val DEFAULT_FOLDER = "Notes"
    }
}
