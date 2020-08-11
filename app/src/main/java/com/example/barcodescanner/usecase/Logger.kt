package com.example.barcodescanner.usecase

import io.sentry.core.Sentry

object Logger {
    var isEnabled: Boolean = true

    fun log(error: Throwable) {
        if (isEnabled) {
            Sentry.captureException(error)
        }
    }
}