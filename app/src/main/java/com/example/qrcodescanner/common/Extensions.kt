package com.example.qrcodescanner.common

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.example.qrcodescanner.App
import com.example.qrcodescanner.feature.error.ErrorDialogFragment
import com.google.zxing.BarcodeFormat

fun AppCompatActivity.showError(error: Throwable) {
    val errorDialog = ErrorDialogFragment.newInstance(error.message)
    errorDialog.show(supportFragmentManager, "")
}

val AndroidViewModel.db
    get() = getApplication<App>().db