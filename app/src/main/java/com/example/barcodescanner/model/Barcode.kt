package com.example.barcodescanner.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.example.barcodescanner.usecase.BarcodeDatabaseTypeConverter
import com.google.zxing.BarcodeFormat

@Entity(tableName = "codes")
@TypeConverters(BarcodeDatabaseTypeConverter::class)
data class Barcode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val format: BarcodeFormat,
    val schema: BarcodeSchema,
    val date: Long,
    val errorCorrectionLevel: String? = null
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<Barcode> {
        override fun createFromParcel(parcel: Parcel): Barcode {
            return Barcode(parcel)
        }

        override fun newArray(size: Int): Array<Barcode?> {
            return arrayOfNulls(size)
        }
    }

    constructor(text: String, schema: BarcodeSchema) : this(text, BarcodeFormat.QR_CODE, schema)

    constructor(text: String, format: BarcodeFormat, schema: BarcodeSchema) : this(
        text = text,
        format = format,
        schema = schema,
        date = System.currentTimeMillis()
    )

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        text = parcel.readString().orEmpty(),
        format = BarcodeFormat.values()[parcel.readInt()],
        schema = BarcodeSchema.values()[parcel.readInt()],
        date = parcel.readLong(),
        errorCorrectionLevel = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeString(text)
            writeInt(format.ordinal)
            writeInt(schema.ordinal)
            writeLong(date)
            writeString(errorCorrectionLevel)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}