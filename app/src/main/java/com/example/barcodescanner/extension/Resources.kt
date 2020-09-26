package com.example.barcodescanner.extension

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat

fun Resources.getBitmapFromDrawable(resId: Int): Bitmap {
    val drawable = ResourcesCompat.getDrawable(this, resId, null)

    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val bitmap = Bitmap.createBitmap(
        drawable?.intrinsicWidth.orZero(),
        drawable?.intrinsicHeight.orZero(),
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    drawable?.apply {
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }

    return bitmap
}