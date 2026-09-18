package org.phireox.ofa.data.model

import java.util.UUID

enum class VaultItemType { PASSWORD, TOTP }

data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val username: String = "",
    val secret: String = "",
    val url: String = "",
    val notes: String = "",
    val type: VaultItemType = VaultItemType.PASSWORD,
    val createdAt: Long = System.currentTimeMillis()
)
