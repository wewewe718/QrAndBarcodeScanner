package com.example.barcodescanner.extension

import android.content.ClipboardManager
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Vibrator
import java.util.*

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

val Context.currentLocale: Locale?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }