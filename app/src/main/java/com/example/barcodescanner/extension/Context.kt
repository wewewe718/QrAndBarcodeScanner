package com.example.barcodescanner.extension

import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Vibrator

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

val Context.wifiManager: WifiManager?
    get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

val Context.downloadManager: DownloadManager?
    get() = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager

val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager