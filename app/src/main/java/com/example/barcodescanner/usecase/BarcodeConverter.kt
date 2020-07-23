package com.example.barcodescanner.usecase

import com.example.barcodescanner.extension.formatOrNull
import com.example.barcodescanner.model.Barcode
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object BarcodeConverter {
    private val dateFormatter by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    }

    fun convertToJson(barcode: Barcode): String {
        return JSONObject()
            .put("date", dateFormatter.formatOrNull(barcode.date))
            .put("format", barcode.format.name)
            .put("text", barcode.text)
            .toString()
    }

    fun convertToCsv(barcode: Barcode): String {
        return StringBuilder()
            .append("Date,Format,Text")
            .append("${dateFormatter.formatOrNull(barcode.date)},${barcode.format},${barcode.text}")
            .toString()
    }
}