package org.phireox.ofa.data.model

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
