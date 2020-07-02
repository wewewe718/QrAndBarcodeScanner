package com.example.barcodescanner

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.example.barcodescanner.usecase.*

class App : MultiDexApplication() {
    val barcodeDatabase by lazy { BarcodeDatabase.newInstance(this) }
    val barcodeImageSaver by lazy { BarcodeImageSaver(BarcodeImageGenerator) }
    val wifiConnector by lazy { WifiConnector(this) }
    val checkReceiptApi by lazy { CheckReceiptApi(this) }
    val settings by lazy { Settings(this) }
}