package com.example.barcodescanner.usecase

import com.google.firebase.crashlytics.FirebaseCrashlytics

object Logger {
    fun log(error: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(error)
    }
}