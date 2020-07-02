package com.example.barcodescanner.usecase

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import com.example.barcodescanner.di.vibrator

object VibratorHelper {

    fun vibrateOnce(context: Context, pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            context.vibrator?.vibrate(pattern, -1)
        }
    }
}