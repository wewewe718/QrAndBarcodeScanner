package com.example.barcodescanner.usecase

import androidx.paging.DataSource
import androidx.room.*
import com.example.barcodescanner.model.BarcodeSchema
import com.example.barcodescanner.model.Barcode
import com.google.zxing.BarcodeFormat
import io.reactivex.Completable


class BarcodeDatabaseTypeConverter {

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


@Database(entities = [Barcode::class], version = 1)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}


@Dao
interface BarcodeDatabase {
    @Query("SELECT * FROM codes ORDER BY date DESC")
    fun getAll(): DataSource.Factory<Int, Barcode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(barcode: Barcode): Completable

    @Delete
    fun delete(barcode: Barcode): Completable
}