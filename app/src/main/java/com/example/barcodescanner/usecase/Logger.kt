package com.example.barcodescanner.usecase

import com.example.barcodescanner.BuildConfig
import io.sentry.core.Sentry

object Logger {
    var isEnabled = BuildConfig.ERROR_REPORTS_ENABLED_BY_DEFAULT

    fun log(error: Throwable) {
        if (isEnabled) {
            Sentry.captureException(error)
        }
    }
}