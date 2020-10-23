package com.example.barcodescanner.feature.common.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.dialog_edit_barcode_name.view.*

class EditBarcodeNameDialogFragment : DialogFragment() {

    interface Listener {
        fun onNameConfirmed(name: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener

        val view = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.dialog_edit_barcode_name, null, false)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setTitle(R.string.dialog_edit_barcode_name_title)
            .setView(view)
            .setPositiveButton(R.string.dialog_confirm_barcode_positive_button) { _, _ ->
                val name = view.edit_text_barcode_name.text.toString()
                listener?.onNameConfirmed(name)
            }
            .setNegativeButton(R.string.dialog_confirm_barcode_negative_button, null)
            .create()

        dialog.setOnShowListener {
            showKeyboard(view.edit_text_barcode_name)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        return dialog
    }

    private fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        val manager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        manager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}