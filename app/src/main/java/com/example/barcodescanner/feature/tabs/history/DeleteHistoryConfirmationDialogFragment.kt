package com.example.barcodescanner.feature.tabs.history

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.barcodescanner.R

class DeleteHistoryConfirmationDialogFragment : DialogFragment() {

    interface Listener {
        fun onDeleteHistoryPositiveButtonClicked()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as? Listener
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_dialog_title)
            .setMessage(R.string.fragment_barcode_history_delete_all_dialog_message)
            .setCancelable(false)
            .setPositiveButton(R.string.fragment_barcode_history_delete_all_dialog_positive_button) { _, _ -> listener?.onDeleteHistoryPositiveButtonClicked() }
            .setNegativeButton(R.string.fragment_barcode_history_delete_all_dialog_negative_button, null)
            .create()
    }
}