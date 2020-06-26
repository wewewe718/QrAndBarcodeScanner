package com.example.barcodescanner.feature.tabs.scan.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.barcodescanner.R
import com.example.barcodescanner.common.showError
import com.example.barcodescanner.feature.BaseActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_scan_barcode_from_file.*

class ScanBarcodeFromFileActivity : BaseActivity() {

    companion object {
        private const val CHOOSE_FILE_REQUEST_CODE = 12

        fun start(context: Context) {
            val intent = Intent(context, ScanBarcodeFromFileActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode_from_file)
        handleToolbarBackPressed()
        chooseFileIfNeeded(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.apply(::showImage)
        }
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun chooseFileIfNeeded(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            chooseFile()
        }
    }

    private fun chooseFile() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CHOOSE_FILE_REQUEST_CODE)
        }
    }

    private fun showImage(imageUri: Uri) {
        crop_image_view
            .load(imageUri)
            .executeAsCompletable()
            .subscribe(
                {},
                ::showError
            )
            .addTo(disposable)
    }
}