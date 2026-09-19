package org.phireox.ofa.core.files

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException

object FileSaver {

    fun saveToDownloads(context: Context, file: File, displayName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, displayName)
                put(MediaStore.Downloads.MIME_TYPE, getMimeType(displayName))
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    file.inputStream().use { input -> input.copyTo(output) }
                }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                uri
            } catch (e: IOException) {
                null
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outFile = File(downloadsDir, displayName)
            try {
                file.copyTo(outFile, overwrite = true)
                androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outFile)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun getMimeType(name: String): String {
        return when {
            name.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            name.endsWith(".png", ignoreCase = true) -> "image/png"
            name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            name.endsWith(".zip", ignoreCase = true) -> "application/zip"
            else -> "application/octet-stream"
        }
    }
}
