package com.example.barcodescanner.usecase

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity

object RotationHelper {

    fun lockCurrentOrientationIfNeeded(activity: AppCompatActivity) {
        if (isAutoRotateOptionDisabled(activity)) {
            lockCurrentOrientation(activity)
        }
    }

    private fun isAutoRotateOptionDisabled(context: Context): Boolean {
        val result = android.provider.Settings.System.getInt(
            context.contentResolver,
            android.provider.Settings.System.ACCELEROMETER_ROTATION,
            0
        )
        return result == 0
    }

    private fun lockCurrentOrientation(activity: AppCompatActivity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }
}