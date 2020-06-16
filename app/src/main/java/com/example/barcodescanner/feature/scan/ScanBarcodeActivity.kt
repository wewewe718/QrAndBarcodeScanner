package com.example.barcodescanner.feature.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.budiyev.android.codescanner.*
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.common.showError
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_scan_barcode.*

class ScanBarcodeActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ScanBarcodeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var codeScanner: CodeScanner
    private val disposable = CompositeDisposable()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ScanBarcodeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
        initScanner()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
        subscribeToViewModel()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        unsubscribeFromViewModel()
        super.onPause()
    }

    private fun initScanner() {
        codeScanner = CodeScanner(this, scanner_view).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback(viewModel::onScanResult)
            errorCallback = ErrorCallback(viewModel::onScanError)
        }
    }

    private fun subscribeToViewModel() {
        subscribeToLoading()
        subscribeToError()
        subscribeToBarcodeSaved()
    }

    private fun subscribeToLoading() {
        viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                layout_loading.isVisible = isLoading
            }
            .addTo(disposable)
    }

    private fun subscribeToError() {
        viewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showError)
            .addTo(disposable)
    }

    private fun subscribeToBarcodeSaved() {
        viewModel.barcodeSaved
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { barcode ->
                BarcodeActivity.start(this, barcode)
                finish()
            }
            .addTo(disposable)
    }

    private fun unsubscribeFromViewModel() {
        disposable.clear()
    }
}