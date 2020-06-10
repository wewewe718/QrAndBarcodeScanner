package com.example.qrcodescanner.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

@Entity(tableName = "codes")
@TypeConverters(QrCodeDbTypeConverter::class)
data class QrCode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val format: BarcodeFormat,
    val scheme: BarcodeSchema,
    val date: Long
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<QrCode> {
        override fun createFromParcel(parcel: Parcel): QrCode {
            return QrCode(parcel)
        }

        override fun newArray(size: Int): Array<QrCode?> {
            return arrayOfNulls(size)
        }
    }

    constructor(scanResult: Result) : this(
        text = scanResult.text,
        format = scanResult.barcodeFormat,
        scheme = BarcodeSchema.from(scanResult.text),
        date = scanResult.timestamp
    )

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        text = parcel.readString().orEmpty(),
        format = BarcodeFormat.values()[parcel.readInt()],
        scheme = BarcodeSchema.values()[parcel.readInt()],
        date = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeLong(id)
            writeString(text)
            writeInt(format.ordinal)
            writeInt(scheme.ordinal)
            writeLong(date)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}

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