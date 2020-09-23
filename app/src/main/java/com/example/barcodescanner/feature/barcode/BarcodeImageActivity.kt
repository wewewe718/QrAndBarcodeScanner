package com.example.barcodescanner.feature.barcode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeImageGenerator
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.toStringId
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.usecase.Logger
import kotlinx.android.synthetic.main.activity_barcode_image.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeImageActivity : BaseActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, BarcodeImageActivity::class.java)
            intent.putExtra(BARCODE_KEY, barcode)
            context.startActivity(intent)
        }
    }

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val barcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }
    private var originalBrightness: Float = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_image)
        supportEdgeToEdge()
        saveOriginalBrightness()
        handleToolbarBackPressed()
        handleToolbarMenuItemClicked()
        showMenu()
        showBarcode()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun saveOriginalBrightness() {
        originalBrightness = window.attributes.screenBrightness
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_increase_brightness -> {
                    increaseBrightnessToMax()
                    toolbar.menu.apply {
                        findItem(R.id.item_increase_brightness).isVisible = false
                        findItem(R.id.item_decrease_brightness).isVisible = true
                    }
                }
                R.id.item_decrease_brightness -> {
                    restoreOriginalBrightness()
                    toolbar.menu.apply {
                        findItem(R.id.item_decrease_brightness).isVisible = false
                        findItem(R.id.item_increase_brightness).isVisible = true
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showMenu() {
        toolbar.inflateMenu(R.menu.menu_barcode_image)
    }

    private fun showBarcode() {
        showBarcodeImage()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateBitmap(barcode, 2000, 2000, 0, settings.barcodeContentColor, settings.barcodeBackgroundColor)
            image_view_barcode.setImageBitmap(bitmap)
            image_view_barcode.setBackgroundColor(settings.barcodeBackgroundColor)
            layout_barcode_image_background.setBackgroundColor(settings.barcodeBackgroundColor)

            if (settings.isDarkTheme.not() || settings.areBarcodeColorsInversed) {
                layout_barcode_image_background.setPadding(0, 0, 0, 0)
            }
        } catch (ex: Exception) {
            Logger.log(ex)
            image_view_barcode.isVisible = false
        }
    }

    private fun showBarcodeDate() {
        text_view_date.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        toolbar.setTitle(format)
    }

    private fun showBarcodeText() {
        text_view_barcode_text.text = barcode.text
    }

    private fun increaseBrightnessToMax() {
        setBrightness(1.0f)
    }

    private fun restoreOriginalBrightness() {
        setBrightness(originalBrightness)
    }

    private fun setBrightness(brightness: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = brightness
        }
    }
}