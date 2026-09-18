package org.phireox.ofa.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolType {
    TEXT_PROCESSOR,
    FILE_PROCESSOR,
    CALCULATOR,
    GENERATOR,
    CONVERTER,
    BUSINESS_TEMPLATE,
    QR_GENERATOR,
    QR_SCANNER,
    PDF_PROCESSOR,
    IMAGE_PROCESSOR,
    DATA_PROCESSOR,
    PROVIDER,
    TEMPORARY
}

data class Tool(
    val id: String,
    val title: String,
    val description: String,
    val category: ToolCategory,
    val aliases: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val icon: ImageVector = Icons.Default.Build,
    val toolType: ToolType = ToolType.TEXT_PROCESSOR,
    val localOnly: Boolean = true,
    val requiresNetwork: Boolean = false,
    val premium: Boolean = false,
    val quota: QuotaType = QuotaType.NONE,
    val supportsBatch: Boolean = false,
    val supportedInputTypes: List<String> = emptyList()
)

enum class QuotaType {
    NONE,
    DAILY_FREE,
    PREMIUM
}
