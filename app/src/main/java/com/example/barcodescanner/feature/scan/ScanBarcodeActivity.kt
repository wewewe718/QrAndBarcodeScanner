package com.example.barcodescanner.feature.scan

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.widget.SeekBar
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
    private var maxZoom: Int = 0
    private val zoomStep = 5
    private val disposable = CompositeDisposable()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ScanBarcodeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
        initScanner()
        handleZoomChanged()
        handleDecreaseZoomClicked()
        handleIncreaseZoomClicked()
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

        findCamera(CodeScanner.CAMERA_BACK)?.parameters?.apply {
            this@ScanBarcodeActivity.maxZoom = maxZoom
            seek_bar_zoom.max = maxZoom
            seek_bar_zoom.progress = zoom
        }
    }

    private fun handleZoomChanged() {
        seek_bar_zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }
        })
    }

    private fun handleDecreaseZoomClicked() {
        button_decrease_zoom.setOnClickListener {
            decreaseZoom()
        }
    }

    private fun handleIncreaseZoomClicked() {
        button_increase_zoom.setOnClickListener {
            increaseZoom()
        }
    }

    private fun decreaseZoom() {
        codeScanner.apply {
            if (zoom > zoomStep) {
                zoom -= zoomStep
                seek_bar_zoom.progress = zoom
            }
        }
    }

    private fun increaseZoom() {
        codeScanner.apply {
            if (zoom < maxZoom - zoomStep) {
                zoom += zoomStep
                seek_bar_zoom.progress = zoom
            }
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

    private fun findCamera(facing: Int): Camera? {
        val cameraId = findCameraId(facing) ?: return null
        return Camera.open(cameraId)
    }

    private fun findCameraId(facing: Int): Int? {
        val cameraFacing = if (facing == CodeScanner.CAMERA_BACK) {
            Camera.CameraInfo.CAMERA_FACING_BACK
        } else {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        }

        for (cameraId in 0..Camera.getNumberOfCameras()) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            if (cameraInfo.facing == cameraFacing) {
                return cameraId
            }
        }

        return null
    }
}