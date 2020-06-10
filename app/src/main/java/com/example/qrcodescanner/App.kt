package com.example.qrcodescanner

import android.app.Application
import androidx.room.Room
import com.example.qrcodescanner.db.DataBase

class App : Application() {
    val db by lazy {
        Room
            .databaseBuilder(this, DataBase::class.java, "db")
            .build()
            .getQrCodeDb()
    }
}