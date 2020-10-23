package com.example.barcodescanner.feature.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.barcodescanner.R

class ErrorDialogFragment : DialogFragment() {

    interface Listener {
        fun onErrorDialogPositiveButtonClicked()
    }

    companion object {
        private const val ERROR_MESSAGE_KEY = "ERROR_MESSAGE_KEY"

        fun newInstance(context: Context, error: Throwable?): ErrorDialogFragment {
            return ErrorDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ERROR_MESSAGE_KEY, getErrorMessage(context, error))
                }
                isCancelable = false
            }
        }

        private fun getErrorMessage(context: Context, error: Throwable?): String {
            return error?.message ?: context.getString(R.string.error_dialog_default_message)
        }
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(ERROR_MESSAGE_KEY).orEmpty()

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(R.string.error_dialog_title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.error_dialog_positive_button_text) { _, _ -> listener?.onErrorDialogPositiveButtonClicked() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }

        return dialog
    }
}