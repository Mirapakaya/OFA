package org.phireox.ofa.engine

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject
import org.phireox.ofa.engine.BusinessPdfGenerator
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
    data class File(val path: String, val mimeType: String = "application/octet-stream") : ToolResult()
    data class Error(val message: String) : ToolResult()
}

object ToolProcessor {

    fun process(context: android.content.Context?, tool: Tool, input: String, params: ParamMap = emptyMap()): ToolResult {
        return try {
            when (tool.toolType) {
                ToolType.TEXT_PROCESSOR -> processText(tool.id, input, params)
                ToolType.GENERATOR -> processGenerator(tool.id, params)
                ToolType.CALCULATOR -> processCalculator(tool.id, params)
                ToolType.CONVERTER -> processCalculator(tool.id, params)
                ToolType.DATA_PROCESSOR -> processData(tool.id, input, params)
                ToolType.QR_GENERATOR -> ToolResult.Text("QR generated")
                ToolType.BUSINESS_TEMPLATE -> {
                    if (context == null) return ToolResult.Error("Context required for PDF generation")
                    BusinessPdfGenerator.generate(context, tool.id, params)
                }
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
            "find_replace" -> ToolResult.Text(input.replace(params["find"] ?: "", params["replace"] ?: ""))
            "remove_empty_lines" -> ToolResult.Text(input.lines().filter { it.isNotBlank() }.joinToString("\n"))
            "shuffle_lines" -> ToolResult.Text(input.lines().shuffled().joinToString("\n"))
            "line_number_adder" -> ToolResult.Text(input.lines().mapIndexed { i, line -> "${i + 1}: $line" }.joinToString("\n"))
            "palindrome_checker" -> ToolResult.Text(checkPalindrome(input))
            "slug_generator" -> ToolResult.Text(generateSlug(input))
            "morse_converter" -> ToolResult.Text(convertMorse(input))
            "number_base_converter" -> ToolResult.Text(convertBase(input, params["fromBase"] ?: "10", params["toBase"] ?: "2"))
            "svg_optimizer" -> ToolResult.Text(optimizeSvg(input))
            "json_repair" -> ToolResult.Text(repairJson(input))
            "yaml_formatter" -> ToolResult.Text(formatYaml(input))
            "yaml_validator" -> ToolResult.Text(validateYaml(input))
            "xml_formatter" -> ToolResult.Text(formatXml(input))
            "xml_minifier" -> ToolResult.Text(minifyXml(input))
            "xml_validator" -> ToolResult.Text(validateXml(input))
            "dockerfile_analyzer" -> ToolResult.Text(analyzeDockerfile(input))
            "csp_generator" -> ToolResult.Text(generateCsp(input))
            "log_analyzer" -> ToolResult.Text(analyzeLog(input))
            "env_validator" -> ToolResult.Text(validateEnv(input))
            "env_leak_scanner" -> ToolResult.Text(scanEnvLeaks(input))
            "api_secret_scanner" -> ToolResult.Text(scanApiSecrets(input))
            "git_secret_scanner" -> ToolResult.Text(scanGitSecrets(input))
            "dns_analyzer" -> ToolResult.Text(analyzeDns(input))
            "citation_apa" -> ToolResult.Text(formatCitationApa(params))
            "citation_mla" -> ToolResult.Text(formatCitationMla(params))
            "citation_ieee" -> ToolResult.Text(formatCitationIeee(params))
            "academic_formatter" -> ToolResult.Text(formatAcademic(input, params["style"] ?: "APA"))
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
            "favicon_generator" -> ToolResult.Text(generateFavicon())
            "random_number" -> {
                val min = params["min"]?.toIntOrNull() ?: 0
                val max = params["max"]?.toIntOrNull() ?: 100
                val a = min.coerceAtMost(max)
                val b = min.coerceAtLeast(max)
                ToolResult.Text((a..b).random().toString())
            }
            "gitignore_generator" -> ToolResult.Text(generateGitignore(params["stack"] ?: "android"))
            "changelog_generator" -> ToolResult.Text(generateChangelog(params["version"] ?: "1.0.0", params["date"] ?: ""))
            "docker_compose_builder" -> ToolResult.Text(generateDockerCompose(params))
            "random_string" -> ToolResult.Text(generateRandomString(params["length"]?.toIntOrNull() ?: 16))
            else -> ToolResult.Text("Generated output")
        }
    }

    private fun processData(toolId: String, input: String, params: ParamMap): ToolResult {
        return when (toolId) {
            "csv_to_json" -> ToolResult.Text(csvToJson(input))
            "json_to_csv" -> ToolResult.Text(jsonToCsv(input))
            "csv_viewer" -> ToolResult.Text(csvToJson(input))
            "csv_cleaner" -> ToolResult.Text(csvCleaner(input))
            "csv_sort" -> ToolResult.Text(csvSort(input, params["column"]?.toIntOrNull() ?: 0))
            "csv_filter" -> ToolResult.Text(csvFilter(input, params["column"]?.toIntOrNull() ?: 0, params["term"] ?: ""))
            "csv_merge" -> ToolResult.Text(csvMerge(input, params["other"] ?: ""))
            "csv_split" -> ToolResult.Text(csvSplit(input, params["rows"]?.toIntOrNull() ?: 10))
            "csv_deduplicate" -> ToolResult.Text(csvDeduplicate(input))
            "csv_column_map" -> ToolResult.Text(csvColumnMap(input, params["mapping"] ?: ""))
            "csv_to_sql" -> ToolResult.Text(csvToSql(input))
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
                "brick_quantity" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val height = params["height"]?.toDoubleOrNull() ?: 0.0
                    val brickL = params["brick_length"]?.toDoubleOrNull() ?: 190.0
                    val brickH = params["brick_height"]?.toDoubleOrNull() ?: 90.0
                    val wallArea = length * height
                    val brickArea = (brickL / 1000) * (brickH / 1000)
                    val count = if (brickArea > 0) (wallArea / brickArea).toInt() else 0
                    ToolResult.Text("Wall area: ${"%.2f".format(wallArea)} m²\nBricks needed: $count (with 5% waste: ${(count * 1.05).toInt()})")
                }
                "block_quantity" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val height = params["height"]?.toDoubleOrNull() ?: 0.0
                    val blockL = params["block_length"]?.toDoubleOrNull() ?: 400.0
                    val blockH = params["block_height"]?.toDoubleOrNull() ?: 200.0
                    val wallArea = length * height
                    val blockArea = (blockL / 1000) * (blockH / 1000)
                    val count = if (blockArea > 0) (wallArea / blockArea).toInt() else 0
                    ToolResult.Text("Wall area: ${"%.2f".format(wallArea)} m²\nBlocks needed: $count (with 5% waste: ${(count * 1.05).toInt()})")
                }
                "plaster_calculator" -> {
                    val area = params["area"]?.toDoubleOrNull() ?: 0.0
                    val thickness = params["thickness"]?.toDoubleOrNull() ?: 12.0
                    val volume = area * (thickness / 1000)
                    val cement = volume * 5.5
                    val sand = volume * 0.5
                    ToolResult.Text("Plaster volume: ${"%.3f".format(volume)} m³\nCement: ${"%.2f".format(cement)} bags\nSand: ${"%.3f".format(sand)} m³")
                }
                "excavation_calculator" -> {
                    val length = params["length"]?.toDoubleOrNull() ?: 0.0
                    val width = params["width"]?.toDoubleOrNull() ?: 0.0
                    val depth = params["depth"]?.toDoubleOrNull() ?: 0.0
                    val volume = length * width * depth
                    ToolResult.Text("Excavation volume: ${"%.3f".format(volume)} m³")
                }
                "slope_calculator" -> {
                    val rise = params["rise"]?.toDoubleOrNull() ?: 0.0
                    val run = params["run"]?.toDoubleOrNull() ?: 0.0
                    val slope = if (run != 0.0) rise / run else 0.0
                    val angle = kotlin.math.atan(slope) * 180 / kotlin.math.PI
                    ToolResult.Text("Slope: ${"%.4f".format(slope)}\nAngle: ${"%.2f".format(angle)}°")
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
                "emi_calculator" -> {
                    val principal = params["principal"]?.toDoubleOrNull() ?: 0.0
                    val annualRate = params["rate"]?.toDoubleOrNull() ?: 0.0
                    val months = params["months"]?.toIntOrNull() ?: 0
                    if (months <= 0 || annualRate < 0) {
                        ToolResult.Text("EMI: 0")
                    } else {
                        val r = annualRate / 12 / 100
                        val emi = principal * r * Math.pow(1 + r, months.toDouble()) / (Math.pow(1 + r, months.toDouble()) - 1)
                        val total = emi * months
                        val interest = total - principal
                        ToolResult.Text("EMI: ${"%.2f".format(emi)}\nTotal interest: ${"%.2f".format(interest)}\nTotal payment: ${"%.2f".format(total)}")
                    }
                }
                "bmi_calculator" -> {
                    val weight = params["weight"]?.toDoubleOrNull() ?: 0.0
                    val height = params["height"]?.toDoubleOrNull() ?: 0.0
                    val bmi = if (height > 0) weight / (height * height) else 0.0
                    val category = when (bmi) {
                        in 0.0..<18.5 -> "Underweight"
                        in 18.5..<25.0 -> "Normal"
                        in 25.0..<30.0 -> "Overweight"
                        else -> if (bmi >= 30.0) "Obese" else ""
                    }
                    ToolResult.Text("BMI: ${"%.2f".format(bmi)}\nCategory: $category")
                }
                "percentage_calculator" -> {
                    val total = params["total"]?.toDoubleOrNull() ?: 0.0
                    val value = params["value"]?.toDoubleOrNull() ?: 0.0
                    val percent = if (total != 0.0) (value / total) * 100 else 0.0
                    val reverse = (total * value) / 100
                    ToolResult.Text("$value is ${"%.2f".format(percent)}% of $total\n$value% of $total = ${"%.2f".format(reverse)}")
                }
                "tip_calculator" -> {
                    val bill = params["bill"]?.toDoubleOrNull() ?: 0.0
                    val tip = params["tip"]?.toDoubleOrNull() ?: 10.0
                    val split = params["split"]?.toIntOrNull() ?: 1
                    val tipAmount = bill * tip / 100
                    val total = bill + tipAmount
                    val perPerson = if (split > 0) total / split else total
                    ToolResult.Text("Tip: ${"%.2f".format(tipAmount)}\nTotal: ${"%.2f".format(total)}\nPer person ($split): ${"%.2f".format(perPerson)}")
                }
                "date_difference" -> {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val d1 = params["from"]?.let { runCatching { sdf.parse(it)?.time }.getOrNull() }
                    val d2 = params["to"]?.let { runCatching { sdf.parse(it)?.time }.getOrNull() }
                    if (d1 == null || d2 == null) {
                        ToolResult.Text("Enter from and to as yyyy-MM-dd")
                    } else {
                        val diff = kotlin.math.abs(d2 - d1) / (1000 * 60 * 60 * 24)
                        ToolResult.Text("Days: $diff")
                    }
                }
                "age_calculator" -> {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val birth = params["birth"]?.let { runCatching { sdf.parse(it)?.time }.getOrNull() }
                    val now = System.currentTimeMillis()
                    if (birth == null) {
                        ToolResult.Text("Enter birth as yyyy-MM-dd")
                    } else {
                        val diffDays = (now - birth) / (1000 * 60 * 60 * 24)
                        val years = diffDays / 365
                        val months = (diffDays % 365) / 30
                        ToolResult.Text("Age: $years years, $months months")
                    }
                }
                "compound_interest" -> {
                    val principal = params["principal"]?.toDoubleOrNull() ?: 0.0
                    val annualRate = params["rate"]?.toDoubleOrNull() ?: 0.0
                    val years = params["years"]?.toDoubleOrNull() ?: 1.0
                    val frequency = params["frequency"]?.toIntOrNull() ?: 1
                    val n = frequency.coerceAtLeast(1)
                    val r = annualRate / 100 / n
                    val amount = principal * Math.pow(1 + r, n * years)
                    val interest = amount - principal
                    ToolResult.Text("Principal: ${"%.2f".format(principal)}\nInterest: ${"%.2f".format(interest)}\nTotal: ${"%.2f".format(amount)}")
                }
                "hex_color_converter" -> {
                    val hex = params["hex"] ?: ""
                    val rgb = hexToRgb(hex)
                    ToolResult.Text(if (rgb != null) "RGB: ${rgb.first}, ${rgb.second}, ${rgb.third}\nHEX: $hex" else "Invalid HEX color")
                }
                "unix_timestamp_converter" -> {
                    val ts = params["timestamp"]?.toLongOrNull() ?: (System.currentTimeMillis() / 1000)
                    val localSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
                    localSdf.timeZone = TimeZone.getDefault()
                    ToolResult.Text("Unix: $ts\nLocal: ${localSdf.format(Date(ts * 1000))}")
                }
                "semantic_version" -> ToolResult.Text(compareSemVer(params["version1"] ?: "", params["version2"] ?: ""))
                "subnet_calculator" -> ToolResult.Text(calculateSubnet(params["ip"] ?: "", params["mask"] ?: "255.255.255.0"))
                "cidr_calculator" -> ToolResult.Text(calculateCidr(params["cidr"] ?: "192.168.0.0/24"))
                "unit_converter" -> {
                    val value = params["value"]?.toDoubleOrNull() ?: 0.0
                    val from = params["from"] ?: ""
                    val to = params["to"] ?: ""
                    val result = convertUnit(value, from, to)
                    ToolResult.Text("$value $from = $result $to")
                }
                "simple_interest" -> {
                    val principal = params["principal"]?.toDoubleOrNull() ?: 0.0
                    val rate = params["rate"]?.toDoubleOrNull() ?: 0.0
                    val years = params["years"]?.toDoubleOrNull() ?: 1.0
                    val interest = principal * rate * years / 100
                    ToolResult.Text("Principal: ${"%.2f".format(principal)}\nInterest: ${"%.2f".format(interest)}\nTotal: ${"%.2f".format(principal + interest)}")
                }
                "loan_calculator" -> {
                    val principal = params["principal"]?.toDoubleOrNull() ?: 0.0
                    val annualRate = params["rate"]?.toDoubleOrNull() ?: 0.0
                    val years = params["years"]?.toDoubleOrNull() ?: 1.0
                    val months = (years * 12).toInt()
                    if (months <= 0 || annualRate < 0) {
                        ToolResult.Text("Loan: 0")
                    } else {
                        val r = annualRate / 12 / 100
                        val emi = principal * r * Math.pow(1 + r, months.toDouble()) / (Math.pow(1 + r, months.toDouble()) - 1)
                        val total = emi * months
                        val interest = total - principal
                        ToolResult.Text("EMI: ${"%.2f".format(emi)}\nTotal interest: ${"%.2f".format(interest)}\nTotal payment: ${"%.2f".format(total)}")
                    }
                }
                "discount_calculator" -> {
                    val price = params["price"]?.toDoubleOrNull() ?: 0.0
                    val discount = params["discount"]?.toDoubleOrNull() ?: 0.0
                    val saved = price * discount / 100
                    val final = price - saved
                    ToolResult.Text("Original: ${"%.2f".format(price)}\nDiscount: ${"%.2f".format(saved)}\nFinal price: ${"%.2f".format(final)}")
                }
                "profit_calculator" -> {
                    val cost = params["cost"]?.toDoubleOrNull() ?: 0.0
                    val revenue = params["revenue"]?.toDoubleOrNull() ?: 0.0
                    val profit = revenue - cost
                    val margin = if (revenue != 0.0) (profit / revenue) * 100 else 0.0
                    ToolResult.Text("Profit: ${"%.2f".format(profit)}\nMargin: ${"%.2f".format(margin)}%")
                }
                "margin_calculator" -> {
                    val cost = params["cost"]?.toDoubleOrNull() ?: 0.0
                    val revenue = params["revenue"]?.toDoubleOrNull() ?: 0.0
                    val profit = revenue - cost
                    val margin = if (revenue != 0.0) (profit / revenue) * 100 else 0.0
                    ToolResult.Text("Margin: ${"%.2f".format(margin)}%\nProfit: ${"%.2f".format(profit)}")
                }
                "markup_calculator" -> {
                    val cost = params["cost"]?.toDoubleOrNull() ?: 0.0
                    val price = params["price"]?.toDoubleOrNull() ?: 0.0
                    val markup = if (cost != 0.0) ((price - cost) / cost) * 100 else 0.0
                    ToolResult.Text("Markup: ${"%.2f".format(markup)}%\nProfit: ${"%.2f".format(price - cost)}")
                }
                "savings_calculator" -> {
                    val monthly = params["monthly"]?.toDoubleOrNull() ?: 0.0
                    val annualRate = params["rate"]?.toDoubleOrNull() ?: 0.0
                    val years = params["years"]?.toDoubleOrNull() ?: 1.0
                    val months = (years * 12).toInt()
                    val r = annualRate / 100 / 12
                    val amount = if (r == 0.0) monthly * months else monthly * ((Math.pow(1 + r, months.toDouble()) - 1) / r)
                    val invested = monthly * months
                    ToolResult.Text("Invested: ${"%.2f".format(invested)}\nFuture value: ${"%.2f".format(amount)}\nInterest: ${"%.2f".format(amount - invested)}")
                }
                "salary_calculator" -> {
                    val annual = params["annual"]?.toDoubleOrNull() ?: 0.0
                    val deductions = params["deductions"]?.toDoubleOrNull() ?: 0.0
                    val net = annual * (1 - deductions / 100)
                    ToolResult.Text("Annual gross: ${"%.2f".format(annual)}\nAnnual net: ${"%.2f".format(net)}\nMonthly net: ${"%.2f".format(net / 12)}")
                }
                else -> ToolResult.Text("Result: ${params.values.joinToString()}")
            }
        } catch (e: Exception) {
            ToolResult.Error(e.localizedMessage ?: "Calculation error")
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

    private fun optimizeSvg(input: String): String {
        return input.replace(Regex("<!--[\\s\\S]*?-->"), "")
            .replace(Regex("\\s+"), " ")
            .replace(Regex(" />"), "/>")
            .trim()
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

    private fun parseCsv(input: String): List<List<String>> {
        return input.lines().filter { it.isNotBlank() }.map { line ->
            if (line.contains("\"") || line.contains("'")) {
                line.split(",").map { it.trim().removeSurrounding("\"").removeSurrounding("'") }
            } else {
                line.split(",").map { it.trim() }
            }
        }
    }

    private fun csvCleaner(input: String): String {
        val rows = parseCsv(input).filter { row -> row.any { it.isNotBlank() } }
        return rows.joinToString("\n") { row -> row.joinToString(",") }
    }

    private fun csvSort(input: String, column: Int): String {
        val rows = parseCsv(input)
        if (rows.size < 2) return input
        val header = rows.first()
        val data = rows.drop(1)
        val sorted = data.sortedBy { it.getOrNull(column)?.lowercase() ?: "" }
        return (listOf(header) + sorted).joinToString("\n") { it.joinToString(",") }
    }

    private fun csvFilter(input: String, column: Int, term: String): String {
        val rows = parseCsv(input)
        if (rows.size < 2) return input
        val header = rows.first()
        val filtered = rows.drop(1).filter { it.getOrNull(column)?.contains(term, ignoreCase = true) == true }
        return (listOf(header) + filtered).joinToString("\n") { it.joinToString(",") }
    }

    private fun csvMerge(input: String, other: String): String {
        val rowsA = parseCsv(input)
        val rowsB = parseCsv(other)
        return (rowsA + rowsB).joinToString("\n") { it.joinToString(",") }
    }

    private fun csvSplit(input: String, rowsPerFile: Int): String {
        val rows = parseCsv(input)
        if (rows.size <= rowsPerFile) return input
        val chunks = rows.chunked(rowsPerFile.coerceAtLeast(1))
        return chunks.mapIndexed { index, chunk ->
            "--- Part ${index + 1} ---\n${chunk.joinToString("\n") { it.joinToString(",") }}"
        }.joinToString("\n\n")
    }

    private fun csvDeduplicate(input: String): String {
        val rows = parseCsv(input)
        val seen = mutableSetOf<List<String>>()
        val unique = rows.filter { seen.add(it) }
        return unique.joinToString("\n") { it.joinToString(",") }
    }

    private fun csvColumnMap(input: String, mapping: String): String {
        val rows = parseCsv(input)
        if (rows.isEmpty()) return input
        val map = mapping.split(",").mapNotNull { part ->
            val p = part.split(":")
            if (p.size == 2) p[0].trim().toIntOrNull() to p[1].trim() else null
        }.toMap()
        return rows.map { row ->
            map.entries.map { (from, to) ->
                val value = row.getOrNull(from) ?: ""
                "$to:$value"
            }.joinToString(",")
        }.joinToString("\n")
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

    private fun generateFavicon(): String {
        val svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><rect fill=\"%236750A4\" width=\"100\" height=\"100\" rx=\"20\"/><text x=\"50\" y=\"65\" font-size=\"60\" text-anchor=\"middle\" fill=\"white\">O</text></svg>"
        return "$svg\n\nSave as favicon.svg and reference it with:\n<link rel=\"icon\" type=\"image/svg+xml\" href=\"/favicon.svg\">"
    }

    private fun luminance(r: Int, g: Int, b: Int): Double {
        fun channel(c: Int): Double {
            val v = c / 255.0
            return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    }


    private fun checkPalindrome(input: String): String {
        return if (input.lowercase() == input.lowercase().reversed()) "Palindrome" else "Not a palindrome"
    }

    private fun generateSlug(input: String): String {
        return input.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
    }

    private fun convertMorse(input: String): String {
        val toMorse = mapOf(
            'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
            'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
            'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
            'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
            'Y' to "-.--", 'Z' to "--..",
            '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
            '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----."
        )
        val fromMorse = toMorse.entries.associate { it.value to it.key }
        return if (input.trim().any { it == '.' || it == '-' }) {
            input.split(" ").mapNotNull { fromMorse[it.uppercase()]?.toString() }.joinToString("")
        } else {
            input.uppercase().mapNotNull { toMorse[it] }.joinToString(" ")
        }
    }

    private fun convertBase(input: String, fromBase: String, toBase: String): String {
        return try {
            val from = fromBase.toIntOrNull() ?: 10
            val to = toBase.toIntOrNull() ?: 10
            val value = input.trim().toLong(from)
            value.toString(to.coerceIn(2, 36)).uppercase()
        } catch (e: Exception) {
            "Invalid input"
        }
    }

    private fun generateRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length.coerceIn(1, 256)).map { chars.random() }.joinToString("")
    }

    private fun hexToRgb(hex: String): Triple<Int, Int, Int>? {
        val clean = hex.removePrefix("#")
        return try {
            when (clean.length) {
                6 -> Triple(clean.substring(0, 2).toInt(16), clean.substring(2, 4).toInt(16), clean.substring(4, 6).toInt(16))
                3 -> Triple(clean[0].toString().repeat(2).toInt(16), clean[1].toString().repeat(2).toInt(16), clean[2].toString().repeat(2).toInt(16))
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun convertUnit(value: Double, from: String, to: String): Double {
        val lowerFrom = from.lowercase()
        val lowerTo = to.lowercase()
        fun lengthToMeters(v: Double, unit: String): Double {
            return when (unit) {
                "m" -> v
                "km" -> v * 1000
                "cm" -> v / 100
                "mm" -> v / 1000
                "in", "inch" -> v * 0.0254
                "ft", "feet" -> v * 0.3048
                "mi", "mile" -> v * 1609.34
                else -> v
            }
        }
        fun weightToGrams(v: Double, unit: String): Double {
            return when (unit) {
                "kg" -> v * 1000
                "g" -> v
                "mg" -> v / 1000
                "lb" -> v * 453.592
                "oz" -> v * 28.3495
                else -> v
            }
        }
        val base = when (lowerFrom) {
            "m", "km", "cm", "mm", "in", "inch", "ft", "feet", "mi", "mile" -> lengthToMeters(value, lowerFrom)
            "kg", "g", "mg", "lb", "oz" -> weightToGrams(value, lowerFrom)
            "c" -> value
            "f" -> (value - 32) * 5 / 9
            "k" -> value - 273.15
            else -> value
        }
        return when (lowerTo) {
            "m" -> lengthToMeters(base, "m")
            "km" -> lengthToMeters(base, "m") / 1000
            "cm" -> lengthToMeters(base, "m") * 100
            "mm" -> lengthToMeters(base, "m") * 1000
            "in", "inch" -> lengthToMeters(base, "m") / 0.0254
            "ft", "feet" -> lengthToMeters(base, "m") / 0.3048
            "mi", "mile" -> lengthToMeters(base, "m") / 1609.34
            "kg" -> weightToGrams(base, "g") / 1000
            "g" -> weightToGrams(base, "g")
            "mg" -> weightToGrams(base, "g") * 1000
            "lb" -> weightToGrams(base, "g") / 453.592
            "oz" -> weightToGrams(base, "g") / 28.3495
            "c" -> base
            "f" -> base * 9 / 5 + 32
            "k" -> base + 273.15
            else -> base
        }
    }

    private fun repairJson(input: String): String {
        return try {
            JSONObject(input)
            formatJson(input)
        } catch (_: Exception) {
            try {
                JSONArray(input)
                formatJson(input)
            } catch (_: Exception) {
                "Could not repair JSON"
            }
        }
    }

    private fun formatYaml(input: String): String {
        return input.lines().map { line ->
            val trimmed = line.trimStart()
            val indent = line.length - trimmed.length
            " ".repeat(indent) + trimmed.trimEnd()
        }.filter { it.isNotBlank() }.joinToString("\n")
    }

    private fun validateYaml(input: String): String {
        return try {
            val lines = input.lines()
            var indent = 0
            for (line in lines) {
                if (line.isBlank()) continue
                val trimmed = line.trimStart()
                if (trimmed.startsWith("#")) continue
                if (trimmed.contains(":") && !trimmed.contains(": ") && !trimmed.endsWith(":")) {
                    return "Invalid YAML: missing space after colon"
                }
            }
            "Valid YAML structure"
        } catch (e: Exception) {
            "Invalid YAML: ${e.localizedMessage}"
        }
    }

    private fun formatXml(input: String): String {
        return try {
            val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(java.io.ByteArrayInputStream(input.toByteArray()))
            val transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            val writer = java.io.StringWriter()
            transformer.transform(javax.xml.transform.dom.DOMSource(doc), javax.xml.transform.stream.StreamResult(writer))
            writer.toString()
        } catch (e: Exception) {
            "Invalid XML: ${e.localizedMessage}"
        }
    }

    private fun minifyXml(input: String): String {
        return input.replace(Regex(">\s+<"), "><")
    }

    private fun validateXml(input: String): String {
        return try {
            val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(java.io.ByteArrayInputStream(input.toByteArray()))
            "Valid XML"
        } catch (e: Exception) {
            "Invalid XML: ${e.localizedMessage}"
        }
    }

    private fun generateGitignore(stack: String): String {
        val common = listOf(".DS_Store", "*.log", ".idea/", ".vscode/", "*.iml", "/build", "/dist", "*.tmp")
        val specific = when (stack.lowercase()) {
            "android", "kotlin", "java" -> listOf(".gradle/", "/local.properties", "/captures", "/.externalNativeBuild", "/.cxx", "*.apk", "*.aab")
            "python" -> listOf("__pycache__/", "*.py[cod]", "*.egg-info/", ".venv/", "venv/", "*.pyc")
            "node" -> listOf("node_modules/", "/dist", "/build", ".next/", "*.lock")
            "go" -> listOf("/vendor", "*.exe", "*.test")
            "rust" -> listOf("/target", "Cargo.lock")
            "flutter" -> listOf(".dart_tool/", ".packages", "build/", "*.lock")
            else -> emptyList()
        }
        return (common + specific).joinToString("\n")
    }

    private fun generateChangelog(version: String, date: String): String {
        val d = date.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        return """# Changelog

## [$version] - $d

### Added
- New features

### Changed
- Improvements

### Fixed
- Bug fixes
""".trimIndent()
    }

    private fun generateDockerCompose(params: ParamMap): String {
        val service = params["service"] ?: "app"
        val image = params["image"] ?: "myapp:latest"
        val port = params["port"] ?: "8080"
        return """version: "3.8"
services:
  $service:
    image: $image
    ports:
      - "$port:$port"
    restart: unless-stopped
""".trimIndent()
    }

    private fun analyzeDockerfile(input: String): String {
        val lines = input.lines()
        val fromCount = lines.count { it.trim().startsWith("FROM", ignoreCase = true) }
        val runCount = lines.count { it.trim().startsWith("RUN", ignoreCase = true) }
        val copyCount = lines.count { it.trim().startsWith("COPY", ignoreCase = true) }
        return "FROM instructions: $fromCount\nRUN instructions: $runCount\nCOPY instructions: $copyCount\nTotal lines: ${lines.size}"
    }

    private fun generateCsp(input: String): String {
        return "default-src 'self'; script-src 'self' $input; style-src 'self' 'unsafe-inline'; img-src 'self' data:;"
    }

    private fun analyzeLog(input: String): String {
        val lines = input.lines()
        val errorCount = lines.count { it.contains("ERROR", ignoreCase = true) }
        val warnCount = lines.count { it.contains("WARN", ignoreCase = true) }
        val infoCount = lines.count { it.contains("INFO", ignoreCase = true) }
        return "Total lines: ${lines.size}\nErrors: $errorCount\nWarnings: $warnCount\nInfo: $infoCount"
    }

    private fun validateEnv(input: String): String {
        val issues = mutableListOf<String>()
        input.lines().forEachIndexed { i, line ->
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("#")) return@forEachIndexed
            if (!trimmed.contains("=")) issues.add("Line ${i + 1}: missing '='")
        }
        return if (issues.isEmpty()) "Valid .env format" else issues.joinToString("\n")
    }

    private fun scanEnvLeaks(input: String): String {
        val findings = input.lines().filter { it.contains("=") && (it.contains("KEY") || it.contains("SECRET") || it.contains("TOKEN") || it.contains("PASSWORD")) }
        return if (findings.isEmpty()) "No obvious secrets in .env" else "Possible secrets found:\n${findings.joinToString("\n")}"
    }

    private fun scanApiSecrets(input: String): String {
        val patterns = listOf("sk_" to "API key", "ghp_" to "GitHub token", "AKIA" to "AWS key", "xoxb-" to "Slack token")
        val found = patterns.filter { input.contains(it.first) }.map { it.second }
        return if (found.isEmpty()) "No common API secret patterns found." else "Possible secrets: ${found.joinToString(", ")}"
    }

    private fun scanGitSecrets(input: String): String {
        return scanApiSecrets(input)
    }

    private fun analyzeDns(input: String): String {
        val domain = input.trim().lowercase()
        return if (domain.matches(Regex("^[a-z0-9]([a-z0-9\-]{0,61}[a-z0-9])?(\.[a-z0-9]([a-z0-9\-]{0,61}[a-z0-9])?)*\$"))) {
            "Domain appears valid: $domain"
        } else {
            "Invalid domain format"
        }
    }

    private fun compareSemVer(v1: String, v2: String): String {
        fun parse(v: String) = v.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        val a = parse(v1)
        val b = parse(v2)
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }
            val y = b.getOrElse(i) { 0 }
            if (x != y) return if (x > y) "$v1 > $v2" else "$v1 < $v2"
        }
        return "$v1 == $v2"
    }

    private fun calculateSubnet(ip: String, mask: String): String {
        return try {
            val ipLong = ipToLong(ip) ?: return "Invalid IP address"
            val maskBits = when {
                mask.matches(Regex("^\\d+")) -> mask.toIntOrNull()
                else -> maskToBits(mask)
            }
            if (maskBits == null || maskBits !in 1..30) return "Invalid subnet mask"
            val subnetMask = 0xFFFFFFFFL shl (32 - maskBits)
            val network = ipLong and subnetMask
            val broadcast = network or (0xFFFFFFFFL ushr maskBits)
            "IP: $ip\nMask bits: $maskBits\nNetwork: ${longToIp(network)}\nBroadcast: ${longToIp(broadcast)}\nUsable hosts: ${broadcast - network - 1}"
        } catch (e: Exception) {
            "Invalid input: ${e.localizedMessage}"
        }
    }

    private fun calculateCidr(cidr: String): String {
        return try {
            val parts = cidr.split("/")
            val ip = parts[0]
            val prefix = parts[1].toInt()
            if (prefix !in 1..30) return "Invalid CIDR prefix"
            val ipLong = ipToLong(ip) ?: return "Invalid IP address"
            val subnetMask = 0xFFFFFFFFL shl (32 - prefix)
            val network = ipLong and subnetMask
            val broadcast = network or (0xFFFFFFFFL ushr prefix)
            val usable = broadcast - network - 1
            "CIDR: $cidr\nNetwork: ${longToIp(network)}/$prefix\nBroadcast: ${longToIp(broadcast)}\nUsable hosts: $usable"
        } catch (e: Exception) {
            "Invalid CIDR: ${e.localizedMessage}"
        }
    }

    private fun ipToLong(ip: String): Long? {
        val parts = ip.split(".").map { it.toIntOrNull() ?: return null }
        if (parts.size != 4 || parts.any { it !in 0..255 }) return null
        return parts.fold(0L) { acc, part -> (acc shl 8) or part.toLong() }
    }

    private fun longToIp(value: Long): String {
        return listOf((value ushr 24) and 0xFF, (value ushr 16) and 0xFF, (value ushr 8) and 0xFF, value and 0xFF).joinToString(".")
    }

    private fun maskToBits(mask: String): Int? {
        val long = ipToLong(mask) ?: return null
        var bits = 0
        var value = long
        while (value != 0L && (value and 1L) == 1L) {
            bits++
            value = value ushr 1
        }
        return if (value == 0L) bits else null
    }

    private fun formatCitationApa(params: ParamMap): String {
        val author = params["author"] ?: ""
        val year = params["year"] ?: ""
        val title = params["title"] ?: ""
        val publisher = params["publisher"] ?: ""
        val url = params["url"] ?: ""
        return "$author ($year). $title. $publisher. ${if (url.isNotBlank()) "URL: $url" else ""}".trim()
    }

    private fun formatCitationMla(params: ParamMap): String {
        val author = params["author"] ?: ""
        val year = params["year"] ?: ""
        val title = params["title"] ?: ""
        val publisher = params["publisher"] ?: ""
        val url = params["url"] ?: ""
        return "$author. \"$title.\" $publisher, $year. ${if (url.isNotBlank()) url else ""}".trim()
    }

    private fun formatCitationIeee(params: ParamMap): String {
        val author = params["author"] ?: ""
        val year = params["year"] ?: ""
        val title = params["title"] ?: ""
        val publisher = params["publisher"] ?: ""
        return "[$author] $title, $publisher, $year.".trim()
    }

    private fun formatAcademic(input: String, style: String): String {
        return when (style.uppercase()) {
            "APA" -> "APA formatted:\n${input.uppercase().replace("\n", "\n- ")}"
            "MLA" -> "MLA formatted:\n${input.uppercase().replace("\n", "\n- ")}"
            else -> "Academic formatted ($style):\n${input.replace("\n", "\n- ")}"
        }
    }

    private fun parseCsv(input: String): List<List<String>> {
        return input.lines().map { it.split(",").map { v -> v.trim() } }.filter { it.isNotEmpty() && it.any { c -> c.isNotBlank() } }
    }

    private fun csvToString(rows: List<List<String>>): String {
        return rows.joinToString("\n") { it.joinToString(",") }
    }

    private fun csvCleaner(input: String): String = csvToString(parseCsv(input).filter { row -> row.any { it.isNotBlank() } })

    private fun csvDeduplicate(input: String): String {
        val rows = parseCsv(input)
        val seen = mutableSetOf<List<String>>()
        return csvToString(rows.filter { seen.add(it) })
    }

    private fun csvSort(input: String, column: Int): String {
        val rows = parseCsv(input)
        if (rows.size < 2) return input
        return csvToString(listOf(rows.first()) + rows.drop(1).sortedBy { it.getOrNull(column) ?: "" })
    }

    private fun csvFilter(input: String, column: Int, term: String): String {
        val rows = parseCsv(input)
        if (rows.size < 2) return input
        return csvToString(listOf(rows.first()) + rows.drop(1).filter { it.getOrNull(column)?.contains(term, ignoreCase = true) == true })
    }

    private fun csvMerge(input: String, other: String): String = csvToString(parseCsv(input) + parseCsv(other))

    private fun csvSplit(input: String, rows: Int): String {
        val all = parseCsv(input)
        if (all.size < 2) return input
        val header = all.first()
        val chunks = all.drop(1).chunked(rows.coerceAtLeast(1))
        return chunks.joinToString("\n---\n") { chunk -> csvToString(listOf(header) + chunk) }
    }

    private fun csvToSql(input: String): String {
        val rows = parseCsv(input)
        if (rows.size < 2) return ""
        val table = "records"
        val columns = rows.first().map { it.replace(Regex("[^A-Za-z0-9_]"), "_") }.joinToString(", ")
        return "CREATE TABLE $table ($columns);\n" + rows.drop(1).joinToString("\n") { row ->
            "INSERT INTO $table ($columns) VALUES (${row.joinToString(", ") { "'$it'" }});"
        }
    }

    private fun csvColumnMap(input: String, mapping: String): String {
        val rows = parseCsv(input)
        if (rows.isEmpty()) return input
        val map = mapping.split(",").associate {
            val parts = it.split(":")
            parts[0].trim() to (parts.getOrNull(1)?.trim() ?: parts[0].trim())
        }
        val newHeader = rows.first().map { map[it] ?: it }
        return csvToString(listOf(newHeader) + rows.drop(1))
    }
}