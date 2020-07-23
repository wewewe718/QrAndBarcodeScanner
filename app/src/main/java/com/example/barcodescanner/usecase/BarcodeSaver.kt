package com.example.barcodescanner.usecase

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.barcodescanner.extension.formatOrNull
import com.example.barcodescanner.model.Barcode
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


object BarcodeSaver {
    private val dateFormatter by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }

    fun saveBarcodeAsJson(context: Context, barcode: Barcode) {
        val json = convertToJson(barcode)
        saveToDownloads(context, barcode, json, ".json", "application/json")
    }

    fun saveBarcodeAsCsv(context: Context, barcode: Barcode) {
        val csv = convertToCsv(barcode)
        saveToDownloads(context, barcode, csv, ".csv", "text/csv")
    }

    private fun convertToJson(barcode: Barcode): String {
        return JSONObject()
            .put("date", dateFormatter.formatOrNull(barcode.date))
            .put("format", barcode.format.name)
            .put("text", barcode.text)
            .toString()
    }

    private fun convertToCsv(barcode: Barcode): String {
        return StringBuilder()
            .append("Date,Format,Text")
            .append('\n')
            .append("${dateFormatter.formatOrNull(barcode.date)},${barcode.format},${barcode.text}")
            .toString()
    }

    private fun saveToDownloads(context: Context, barcode: Barcode, content: String, extension: String, mimeType: String) {
        val fileName = "${barcode.format}_${barcode.schema}_${barcode.date}$extension"
        openFileOutputStream(context, fileName, mimeType).apply {
            write(content.toByteArray())
            flush()
            close()
        }
    }

    private fun openFileOutputStream(context: Context, fileName: String, mimeType: String): OutputStream {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            openFileOutputStreamOldSdk(fileName)
        } else {
            openFileOutputStreamNewSdk(context, fileName, mimeType)
        }
    }

    @Suppress("DEPRECATION")
    private fun openFileOutputStreamOldSdk(fileName: String): OutputStream {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, fileName)
        if (file.exists()) {
            file.delete()
        }
        return FileOutputStream(file)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openFileOutputStreamNewSdk(context: Context, fileName: String, mimeType: String): OutputStream {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: throw IOException()
        return resolver.openOutputStream(uri) ?: throw IOException()
    }
}