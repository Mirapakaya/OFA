package org.phireox.ofa.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import org.phireox.ofa.data.model.Tool
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FileToolProcessor {

    fun process(context: Context, tool: Tool, uri: Uri, params: Map<String, String> = emptyMap()): ToolResult {
        return try {
            when (tool.id) {
                "pdf_metadata" -> pdfMetadata(context, uri)
                "pdf_to_images" -> pdfToImages(context, uri)
                "pdf_merge" -> pdfMerge(context, uri)
                "pdf_split" -> pdfSplit(context, uri)
                "pdf_compress" -> pdfCompress(context, uri)
                "images_to_pdf" -> imageToPdf(context, uri)
                "image_compress" -> imageCompress(context, uri, params)
                "image_resize" -> imageResize(context, uri, params)
                "image_rotate" -> imageRotate(context, uri, params)
                "image_flip" -> imageFlip(context, uri, params)
                "image_crop" -> imageCrop(context, uri, params)
                "image_to_pdf" -> imageToPdf(context, uri)
                "exif_viewer" -> exifViewer(context, uri)
                "file_hash" -> fileHash(context, uri, params["algo"] ?: "SHA-256")
                "mime_detector" -> ToolResult.Text(context.contentResolver.getType(uri) ?: "unknown/unknown")
                "zip_creator" -> zipFiles(context, listOf(uri))
                "zip_extractor" -> extractZip(context, uri)
                else -> ToolResult.Text("File processing not yet implemented for ${tool.title}")
            }
        } catch (e: Exception) {
            ToolResult.Error(e.localizedMessage ?: "File processing error")
        }
    }

    private fun pdfMetadata(context: Context, uri: Uri): ToolResult {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PDDocument.load(stream).use { doc ->
                val info = doc.documentInformation
                val text = buildString {
                    appendLine("Title: ${info.title ?: ""}")
                    appendLine("Author: ${info.author ?: ""}")
                    appendLine("Subject: ${info.subject ?: ""}")
                    appendLine("Creator: ${info.creator ?: ""}")
                    appendLine("Producer: ${info.producer ?: ""}")
                    appendLine("Pages: ${doc.numberOfPages}")
                }
                return ToolResult.Text(text)
            }
        }
        return ToolResult.Error("Cannot open PDF")
    }

    private fun pdfToImages(context: Context, uri: Uri): ToolResult {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return ToolResult.Error("Cannot open PDF")
        val renderer = PdfRenderer(pfd)
        val outDir = File(context.cacheDir, "ofa_pdf_images").apply { mkdirs() }
        val paths = mutableListOf<String>()
        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            val file = File(outDir, "page_$i.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            paths.add(file.absolutePath)
        }
        renderer.close()
        pfd.close()
        return ToolResult.Text("Extracted ${paths.size} images:\n${paths.joinToString("\n")}")
    }

    private fun pdfSplit(context: Context, uri: Uri): ToolResult {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return ToolResult.Error("Cannot open PDF")
        val renderer = PdfRenderer(pfd)
        val outDir = File(context.cacheDir, "ofa_pdf_split").apply { mkdirs() }
        val paths = mutableListOf<String>()
        for (i in 0 until renderer.pageCount) {
            val document = PdfDocument()
            val page = renderer.openPage(i)
            val pageInfo = PdfDocument.PageInfo.Builder(page.width, page.height, i + 1).create()
            val docPage = document.startPage(pageInfo)
            page.render(docPage.canvas, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            document.finishPage(docPage)
            val file = File(outDir, "page_${i + 1}.pdf")
            FileOutputStream(file).use { document.writeTo(it) }
            document.close()
            paths.add(file.absolutePath)
        }
        renderer.close()
        pfd.close()
        return ToolResult.Text("Split into ${paths.size} pages:\n${paths.joinToString("\n")}")
    }

    private fun pdfCompress(context: Context, uri: Uri): ToolResult {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PDDocument.load(stream).use { doc ->
                val outFile = File(context.cacheDir, "compressed.pdf")
                FileOutputStream(outFile).use { doc.save(it) }
                return ToolResult.Text("Saved compressed PDF to ${outFile.absolutePath}")
            }
        }
        return ToolResult.Error("Cannot open PDF")
    }

    private fun pdfMerge(context: Context, uri: Uri): ToolResult {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PDDocument.load(stream).use { doc ->
                val outFile = File(context.cacheDir, "merged.pdf")
                FileOutputStream(outFile).use { doc.save(it) }
                return ToolResult.Text("Saved merged PDF to ${outFile.absolutePath}\n(Multi-file merge requires selecting multiple files.)")
            }
        }
        return ToolResult.Error("Cannot open PDF")
    }

    private fun exifViewer(context: Context, uri: Uri): ToolResult {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = android.media.ExifInterface(stream)
                val tags = listOf(
                    ExifInterface.TAG_MAKE to "Make",
                    ExifInterface.TAG_MODEL to "Model",
                    ExifInterface.TAG_DATETIME to "Date",
                    ExifInterface.TAG_IMAGE_WIDTH to "Width",
                    ExifInterface.TAG_IMAGE_LENGTH to "Height",
                    ExifInterface.TAG_F_NUMBER to "Aperture",
                    ExifInterface.TAG_EXPOSURE_TIME to "Exposure",
                    ExifInterface.TAG_ISO to "ISO",
                    ExifInterface.TAG_FOCAL_LENGTH to "Focal length",
                    ExifInterface.TAG_GPS_LATITUDE to "Latitude",
                    ExifInterface.TAG_GPS_LONGITUDE to "Longitude"
                )
                val text = tags.joinToString("\n") { "${it.second}: ${exif.getAttribute(it.first) ?: "n/a"}" }
                return ToolResult.Text(text)
            } ?: ToolResult.Error("Cannot open image")
        } catch (e: Exception) {
            return ToolResult.Error("EXIF read failed: ${e.localizedMessage}")
        }
    }

    private fun imageCompress(context: Context, uri: Uri, params: Map<String, String>): ToolResult {
        val quality = params["quality"]?.toIntOrNull() ?: 80
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val outFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), it) }
        return ToolResult.Text("Saved to ${outFile.absolutePath}")
    }

    private fun imageResize(context: Context, uri: Uri, params: Map<String, String>): ToolResult {
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val width = params["width"]?.toIntOrNull() ?: bitmap.width
        val height = params["height"]?.toIntOrNull() ?: bitmap.height
        val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
        val outFile = File(context.cacheDir, "resized_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return ToolResult.Text("Saved resized image to ${outFile.absolutePath}")
    }

    private fun imageRotate(context: Context, uri: Uri, params: Map<String, String>): ToolResult {
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val angle = params["angle"]?.toFloatOrNull() ?: 90f
        val matrix = Matrix().apply { postRotate(angle) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val outFile = File(context.cacheDir, "rotated_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { rotated.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return ToolResult.Text("Saved rotated image to ${outFile.absolutePath}")
    }

    private fun imageFlip(context: Context, uri: Uri, params: Map<String, String>): ToolResult {
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val horizontal = params["horizontal"] != "false"
        val matrix = Matrix().apply {
            if (horizontal) postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            else postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        val flipped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val outFile = File(context.cacheDir, "flipped_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { flipped.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return ToolResult.Text("Saved flipped image to ${outFile.absolutePath}")
    }

    private fun imageCrop(context: Context, uri: Uri, params: Map<String, String>): ToolResult {
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val cropWidth = params["width"]?.toIntOrNull() ?: 300
        val cropHeight = params["height"]?.toIntOrNull() ?: 300
        val width = cropWidth.coerceAtMost(bitmap.width)
        val height = cropHeight.coerceAtMost(bitmap.height)
        val left = ((bitmap.width - width) / 2).coerceAtLeast(0)
        val top = ((bitmap.height - height) / 2).coerceAtLeast(0)
        val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
        val outFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { cropped.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return ToolResult.Text("Saved cropped image to ${outFile.absolutePath}")
    }

    private fun imageToPdf(context: Context, uri: Uri): ToolResult {
        val bitmap = decodeBitmap(context, uri) ?: return ToolResult.Error("Cannot decode image")
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)
        val outFile = File(context.cacheDir, "image_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outFile).use { document.writeTo(it) }
        document.close()
        return ToolResult.Text("Saved PDF to ${outFile.absolutePath}")
    }

    private fun fileHash(context: Context, uri: Uri, algo: String): ToolResult {
        val digest = MessageDigest.getInstance(algo.uppercase())
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                digest.update(buffer, 0, read)
            }
        } ?: return ToolResult.Error("Cannot open file")
        val hash = BigInteger(1, digest.digest()).toString(16)
        return ToolResult.Text("$algo: $hash")
    }

    private fun zipFiles(context: Context, uris: List<Uri>): ToolResult {
        val outFile = File(context.cacheDir, "archive_${System.currentTimeMillis()}.zip")
        ZipOutputStream(FileOutputStream(outFile)).use { zos ->
            for (uri in uris) {
                val name = uri.lastPathSegment ?: "file"
                context.contentResolver.openInputStream(uri)?.use { input ->
                    zos.putNextEntry(ZipEntry(name))
                    input.copyTo(zos)
                    zos.closeEntry()
                }
            }
        }
        return ToolResult.Text("ZIP created: ${outFile.absolutePath}")
    }

    private fun extractZip(context: Context, uri: Uri): ToolResult {
        val outDir = File(context.cacheDir, "ofa_zip_${System.currentTimeMillis()}").apply { mkdirs() }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            ZipInputStream(stream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val file = File(outDir, entry.name)
                    file.parentFile?.mkdirs()
                    FileOutputStream(file).use { zis.copyTo(it) }
                    entry = zis.nextEntry
                }
            }
        } ?: return ToolResult.Error("Cannot open ZIP")
        return ToolResult.Text("Extracted to ${outDir.absolutePath}")
    }

    private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            android.graphics.BitmapFactory.decodeStream(stream)
        }
    }
}
