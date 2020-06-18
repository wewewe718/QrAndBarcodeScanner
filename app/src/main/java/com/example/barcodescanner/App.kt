package com.example.barcodescanner

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.example.barcodescanner.usecase.BarcodeDatabaseFactory
import com.example.barcodescanner.usecase.BarcodeImageGenerator
import com.example.barcodescanner.usecase.BarcodeImageSaver
import com.example.barcodescanner.usecase.BarcodeSchemaParser

class App : MultiDexApplication() {
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