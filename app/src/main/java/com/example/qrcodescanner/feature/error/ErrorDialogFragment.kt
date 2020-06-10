package com.example.qrcodescanner.feature.error

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.qrcodescanner.R

class ErrorDialogFragment : DialogFragment() {

    companion object {
        private const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE_KEY"

        fun newInstance(errorMessage: String?): ErrorDialogFragment {
            return ErrorDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ERROR_MESSAGE_KEY, errorMessage)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(ERROR_MESSAGE_KEY) ?: getString(
            R.string.error_dialog_default_message
        )
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.error_dialog_positive_button_text, null)
            .create()
    }
}