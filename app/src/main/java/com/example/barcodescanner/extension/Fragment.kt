package com.example.barcodescanner.extension

import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.feature.common.dialog.ErrorDialogFragment

val Fragment.packageManager: PackageManager
    get() = requireContext().packageManager

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}

fun Fragment.setBlackStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return
    }

    if (settings.isDarkTheme) {
        return
    }

    requireActivity().window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = 0
        statusBarColor = ContextCompat.getColor(context, R.color.black)
    }
}

fun Fragment.setWhiteStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return
    }

    if (settings.isDarkTheme) {
        return
    }

    requireActivity().window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        statusBarColor = ContextCompat.getColor(context, R.color.white)
    }
}