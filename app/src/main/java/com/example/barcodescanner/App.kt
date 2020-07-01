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
    val barcodeImageScanner by lazy { BarcodeImageScanner() }
    val barcodeScanResultParser by lazy { BarcodeScanResultParser() }
    val barcodeImageGenerator by lazy { BarcodeImageGenerator() }
    val barcodeImageSaver by lazy { BarcodeImageSaver(barcodeImageGenerator) }
    val wifiConnector by lazy { WifiConnector(this) }
    val checkReceiptApi by lazy { CheckReceiptApi(this) }
    val settings by lazy { Settings(this) }
}