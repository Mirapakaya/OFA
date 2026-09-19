package org.phireox.ofa.feature.maps

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

data class MapTile(val zoom: Int, val x: Int, val y: Int, val data: ByteArray)

class MbtilesReader(private val dbPath: String) {

    private var db: SQLiteDatabase? = null

    init {
        if (File(dbPath).exists()) {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        }
    }

    fun isOpen(): Boolean = db != null

    fun minZoom(): Int {
        val c = db?.rawQuery("SELECT MIN(zoom_level) FROM tiles", null) ?: return 0
        c.use { return if (c.moveToFirst()) c.getInt(0) else 0 }
    }

    fun maxZoom(): Int {
        val c = db?.rawQuery("SELECT MAX(zoom_level) FROM tiles", null) ?: return 0
        c.use { return if (c.moveToFirst()) c.getInt(0) else 0 }
    }

    fun getTile(zoom: Int, x: Int, y: Int): MapTile? {
        val c = db?.rawQuery(
            "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
            arrayOf(zoom.toString(), x.toString(), y.toString())
        ) ?: return null
        c.use {
            if (it.moveToFirst()) {
                val data = it.getBlob(0)
                return MapTile(zoom, x, y, data)
            }
        }
        return null
    }

    fun listTiles(zoom: Int): List<TileIndex> {
        val result = mutableListOf<TileIndex>()
        val c = db?.rawQuery(
            "SELECT tile_column, tile_row FROM tiles WHERE zoom_level=?",
            arrayOf(zoom.toString())
        ) ?: return result
        c.use {
            while (it.moveToNext()) {
                result.add(TileIndex(it.getInt(0), it.getInt(1)))
            }
        }
        return result
    }

    data class TileIndex(val x: Int, val y: Int)

    companion object {
        fun import(context: Context, uri: Uri): MbtilesReader? {
            val dir = File(context.filesDir, "ofa_maps").apply { mkdirs() }
            val out = File(dir, "offline.mbtiles")
            context.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            return if (out.exists()) MbtilesReader(out.absolutePath) else null
        }
    }
}
