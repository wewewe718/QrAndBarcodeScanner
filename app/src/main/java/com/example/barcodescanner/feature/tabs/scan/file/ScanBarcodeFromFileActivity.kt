package com.example.barcodescanner.feature.tabs.scan.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.MotionEvent.ACTION_UP
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.*
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.usecase.save
import com.google.zxing.Result
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan_barcode_from_file.*
import java.util.concurrent.TimeUnit

class ScanBarcodeFromFileActivity : BaseActivity() {

    companion object {
        private const val CHOOSE_FILE_REQUEST_CODE = 12
        private const val CHOOSE_FILE_AGAIN_REQUEST_CODE = 13
        private const val PERMISSIONS_REQUEST_CODE = 14
        private val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        fun start(context: Context) {
            val intent = Intent(context, ScanBarcodeFromFileActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var imageUri: Uri? = null
    private var lastScanResult: Result? = null
    private val disposable = CompositeDisposable()
    private val scanDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode_from_file)

        supportEdgeToEdge()
        handleToolbarBackPressed()
        handleToolbarMenuItemClicked()
        handleImageCropAreaChanged()
        handleScanButtonClicked()

        if (showImageFromIntent().not()) {
            startChooseImageActivity(savedInstanceState)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == CHOOSE_FILE_REQUEST_CODE || requestCode == CHOOSE_FILE_AGAIN_REQUEST_CODE) && resultCode == RESULT_OK) {
            data?.data?.apply(::showImage)
            return
        }

        if (requestCode == CHOOSE_FILE_REQUEST_CODE) {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && permissionsHelper.areAllPermissionsGranted(grantResults)) {
            imageUri?.apply(::showImage)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanDisposable.clear()
        disposable.clear()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun showImageFromIntent(): Boolean {
        var uri: Uri? = null

        if (intent?.action == Intent.ACTION_SEND && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        }

        if (intent?.action == Intent.ACTION_VIEW && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.data
        }

        if (uri == null) {
            return false
        }

        showImage(uri)
        return true
    }

    private fun startChooseImageActivity(savedInstanceState: Bundle?) {
        startChooseImageActivity(CHOOSE_FILE_REQUEST_CODE, savedInstanceState)
    }

    private fun startChooseImageActivityAgain() {
        startChooseImageActivity(CHOOSE_FILE_AGAIN_REQUEST_CODE, null)
    }

    private fun startChooseImageActivity(requestCode: Int,  savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_rotate_left -> crop_image_view.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
                R.id.item_rotate_right -> crop_image_view.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
                R.id.item_change_image -> startChooseImageActivityAgain()
            }
            return@setOnMenuItemClickListener true
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
        this.imageUri = imageUri

        crop_image_view
            .load(imageUri)
            .executeAsCompletable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { scanCroppedImage() },
                ::showErrorOrRequestPermissions
            )
            .addTo(disposable)
    }

    private fun showErrorOrRequestPermissions(error: Throwable) {
        when (error) {
            is SecurityException -> permissionsHelper.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            else -> showError(error)
        }
    }

    private fun scanCroppedImage() {
        showLoading(true)
        showScanButtonEnabled(false)

        scanDisposable.clear()
        lastScanResult = null

        crop_image_view
            .cropAsSingle()
            .subscribeOn(Schedulers.io())
            .subscribe(::scanCroppedImage, ::showError)
            .addTo(scanDisposable)
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
            .addTo(scanDisposable)
    }

    private fun saveScanResult() {
        val barcode = lastScanResult?.let(barcodeParser::parseResult) ?: return
        if (settings.saveScannedBarcodesToHistory.not()) {
            navigateToBarcodeScreen(barcode)
            return
        }

        showLoading(true)

        barcodeDatabase.save(barcode, settings.doNotSaveDuplicates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    navigateToBarcodeScreen(barcode.copy(id = id))
                },
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