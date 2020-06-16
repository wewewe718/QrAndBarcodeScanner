package com.example.barcodescanner

import android.app.Application
import androidx.room.Room
import com.example.barcodescanner.usecase.BarcodeImageGenerator
import com.example.barcodescanner.usecase.BarcodeSchemaParser
import com.example.barcodescanner.usecase.BarcodeDatabaseFactory
import com.example.barcodescanner.usecase.BarcodeImageSaver

class App : Application() {
    val barcodeDatabase by lazy {
        Room
            .databaseBuilder(this, BarcodeDatabaseFactory::class.java, "db")
            .build()
            .getBarcodeDatabase()
    }

    val barcodeSchemaParser by lazy { BarcodeSchemaParser() }
    val barcodeImageGenerator by lazy { BarcodeImageGenerator() }
    val barcodeImageSaver by lazy { BarcodeImageSaver(barcodeImageGenerator) }
}