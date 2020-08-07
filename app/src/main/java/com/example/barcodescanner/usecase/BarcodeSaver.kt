package com.example.barcodescanner.usecase

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.barcodescanner.extension.endsWithIgnoreCase
import com.example.barcodescanner.extension.formatOrNull
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ExportBarcode
import com.google.zxing.BarcodeFormat
import io.reactivex.Completable
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


object BarcodeSaver {
    private const val JSON_MIME_TYPE = "application/json"
    private const val JSON_FILE_EXTENSION = ".json"

    private const val CSV_MIME_TYPE = "text/csv"
    private const val CSV_FILE_EXTENSION = ".csv"

    private val dateFormatter by unsafeLazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }


    fun saveBarcodeAsJson(context: Context, barcode: Barcode): Completable {
        return Completable.create { emitter ->
            try {
                val json = convertToJson(barcode)
                saveToDownloads(context, barcode, json, JSON_FILE_EXTENSION, JSON_MIME_TYPE)
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }

    fun saveBarcodeAsCsv(context: Context, barcode: Barcode): Completable {
        return Completable.create { emitter ->
            try {
                val csv = convertToCsv(barcode)
                saveToDownloads(context, barcode, csv, CSV_FILE_EXTENSION, CSV_MIME_TYPE)
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }


    fun saveBarcodeHistoryAsJson(context: Context, fileName: String, barcodes: List<ExportBarcode>): Completable {
        return Completable.create { emitter ->
            try {
                trySaveBarcodeHistoryAsJson(context, fileName, barcodes)
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }

    fun saveBarcodeHistoryAsCsv(context: Context, fileName: String, barcodes: List<ExportBarcode>): Completable {
        return Completable.create { emitter ->
            try {
                trySaveBarcodeHistoryCsv(context, fileName, barcodes)
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }


    private fun trySaveBarcodeHistoryAsJson(context: Context, fileName: String, barcodes: List<ExportBarcode>) {
        val jsons = barcodes.map(::convertToJson)
        val result = JSONArray(jsons)

        val newFileName = if (fileName.endsWithIgnoreCase(JSON_FILE_EXTENSION)) {
            fileName
        } else {
            "$fileName$JSON_FILE_EXTENSION"
        }

        saveToDownloads(context, newFileName, result.toString(), JSON_FILE_EXTENSION)
    }

    private fun convertToJson(barcode: Barcode): String {
        return convertToJson(barcode.date, barcode.format, barcode.text).toString()
    }

    private fun convertToJson(barcode: ExportBarcode): JSONObject {
        return convertToJson(barcode.date, barcode.format, barcode.text)
    }

    private fun convertToJson(date: Long, format: BarcodeFormat, text: String): JSONObject {
        return JSONObject()
            .put("date", dateFormatter.formatOrNull(date))
            .put("format", format.name)
            .put("text", text)
    }


    private fun trySaveBarcodeHistoryCsv(context: Context, fileName: String, barcodes: List<ExportBarcode>) {
        val result = StringBuilder()
            .append("Date,Format,Text\n")

        barcodes.forEach { barcode ->
            result.append("${dateFormatter.formatOrNull(barcode.date)},${barcode.format},${barcode.text}\n")
        }

        val newFileName = if (fileName.endsWithIgnoreCase(CSV_FILE_EXTENSION)) {
            fileName
        } else {
            "$fileName$CSV_FILE_EXTENSION"
        }

        saveToDownloads(context, newFileName, result.toString(), CSV_FILE_EXTENSION)
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
        saveToDownloads(context, fileName, content, mimeType)
    }

    private fun saveToDownloads(context: Context, fileName: String, content: String, mimeType: String) {
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