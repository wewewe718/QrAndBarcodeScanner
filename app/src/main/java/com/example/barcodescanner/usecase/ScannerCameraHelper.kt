package com.example.barcodescanner.usecase

import android.hardware.Camera
import com.budiyev.android.codescanner.CodeScanner


class ScannerCameraHelper {

    fun getCameraParameters(facing: Int): Camera.Parameters? {
        return try {
            val cameraFacing = getCameraFacing(facing)
            val cameraId = getCameraId(cameraFacing) ?: return null
            Camera.open(cameraId)?.parameters
        } catch (_: Exception) {
            null
        }
    }

    private fun getCameraFacing(facing: Int): Int {
        return if (facing == CodeScanner.CAMERA_BACK) {
            Camera.CameraInfo.CAMERA_FACING_BACK
        } else {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        }
    }

    private fun getCameraId(cameraFacing: Int): Int? {
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