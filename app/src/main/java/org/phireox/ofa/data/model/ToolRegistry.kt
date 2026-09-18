package org.phireox.ofa.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object ToolRegistry {

    private val _tools = mutableListOf<Tool>()
    val tools: List<Tool> = _tools

    init {
        // PDF
        register(id = "pdf_merge", title = "Merge PDF", cat = ToolCategory.PDF, icon = Icons.Default.MergeType, type = ToolType.PDF_PROCESSOR, keywords = listOf("pdf", "combine"))
        register(id = "pdf_split", title = "Split PDF", cat = ToolCategory.PDF, icon = Icons.Default.Splitscreen, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_compress", title = "Compress PDF", cat = ToolCategory.PDF, icon = Icons.Default.Compress, type = ToolType.PDF_PROCESSOR, premium = true)
        register(id = "pdf_to_images", title = "PDF to Images", cat = ToolCategory.PDF, icon = Icons.Default.Image, type = ToolType.PDF_PROCESSOR)
        register(id = "images_to_pdf", title = "Images to PDF", cat = ToolCategory.PDF, icon = Icons.Default.PictureAsPdf, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_metadata", title = "PDF Metadata Viewer", cat = ToolCategory.PDF, icon = Icons.Default.Info, type = ToolType.PDF_PROCESSOR)

        // Images
        register(id = "image_compress", title = "Image Compressor", cat = ToolCategory.IMAGE, icon = Icons.Default.Compress, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_resize", title = "Image Resize", cat = ToolCategory.IMAGE, icon = Icons.Default.PhotoSizeSelectLarge, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_crop", title = "Image Crop", cat = ToolCategory.IMAGE, icon = Icons.Default.Crop, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_rotate", title = "Image Rotate", cat = ToolCategory.IMAGE, icon = Icons.Default.RotateRight, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_flip", title = "Image Flip", cat = ToolCategory.IMAGE, icon = Icons.Default.Flip, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_to_pdf", title = "Image to PDF", cat = ToolCategory.IMAGE, icon = Icons.Default.PictureAsPdf, type = ToolType.IMAGE_PROCESSOR)
        register(id = "exif_viewer", title = "EXIF Viewer", cat = ToolCategory.IMAGE, icon = Icons.Default.Info, type = ToolType.IMAGE_PROCESSOR)

        // SVG / Design
        register(id = "svg_optimizer", title = "SVG Optimizer", cat = ToolCategory.SVG, icon = Icons.Default.Brush, type = ToolType.TEXT_PROCESSOR)
        register(id = "favicon_generator", title = "Favicon Generator", cat = ToolCategory.SVG, icon = Icons.Default.Web, type = ToolType.GENERATOR)
        register(id = "color_palette", title = "Color Palette Generator", cat = ToolCategory.SVG, icon = Icons.Default.Palette, type = ToolType.GENERATOR)
        register(id = "contrast_checker", title = "Contrast Checker", cat = ToolCategory.SVG, icon = Icons.Default.Contrast, type = ToolType.CALCULATOR)

        // Documents
        register(id = "case_converter", title = "Case Converter", cat = ToolCategory.DOCUMENT, icon = Icons.Default.TextFields, type = ToolType.TEXT_PROCESSOR)
        register(id = "text_reverser", title = "Text Reverser", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Replay, type = ToolType.TEXT_PROCESSOR)
        register(id = "word_counter", title = "Word Counter", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FormatListNumbered, type = ToolType.TEXT_PROCESSOR)
        register(id = "whitespace_cleaner", title = "Whitespace Cleaner", cat = ToolCategory.DOCUMENT, icon = Icons.Default.CleaningServices, type = ToolType.TEXT_PROCESSOR)
        register(id = "text_diff", title = "Text Diff", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Difference, type = ToolType.TEXT_PROCESSOR)
        register(id = "text_sorter", title = "Text Sorter", cat = ToolCategory.DOCUMENT, icon = Icons.Default.SortByAlpha, type = ToolType.TEXT_PROCESSOR)
        register(id = "text_deduplicator", title = "Text Deduplicator", cat = ToolCategory.DOCUMENT, icon = Icons.Default.ContentCut, type = ToolType.TEXT_PROCESSOR)
        register(id = "lorem_ipsum", title = "Lorem Ipsum Generator", cat = ToolCategory.DOCUMENT, icon = Icons.Default.ShortText, type = ToolType.GENERATOR)
        register(id = "markdown_to_html", title = "Markdown to HTML", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Html, type = ToolType.TEXT_PROCESSOR)

        // Developer
        register(id = "json_formatter", title = "JSON Formatter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DataObject, type = ToolType.TEXT_PROCESSOR)
        register(id = "json_minifier", title = "JSON Minifier", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DataObject, type = ToolType.TEXT_PROCESSOR)
        register(id = "json_validator", title = "JSON Validator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Rule, type = ToolType.TEXT_PROCESSOR)
        register(id = "uuid_generator", title = "UUID Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Fingerprint, type = ToolType.GENERATOR)
        register(id = "hash_generator", title = "Hash Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Tag, type = ToolType.TEXT_PROCESSOR)
        register(id = "base64_encoder", title = "Base64 Encoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.VpnKey, type = ToolType.TEXT_PROCESSOR)
        register(id = "base64_decoder", title = "Base64 Decoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.VpnKey, type = ToolType.TEXT_PROCESSOR)
        register(id = "url_encoder", title = "URL Encoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Link, type = ToolType.TEXT_PROCESSOR)
        register(id = "url_decoder", title = "URL Decoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Link, type = ToolType.TEXT_PROCESSOR)
        register(id = "html_encoder", title = "HTML Entity Encoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Code, type = ToolType.TEXT_PROCESSOR)
        register(id = "html_decoder", title = "HTML Entity Decoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Code, type = ToolType.TEXT_PROCESSOR)
        register(id = "regex_tester", title = "Regex Tester", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Search, type = ToolType.TEXT_PROCESSOR)
        register(id = "timestamp_converter", title = "Timestamp Converter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Schedule, type = ToolType.CALCULATOR)
        register(id = "jwt_decoder", title = "JWT Decoder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.VpnKey, type = ToolType.TEXT_PROCESSOR, premium = true)
        register(id = "cron_generator", title = "Cron Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Timer, type = ToolType.GENERATOR)
        register(id = "cron_explainer", title = "Cron Explainer", cat = ToolCategory.DEVELOPER, icon = Icons.Default.AccessTime, type = ToolType.TEXT_PROCESSOR)

        // Data / CSV
        register(id = "csv_to_json", title = "CSV to JSON", cat = ToolCategory.DATA, icon = Icons.Default.SyncAlt, type = ToolType.DATA_PROCESSOR)
        register(id = "json_to_csv", title = "JSON to CSV", cat = ToolCategory.DATA, icon = Icons.Default.SyncAlt, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_viewer", title = "CSV Viewer", cat = ToolCategory.DATA, icon = Icons.Default.TableChart, type = ToolType.DATA_PROCESSOR)

        // Privacy / Security
        register(id = "password_generator", title = "Password Generator", cat = ToolCategory.PRIVACY, icon = Icons.Default.Lock, type = ToolType.GENERATOR)
        register(id = "password_strength", title = "Password Strength Auditor", cat = ToolCategory.PRIVACY, icon = Icons.Default.Security, type = ToolType.TEXT_PROCESSOR)
        register(id = "pii_detector", title = "PII Detector", cat = ToolCategory.PRIVACY, icon = Icons.Default.VisibilityOff, type = ToolType.TEXT_PROCESSOR)
        register(id = "secret_scanner", title = "Secret Scanner", cat = ToolCategory.PRIVACY, icon = Icons.Default.Policy, type = ToolType.TEXT_PROCESSOR)

        // Business
        register(id = "invoice_generator", title = "Invoice Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.Receipt, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "receipt_generator", title = "Receipt Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.ReceiptLong, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "business_card", title = "Business Card Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.BusinessCenter, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "qr_business_card", title = "QR Business Card", cat = ToolCategory.BUSINESS, icon = Icons.Default.QrCode, type = ToolType.QR_GENERATOR)
        register(id = "price_list", title = "Price List Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.List, type = ToolType.BUSINESS_TEMPLATE)

        // GST
        register(id = "gst_calculator", title = "GST Calculator", cat = ToolCategory.GST, icon = Icons.Default.Calculate, type = ToolType.CALCULATOR)
        register(id = "gst_breakup", title = "GST Breakup", cat = ToolCategory.GST, icon = Icons.Default.AccountBalance, type = ToolType.CALCULATOR)

        // Education
        register(id = "certificate_generator", title = "Certificate Generator", cat = ToolCategory.EDUCATION, icon = Icons.Default.EmojiEvents, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "id_card_generator", title = "ID Card Generator", cat = ToolCategory.EDUCATION, icon = Icons.Default.Badge, type = ToolType.BUSINESS_TEMPLATE)

        // Engineering
        register(id = "tile_calculator", title = "Tile Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.GridOn, type = ToolType.CALCULATOR)
        register(id = "paint_calculator", title = "Paint Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.FormatPaint, type = ToolType.CALCULATOR)
        register(id = "concrete_calculator", title = "Concrete Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Foundation, type = ToolType.CALCULATOR)
        register(id = "steel_weight", title = "Steel Weight Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.FitnessCenter, type = ToolType.CALCULATOR)
        register(id = "electrical_load", title = "Electrical Load Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.ElectricBolt, type = ToolType.CALCULATOR)
        register(id = "solar_sizing", title = "Solar Sizing Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.WbSunny, type = ToolType.CALCULATOR)
        register(id = "hvac_sizing", title = "HVAC Sizing", cat = ToolCategory.ENGINEERING, icon = Icons.Default.AcUnit, type = ToolType.CALCULATOR)
        register(id = "roofing_calculator", title = "Roofing Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Roofing, type = ToolType.CALCULATOR)
        register(id = "flooring_estimator", title = "Flooring Estimator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.GridOn, type = ToolType.CALCULATOR)
        register(id = "land_area_converter", title = "Land Area Converter", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Landscape, type = ToolType.CALCULATOR)
        register(id = "waste_allowance", title = "Waste Allowance", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Percent, type = ToolType.CALCULATOR)
        register(id = "material_quantity", title = "Material Quantity", cat = ToolCategory.ENGINEERING, icon = Icons.Default.SquareFoot, type = ToolType.CALCULATOR)
        register(id = "construction_cost", title = "Construction Cost Estimator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.AccountBalance, type = ToolType.CALCULATOR)

        // QR
        register(id = "qr_generator", title = "QR Generator", cat = ToolCategory.QR, icon = Icons.Default.QrCode, type = ToolType.QR_GENERATOR)
        register(id = "qr_text", title = "Text QR", cat = ToolCategory.QR, icon = Icons.Default.QrCode, type = ToolType.QR_GENERATOR)
        register(id = "qr_url", title = "URL QR", cat = ToolCategory.QR, icon = Icons.Default.QrCode, type = ToolType.QR_GENERATOR)
        register(id = "qr_wifi", title = "Wi-Fi QR", cat = ToolCategory.QR, icon = Icons.Default.Wifi, type = ToolType.QR_GENERATOR)
        register(id = "qr_contact", title = "Contact QR", cat = ToolCategory.QR, icon = Icons.Default.Contacts, type = ToolType.QR_GENERATOR)
        register(id = "qr_email", title = "Email QR", cat = ToolCategory.QR, icon = Icons.Default.Email, type = ToolType.QR_GENERATOR)
        register(id = "qr_sms", title = "SMS QR", cat = ToolCategory.QR, icon = Icons.Default.Sms, type = ToolType.QR_GENERATOR)
        register(id = "barcode_generator", title = "Barcode Generator", cat = ToolCategory.QR, icon = Icons.Default.ViewWeek, type = ToolType.QR_GENERATOR)

        // Files
        register(id = "file_renamer", title = "File Renamer", cat = ToolCategory.FILES, icon = Icons.Default.DriveFileRenameOutline, type = ToolType.FILE_PROCESSOR)
        register(id = "mime_detector", title = "MIME Type Detector", cat = ToolCategory.FILES, icon = Icons.Default.FilePresent, type = ToolType.FILE_PROCESSOR)
        register(id = "file_hash", title = "File Hash Calculator", cat = ToolCategory.FILES, icon = Icons.Default.Tag, type = ToolType.FILE_PROCESSOR)
        register(id = "zip_creator", title = "ZIP Creator", cat = ToolCategory.FILES, icon = Icons.Default.FolderZip, type = ToolType.FILE_PROCESSOR)
        register(id = "zip_extractor", title = "ZIP Extractor", cat = ToolCategory.FILES, icon = Icons.Default.FolderZip, type = ToolType.FILE_PROCESSOR)
        register(id = "duplicate_finder", title = "Duplicate File Finder", cat = ToolCategory.FILES, icon = Icons.Default.ContentCopy, type = ToolType.FILE_PROCESSOR, premium = true)

        // Generators
        register(id = "random_number", title = "Random Number", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Casino, type = ToolType.GENERATOR)

        // Social / Temporary / Communication require external providers
        register(id = "public_image_downloader", title = "Public Image Downloader", cat = ToolCategory.SOCIAL, icon = Icons.Default.Download, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "public_video_downloader", title = "Public Video Downloader", cat = ToolCategory.SOCIAL, icon = Icons.Default.Download, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "link_cleaner", title = "Link Cleaner", cat = ToolCategory.SOCIAL, icon = Icons.Default.LinkOff, type = ToolType.TEXT_PROCESSOR)

        register(id = "temp_notes", title = "Temporary Notes", cat = ToolCategory.TEMPORARY, icon = Icons.Default.NoteAdd, type = ToolType.TEMPORARY)
        register(id = "temp_mail", title = "Temporary Mail", cat = ToolCategory.TEMPORARY, icon = Icons.Default.Email, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "temp_phone", title = "Temporary Phone", cat = ToolCategory.TEMPORARY, icon = Icons.Default.Phone, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "file_share", title = "File Sharing", cat = ToolCategory.TEMPORARY, icon = Icons.Default.Share, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "meeting_room", title = "Meeting Rooms", cat = ToolCategory.TEMPORARY, icon = Icons.Default.VideoCall, type = ToolType.PROVIDER, requiresNetwork = true)

        register(id = "messaging", title = "Messaging", cat = ToolCategory.COMMUNICATION, icon = Icons.Default.Chat, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "voice_call", title = "Voice Call", cat = ToolCategory.COMMUNICATION, icon = Icons.Default.Phone, type = ToolType.PROVIDER, requiresNetwork = true)
        register(id = "video_call", title = "Video Call", cat = ToolCategory.COMMUNICATION, icon = Icons.Default.VideoCall, type = ToolType.PROVIDER, requiresNetwork = true)
    }

    private fun register(
        id: String,
        title: String,
        cat: ToolCategory,
        icon: ImageVector,
        type: ToolType,
        keywords: List<String> = emptyList(),
        premium: Boolean = false,
        requiresNetwork: Boolean = false,
        localOnly: Boolean = !requiresNetwork,
        supportsBatch: Boolean = false
    ) {
        _tools.add(
            Tool(
                id = id,
                title = title,
                description = "OFA $title tool — process locally or through a provider where applicable.",
                category = cat,
                keywords = keywords + title.split(" ") + listOf(cat.key),
                icon = icon,
                toolType = type,
                localOnly = localOnly,
                requiresNetwork = requiresNetwork,
                premium = premium,
                supportsBatch = supportsBatch
            )
        )
    }

    fun byId(id: String): Tool? = tools.find { it.id == id }
    fun byCategory(category: ToolCategory): List<Tool> = tools.filter { it.category == category }
    fun search(query: String): List<Tool> {
        val q = query.trim().lowercase()
        return tools.filter { tool ->
            tool.title.lowercase().contains(q) ||
            tool.description.lowercase().contains(q) ||
            tool.keywords.any { it.lowercase().contains(q) } ||
            tool.aliases.any { it.lowercase().contains(q) }
        }
    }
}
