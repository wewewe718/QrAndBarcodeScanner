package com.example.qrcodescanner.usecase

import androidx.paging.DataSource
import androidx.room.*
import com.example.qrcodescanner.model.BarcodeSchema
import com.example.qrcodescanner.model.QrCode
import com.google.zxing.BarcodeFormat
import io.reactivex.Completable

class QrCodeDbTypeConverter {

    @TypeConverter
    fun fromBarcodeFormat(barcodeFormat: BarcodeFormat): Int {
        return barcodeFormat.ordinal
    }

    @TypeConverter
    fun toBarcodeFormat(value: Int): BarcodeFormat {
        return BarcodeFormat.values()[value]
    }

    @TypeConverter
    fun fromBarcodeSchema(barcodeSchema: BarcodeSchema): Int {
        return barcodeSchema.ordinal
    }

    @TypeConverter
    fun toBarcodeSchema(value: Int): BarcodeSchema {
        return BarcodeSchema.values()[value]
    }
}

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

    @Delete
    fun delete(qrCode: QrCode): Completable
}