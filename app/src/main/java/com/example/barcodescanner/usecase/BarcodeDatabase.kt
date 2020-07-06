package com.example.barcodescanner.usecase

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import io.reactivex.Completable
import io.reactivex.Single


class BarcodeDatabaseTypeConverter {

    @TypeConverter
    fun fromBarcodeFormat(barcodeFormat: BarcodeFormat): String {
        return barcodeFormat.name
    }

    @TypeConverter
    fun toBarcodeFormat(value: String): BarcodeFormat {
        return BarcodeFormat.valueOf(value)
    }

    @TypeConverter
    fun fromBarcodeSchema(barcodeSchema: BarcodeSchema): String {
        return barcodeSchema.name
    }

    @TypeConverter
    fun toBarcodeSchema(value: String): BarcodeSchema {
        return BarcodeSchema.valueOf(value)
    }
}


@Database(entities = [Barcode::class], version = 1)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}


@Dao
interface BarcodeDatabase {

    companion object {
        private var INSTANCE: BarcodeDatabase? = null

        fun getInstance(context: Context): BarcodeDatabase {
            return INSTANCE ?: Room
                .databaseBuilder(context, BarcodeDatabaseFactory::class.java, "db")
                .build()
                .getBarcodeDatabase().apply {
                    INSTANCE = this
                }
        }
    }

    @Query("SELECT * FROM codes ORDER BY date DESC")
    fun getAll(): DataSource.Factory<Int, Barcode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(barcode: Barcode): Single<Long>

    @Query("DELETE FROM codes WHERE id = :id")
    fun delete(id: Long): Completable

    @Query("DELETE FROM codes")
    fun deleteAll(): Completable
}
