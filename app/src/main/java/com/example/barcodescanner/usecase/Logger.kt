package com.example.barcodescanner.usecase

import io.sentry.core.Sentry

object Logger {
    fun log(error: Throwable) {
        Sentry.captureException(error)
    }
}