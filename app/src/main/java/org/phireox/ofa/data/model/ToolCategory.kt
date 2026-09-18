package org.phireox.ofa.data.model

import androidx.annotation.StringRes
import org.phireox.ofa.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolCategory(
    val key: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    HOME("home", R.string.category_home, Icons.Default.Home),
    PDF("pdf", R.string.category_pdf, Icons.Default.PictureAsPdf),
    IMAGE("image", R.string.category_image, Icons.Default.Image),
    SVG("svg", R.string.category_svg, Icons.Default.FormatPaint),
    DOCUMENT("document", R.string.category_document, Icons.Default.Description),
    DEVELOPER("developer", R.string.category_developer, Icons.Default.Code),
    DATA("data", R.string.category_data, Icons.Default.TableChart),
    PRIVACY("privacy", R.string.category_privacy, Icons.Default.Security),
    BUSINESS("business", R.string.category_business, Icons.Default.BusinessCenter),
    GST("gst", R.string.category_gst, Icons.Default.AccountBalance),
    EDUCATION("education", R.string.category_education, Icons.Default.School),
    ENGINEERING("engineering", R.string.category_engineering, Icons.Default.Construction),
    QR("qr", R.string.category_qr, Icons.Default.QrCode),
    FILES("files", R.string.category_files, Icons.Default.Folder),
    SOCIAL("social", R.string.category_social, Icons.Default.Share),
    TEMPORARY("temporary", R.string.category_temporary, Icons.Default.HourglassEmpty),
    COMMUNICATION("communication", R.string.category_communication, Icons.Default.Chat),
    FAVORITES("favorites", R.string.category_favorites, Icons.Default.Star),
    RECENT("recent", R.string.category_recent, Icons.Default.History),
    SETTINGS("settings", R.string.category_settings, Icons.Default.Settings)
}
