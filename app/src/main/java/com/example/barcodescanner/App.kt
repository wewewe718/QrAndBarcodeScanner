package com.example.barcodescanner

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.*
import androidx.multidex.MultiDexApplication
import com.example.barcodescanner.di.*
import io.reactivex.plugins.RxJavaPlugins

class App : MultiDexApplication() {

    override fun onCreate() {
        handleUnhandledRxJavaErrors()
        enableStrictModeIfNeeded()
        showTheme()
        super.onCreate()
    }

    private fun showTheme() {
        AppCompatDelegate.setDefaultNightMode(settings.theme)
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