package com.example.barcodescanner.usecase

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.barcodescanner.BuildConfig
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.model.SearchEngine
import com.google.zxing.BarcodeFormat

class Settings(private val context: Context) {

    companion object {
        const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES

        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
        private var INSTANCE: Settings? = null

        fun getInstance(context: Context): Settings {
            return INSTANCE ?: Settings(context.applicationContext).apply { INSTANCE = this }
        }
    }

    private enum class Key {
        THEME,
        INVERSE_BARCODE_COLORS,
        OPEN_LINKS_AUTOMATICALLY,
        COPY_TO_CLIPBOARD,
        SIMPLE_AUTO_FOCUS,
        FLASHLIGHT,
        VIBRATE,
        CONTINUOUS_SCANNING,
        CONFIRM_SCANS_MANUALLY,
        IS_BACK_CAMERA,
        SAVE_SCANNED_BARCODES_TO_HISTORY,
        SAVE_CREATED_BARCODES_TO_HISTORY,
        DO_NOT_SAVE_DUPLICATES,
        SEARCH_ENGINE,
        ERROR_REPORTS,
    }

    private val sharedPreferences by unsafeLazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var theme: Int
        get() = get(Key.THEME, THEME_SYSTEM)
        set(value) {
            set(Key.THEME, value)
            applyTheme(value)
        }

    val isDarkTheme: Boolean
        get() = theme == THEME_DARK || (theme == THEME_SYSTEM && isSystemDarkModeEnabled())

    var areBarcodeColorsInversed: Boolean
        get() = get(Key.INVERSE_BARCODE_COLORS, false)
        set(value) = set(Key.INVERSE_BARCODE_COLORS, value)

    val barcodeContentColor: Int
        get() = when  {
            isDarkTheme && areBarcodeColorsInversed -> Color.WHITE
            else -> Color.BLACK
        }

    val barcodeBackgroundColor: Int
        get() = when {
            isDarkTheme && areBarcodeColorsInversed.not() -> Color.WHITE
            else -> Color.TRANSPARENT
        }

    var openLinksAutomatically: Boolean
        get() = get(Key.OPEN_LINKS_AUTOMATICALLY, false)
        set(value) = set(Key.OPEN_LINKS_AUTOMATICALLY, value)

    var copyToClipboard: Boolean
        get() = get(Key.COPY_TO_CLIPBOARD, true)
        set(value) = set(Key.COPY_TO_CLIPBOARD, value)

    var simpleAutoFocus: Boolean
        get() = get(Key.SIMPLE_AUTO_FOCUS, false)
        set(value) = set(Key.SIMPLE_AUTO_FOCUS, value)

    var flash: Boolean
        get() = get(Key.FLASHLIGHT, false)
        set(value) = set(Key.FLASHLIGHT, value)

    var vibrate: Boolean
        get() = get(Key.VIBRATE, true)
        set(value) = set(Key.VIBRATE, value)

    var continuousScanning: Boolean
        get() = get(Key.CONTINUOUS_SCANNING, false)
        set(value) = set(Key.CONTINUOUS_SCANNING, value)

    var confirmScansManually: Boolean
        get() = get(Key.CONFIRM_SCANS_MANUALLY, false)
        set(value) = set(Key.CONFIRM_SCANS_MANUALLY, value)

    var isBackCamera: Boolean
        get() = get(Key.IS_BACK_CAMERA, true)
        set(value) = set(Key.IS_BACK_CAMERA, value)

    var saveScannedBarcodesToHistory: Boolean
        get() = get(Key.SAVE_SCANNED_BARCODES_TO_HISTORY, true)
        set(value) = set(Key.SAVE_SCANNED_BARCODES_TO_HISTORY, value)

    var saveCreatedBarcodesToHistory: Boolean
        get() = get(Key.SAVE_CREATED_BARCODES_TO_HISTORY, true)
        set(value) = set(Key.SAVE_CREATED_BARCODES_TO_HISTORY, value)

    var doNotSaveDuplicates: Boolean
        get() = get(Key.DO_NOT_SAVE_DUPLICATES, false)
        set(value) = set(Key.DO_NOT_SAVE_DUPLICATES, value)

    var searchEngine: SearchEngine
        get() = get(Key.SEARCH_ENGINE, SearchEngine.NONE)
        set(value) = set(Key.SEARCH_ENGINE, value)

    var areErrorReportsEnabled: Boolean
        get() = get(Key.ERROR_REPORTS, BuildConfig.ERROR_REPORTS_ENABLED_BY_DEFAULT)
        set(value) {
            set(Key.ERROR_REPORTS, value)
            Logger.isEnabled = value
        }

    fun isFormatSelected(format: BarcodeFormat): Boolean {
        return sharedPreferences.getBoolean(format.name, true)
    }

    fun setFormatSelected(format: BarcodeFormat, isSelected: Boolean) {
        sharedPreferences.edit()
            .putBoolean(format.name, isSelected)
            .apply()
    }

    fun reapplyTheme() {
        applyTheme(theme)
    }

    private fun get(key: Key, default: Int): Int {
        return sharedPreferences.getInt(key.name, default)
    }

    private fun set(key: Key, value: Int) {
        return sharedPreferences.edit()
            .putInt(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }

    private fun set(key: Key, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: SearchEngine = SearchEngine.NONE): SearchEngine {
        val rawValue = sharedPreferences.getString(key.name, null) ?: default.name
        return SearchEngine.valueOf(rawValue)
    }

    private fun set(key: Key, value: SearchEngine) {
        sharedPreferences.edit()
            .putString(key.name, value.name)
            .apply()
    }

    private fun applyTheme(theme: Int) {
        when (theme) {
            AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(theme)
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }

    private fun isSystemDarkModeEnabled(): Boolean {
        val mode = context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        return mode == Configuration.UI_MODE_NIGHT_YES
    }
}