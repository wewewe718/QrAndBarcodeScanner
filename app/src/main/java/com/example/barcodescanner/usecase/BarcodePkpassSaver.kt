package com.example.barcodescanner.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.BoardingPass
import com.example.barcodescanner.extension.unsafeLazy
import io.reactivex.Completable
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import android.util.Log

object BarcodePkpassSaver {
    private const val MIME_TYPE = "application/vnd.apple.pkpass"
    private const val FILE_EXTENSION = ".pkpass"
    private const val TAG = "BarcodePkpassSaver"

    private val DATE_FORMATTER by unsafeLazy { SimpleDateFormat("YYYY-MM-dd\'T\'HH:MMZZZZZ", Locale.ENGLISH) }

    fun saveBarcodeAsPkpass(context: Context, barcode: Barcode, uri: Array<Uri?>?): Completable {
        Log.i(TAG, "saving barcode")
        return Completable.create { emitter ->
            try {
                val bp = BoardingPass.parse(barcode.text)
                saveToDownloads(context, barcode, bp!!, FILE_EXTENSION, MIME_TYPE, uri)
                emitter.onComplete()
            } catch (ex: Exception) {
                Log.e(TAG, "Save zipfile", ex)
                emitter.onError(ex)
            }
        }
    }

    private fun airportTrim(name: String) : String {
        return name.removeSuffix("Airport")
            .removeSuffix("airport")
            .trim()
            .removeSuffix("International")
            .removeSuffix("international")
            .trim()
    }

    private fun JSONArray.label(key: String, label: String,
        value: String?) : JSONArray {
        if (value == null) {
            return this
        }

        return this.put(JSONObject()
            .put("key", key)
            .put("label", label)
            .put("value", value)
        )
    }
    private fun removeLeadingZeros(value: String?) : String? {
        when(value) {
            null -> return null
            "" -> return ""
        }

        for (i in 0 until value!!.length) {
            if (value[i] != '0') {
                return value.slice(i..value.length-1)
            }
        }
        return "0"
    }

    private fun alternate(bp: BoardingPass) : String {
        if (bp.fasttrack == "Y" && bp.selectee == "3") {
            return "FAST TRACK|TSA PRECHK"
        } else if (bp.fasttrack == "Y") {
            return "FAST TRACK"
        } else if (bp.selectee == "3") {
            return "TSA PRECHK"
        } else {
            return ""
        }
    }

    private fun cabin(bp: BoardingPass) : String? {
        if (bp.cabin == null) {
            return null
        }
        when (bp.cabin) {
            in "R","P" -> return "Premium First"
            in "F","A" -> return "First"
            in "J","C","D","I","Z" -> return "Business"
            "W" -> return "Premium Economy"
            in "Y","B","M","S","H","K","L","N","Q","T","V","X" -> return "Economy"
        }
        return null
    }

    private fun convertToJson(bp : BoardingPass): JSONObject {
        val flight = removeLeadingZeros(bp.flight)
	val date = Calendar.getInstance()
        var from = bp.from!!
        var to = bp.to!!
        val locations = JSONArray()
	val seq = if (bp.seq.isNullOrBlank()) {
	    null
	} else {
	    removeLeadingZeros(bp.seq)
	}
	val ticket = if (bp.ticket.isNullOrBlank()) {
	    null
	} else {
	    bp.ticket
	}
	val ff = if (bp.ffNo.isNullOrBlank()) {
	    null
	} else {
	    "${bp.ffAirline} ${bp.ffNo}"
	}
	date.set(Calendar.DAY_OF_YEAR, bp.dateJ)
	// cheat: we don't know the time, so set to current time on boarding day
	val relevantDate: String = DATE_FORMATTER.format(date.getTime())
        return JSONObject()
            .put("barcode", JSONObject()
                .put("altText", alternate(bp))
                .put("format", "PkBarcodeFormatAztec")
                .put("message", bp.blob)
                .put("messageEncoding", "iso-8859-1")
            )
            .put("description", "${bp.carrier} Flight ${flight} on ${bp.date} departing ${from}")
            .put("locations", locations)
            .put("boardingPass", JSONObject()
                .put("primaryFields", JSONArray()
                    .label("depart", "Depart", from)
                    .label("arrive", "Arrive", to)
                )
                .put("secondaryFields", JSONArray()
                    .label("passenger", "", bp.name)
                    .label("priorityaccess", "Cabin", cabin(bp))
                )
                .put("headerFields", JSONArray()
                    .label("flight", "Flight", "${bp.carrier}${flight}")
                    .label("gate", "Gate", "")
                )
                .put("auxiliaryFields", JSONArray()
                    .label("group", "Group", "")
                    .label("seat", "Seat", removeLeadingZeros(bp.seat))
                    .label("status", "Status", "On Time")
                    .label("terminal", "Terminal", "")
                    .label("boardingTime", "Departs", "")
                )
                .put("backFields", JSONArray()
                    .label("date", "Date", bp.date)
                    .label("gateBoardingTime", "Boarding Time", "")
                    .label("record_locator", "Record Locator", bp.pnr)
                    .label("seq", "Sequence", seq)
                    .label("ff_number", "Frequent Flyer", ff)
                    .label("ticket_number", "Ticket", ticket)
                    )
                .put("transitType", "PKTransitTypeAir")
                )
            .put("serialNumber", UUID.randomUUID())
	    .put("organizationName", "QRAndBarcodeScanner")
	    .put("passTypeIdentifier", "com.example.barcodescanner")
            .put("formatVersion", 1)
            .put("backgroundColor", "#ff0000ff")
            .put("relevantDate", relevantDate)
            .put("voided", false)
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") {
        eachByte -> "%02x".format(eachByte)
    }

