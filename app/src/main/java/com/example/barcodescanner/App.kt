package com.example.barcodescanner

import android.app.Application
import androidx.room.Room
import com.example.barcodescanner.usecase.BarcodeImageGenerator
import com.example.barcodescanner.usecase.BarcodeSchemaParser
import com.example.barcodescanner.usecase.BarcodeDatabaseFactory

class App : Application() {
    val db by lazy {
        Room
            .databaseBuilder(this, BarcodeDatabaseFactory::class.java, "db")
            .build()
            .getBarcodeDatabase()
    }

    val barcodeSchemaParser by lazy { BarcodeSchemaParser() }
    val barcodeImageGenerator by lazy { BarcodeImageGenerator() }
}