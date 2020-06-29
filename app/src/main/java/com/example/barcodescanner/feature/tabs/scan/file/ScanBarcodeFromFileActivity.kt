package com.example.barcodescanner.feature.tabs.scan.file

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent.ACTION_UP
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.di.barcodeImageScanner
import com.example.barcodescanner.di.barcodeScanResultParser
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.model.Barcode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan_barcode_from_file.*
import com.google.zxing.*
import java.util.concurrent.TimeUnit
import com.example.barcodescanner.extension.showError

class ScanBarcodeFromFileActivity : BaseActivity() {

    companion object {
        private const val CHOOSE_FILE_REQUEST_CODE = 12

        fun start(context: Context) {
            val intent = Intent(context, ScanBarcodeFromFileActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var lastScanResult: Result? = null
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode_from_file)
        startChooseImageActivity(savedInstanceState)
        handleToolbarBackPressed()
        handleImageCropAreaChanged()
        handleScanButtonClicked()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != CHOOSE_FILE_REQUEST_CODE || resultCode != RESULT_OK) {
            finish()
            return
        }

        data?.data?.apply(::showImage)
    }

    private fun startChooseImageActivity(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }

        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CHOOSE_FILE_REQUEST_CODE)
        }
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleImageCropAreaChanged() {
        crop_image_view.touches()
            .filter { it.action == ACTION_UP }
            .debounce(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { scanCroppedImage() }
            .addTo(disposable)
    }

    private fun handleScanButtonClicked() {
        button_scan.setOnClickListener {
            saveScanResult()
        }
    }

    private fun showImage(imageUri: Uri) {
        crop_image_view
            .load(imageUri)
            .executeAsCompletable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { scanCroppedImage() },
                ::showError
            )
            .addTo(disposable)
    }

    private fun scanCroppedImage() {
        showLoading(true)
        showScanButtonEnabled(false)

        lastScanResult = null

        crop_image_view
            .cropAsSingle()
            .subscribeOn(Schedulers.io())
            .subscribe(::scanCroppedImage, ::showError)
            .addTo(disposable)
    }

    private fun scanCroppedImage(image: Bitmap) {
        barcodeImageScanner
            .parse(image)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { scanResult ->
                    lastScanResult = scanResult
                    showScanButtonEnabled(true)
                    showLoading(false)
                },
                { showLoading(false) }
            )
            .addTo(disposable)
    }

    private fun saveScanResult() {
        val barcode = lastScanResult?.let(barcodeScanResultParser::parseResult) ?: return

        showLoading(true)

        barcodeDatabase.save(barcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { navigateToBarcodeScreen(barcode) },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        button_scan.isInvisible = isLoading
    }

    private fun showScanButtonEnabled(isEnabled: Boolean) {
        button_scan.isEnabled = isEnabled
    }

    private fun navigateToBarcodeScreen(barcode: Barcode) {
        BarcodeActivity.start(this, barcode)
        finish()
    }
}