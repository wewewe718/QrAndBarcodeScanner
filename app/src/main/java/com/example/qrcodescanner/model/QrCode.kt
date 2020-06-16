package com.example.qrcodescanner.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.qrcodescanner.usecase.QrCodeDbTypeConverter
import com.google.zxing.BarcodeFormat

@Entity(tableName = "codes")
@TypeConverters(QrCodeDbTypeConverter::class)
data class QrCode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val format: BarcodeFormat,
    val schema: BarcodeSchema,
    val date: Long,
    val errorCorrectionLevel: String?
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<QrCode> {
        override fun createFromParcel(parcel: Parcel): QrCode {
            return QrCode(parcel)
        }

        override fun newArray(size: Int): Array<QrCode?> {
            return arrayOfNulls(size)
        }
    }

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