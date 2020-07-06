package com.example.barcodescanner.usecase

import android.content.Context
import com.google.zxing.BarcodeFormat

class Settings(context: Context) {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
        private var INSTANCE: Settings? = null

        fun getInstance(context: Context): Settings {
            return INSTANCE ?: Settings(context).apply { INSTANCE = this }
        }
    }

    private enum class Key {
        OPEN_LINKS_AUTOMATICALLY,
        COPY_TO_CLIPBOARD,
        FLASHLIGHT,
        AUTO_FOCUS,
        VIBRATE,
        CONTINUOUS_SCANNING,
        CONFIRM_SCANS_MANUALLY,
        IS_BACK_CAMERA,
        SAVE_SCANNED_BARCODES_TO_HISTORY,
        SAVE_CREATED_BARCODES_TO_HISTORY,
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var openLinksAutomatically: Boolean
        get() = getBoolean(Key.OPEN_LINKS_AUTOMATICALLY, false)
        set(value) = setBoolean(Key.OPEN_LINKS_AUTOMATICALLY, value)

    var copyToClipboard: Boolean
        get() = getBoolean(Key.COPY_TO_CLIPBOARD, true)
        set(value) = setBoolean(Key.COPY_TO_CLIPBOARD, value)

    var flash: Boolean
        get() = getBoolean(Key.FLASHLIGHT, false)
        set(value) = setBoolean(Key.FLASHLIGHT, value)

    var autoFocus: Boolean
        get() = getBoolean(Key.AUTO_FOCUS, true)
        set(value) = setBoolean(Key.AUTO_FOCUS, value)

    var vibrate: Boolean
        get() = getBoolean(Key.VIBRATE, true)
        set(value) = setBoolean(Key.VIBRATE, value)

    var continuousScanning: Boolean
        get() = getBoolean(Key.CONTINUOUS_SCANNING, false)
        set(value) = setBoolean(Key.CONTINUOUS_SCANNING, value)

    var confirmScansManually: Boolean
        get() = getBoolean(Key.CONFIRM_SCANS_MANUALLY, false)
        set(value) = setBoolean(Key.CONFIRM_SCANS_MANUALLY, value)

    var isBackCamera: Boolean
        get() = getBoolean(Key.IS_BACK_CAMERA, true)
        set(value) = setBoolean(Key.IS_BACK_CAMERA, value)

    var saveScannedBarcodesToHistory: Boolean
        get() = getBoolean(Key.SAVE_SCANNED_BARCODES_TO_HISTORY, true)
        set(value) = setBoolean(Key.SAVE_SCANNED_BARCODES_TO_HISTORY, value)

    var saveCreatedBarcodesToHistory: Boolean
        get() = getBoolean(Key.SAVE_CREATED_BARCODES_TO_HISTORY, true)
        set(value) = setBoolean(Key.SAVE_CREATED_BARCODES_TO_HISTORY, value)

    fun isFormatSelected(format: BarcodeFormat): Boolean {
        return sharedPreferences.getBoolean(format.name, true)
    }

    fun setFormatSelected(format: BarcodeFormat, isSelected: Boolean) {
        sharedPreferences.edit()
            .putBoolean(format.name, isSelected)
            .apply()
    }

    private fun getBoolean(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }

    private fun setBoolean(key: Key, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key.name, value)
            .apply()
    }
}