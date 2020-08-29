package com.example.barcodescanner

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.example.barcodescanner.di.settings
import io.reactivex.plugins.RxJavaPlugins

class App : MultiDexApplication() {

    override fun onCreate() {
        handleUnhandledRxJavaErrors()
        enableStrictModeIfNeeded()
        applyTheme()
        super.onCreate()
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }

    private fun handleUnhandledRxJavaErrors() {
        RxJavaPlugins.setErrorHandler { error ->
            error.printStackTrace()
        }
    }

    private fun enableStrictModeIfNeeded() {
        if (true) {
            return
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
        )

        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDropBox()
                .build()
        )
    }
}