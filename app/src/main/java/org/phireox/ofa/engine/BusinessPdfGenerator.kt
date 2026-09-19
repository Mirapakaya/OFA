package org.phireox.ofa.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BusinessPdfGenerator {

    fun generate(context: Context, toolId: String, params: Map<String, String>): ToolResult {
        return try {
            val file = when (toolId) {
                "invoice_generator" -> generateInvoice(context, params)
                "quotation_generator" -> generateQuotation(context, params)
                "proforma_invoice" -> generateProforma(context, params)
                "receipt_generator" -> generateReceipt(context, params)
                "purchase_order" -> generatePurchaseOrder(context, params)
                "delivery_challan" -> generateDeliveryChallan(context, params)
                "packing_slip" -> generatePackingSlip(context, params)
                "warranty_card" -> generateWarrantyCard(context, params)
                "business_card" -> generateBusinessCard(context, params)
                "business_letter" -> generateBusinessLetter(context, params)
                "price_list" -> generatePriceList(context, params)
                "certificate_generator" -> generateCertificate(context, params)
                "id_card_generator" -> generateIdCard(context, params)
                "student_id" -> generateStudentId(context, params)
                "marksheet" -> generateMarksheet(context, params)
                "timetable" -> generateTimetable(context, params)
                "worksheet" -> generateWorksheet(context, params)
                "answer_sheet" -> generateAnswerSheet(context, params)
                else -> return ToolResult.Error("Unsupported business template: $toolId")
            }
            ToolResult.File(file.absolutePath, "application/pdf")
        } catch (e: Exception) {
            ToolResult.Error("PDF generation failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun pageWidth() = 595
    private fun pageHeight() = 842
    private fun margin() = 40f

    private fun createDocument(): PdfDocument = PdfDocument()

    private fun startPage(document: PdfDocument, number: Int): PdfDocument.Page {
        val info = PdfDocument.PageInfo.Builder(pageWidth(), pageHeight(), number).create()
        return document.startPage(info)
    }

    private fun save(document: PdfDocument, file: File) {
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
    }

    private fun outputFile(context: Context, prefix: String): File {
        val dir = File(context.filesDir, "ofa_business").apply { mkdirs() }
        return File(dir, "${prefix}_${System.currentTimeMillis()}.pdf")
    }

    private fun dateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun generateInvoice(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val headerPaint = Paint().apply { textSize = 14f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }

        val from = params["from"] ?: "From: ________"
        val to = params["to"] ?: "To: ________"
        val amount = params["amount"] ?: "0"

        canvas.drawText("INVOICE", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("Bill To:", margin(), margin() + 140f, headerPaint)
        canvas.drawText(to, margin(), margin() + 160f, bodyPaint)
        canvas.drawText("Amount: $amount", margin(), margin() + 210f, headerPaint)
        canvas.drawText("Thank you for your business.", margin(), margin() + 260f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "invoice")
        save(document, file)
        return file
    }

    private fun generateReceipt(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }

        val from = params["from"] ?: "Received from: ________"
        val amount = params["amount"] ?: "0"

        canvas.drawText("RECEIPT", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("Amount: $amount", margin(), margin() + 140f, bodyPaint)
        canvas.drawText("This receipt was generated by OFA.", margin(), margin() + 190f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "receipt")
        save(document, file)
        return file
    }

    private fun generateBusinessCard(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }

        val name = params["name"] ?: "Name"
        val title = params["title"] ?: ""
        val phone = params["phone"] ?: ""
        val email = params["email"] ?: ""

        canvas.drawText(name, margin(), margin() + 30f, titlePaint)
        canvas.drawText(title, margin(), margin() + 55f, bodyPaint)
        canvas.drawText(phone, margin(), margin() + 80f, bodyPaint)
        canvas.drawText(email, margin(), margin() + 105f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "business_card")
        save(document, file)
        return file
    }

    private fun generatePriceList(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }

        canvas.drawText("PRICE LIST", margin(), margin() + 30f, titlePaint)
        val items = params["items"] ?: ""
        val textPaint = TextPaint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        var y = margin() + 60f
        items.lines().forEach { line ->
            canvas.drawText(line, margin(), y, bodyPaint)
            y += 20f
        }

        document.finishPage(page)
        val file = outputFile(context, "price_list")
        save(document, file)
        return file
    }

    private fun generateCertificate(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 26f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 14f; color = 0xFF000000.toInt() }

        val name = params["name"] ?: "________"
        val course = params["course"] ?: "________"

        canvas.drawText("CERTIFICATE", margin(), margin() + 40f, titlePaint)
        canvas.drawText("This certifies that", margin(), margin() + 100f, bodyPaint)
        canvas.drawText(name, margin(), margin() + 130f, Paint().apply { textSize = 18f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() })
        canvas.drawText("has successfully completed", margin(), margin() + 160f, bodyPaint)
        canvas.drawText(course, margin(), margin() + 190f, bodyPaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 240f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "certificate")
        save(document, file)
        return file
    }

    private fun generateIdCard(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }

        val name = params["name"] ?: "________"
        val id = params["id"] ?: "________"
        val expires = params["expires"] ?: "________"

        canvas.drawText("ID CARD", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Name: $name", margin(), margin() + 70f, bodyPaint)
        canvas.drawText("ID: $id", margin(), margin() + 100f, bodyPaint)
        canvas.drawText("Expires: $expires", margin(), margin() + 130f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "id_card")
        save(document, file)
        return file
    }

    private fun generateQuotation(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val from = params["from"] ?: "From: ________"
        val to = params["to"] ?: "To: ________"
        val amount = params["amount"] ?: "0"
        val items = params["items"] ?: ""

        canvas.drawText("QUOTATION", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("To: $to", margin(), margin() + 140f, bodyPaint)
        items.lines().take(15).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 170f + index * 20f, bodyPaint)
        }
        canvas.drawText("Quoted amount: $amount", margin(), margin() + 440f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "quotation")
        save(document, file)
        return file
    }

    private fun generateProforma(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val from = params["from"] ?: "From: ________"
        val to = params["to"] ?: "To: ________"
        val amount = params["amount"] ?: "0"

        canvas.drawText("PROFORMA INVOICE", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("To: $to", margin(), margin() + 140f, bodyPaint)
        canvas.drawText("Estimated amount: $amount", margin(), margin() + 190f, bodyPaint)
        canvas.drawText("This is a proforma invoice, not a tax invoice.", margin(), margin() + 240f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "proforma")
        save(document, file)
        return file
    }

    private fun generatePurchaseOrder(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val buyer = params["buyer"] ?: "Buyer: ________"
        val seller = params["seller"] ?: "Seller: ________"
        val amount = params["amount"] ?: "0"
        val items = params["items"] ?: ""

        canvas.drawText("PURCHASE ORDER", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(buyer, margin(), margin() + 100f, bodyPaint)
        canvas.drawText(seller, margin(), margin() + 140f, bodyPaint)
        items.lines().take(15).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 180f + index * 20f, bodyPaint)
        }
        canvas.drawText("Order total: $amount", margin(), margin() + 500f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "purchase_order")
        save(document, file)
        return file
    }

    private fun generateDeliveryChallan(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val from = params["from"] ?: "From: ________"
        val to = params["to"] ?: "To: ________"
        val items = params["items"] ?: ""

        canvas.drawText("DELIVERY CHALLAN", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("To: $to", margin(), margin() + 140f, bodyPaint)
        items.lines().take(20).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 180f + index * 20f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "delivery_challan")
        save(document, file)
        return file
    }

    private fun generatePackingSlip(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val from = params["from"] ?: "From: ________"
        val to = params["to"] ?: "To: ________"
        val items = params["items"] ?: ""

        canvas.drawText("PACKING SLIP", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText(from, margin(), margin() + 100f, bodyPaint)
        canvas.drawText("To: $to", margin(), margin() + 140f, bodyPaint)
        items.lines().take(20).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 180f + index * 20f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "packing_slip")
        save(document, file)
        return file
    }

    private fun generateWarrantyCard(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 24f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val product = params["product"] ?: "________"
        val customer = params["customer"] ?: "________"
        val period = params["period"] ?: "________"
        val date = params["date"] ?: dateString()

        canvas.drawText("WARRANTY CARD", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Product: $product", margin(), margin() + 70f, bodyPaint)
        canvas.drawText("Customer: $customer", margin(), margin() + 100f, bodyPaint)
        canvas.drawText("Warranty period: $period", margin(), margin() + 130f, bodyPaint)
        canvas.drawText("Date: $date", margin(), margin() + 160f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "warranty_card")
        save(document, file)
        return file
    }

    private fun generateBusinessLetter(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val from = params["from"] ?: "________"
        val to = params["to"] ?: "________"
        val subject = params["subject"] ?: "________"
        val body = params["body"] ?: ""

        canvas.drawText("BUSINESS LETTER", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Date: ${dateString()}", margin(), margin() + 60f, bodyPaint)
        canvas.drawText("From: $from", margin(), margin() + 100f, bodyPaint)
        canvas.drawText("To: $to", margin(), margin() + 130f, bodyPaint)
        canvas.drawText("Subject: $subject", margin(), margin() + 170f, bodyPaint)
        body.lines().take(25).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 210f + index * 20f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "business_letter")
        save(document, file)
        return file
    }

    private fun generateStudentId(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val name = params["name"] ?: "________"
        val roll = params["roll"] ?: "________"
        val course = params["course"] ?: "________"
        val expires = params["expires"] ?: "________"

        canvas.drawText("STUDENT ID", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Name: $name", margin(), margin() + 70f, bodyPaint)
        canvas.drawText("Roll: $roll", margin(), margin() + 100f, bodyPaint)
        canvas.drawText("Course: $course", margin(), margin() + 130f, bodyPaint)
        canvas.drawText("Valid until: $expires", margin(), margin() + 160f, bodyPaint)

        document.finishPage(page)
        val file = outputFile(context, "student_id")
        save(document, file)
        return file
    }

    private fun generateMarksheet(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val name = params["name"] ?: "________"
        val roll = params["roll"] ?: "________"
        val marks = params["marks"] ?: ""

        canvas.drawText("MARK SHEET", margin(), margin() + 30f, titlePaint)
        canvas.drawText("Name: $name", margin(), margin() + 60f, bodyPaint)
        canvas.drawText("Roll: $roll", margin(), margin() + 90f, bodyPaint)
        canvas.drawText("Marks:", margin(), margin() + 130f, bodyPaint)
        marks.lines().take(20).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 160f + index * 20f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "marksheet")
        save(document, file)
        return file
    }

    private fun generateTimetable(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val title = params["title"] ?: "Timetable"
        val entries = params["entries"] ?: ""

        canvas.drawText(title.uppercase(), margin(), margin() + 30f, titlePaint)
        entries.lines().take(25).forEachIndexed { index, line ->
            canvas.drawText(line, margin(), margin() + 70f + index * 20f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "timetable")
        save(document, file)
        return file
    }

    private fun generateWorksheet(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val title = params["title"] ?: "Worksheet"
        val questions = params["questions"] ?: ""

        canvas.drawText(title, margin(), margin() + 30f, titlePaint)
        questions.lines().take(25).forEachIndexed { index, line ->
            canvas.drawText("${index + 1}. $line", margin(), margin() + 70f + index * 25f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "worksheet")
        save(document, file)
        return file
    }

    private fun generateAnswerSheet(context: Context, params: Map<String, String>): File {
        val document = createDocument()
        val page = startPage(document, 1)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 20f; typeface = Typeface.DEFAULT_BOLD; color = 0xFF000000.toInt() }
        val bodyPaint = Paint().apply { textSize = 12f; color = 0xFF000000.toInt() }
        val title = params["title"] ?: "Answer Sheet"
        val questions = params["questions"] ?: ""

        canvas.drawText(title, margin(), margin() + 30f, titlePaint)
        questions.lines().take(25).forEachIndexed { index, line ->
            canvas.drawText("${index + 1}. $line", margin(), margin() + 70f + index * 25f, bodyPaint)
            canvas.drawText("Answer: ____________________", margin() + 20f, margin() + 90f + index * 25f, bodyPaint)
        }

        document.finishPage(page)
        val file = outputFile(context, "answer_sheet")
        save(document, file)
        return file
    }
}
