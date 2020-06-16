package com.example.barcodescanner.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.barcodescanner.model.Barcode
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

class BarcodeImageSaver(private val barcodeImageGenerator: BarcodeImageGenerator) {

    fun saveImageToCache(context: Context, barcode: Barcode): Uri {
        // Create folder for image
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()

        // Create image file
        val imageFileName = "${barcode.format}_${barcode.schema}_${barcode.date}.png"
        val imageFile = File(imagesFolder, imageFileName)

        // Generate image
        val image = barcodeImageGenerator.generateImage(barcode, 200, 200, 1)

        // Save image to file
        FileOutputStream(imageFile).apply {
            image.compress(Bitmap.CompressFormat.PNG, 90, this)
            flush()
            close()
        }

        // Return Uri for image file
        return FileProvider.getUriForFile(context, "com.example.barcodescanner.fileprovider", imageFile)
    }
}