    private fun ZipOutputStream.addManifest(manifest: JSONObject, fileName: String, content: ByteArray) {
        val md = MessageDigest.getInstance("SHA-1")
        this.putNextEntry(ZipEntry(fileName))
        this.write(content)
        this.closeEntry()
        md.update(content)
        manifest.put(fileName, md.digest().toHex())
    }


    private fun saveToDownloads(context: Context, barcode: Barcode, bp: BoardingPass, extension: String, mimeType: String, uri: Array<Uri?>?) {
        val fileName = "BoardingPass_${barcode.date}$extension"
        Log.i(TAG, "constructed filename $fileName")
        val main = convertToJson(bp).toString()
        val out = openFileOutputStream(context, fileName, mimeType, uri)
        val zipout = ZipOutputStream(out)
        val manifest = JSONObject()
        val assets = context.getAssets()

        val icon =  try {
            assets.open("img/${bp.carrier}/icon.png").readBytes()
        } catch(e: Exception) {
            assets.open("img/icon.png").readBytes()
        }

        zipout.addManifest(manifest, "pass.json", main.toByteArray())
        zipout.addManifest(manifest, "icon.png", icon)
        if (bp.selectee == "3") {
            val footer = assets.open("img/tsapre.png").readBytes()
            zipout.addManifest(manifest, "footer.png", footer)
	}
        try {
            val thumbnail = assets.open("img/${bp.carrier}/thumbnail.png").readBytes()
            zipout.addManifest(manifest, "thumbnail.png", thumbnail)
        } catch(e: Exception) { }
        try {
            val logo = assets.open("img/${bp.carrier}/logo.png").readBytes()
            zipout.addManifest(manifest, "logo.png", logo)
        } catch(e: Exception) { }
        zipout.putNextEntry(ZipEntry("manifest.json"))
        zipout.write(manifest.toString().toByteArray())
        zipout.close()
        out.close()
    }

    private fun openFileOutputStream(context: Context, fileName: String, mimeType: String, uri: Array<Uri?>?): OutputStream {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            openFileOutputStreamOldSdk(fileName, uri)
        } else {
            openFileOutputStreamNewSdk(context, fileName, mimeType, uri)
        }
    }

    @Suppress("DEPRECATION")
    private fun openFileOutputStreamOldSdk(fileName: String, uri: Array<Uri?>?): OutputStream {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, fileName)
        if (file.exists()) {
            file.delete()
        }
	if (uri != null) {
	    uri[0] = Uri.fromFile(file)
	    file.deleteOnExit()
	}
        return FileOutputStream(file)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openFileOutputStreamNewSdk(context: Context, fileName: String, mimeType: String, uri: Array<Uri?>?): OutputStream {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
        }
        val uris = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: throw IOException()
	if (uri != null) {
	    Log.i(TAG, "URI is " + uris)
	    uri[0] = uris
	}
        return resolver.openOutputStream(uris) ?: throw IOException()
    }
}
