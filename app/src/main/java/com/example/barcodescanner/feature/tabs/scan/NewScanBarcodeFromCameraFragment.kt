package com.example.barcodescanner.feature.tabs.scan

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.barcodescanner.R
import com.example.barcodescanner.di.permissionsHelper
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.feature.tabs.scan.file.ScanBarcodeFromFileActivity
import com.example.barcodescanner.usecase.Logger
import kotlinx.android.synthetic.main.fragment_scan_barcode_from_camera_new.*
import java.util.concurrent.TimeUnit

class NewScanBarcodeFromCameraFragment : Fragment() {

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 111
        private const val ZOOM_STEP = 5
    }


    private var camera: Camera? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan_barcode_from_camera_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supportEdgeToEdge()
        setDarkStatusBar()

        initFlashButton()
        initScanFromFileButton()
        initZoomSeekBar()
        initZoomButtons()

        if (areAllPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && areAllPermissionsGranted(grantResults)) {
            startCamera()
        }
    }


    private fun supportEdgeToEdge() {
        image_view_flash.applySystemWindowInsets(applyTop = true)
        image_view_scan_from_file.applySystemWindowInsets(applyTop = true)
    }

    private fun setDarkStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        if (settings.isDarkTheme) {
            return
        }

        requireActivity().window.decorView.apply {
            systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }


    private fun initFlashButton() {
        layout_flash_container.setOnClickListener {
            toggleFlash()
        }
    }

    private fun initScanFromFileButton() {
        layout_scan_from_file_container.setOnClickListener {
            navigateToScanFromFileScreen()
        }
    }

    private fun initZoomSeekBar() {
        camera?.cameraInfo?.zoomState?.observe(this, Observer { zoomState ->
            seek_bar_zoom.max = (zoomState.maxZoomRatio * 100).toInt()
            seek_bar_zoom.progress = (zoomState.linearZoom * 100).toInt()
        })

        seek_bar_zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setZoom(progress)
            }
        })
    }

    private fun initZoomButtons() {
        button_decrease_zoom.setOnClickListener {
            decreaseZoom()
        }

        button_increase_zoom.setOnClickListener {
            increaseZoom()
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = if (settings.isBackCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            preview.setSurfaceProvider(preview_view.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch(error: Exception) {
                Logger.log(error)
                return@Runnable
            }

            if (settings.flash) {
                toggleFlash()
            }

            if (settings.simpleAutoFocus) {
                startSimpleAutoFocusMode()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlash() {
        camera?.apply {
            if (cameraInfo.hasFlashUnit()) {
                image_view_flash.isActivated = image_view_flash.isActivated.not()
                cameraControl.enableTorch(image_view_flash.isActivated)
            }
        }
    }

    private fun startSimpleAutoFocusMode() {
        val centerWidth = preview_view.width.toFloat() / 2
        val centerHeight = preview_view.height.toFloat() / 2
        val autoFocusPoint = SurfaceOrientedMeteringPointFactory(preview_view.width.toFloat(), preview_view.height.toFloat())
            .createPoint(centerWidth, centerHeight)

        val focusConfig = FocusMeteringAction.Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
            .setAutoCancelDuration(1, TimeUnit.SECONDS)
            .build()

        try {
            camera?.cameraControl?.startFocusAndMetering(focusConfig)
        } catch (e: Exception) {
            Logger.log(e)
        }
    }

    private fun decreaseZoom() {
        var newZoom = seek_bar_zoom.progress - ZOOM_STEP
        if (newZoom < 0) {
            newZoom = 0
        }
        seek_bar_zoom.progress = newZoom
    }

    private fun increaseZoom() {
        var newZoom = seek_bar_zoom.progress + ZOOM_STEP
        if (newZoom > seek_bar_zoom.max) {
            newZoom = seek_bar_zoom.max
        }
        seek_bar_zoom.progress = newZoom
    }

    private fun setZoom(zoom: Int) {
        camera?.cameraControl?.setLinearZoom(zoom / 100.toFloat())
    }


    private fun navigateToScanFromFileScreen() {
        ScanBarcodeFromFileActivity.start(requireActivity())
    }


    private fun requestPermissions() {
        permissionsHelper.requestNotGrantedPermissions(requireActivity() as AppCompatActivity, PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    private fun areAllPermissionsGranted(): Boolean {
        return permissionsHelper.areAllPermissionsGranted(requireActivity(), PERMISSIONS)
    }

    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        return permissionsHelper.areAllPermissionsGranted(grantResults)
    }
}