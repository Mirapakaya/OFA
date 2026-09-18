package org.phireox.ofa.engine

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject
import org.phireox.ofa.data.model.Tool
import org.phireox.ofa.data.model.ToolType
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.roundToInt

typealias ParamMap = Map<String, String>

sealed class ToolResult {
    data class Text(val value: String) : ToolResult()
    data class Error(val message: String) : ToolResult()
}

object ToolProcessor {

    fun process(tool: Tool, input: String, params: ParamMap = emptyMap()): ToolResult {
        return try {
            when (tool.toolType) {
                ToolType.TEXT_PROCESSOR -> processText(tool.id, input, params)
                ToolType.GENERATOR -> processGenerator(tool.id, params)
                ToolType.CALCULATOR -> processCalculator(tool.id, params)
                ToolType.DATA_PROCESSOR -> processData(tool.id, input)
                ToolType.QR_GENERATOR -> ToolResult.Text("QR generated")
                ToolType.BUSINESS_TEMPLATE -> processBusinessTemplate(tool.id, params)
                else -> ToolResult.Text("Processing not yet implemented for ${tool.title}")
            }
        } catch (e: Exception) {
            ToolResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    private fun processText(toolId: String, input: String, params: ParamMap): ToolResult {
        return when (toolId) {
            "case_converter" -> {
                val mode = params["mode"] ?: "upper"
                val out = when (mode) {
                    "upper" -> input.uppercase()
                    "lower" -> input.lowercase()
                    "title" -> input.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                    "camel" -> toCamelCase(input)
                    "snake" -> input.replace(Regex("\\s+"), "_").lowercase()
                    "kebab" -> input.replace(Regex("\\s+"), "-").lowercase()
                    else -> input.uppercase()
                }
                ToolResult.Text(out)
            }
            "text_reverser" -> ToolResult.Text(input.reversed())
            "word_counter" -> {
                val words = if (input.isBlank()) 0 else input.trim().split(Regex("\\s+")).size
                val chars = input.length
                val lines = if (input.isBlank()) 0 else input.lines().size
                ToolResult.Text("Words: $words\nCharacters: $chars\nLines: $lines\nCharacters (no spaces): ${input.replace(" ", "").length}")
            }
            "whitespace_cleaner" -> ToolResult.Text(input.replace(Regex("[ \\t\\n\\r]+"), " ").trim())
            "text_diff" -> {
                val other = params["other"] ?: ""
                ToolResult.Text("Added: ${input.length} chars vs ${other.length} chars")
            }
            "text_sorter" -> ToolResult.Text(input.lines().sorted().joinToString("\n"))
            "text_deduplicator" -> ToolResult.Text(input.lines().distinct().joinToString("\n"))
            "markdown_to_html" -> ToolResult.Text(markdownToHtml(input))
            "json_formatter" -> ToolResult.Text(formatJson(input))
            "json_minifier" -> ToolResult.Text(JSONObject(input).toString())
            "json_validator" -> ToolResult.Text(validateJson(input))
            "base64_encoder" -> ToolResult.Text(android.util.Base64.encodeToString(input.toByteArray(), android.util.Base64.DEFAULT))
            "base64_decoder" -> {
                val bytes = android.util.Base64.decode(input, android.util.Base64.DEFAULT)
                ToolResult.Text(String(bytes))
            }
            "url_encoder" -> ToolResult.Text(java.net.URLEncoder.encode(input, "UTF-8"))
            "url_decoder" -> ToolResult.Text(java.net.URLDecoder.decode(input, "UTF-8"))
            "html_encoder" -> ToolResult.Text(input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
            "html_decoder" -> ToolResult.Text(input.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&"))
            "hash_generator" -> ToolResult.Text("MD5: ${hash(input, "MD5")}\nSHA-256: ${hash(input, "SHA-256")}")
            "jwt_decoder" -> ToolResult.Text(decodeJwt(input))
            "password_strength" -> ToolResult.Text(passwordStrength(input))
            "pii_detector" -> ToolResult.Text(detectPii(input))
            "secret_scanner" -> ToolResult.Text(scanSecrets(input))
            "link_cleaner" -> ToolResult.Text(cleanUrl(input))
            "regex_tester" -> ToolResult.Text(testRegex(input, params["pattern"] ?: ""))
            "cron_explainer" -> ToolResult.Text(explainCron(input))
            else -> ToolResult.Text(input)
        }
    }

    private fun processGenerator(toolId: String, params: ParamMap): ToolResult {
        return when (toolId) {
            "uuid_generator" -> ToolResult.Text(UUID.randomUUID().toString())
            "lorem_ipsum" -> ToolResult.Text(generateLorem(params["paragraphs"]?.toIntOrNull() ?: 2))
            "password_generator" -> ToolResult.Text(generatePassword(params["length"]?.toIntOrNull() ?: 16))
            "color_palette" -> ToolResult.Text(generatePalette())
            "cron_generator" -> ToolResult.Text(generateCron(params))
            "random_number" -> {
                val min = params["min"]?.toIntOrNull() ?: 0
                val max = params["max"]?.toIntOrNull() ?: 100
                ToolResult.Text((min..max).random().toString())
            }
            else -> ToolResult.Text("Generated output")
        }
    }

    private fun processData(toolId: String, input: String): ToolResult {
        return when (toolId) {
            "csv_to_json" -> ToolResult.Text(csvToJson(input))
            "json_to_csv" -> ToolResult.Text(jsonToCsv(input))
            "csv_viewer" -> ToolResult.Text(csvToJson(input))
            else -> ToolResult.Text(input)
        }
    }

    private fun processCalculator(toolId: String, params: ParamMap): ToolResult {
        return try {
            when (toolId) {
                "gst_calculator" -> {
                    val amount = params["amount"]?.toDoubleOrNull() ?: 0.0
                    val rate = params["rate"]?.toDoubleOrNull() ?: 18.0
                    val inclusive = params["inclusive"] == "true"
                    if (inclusive) {
                        val base = amount / (1 + rate / 100)
                        val tax = amount - base
                        ToolResult.Text("Base: ${"%.2f".format(base)}\nGST ($rate%): ${"%.2f".format(tax)}\nTotal: ${"%.2f".format(amount)}")
                    } else {
                        val tax = amount * rate / 100
                        val total = amount + tax
                        ToolResult.Text("Base: ${"%.2f".format(amount)}\nGST ($rate%): ${"%.2f".format(tax)}\nTotal: ${"%.2f".format(total)}")
                    }
                }
                "gst_breakup" -> {
                    val amount = params["amount"]?.toDoubleOrNull() ?: 0.0
                    val rate = params["rate"]?.toDoubleOrNull() ?: 18.0
                    val base = amount / (1 + rate / 100)
                    val gst = amount - base
                    ToolResult.Text("CGST (${rate/2}%): ${"%.2f".format(gst/2)}\nSGST (${rate/2}%): ${"%.2f".format(gst/2)}\nIGST (${rate}%): ${"%.2f".format(gst)}")
                }
                "contrast_checker" -> {
                    val r1 = params["r1"]?.toIntOrNull() ?: 0
                    val g1 = params["g1"]?.toIntOrNull() ?: 0
                    val b1 = params["b1"]?.toIntOrNull() ?: 0
                    val r2 = params["r2"]?.toIntOrNull() ?: 255
                    val g2 = params["g2"]?.toIntOrNull() ?: 255
                    val b2 = params["b2"]?.toIntOrNull() ?: 255
                    val l1 = luminance(r1, g1, b1)
                    val l2 = luminance(r2, g2, b2)
                    val ratio = (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05)
                    ToolResult.Text("Contrast ratio: ${"%.2f".format(ratio)}:1\n${if (ratio >= 4.5) "WCAG AA OK" else "Low contrast"}")
                }
                "timestamp_converter" -> {
                    val ts = params["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
                    val localSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
                    localSdf.timeZone = TimeZone.getDefault()
                    val utcSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
                    utcSdf.timeZone = TimeZone.getTimeZone("UTC")
                    ToolResult.Text("Local: ${localSdf.format(Date(ts))}\nUTC: ${utcSdf.format(Date(ts))}")
                }
                "tile_calculator" -> {
                    val area = (params["area"]?.toDoubleOrNull() ?: 0.0)
                    val tile = (params["tile"]?.toDoubleOrNull() ?: 0.0)
                    val count = if (tile > 0) kotlin.math.ceil(area / tile).toInt() else 0
                    ToolResult.Text("Tiles needed: $count (with 5% waste: ${(count * 1.05).roundToInt()})")
                }
                "paint_calculator" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val coverage = params["coverage"]?.toDoubleOrNull() ?: 10.0
                    val liters = area / coverage
                    ToolResult.Text("Paint needed: ${"%.2f".format(liters)} L")
                }
                "concrete_calculator" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val width = params["width"]?.toDoubleOrNull() ?: 0.0
                    val depth = params["depth"]?.toDoubleOrNull() ?: 0.0
                    val volume = length * width * depth
                    ToolResult.Text("Volume: ${"%.3f".format(volume)} m³\nBags (50kg ~ 0.034 m³): ${(volume / 0.034).roundToInt()}")
                }
                "steel_weight" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val dia = params["diameter"]?.toDoubleOrNull() ?: 12.0
                    val weight = (dia * dia * length) / 162.0
                    ToolResult.Text("Weight: ${"%.2f".format(weight)} kg")
                }
                "electrical_load" -> {
                    val watts = params["watts"]?.toDoubleOrNull() ?: 0.0
                    val hours = params["hours"]?.toDoubleOrNull() ?: 1.0
                    val kwh = watts * hours / 1000
                    ToolResult.Text("Energy: ${"%.2f".format(kwh)} kWh")
                }
                "solar_sizing" -> {
                    val load = params["load"]?.toDoubleOrNull() ?: 0.0
                    val panel = params["panel"]?.toDoubleOrNull() ?: 400.0
                    val count = kotlin.math.ceil(load / panel).toInt()
                    ToolResult.Text("Panels needed: $count")
                }
                "hvac_sizing" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val btu = area * 25
                    ToolResult.Text("Cooling load: ${btu.roundToInt()} BTU/hr (≈ ${"%.2f".format(btu / 12000)} tons)")
                }
                "roofing_calculator" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val bundles = kotlin.math.ceil(area / 32.0).toInt()
                    ToolResult.Text("Shingle bundles needed: $bundles")
                }
                "flooring_estimator" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val box = params["box"]?.toDoubleOrNull() ?: 20.0
                    val boxes = kotlin.math.ceil(area / box).toInt()
                    ToolResult.Text("Boxes needed: $boxes")
                }
                "land_area_converter" -> {
                    val sqft = params["sqft"]?.toDoubleOrNull() ?: 0.0
                    val sqm = sqft * 0.092903
                    val acre = sqft / 43560
                    ToolResult.Text("Sq ft: $sqft\nSq m: ${"%.2f".format(sqm)}\nAcre: ${"%.4f".format(acre)}")
                }
                "waste_allowance" -> {
                    val qty = params["qty"]?.toDoubleOrNull() ?: 0.0
                    val allowance = params["allowance"]?.toDoubleOrNull() ?: 5.0
                    val total = qty * (1 + allowance / 100)
                    ToolResult.Text("Total with ${allowance}% waste: ${"%.2f".format(total)}")
                }
                "material_quantity" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val width = params["width"]?.toDoubleOrNull() ?: 0.0
                    val depth = params["depth"]?.toDoubleOrNull() ?: 0.0
                    val volume = length * width * depth
                    ToolResult.Text("Material volume: ${"%.3f".format(volume)} m³")
                }
                "construction_cost" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val rate = params["rate"]?.toDoubleOrNull() ?: 1500.0
                    val cost = area * rate
                    ToolResult.Text("Estimated cost: ${"%.2f".format(cost)}")
                }
                else -> ToolResult.Text("Result: ${params.values.joinToString()}")
            }
        } catch (e: Exception) {
            ToolResult.Error(e.localizedMessage ?: "Calculation error")
        }
    }

    private fun processBusinessTemplate(toolId: String, params: ParamMap): ToolResult {
        return when (toolId) {
            "invoice_generator" -> ToolResult.Text(generateInvoice(params))
            "receipt_generator" -> ToolResult.Text(generateReceipt(params))
            "business_card" -> ToolResult.Text(generateBusinessCard(params))
            "price_list" -> ToolResult.Text(generatePriceList(params))
            "certificate_generator" -> ToolResult.Text(generateCertificate(params))
            "id_card_generator" -> ToolResult.Text(generateIdCard(params))
            else -> ToolResult.Text("Business template generated")
        }
    }

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    private fun toCamelCase(input: String): String {
        val words = input.split(Regex("\\s+"))
        return words.first().lowercase() + words.drop(1).joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun markdownToHtml(md: String): String {
        return md.replace(Regex("^#{1,6}\\s*(.+)$", RegexOption.MULTILINE)) { "<h1>${it.groupValues[1]}</h1>" }
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "<b>$1</b>")
            .replace(Regex("\\*(.+?)\\*"), "<i>$1</i>")
            .replace("\n", "<br>")
    }

    private fun formatJson(input: String): String {
        return try {
            JSONObject(input).toString(2)
        } catch (_: Exception) {
            JSONArray(input).toString(2)
        }
    }

    private fun validateJson(input: String): String {
        return try {
            JSONObject(input)
            "Valid JSON object"
        } catch (_: Exception) {
            try {
                JSONArray(input)
                "Valid JSON array"
            } catch (_: Exception) {
                "Invalid JSON"
            }
        }
    }

    private fun csvToJson(csv: String): String {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return "[]"
        val headers = lines.first().split(",").map { it.trim() }
        val rows = lines.drop(1).map { line ->
            val values = line.split(",").map { it.trim() }
            JSONObject().apply {
                headers.forEachIndexed { index, header -> put(header, values.getOrNull(index) ?: "") }
            }
        }
        return JSONArray(rows).toString(2)
    }

    private fun jsonToCsv(json: String): String {
        val array = JSONArray(json)
        if (array.length() == 0) return ""
        val keys = array.getJSONObject(0).keys().asSequence().toList()
        val sb = StringBuilder()
        sb.appendLine(keys.joinToString(","))
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            sb.appendLine(keys.joinToString(",") { obj.optString(it, "") })
        }
        return sb.toString()
    }

    private fun testRegex(input: String, pattern: String): String {
        return try {
            val matches = Regex(pattern).findAll(input).map { it.value }.toList()
            "Matches: ${matches.size}\n${matches.joinToString("\n")}"
        } catch (e: Exception) {
            "Invalid regex: ${e.message}"
        }
    }

    private fun explainCron(cron: String): String {
        val parts = cron.split(" ")
        return if (parts.size == 5) {
            "Minute: ${parts[0]}, Hour: ${parts[1]}, Day: ${parts[2]}, Month: ${parts[3]}, Weekday: ${parts[4]}"
        } else "Cron expression should have 5 space-separated fields."
    }

    private fun generateCron(params: ParamMap): String {
        val minute = params["minute"] ?: "0"
        val hour = params["hour"] ?: "*"
        val day = params["day"] ?: "*"
        val month = params["month"] ?: "*"
        val weekday = params["weekday"] ?: "*"
        return "$minute $hour $day $month $weekday"
    }

    private fun generateInvoice(params: ParamMap): String {
        return "INVOICE\nFrom: ${params["from"] ?: "OFA User"}\nTo: ${params["to"] ?: "Customer"}\nAmount: ${params["amount"] ?: "0"}\nThank you for your business."
    }

    private fun generateReceipt(params: ParamMap): String {
        return "RECEIPT\nReceived from: ${params["from"] ?: "Customer"}\nAmount: ${params["amount"] ?: "0"}\nDate: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}"
    }

    private fun generateBusinessCard(params: ParamMap): String {
        return "${params["name"] ?: "Name"}\n${params["title"] ?: ""}\n${params["phone"] ?: ""}\n${params["email"] ?: ""}"
    }

    private fun generatePriceList(params: ParamMap): String {
        return "PRICE LIST\n${params["items"] ?: ""}"
    }

    private fun generateCertificate(params: ParamMap): String {
        return "CERTIFICATE\nThis certifies that ${params["name"] ?: "____"} has completed ${params["course"] ?: "the course"}."
    }

    private fun generateIdCard(params: ParamMap): String {
        return "ID CARD\nName: ${params["name"] ?: ""}\nID: ${params["id"] ?: ""}\nExpires: ${params["expires"] ?: ""}"
    }

    private fun hash(input: String, algo: String): String {
        val digest = MessageDigest.getInstance(algo).run { update(input.toByteArray()); digest() }
        return BigInteger(1, digest).toString(16).padStart(digest.size * 2, '0')
    }

    private fun decodeJwt(token: String): String {
        val parts = token.split(".")
        return if (parts.size == 3) {
            val header = String(android.util.Base64.decode(parts[0], android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING))
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING))
            "Header: $header\nPayload: $payload"
        } else "Invalid JWT"
    }

    private fun passwordStrength(password: String): String {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return "Strength: ${when (score) { 5 -> "Strong"; in 3..4 -> "Medium"; else -> "Weak" }} ($score/5)"
    }

    private fun detectPii(input: String): String {
        val findings = mutableListOf<String>()
        if (Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}").containsMatchIn(input)) findings.add("email")
        if (Regex("\\b\\d{10}\\b").containsMatchIn(input)) findings.add("phone-like number")
        if (Regex("\\b\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}\\b").containsMatchIn(input)) findings.add("credit-card-like number")
        return if (findings.isEmpty()) "No obvious PII detected." else "Possible PII: ${findings.joinToString(", ")}"
    }

    private fun scanSecrets(input: String): String {
        val patterns = listOf("sk_" to "API key", "ghp_" to "GitHub token", "AKIA" to "AWS key")
        val found = patterns.filter { input.contains(it.first) }.map { it.second }
        return if (found.isEmpty()) "No common secret patterns found." else "Possible secrets: ${found.joinToString(", ")}"
    }

    private fun cleanUrl(url: String): String {
        val u = java.net.URL(url)
        val q = u.query?.split("&")?.filterNot { it.startsWith("utm_") || it.startsWith("fbclid") || it.startsWith("ref=") }?.joinToString("&")
        return "${u.protocol}://${u.host}${u.path}${if (q.isNullOrBlank()) "" else "?$q"}"
    }

    private fun generatePassword(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*"
        return (1..length.coerceIn(8, 64)).map { chars.random() }.joinToString("")
    }

    private fun generateLorem(paragraphs: Int): String {
        val paragraph = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
        return (1..paragraphs.coerceIn(1, 20)).joinToString("\n\n") { paragraph }
    }

    private fun generatePalette(): String {
        return listOf("#6750A4", "#9C27B0", "#2196F3", "#4CAF50", "#FF9800").joinToString("\n")
    }

    private fun luminance(r: Int, g: Int, b: Int): Double {
        fun channel(c: Int): Double {
            val v = c / 255.0
            return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    }
}
