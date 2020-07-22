package com.example.barcodescanner.usecase

import android.graphics.Bitmap
import com.example.barcodescanner.model.Barcode
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

object BarcodeImageGenerator {
    private val encoder = BarcodeEncoder()
    private val writer = MultiFormatWriter()

    fun generateBitmap(barcode: Barcode, width: Int, height: Int, margin: Int = 0): Bitmap {
        return encoder.encodeBitmap(
            barcode.text,
            barcode.format,
            width,
            height,
            createHints(barcode.errorCorrectionLevel, margin)
        )
    }

    fun generateSvg(barcode: Barcode, width: Int, height: Int, margin: Int = 0): String {
        val matrix = writer.encode(
            barcode.text,
            barcode.format,
            0,
            0,
            createHints(barcode.errorCorrectionLevel, margin)
        )
        return createSvg(width, height, matrix)
    }

    private fun createHints(errorCorrectionLevel: String?, margin: Int): Map<EncodeHintType, Any> {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.MARGIN to margin
        )

        if (errorCorrectionLevel != null) {
            hints.plus(EncodeHintType.ERROR_CORRECTION to errorCorrectionLevel)
        }

        return hints
    }

    private fun createSvg(width: Int, height: Int, matrix: BitMatrix): String {
        val result = StringBuilder()
            .append("<svg width=\"$width\" height=\"$height\"")
            .append(" viewBox=\"0 0 $width $height\"")
            .append(" xmlns=\"http://www.w3.org/2000/svg\">\n")

        val w = matrix.width
        val h = matrix.height
        val xf = width.toFloat() / w
        val yf = height.toFloat() / h

        for (y in 0 until h) {
            for (x in 0 until w) {
                if (matrix.get(x, y)) {
                    val ox = x * xf
                    val oy = y * yf
                    result.append("<rect x=\"$ox\" y=\"$oy\"")
                    result.append(" width=\"$xf\" height=\"$yf\"/>\n")
                }
            }
        }

        result.append("</svg>\n")

        return result.toString()
    }
}