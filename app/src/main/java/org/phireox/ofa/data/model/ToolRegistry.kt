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
        register(id = "pdf_rotate", title = "Rotate PDF", cat = ToolCategory.PDF, icon = Icons.Default.RotateRight, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_delete_pages", title = "Delete PDF Pages", cat = ToolCategory.PDF, icon = Icons.Default.Delete, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_extract_text", title = "PDF Extract Text", cat = ToolCategory.PDF, icon = Icons.Default.TextFields, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_protect", title = "PDF Protect", cat = ToolCategory.PDF, icon = Icons.Default.Lock, type = ToolType.PDF_PROCESSOR)
        register(id = "pdf_reorder", title = "PDF Reorder Pages", cat = ToolCategory.PDF, icon = Icons.Default.Reorder, type = ToolType.PDF_PROCESSOR)

        // Images
        register(id = "image_compress", title = "Image Compressor", cat = ToolCategory.IMAGE, icon = Icons.Default.Compress, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_resize", title = "Image Resize", cat = ToolCategory.IMAGE, icon = Icons.Default.PhotoSizeSelectLarge, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_crop", title = "Image Crop", cat = ToolCategory.IMAGE, icon = Icons.Default.Crop, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_rotate", title = "Image Rotate", cat = ToolCategory.IMAGE, icon = Icons.Default.RotateRight, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_flip", title = "Image Flip", cat = ToolCategory.IMAGE, icon = Icons.Default.Flip, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_convert", title = "Image Format Converter", cat = ToolCategory.IMAGE, icon = Icons.Default.Transform, type = ToolType.IMAGE_PROCESSOR)
        register(id = "image_to_pdf", title = "Image to PDF", cat = ToolCategory.IMAGE, icon = Icons.Default.PictureAsPdf, type = ToolType.IMAGE_PROCESSOR)
        register(id = "exif_viewer", title = "EXIF Viewer", cat = ToolCategory.IMAGE, icon = Icons.Default.Info, type = ToolType.IMAGE_PROCESSOR)
        register(id = "exif_cleaner", title = "EXIF Cleaner", cat = ToolCategory.IMAGE, icon = Icons.Default.CleaningServices, type = ToolType.IMAGE_PROCESSOR)

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
        register(id = "find_replace", title = "Find & Replace", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FindReplace, type = ToolType.TEXT_PROCESSOR)
        register(id = "remove_empty_lines", title = "Remove Empty Lines", cat = ToolCategory.DOCUMENT, icon = Icons.Default.ClearAll, type = ToolType.TEXT_PROCESSOR)
        register(id = "shuffle_lines", title = "Shuffle Lines", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Shuffle, type = ToolType.TEXT_PROCESSOR)
        register(id = "line_number_adder", title = "Line Number Adder", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FormatListNumbered, type = ToolType.TEXT_PROCESSOR)
        register(id = "palindrome_checker", title = "Palindrome Checker", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Repeat, type = ToolType.TEXT_PROCESSOR)
        register(id = "slug_generator", title = "Slug Generator", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Link, type = ToolType.TEXT_PROCESSOR)
        register(id = "morse_converter", title = "Morse Converter", cat = ToolCategory.DOCUMENT, icon = Icons.Default.Radio, type = ToolType.TEXT_PROCESSOR)
        register(id = "citation_apa", title = "APA Citation", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FormatQuote, type = ToolType.TEXT_PROCESSOR)
        register(id = "citation_mla", title = "MLA Citation", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FormatQuote, type = ToolType.TEXT_PROCESSOR)
        register(id = "citation_ieee", title = "IEEE Citation", cat = ToolCategory.DOCUMENT, icon = Icons.Default.FormatQuote, type = ToolType.TEXT_PROCESSOR)
        register(id = "academic_formatter", title = "Academic Formatter", cat = ToolCategory.DOCUMENT, icon = Icons.Default.School, type = ToolType.TEXT_PROCESSOR)

        // Developer
        register(id = "json_formatter", title = "JSON Formatter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DataObject, type = ToolType.TEXT_PROCESSOR)
        register(id = "json_minifier", title = "JSON Minifier", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DataObject, type = ToolType.TEXT_PROCESSOR)
        register(id = "json_validator", title = "JSON Validator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Rule, type = ToolType.TEXT_PROCESSOR)
        register(id = "json_repair", title = "JSON Repair", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Build, type = ToolType.TEXT_PROCESSOR)
        register(id = "yaml_formatter", title = "YAML Formatter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.List, type = ToolType.TEXT_PROCESSOR)
        register(id = "yaml_validator", title = "YAML Validator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Rule, type = ToolType.TEXT_PROCESSOR)
        register(id = "xml_formatter", title = "XML Formatter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Code, type = ToolType.TEXT_PROCESSOR)
        register(id = "xml_minifier", title = "XML Minifier", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Code, type = ToolType.TEXT_PROCESSOR)
        register(id = "xml_validator", title = "XML Validator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Rule, type = ToolType.TEXT_PROCESSOR)
        register(id = "unix_timestamp_converter", title = "Unix Timestamp Converter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Schedule, type = ToolType.CALCULATOR)
        register(id = "gitignore_generator", title = ".gitignore Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Source, type = ToolType.GENERATOR)
        register(id = "changelog_generator", title = "Changelog Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Note, type = ToolType.GENERATOR)
        register(id = "semantic_version", title = "Semantic Version Helper", cat = ToolCategory.DEVELOPER, icon = Icons.Default.NewReleases, type = ToolType.CALCULATOR)
        register(id = "docker_compose_builder", title = "Docker Compose Builder", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DeveloperBoard, type = ToolType.GENERATOR)
        register(id = "dockerfile_analyzer", title = "Dockerfile Analyzer", cat = ToolCategory.DEVELOPER, icon = Icons.Default.DeveloperBoard, type = ToolType.TEXT_PROCESSOR)
        register(id = "subnet_calculator", title = "Subnet Calculator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.NetworkCheck, type = ToolType.CALCULATOR)
        register(id = "cidr_calculator", title = "CIDR Calculator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.NetworkCheck, type = ToolType.CALCULATOR)
        register(id = "dns_analyzer", title = "DNS Analyzer", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Dns, type = ToolType.TEXT_PROCESSOR)
        register(id = "csp_generator", title = "CSP Generator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Security, type = ToolType.TEXT_PROCESSOR)
        register(id = "log_analyzer", title = "Log Analyzer", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Terminal, type = ToolType.TEXT_PROCESSOR)
        register(id = "env_validator", title = ".env Validator", cat = ToolCategory.DEVELOPER, icon = Icons.Default.PlaylistAddCheck, type = ToolType.TEXT_PROCESSOR)
        register(id = "env_leak_scanner", title = ".env Leak Scanner", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Policy, type = ToolType.TEXT_PROCESSOR)
        register(id = "api_secret_scanner", title = "API Secret Scanner", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Policy, type = ToolType.TEXT_PROCESSOR)
        register(id = "git_secret_scanner", title = "Git Secret Scanner", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Policy, type = ToolType.TEXT_PROCESSOR)
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
        register(id = "number_base_converter", title = "Number Base Converter", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Numbers, type = ToolType.TEXT_PROCESSOR)
        register(id = "random_string", title = "Random String", cat = ToolCategory.DEVELOPER, icon = Icons.Default.Casino, type = ToolType.GENERATOR)

        // Data / CSV
        register(id = "csv_to_json", title = "CSV to JSON", cat = ToolCategory.DATA, icon = Icons.Default.SyncAlt, type = ToolType.DATA_PROCESSOR)
        register(id = "json_to_csv", title = "JSON to CSV", cat = ToolCategory.DATA, icon = Icons.Default.SyncAlt, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_viewer", title = "CSV Viewer", cat = ToolCategory.DATA, icon = Icons.Default.TableChart, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_cleaner", title = "CSV Cleaner", cat = ToolCategory.DATA, icon = Icons.Default.CleaningServices, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_sort", title = "CSV Sort", cat = ToolCategory.DATA, icon = Icons.Default.Sort, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_filter", title = "CSV Filter", cat = ToolCategory.DATA, icon = Icons.Default.FilterList, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_merge", title = "CSV Merge", cat = ToolCategory.DATA, icon = Icons.Default.MergeType, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_split", title = "CSV Split", cat = ToolCategory.DATA, icon = Icons.Default.Splitscreen, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_deduplicate", title = "CSV Deduplicate", cat = ToolCategory.DATA, icon = Icons.Default.ContentCut, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_column_map", title = "CSV Column Map", cat = ToolCategory.DATA, icon = Icons.Default.Map, type = ToolType.DATA_PROCESSOR)
        register(id = "csv_to_sql", title = "CSV to SQL", cat = ToolCategory.DATA, icon = Icons.Default.Storage, type = ToolType.DATA_PROCESSOR)

        // Privacy / Security
        register(id = "password_generator", title = "Password Generator", cat = ToolCategory.PRIVACY, icon = Icons.Default.Lock, type = ToolType.GENERATOR)
        register(id = "password_strength", title = "Password Strength Auditor", cat = ToolCategory.PRIVACY, icon = Icons.Default.Security, type = ToolType.TEXT_PROCESSOR)
        register(id = "pii_detector", title = "PII Detector", cat = ToolCategory.PRIVACY, icon = Icons.Default.VisibilityOff, type = ToolType.TEXT_PROCESSOR)
        register(id = "secret_scanner", title = "Secret Scanner", cat = ToolCategory.PRIVACY, icon = Icons.Default.Policy, type = ToolType.TEXT_PROCESSOR)

        // Business
        register(id = "invoice_generator", title = "Invoice Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.Receipt, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "quotation_generator", title = "Quotation Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.FormatQuote, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "proforma_invoice", title = "Proforma Invoice", cat = ToolCategory.BUSINESS, icon = Icons.Default.ReceiptLong, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "receipt_generator", title = "Receipt Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.ReceiptLong, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "purchase_order", title = "Purchase Order", cat = ToolCategory.BUSINESS, icon = Icons.Default.ShoppingCart, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "delivery_challan", title = "Delivery Challan", cat = ToolCategory.BUSINESS, icon = Icons.Default.LocalShipping, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "packing_slip", title = "Packing Slip", cat = ToolCategory.BUSINESS, icon = Icons.Default.Inventory, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "warranty_card", title = "Warranty Card", cat = ToolCategory.BUSINESS, icon = Icons.Default.Verified, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "business_card", title = "Business Card Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.BusinessCenter, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "business_letter", title = "Business Letter", cat = ToolCategory.BUSINESS, icon = Icons.Default.Mail, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "qr_business_card", title = "QR Business Card", cat = ToolCategory.BUSINESS, icon = Icons.Default.QrCode, type = ToolType.QR_GENERATOR)
        register(id = "price_list", title = "Price List Generator", cat = ToolCategory.BUSINESS, icon = Icons.Default.List, type = ToolType.BUSINESS_TEMPLATE)

        // GST
        register(id = "gst_calculator", title = "GST Calculator", cat = ToolCategory.GST, icon = Icons.Default.Calculate, type = ToolType.CALCULATOR)
        register(id = "gst_breakup", title = "GST Breakup", cat = ToolCategory.GST, icon = Icons.Default.AccountBalance, type = ToolType.CALCULATOR)

        // Education
        register(id = "certificate_generator", title = "Certificate Generator", cat = ToolCategory.EDUCATION, icon = Icons.Default.EmojiEvents, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "id_card_generator", title = "ID Card Generator", cat = ToolCategory.EDUCATION, icon = Icons.Default.Badge, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "student_id", title = "Student ID Card", cat = ToolCategory.EDUCATION, icon = Icons.Default.Badge, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "marksheet", title = "Mark Sheet", cat = ToolCategory.EDUCATION, icon = Icons.Default.TableChart, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "timetable", title = "Timetable", cat = ToolCategory.EDUCATION, icon = Icons.Default.Schedule, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "worksheet", title = "Worksheet", cat = ToolCategory.EDUCATION, icon = Icons.Default.Assignment, type = ToolType.BUSINESS_TEMPLATE)
        register(id = "answer_sheet", title = "Answer Sheet", cat = ToolCategory.EDUCATION, icon = Icons.Default.Description, type = ToolType.BUSINESS_TEMPLATE)

        // Engineering
        register(id = "tile_calculator", title = "Tile Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.GridOn, type = ToolType.CALCULATOR)
        register(id = "paint_calculator", title = "Paint Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.FormatPaint, type = ToolType.CALCULATOR)
        register(id = "brick_quantity", title = "Brick Quantity", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Square, type = ToolType.CALCULATOR)
        register(id = "block_quantity", title = "Block Quantity", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Square, type = ToolType.CALCULATOR)
        register(id = "plaster_calculator", title = "Plaster Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.FormatPaint, type = ToolType.CALCULATOR)
        register(id = "excavation_calculator", title = "Excavation Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.Landscape, type = ToolType.CALCULATOR)
        register(id = "slope_calculator", title = "Slope Calculator", cat = ToolCategory.ENGINEERING, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
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

        // Calculators
        register(id = "emi_calculator", title = "EMI Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.AccountBalance, type = ToolType.CALCULATOR)
        register(id = "compound_interest", title = "Compound Interest", cat = ToolCategory.CALCULATORS, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
        register(id = "simple_interest", title = "Simple Interest", cat = ToolCategory.CALCULATORS, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
        register(id = "loan_calculator", title = "Loan Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.AccountBalance, type = ToolType.CALCULATOR)
        register(id = "discount_calculator", title = "Discount Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Percent, type = ToolType.CALCULATOR)
        register(id = "profit_calculator", title = "Profit Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
        register(id = "margin_calculator", title = "Margin Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
        register(id = "markup_calculator", title = "Markup Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.TrendingUp, type = ToolType.CALCULATOR)
        register(id = "savings_calculator", title = "Savings Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.AccountBalanceWallet, type = ToolType.CALCULATOR)
        register(id = "salary_calculator", title = "Salary Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.AttachMoney, type = ToolType.CALCULATOR)
        register(id = "hex_color_converter", title = "Hex Color Converter", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Colorize, type = ToolType.CALCULATOR)
        register(id = "bmi_calculator", title = "BMI Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Favorite, type = ToolType.CALCULATOR)
        register(id = "percentage_calculator", title = "Percentage Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Percent, type = ToolType.CALCULATOR)
        register(id = "tip_calculator", title = "Tip Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.LocalAtm, type = ToolType.CALCULATOR)
        register(id = "date_difference", title = "Date Difference", cat = ToolCategory.CALCULATORS, icon = Icons.Default.DateRange, type = ToolType.CALCULATOR)
        register(id = "age_calculator", title = "Age Calculator", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Cake, type = ToolType.CALCULATOR)
        register(id = "unit_converter", title = "Unit Converter", cat = ToolCategory.CALCULATORS, icon = Icons.Default.SwapHoriz, type = ToolType.CALCULATOR)
        register(id = "compass", title = "Compass", cat = ToolCategory.CALCULATORS, icon = Icons.Default.Explore, type = ToolType.CALCULATOR)
        register(id = "weather", title = "Weather", cat = ToolCategory.WEATHER, icon = Icons.Default.WbSunny, type = ToolType.PROVIDER, requiresNetwork = true)

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
        register(id = "ai_assistant", title = "AI Assistant", cat = ToolCategory.AI, icon = Icons.Default.Psychology, type = ToolType.PROVIDER, requiresNetwork = true)
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
