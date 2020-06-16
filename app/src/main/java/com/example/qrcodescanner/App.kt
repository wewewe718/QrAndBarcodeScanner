package com.example.qrcodescanner

import android.app.Application
import androidx.room.Room
import com.example.qrcodescanner.usecase.BarcodeImageGenerator
import com.example.qrcodescanner.usecase.BarcodeSchemaParser
import com.example.qrcodescanner.usecase.DataBase

class App : Application() {
    val db by lazy {
        Room
            .databaseBuilder(this, DataBase::class.java, "db")
            .build()
            .getQrCodeDb()
    }

    val barcodeSchemaParser by lazy { BarcodeSchemaParser() }
    val barcodeImageGenerator by lazy { BarcodeImageGenerator() }
}