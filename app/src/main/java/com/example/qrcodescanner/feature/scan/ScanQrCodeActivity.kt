package com.example.qrcodescanner.feature.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.budiyev.android.codescanner.*
import com.example.qrcodescanner.R
import com.example.qrcodescanner.feature.common.showError
import com.example.qrcodescanner.feature.qrcode.QrCodeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_scan_qr_code.*

class ScanQrCodeActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ScanQrCodeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var codeScanner: CodeScanner
    private val disposable = CompositeDisposable()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ScanQrCodeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)
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
        subscribeToQrCodeSaved()
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

    private fun subscribeToQrCodeSaved() {
        viewModel.qrCodeSaved
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { qrCode ->
                QrCodeActivity.start(this, qrCode)
                finish()
            }
            .addTo(disposable)
    }

    private fun unsubscribeFromViewModel() {
        disposable.clear()
    }
}