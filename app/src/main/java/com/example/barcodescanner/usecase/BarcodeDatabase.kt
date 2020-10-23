package com.example.barcodescanner.usecase

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ExportBarcode
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


@Database(entities = [Barcode::class], version = 2)
abstract class BarcodeDatabaseFactory : RoomDatabase() {
    abstract fun getBarcodeDatabase(): BarcodeDatabase
}


@Dao
interface BarcodeDatabase {

    companion object {
        private var INSTANCE: BarcodeDatabase? = null

        fun getInstance(context: Context): BarcodeDatabase {
            return INSTANCE ?: Room
                .databaseBuilder(context.applicationContext, BarcodeDatabaseFactory::class.java, "db")
                .addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE codes ADD COLUMN name TEXT")
                    }
                })
                .build()
                .getBarcodeDatabase().apply {
                    INSTANCE = this
                }
        }
    }

    @Query("SELECT * FROM codes ORDER BY date DESC")
    fun getAll(): DataSource.Factory<Int, Barcode>

    @Query("SELECT * FROM codes WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavorites(): DataSource.Factory<Int, Barcode>

    @Query("SELECT date, format, text FROM codes ORDER BY date DESC")
    fun getAllForExport(): Single<List<ExportBarcode>>

    @Query("SELECT * FROM codes WHERE format = :format AND text = :text LIMIT 1")
    fun find(format: String, text: String): Single<List<Barcode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(barcode: Barcode): Single<Long>

    @Query("DELETE FROM codes WHERE id = :id")
    fun delete(id: Long): Completable

    @Query("DELETE FROM codes")
    fun deleteAll(): Completable
}

fun BarcodeDatabase.save(barcode: Barcode, doNotSaveDuplicates: Boolean): Single<Long> {
    return if (doNotSaveDuplicates) {
        saveIfNotPresent(barcode)
    } else {
        save(barcode)
    }
}

fun BarcodeDatabase.saveIfNotPresent(barcode: Barcode): Single<Long> {
    return find(barcode.format.name, barcode.text)
        .flatMap { found ->
            if (found.isEmpty()) {
                save(barcode)
            } else {
                Single.just(found[0].id)
            }
        }
}
