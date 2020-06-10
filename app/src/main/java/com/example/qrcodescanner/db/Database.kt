package com.example.qrcodescanner.db

import androidx.paging.DataSource
import androidx.room.*
import com.example.qrcodescanner.model.QrCode
import io.reactivex.Completable

@Database(entities = [QrCode::class], version = 1)
abstract class DataBase : RoomDatabase() {
   abstract fun getQrCodeDb(): QrCodeDb
}

@Dao
interface QrCodeDb {
    @Query("SELECT * FROM codes ORDER BY date DESC")
    fun getAll(): DataSource.Factory<Int, QrCode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(qrCode: QrCode): Completable
}