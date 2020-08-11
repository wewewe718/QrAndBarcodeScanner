package com.example.barcodescanner.extension

import androidx.appcompat.app.AppCompatActivity
import com.example.barcodescanner.feature.common.dialog.ErrorDialogFragment

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
        ErrorDialogFragment.newInstance(
            this,
            error
        )
    errorDialog.show(supportFragmentManager, "")
}