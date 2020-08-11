package com.example.barcodescanner.usecase

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore.Images
import androidx.core.content.FileProvider
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ParsedBarcode
import io.reactivex.Completable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


object BarcodeImageSaver {

    fun saveImageToCache(context: Context, image: Bitmap, barcode: ParsedBarcode): Uri? {
        // Create folder for image
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()

        // Create image file
        val imageFileName = "${barcode.format}_${barcode.schema}_${barcode.date}.png"
        val imageFile = File(imagesFolder, imageFileName)

        // Save image to file
        FileOutputStream(imageFile).apply {
            image.compress(Bitmap.CompressFormat.PNG, 100, this)
            flush()
            close()
        }

        // Return Uri for image file
        return FileProvider.getUriForFile(context, "com.example.barcodescanner.fileprovider", imageFile)
    }

    fun savePngImageToPublicDirectory(context: Context, image: Bitmap, barcode: Barcode): Completable {
        return Completable.create { emitter ->
            try {
                saveToPublicDirectory(context, barcode, "image/png") { outputStream ->
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }

    fun saveSvgImageToPublicDirectory(context: Context, image: String, barcode: Barcode): Completable {
        return Completable.create { emitter ->
            try {
                saveToPublicDirectory(context, barcode, "image/svg+xml") { outputStream ->
                    outputStream.write(image.toByteArray())
                }
                emitter.onComplete()
            } catch (ex: Exception) {
                Logger.log(ex)
                emitter.onError(ex)
            }
        }
    }

    private fun saveToPublicDirectory(context: Context, barcode: Barcode, mimeType:String, action: (OutputStream)-> Unit) {
        val contentResolver = context.contentResolver ?: return

        val imageTitle = "${barcode.format}_${barcode.schema}_${barcode.date}"

        val values = ContentValues().apply {
            put(Images.Media.TITLE, imageTitle)
            put(Images.Media.DISPLAY_NAME, imageTitle)
            put(Images.Media.MIME_TYPE, mimeType)
            put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        }

        val uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
        contentResolver.openOutputStream(uri)?.apply {
            action(this)
            flush()
            close()
        }
    }
}