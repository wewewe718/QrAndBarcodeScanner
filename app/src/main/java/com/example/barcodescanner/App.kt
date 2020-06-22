package com.example.barcodescanner

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.example.barcodescanner.usecase.*

class App : MultiDexApplication() {
    val barcodeDatabase by lazy {
        Room
            .databaseBuilder(this, BarcodeDatabaseFactory::class.java, "db")
            .build()
            .getBarcodeDatabase()
    }
    val scannerCameraHelper by lazy { ScannerCameraHelper() }
    val barcodeSchemaParser by lazy { BarcodeSchemaParser() }
    val barcodeImageGenerator by lazy { BarcodeImageGenerator() }
    val barcodeImageSaver by lazy { BarcodeImageSaver(barcodeImageGenerator) }
    val checkReceiptApi by lazy { CheckReceiptApi() }
}