package com.example.barcodescanner.extension

import androidx.fragment.app.Fragment
import com.example.barcodescanner.feature.common.ErrorDialogFragment

fun Fragment.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(requireContext(), error)
    errorDialog.show(childFragmentManager, "")
}