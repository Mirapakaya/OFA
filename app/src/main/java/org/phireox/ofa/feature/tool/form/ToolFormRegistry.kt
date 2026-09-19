package org.phireox.ofa.feature.tool.form

enum class FieldType {
    TEXT,
    MULTILINE,
    NUMBER,
    INTEGER,
    BOOLEAN,
    DROPDOWN,
    DATE,
    FILE,
    FILES
}

data class FieldSpec(
    val key: String,
    val label: String,
    val type: FieldType = FieldType.TEXT,
    val defaultValue: String = "",
    val options: List<String> = emptyList()
)

object ToolFormRegistry {

    fun forTool(toolId: String): List<FieldSpec> = when (toolId) {
        // Text processors with only an input field
        "text_reverser",
        "word_counter",
        "whitespace_cleaner",
        "text_sorter",
        "text_deduplicator",
        "markdown_to_html",
        "json_formatter",
        "json_minifier",
        "json_validator",
        "hash_generator",
        "base64_encoder",
        "base64_decoder",
        "url_encoder",
        "url_decoder",
        "html_encoder",
        "html_decoder",
        "remove_empty_lines",
        "shuffle_lines",
        "line_number_adder",
        "palindrome_checker",
        "password_strength",
        "pii_detector",
        "secret_scanner",
        "link_cleaner",
        "svg_optimizer",
        "jwt_decoder",
        "json_repair",
        "yaml_formatter",
        "yaml_validator",
        "xml_formatter",
        "xml_minifier",
        "xml_validator",
        "dockerfile_analyzer",
        "csp_generator",
        "log_analyzer",
        "env_validator",
        "env_leak_scanner",
        "api_secret_scanner",
        "git_secret_scanner",
        "dns_analyzer" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE)
        )

        "number_base_converter" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("fromBase", "From base", FieldType.INTEGER, defaultValue = "10"),
            FieldSpec("toBase", "To base", FieldType.INTEGER, defaultValue = "2")
        )

        // Text processors with extra parameters
        "case_converter" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec(
                "mode",
                "Mode",
                FieldType.DROPDOWN,
                defaultValue = "upper",
                options = listOf("upper", "lower", "title", "camel", "snake", "kebab")
            )
        )

        "text_diff" -> listOf(
            FieldSpec("input", "Original text", FieldType.MULTILINE),
            FieldSpec("other", "Comparison text", FieldType.MULTILINE)
        )

        "find_replace" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("find", "Find"),
            FieldSpec("replace", "Replace")
        )

        "regex_tester" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("pattern", "Pattern")
        )

        "cron_explainer" -> listOf(
            FieldSpec("input", "Cron expression")
        )

        "slug_generator", "morse_converter" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE)
        )

        "citation_apa",
        "citation_mla",
        "citation_ieee" -> listOf(
            FieldSpec("title", "Title"),
            FieldSpec("author", "Author"),
            FieldSpec("year", "Year"),
            FieldSpec("publisher", "Publisher"),
            FieldSpec("url", "URL")
        )

        "academic_formatter" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("style", "Style", defaultValue = "APA")
        )

        // Data processors
        "csv_to_json",
        "json_to_csv",
        "csv_viewer",
        "csv_cleaner",
        "csv_deduplicate",
        "csv_to_sql" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE)
        )

        "csv_sort" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("column", "Column index (0-based)", FieldType.INTEGER, defaultValue = "0")
        )

        "csv_filter" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("column", "Column index (0-based)", FieldType.INTEGER, defaultValue = "0"),
            FieldSpec("term", "Search term")
        )

        "csv_merge",
        "csv_split" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("rows", "Rows per file", FieldType.INTEGER, defaultValue = "10")
        )

        "csv_column_map" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE),
            FieldSpec("mapping", "Mapping (old:new, comma separated)")
        )

        // Generators
        "uuid_generator",
        "color_palette",
        "favicon_generator" -> emptyList()

        "random_string" -> listOf(
            FieldSpec("length", "Length", FieldType.INTEGER, defaultValue = "16")
        )

        "lorem_ipsum" -> listOf(
            FieldSpec("paragraphs", "Paragraphs", FieldType.INTEGER, defaultValue = "2")
        )

        "password_generator" -> listOf(
            FieldSpec("length", "Length", FieldType.INTEGER, defaultValue = "16")
        )

        "cron_generator" -> listOf(
            FieldSpec("minute", "Minute", defaultValue = "0"),
            FieldSpec("hour", "Hour", defaultValue = "*"),
            FieldSpec("day", "Day", defaultValue = "*"),
            FieldSpec("month", "Month", defaultValue = "*"),
            FieldSpec("weekday", "Weekday", defaultValue = "*")
        )

        "random_number" -> listOf(
            FieldSpec("min", "Min", FieldType.INTEGER, defaultValue = "0"),
            FieldSpec("max", "Max", FieldType.INTEGER, defaultValue = "100")
        )

        "unix_timestamp_converter" -> listOf(
            FieldSpec("timestamp", "Timestamp (seconds)", FieldType.INTEGER)
        )

        "gitignore_generator" -> listOf(
            FieldSpec(
                "stack",
                "Stack",
                FieldType.DROPDOWN,
                defaultValue = "android",
                options = listOf("android", "kotlin", "java", "python", "node", "go", "rust", "flutter")
            )
        )

        "changelog_generator" -> listOf(
            FieldSpec("version", "Version", defaultValue = "1.0.0"),
            FieldSpec("date", "Date", FieldType.DATE)
        )

        "semantic_version" -> listOf(
            FieldSpec("version1", "Version 1", defaultValue = "1.0.0"),
            FieldSpec("version2", "Version 2", defaultValue = "1.0.1")
        )

        "docker_compose_builder" -> listOf(
            FieldSpec("service", "Service name", defaultValue = "app"),
            FieldSpec("image", "Image", defaultValue = "myapp:latest"),
            FieldSpec("port", "Port", FieldType.INTEGER, defaultValue = "8080")
        )

        "subnet_calculator" -> listOf(
            FieldSpec("ip", "IP address"),
            FieldSpec("mask", "Subnet mask", defaultValue = "255.255.255.0")
        )

        "cidr_calculator" -> listOf(
            FieldSpec("cidr", "CIDR", defaultValue = "192.168.0.0/24")
        )

        "dns_analyzer" -> listOf(
            FieldSpec("input", "Domain", FieldType.TEXT)
        )

        // Calculators
        "gst_calculator" -> listOf(
            FieldSpec("amount", "Amount", FieldType.NUMBER),
            FieldSpec("rate", "Rate (%)", FieldType.NUMBER, defaultValue = "18"),
            FieldSpec("inclusive", "Inclusive", FieldType.BOOLEAN, defaultValue = "false")
        )

        "gst_breakup" -> listOf(
            FieldSpec("amount", "Amount", FieldType.NUMBER),
            FieldSpec("rate", "Rate (%)", FieldType.NUMBER, defaultValue = "18")
        )

        "contrast_checker" -> listOf(
            FieldSpec("r1", "Red 1", FieldType.INTEGER, defaultValue = "0"),
            FieldSpec("g1", "Green 1", FieldType.INTEGER, defaultValue = "0"),
            FieldSpec("b1", "Blue 1", FieldType.INTEGER, defaultValue = "0"),
            FieldSpec("r2", "Red 2", FieldType.INTEGER, defaultValue = "255"),
            FieldSpec("g2", "Green 2", FieldType.INTEGER, defaultValue = "255"),
            FieldSpec("b2", "Blue 2", FieldType.INTEGER, defaultValue = "255")
        )

        "timestamp_converter" -> listOf(
            FieldSpec("timestamp", "Timestamp (ms)", FieldType.INTEGER)
        )

        "tile_calculator" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER),
            FieldSpec("tile", "Tile size", FieldType.NUMBER)
        )

        "paint_calculator" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER),
            FieldSpec("coverage", "Coverage per liter", FieldType.NUMBER, defaultValue = "10")
        )

        "brick_quantity" -> listOf(
            FieldSpec("length", "Wall length (m)", FieldType.NUMBER),
            FieldSpec("height", "Wall height (m)", FieldType.NUMBER),
            FieldSpec("brick_length", "Brick length (mm)", FieldType.NUMBER, defaultValue = "190"),
            FieldSpec("brick_height", "Brick height (mm)", FieldType.NUMBER, defaultValue = "90")
        )

        "block_quantity" -> listOf(
            FieldSpec("length", "Wall length (m)", FieldType.NUMBER),
            FieldSpec("height", "Wall height (m)", FieldType.NUMBER),
            FieldSpec("block_length", "Block length (mm)", FieldType.NUMBER, defaultValue = "400"),
            FieldSpec("block_height", "Block height (mm)", FieldType.NUMBER, defaultValue = "200")
        )

        "plaster_calculator" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER),
            FieldSpec("thickness", "Thickness (mm)", FieldType.NUMBER, defaultValue = "12")
        )

        "excavation_calculator" -> listOf(
            FieldSpec("length", "Length", FieldType.NUMBER),
            FieldSpec("width", "Width", FieldType.NUMBER),
            FieldSpec("depth", "Depth", FieldType.NUMBER)
        )

        "slope_calculator" -> listOf(
            FieldSpec("rise", "Rise", FieldType.NUMBER),
            FieldSpec("run", "Run", FieldType.NUMBER)
        )

        "concrete_calculator" -> listOf(
            FieldSpec("length", "Length", FieldType.NUMBER),
            FieldSpec("width", "Width", FieldType.NUMBER),
            FieldSpec("depth", "Depth", FieldType.NUMBER)
        )

        "steel_weight" -> listOf(
            FieldSpec("length", "Length", FieldType.NUMBER),
            FieldSpec("diameter", "Diameter", FieldType.NUMBER, defaultValue = "12")
        )

        "electrical_load" -> listOf(
            FieldSpec("watts", "Watts", FieldType.NUMBER),
            FieldSpec("hours", "Hours", FieldType.NUMBER, defaultValue = "1")
        )

        "solar_sizing" -> listOf(
            FieldSpec("load", "Load (W)", FieldType.NUMBER),
            FieldSpec("panel", "Panel rating (W)", FieldType.NUMBER, defaultValue = "400")
        )

        "hvac_sizing" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER)
        )

        "roofing_calculator" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER)
        )

        "flooring_estimator" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER),
            FieldSpec("box", "Box coverage", FieldType.NUMBER, defaultValue = "20")
        )

        "land_area_converter" -> listOf(
            FieldSpec("sqft", "Square feet", FieldType.NUMBER)
        )

        "waste_allowance" -> listOf(
            FieldSpec("qty", "Quantity", FieldType.NUMBER),
            FieldSpec("allowance", "Waste allowance (%)", FieldType.NUMBER, defaultValue = "5")
        )

        "material_quantity" -> listOf(
            FieldSpec("length", "Length", FieldType.NUMBER),
            FieldSpec("width", "Width", FieldType.NUMBER),
            FieldSpec("depth", "Depth", FieldType.NUMBER)
        )

        "construction_cost" -> listOf(
            FieldSpec("area", "Area", FieldType.NUMBER),
            FieldSpec("rate", "Rate per unit", FieldType.NUMBER, defaultValue = "1500")
        )

        "emi_calculator" -> listOf(
            FieldSpec("principal", "Principal", FieldType.NUMBER),
            FieldSpec("rate", "Annual interest rate (%)", FieldType.NUMBER),
            FieldSpec("months", "Months", FieldType.INTEGER)
        )

        "bmi_calculator" -> listOf(
            FieldSpec("weight", "Weight (kg)", FieldType.NUMBER),
            FieldSpec("height", "Height (m)", FieldType.NUMBER)
        )

        "percentage_calculator" -> listOf(
            FieldSpec("total", "Total", FieldType.NUMBER),
            FieldSpec("value", "Value", FieldType.NUMBER)
        )

        "tip_calculator" -> listOf(
            FieldSpec("bill", "Bill amount", FieldType.NUMBER),
            FieldSpec("tip", "Tip (%)", FieldType.NUMBER, defaultValue = "10"),
            FieldSpec("split", "Split by", FieldType.INTEGER, defaultValue = "1")
        )

        "date_difference" -> listOf(
            FieldSpec("from", "From", FieldType.DATE),
            FieldSpec("to", "To", FieldType.DATE)
        )

        "age_calculator" -> listOf(
            FieldSpec("birth", "Birth date", FieldType.DATE)
        )

        "compound_interest" -> listOf(
            FieldSpec("principal", "Principal", FieldType.NUMBER),
            FieldSpec("rate", "Annual interest rate (%)", FieldType.NUMBER),
            FieldSpec("years", "Years", FieldType.NUMBER, defaultValue = "1"),
            FieldSpec("frequency", "Compounding frequency per year", FieldType.INTEGER, defaultValue = "1")
        )

        "simple_interest" -> listOf(
            FieldSpec("principal", "Principal", FieldType.NUMBER),
            FieldSpec("rate", "Annual interest rate (%)", FieldType.NUMBER),
            FieldSpec("years", "Years", FieldType.NUMBER, defaultValue = "1")
        )

        "loan_calculator" -> listOf(
            FieldSpec("principal", "Principal", FieldType.NUMBER),
            FieldSpec("rate", "Annual interest rate (%)", FieldType.NUMBER),
            FieldSpec("years", "Years", FieldType.NUMBER, defaultValue = "1")
        )

        "discount_calculator" -> listOf(
            FieldSpec("price", "Original price", FieldType.NUMBER),
            FieldSpec("discount", "Discount (%)", FieldType.NUMBER)
        )

        "profit_calculator" -> listOf(
            FieldSpec("cost", "Cost", FieldType.NUMBER),
            FieldSpec("revenue", "Revenue", FieldType.NUMBER)
        )

        "margin_calculator" -> listOf(
            FieldSpec("cost", "Cost", FieldType.NUMBER),
            FieldSpec("revenue", "Revenue", FieldType.NUMBER)
        )

        "markup_calculator" -> listOf(
            FieldSpec("cost", "Cost", FieldType.NUMBER),
            FieldSpec("price", "Selling price", FieldType.NUMBER)
        )

        "savings_calculator" -> listOf(
            FieldSpec("monthly", "Monthly savings", FieldType.NUMBER),
            FieldSpec("rate", "Annual interest rate (%)", FieldType.NUMBER),
            FieldSpec("years", "Years", FieldType.NUMBER, defaultValue = "1")
        )

        "salary_calculator" -> listOf(
            FieldSpec("annual", "Annual salary", FieldType.NUMBER),
            FieldSpec("deductions", "Deductions (%)", FieldType.NUMBER, defaultValue = "0")
        )

        "hex_color_converter" -> listOf(
            FieldSpec("hex", "HEX color", defaultValue = "#6750A4")
        )

        "unit_converter" -> listOf(
            FieldSpec("value", "Value", FieldType.NUMBER),
            FieldSpec(
                "from",
                "From unit",
                FieldType.DROPDOWN,
                defaultValue = "m",
                options = listOf("m", "km", "cm", "mm", "in", "ft", "mi", "kg", "g", "mg", "lb", "oz", "c", "f", "k")
            ),
            FieldSpec(
                "to",
                "To unit",
                FieldType.DROPDOWN,
                defaultValue = "ft",
                options = listOf("m", "km", "cm", "mm", "in", "ft", "mi", "kg", "g", "mg", "lb", "oz", "c", "f", "k")
            )
        )

        // Business templates
        "invoice_generator" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("amount", "Amount", FieldType.NUMBER)
        )

        "quotation_generator" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("items", "Items", FieldType.MULTILINE),
            FieldSpec("amount", "Amount", FieldType.NUMBER)
        )

        "proforma_invoice" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("amount", "Amount", FieldType.NUMBER)
        )

        "receipt_generator" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("amount", "Amount", FieldType.NUMBER)
        )

        "purchase_order" -> listOf(
            FieldSpec("buyer", "Buyer"),
            FieldSpec("seller", "Seller"),
            FieldSpec("items", "Items", FieldType.MULTILINE),
            FieldSpec("amount", "Amount", FieldType.NUMBER)
        )

        "delivery_challan" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("items", "Items", FieldType.MULTILINE)
        )

        "packing_slip" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("items", "Items", FieldType.MULTILINE)
        )

        "warranty_card" -> listOf(
            FieldSpec("product", "Product"),
            FieldSpec("customer", "Customer"),
            FieldSpec("period", "Warranty period"),
            FieldSpec("date", "Date", FieldType.DATE)
        )

        "business_card" -> listOf(
            FieldSpec("name", "Name"),
            FieldSpec("title", "Title"),
            FieldSpec("phone", "Phone"),
            FieldSpec("email", "Email")
        )

        "business_letter" -> listOf(
            FieldSpec("from", "From"),
            FieldSpec("to", "To"),
            FieldSpec("subject", "Subject"),
            FieldSpec("body", "Body", FieldType.MULTILINE)
        )

        "price_list" -> listOf(
            FieldSpec("items", "Items", FieldType.MULTILINE)
        )

        "certificate_generator" -> listOf(
            FieldSpec("name", "Name"),
            FieldSpec("course", "Course")
        )

        "id_card_generator" -> listOf(
            FieldSpec("name", "Name"),
            FieldSpec("id", "ID"),
            FieldSpec("expires", "Expires", FieldType.DATE)
        )

        "student_id" -> listOf(
            FieldSpec("name", "Name"),
            FieldSpec("roll", "Roll number"),
            FieldSpec("course", "Course"),
            FieldSpec("expires", "Expires", FieldType.DATE)
        )

        "marksheet" -> listOf(
            FieldSpec("name", "Name"),
            FieldSpec("roll", "Roll number"),
            FieldSpec("marks", "Subjects and marks", FieldType.MULTILINE)
        )

        "timetable" -> listOf(
            FieldSpec("title", "Title"),
            FieldSpec("entries", "Schedule entries", FieldType.MULTILINE)
        )

        "worksheet" -> listOf(
            FieldSpec("title", "Title"),
            FieldSpec("questions", "Questions", FieldType.MULTILINE)
        )

        "answer_sheet" -> listOf(
            FieldSpec("title", "Title"),
            FieldSpec("questions", "Questions", FieldType.MULTILINE)
        )

        // QR generators
        "qr_generator",
        "qr_text",
        "qr_url",
        "qr_wifi",
        "qr_contact",
        "qr_email",
        "qr_sms",
        "barcode_generator",
        "qr_business_card" -> listOf(
            FieldSpec("input", "Input", FieldType.MULTILINE)
        )

        // File processors with just a file
        "pdf_split",
        "pdf_to_images",
        "images_to_pdf",
        "pdf_metadata",
        "pdf_extract_text",
        "image_to_pdf",
        "exif_viewer",
        "exif_cleaner",
        "pdf_compress",
        "file_renamer",
        "mime_detector",
        "zip_extractor",
        "duplicate_finder" -> listOf(
            FieldSpec("file", "File", FieldType.FILE)
        )

        "pdf_rotate" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("angle", "Angle", FieldType.INTEGER, defaultValue = "90")
        )

        "pdf_delete_pages" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("pages", "Pages to delete (comma separated)")
        )

        "pdf_merge" -> listOf(
            FieldSpec("files", "PDF files", FieldType.FILES)
        )

        "pdf_protect" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("password", "Password")
        )

        "pdf_reorder" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("order", "New page order (e.g. 2,1,3)")
        )

        // Image processors with extra parameters
        "image_compress" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("quality", "Quality", FieldType.INTEGER, defaultValue = "80")
        )

        "image_resize" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("width", "Width", FieldType.INTEGER),
            FieldSpec("height", "Height", FieldType.INTEGER)
        )

        "image_rotate" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("angle", "Angle", FieldType.INTEGER, defaultValue = "90")
        )

        "image_convert" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec(
                "format",
                "Format",
                FieldType.DROPDOWN,
                defaultValue = "png",
                options = listOf("png", "jpg", "jpeg", "webp")
            )
        )

        "image_flip" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("horizontal", "Horizontal", FieldType.BOOLEAN, defaultValue = "true")
        )

        "image_crop" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("width", "Width", FieldType.INTEGER, defaultValue = "300"),
            FieldSpec("height", "Height", FieldType.INTEGER, defaultValue = "300")
        )

        "file_hash" -> listOf(
            FieldSpec("file", "File", FieldType.FILE),
            FieldSpec("algo", "Algorithm", defaultValue = "SHA-256")
        )

        "zip_creator" -> listOf(
            FieldSpec("files", "Files", FieldType.FILES)
        )

        // Provider / temporary / communication tools with no local parameters
        "public_image_downloader",
        "public_video_downloader",
        "temp_notes",
        "temp_mail",
        "temp_phone",
        "file_share",
        "meeting_room",
        "messaging",
        "voice_call",
        "video_call",
        "ai_assistant" -> emptyList()

        else -> emptyList()
    }
}
