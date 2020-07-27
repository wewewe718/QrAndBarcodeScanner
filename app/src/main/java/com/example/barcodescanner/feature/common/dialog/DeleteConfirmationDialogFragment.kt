package com.example.barcodescanner.feature.common.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.orZero
import kotlinx.android.synthetic.main.dialog_delete_confirmation.view.*

class DeleteConfirmationDialogFragment : DialogFragment() {

    companion object {
        private const val MESSAGE_ID_KEY = "MESSAGE_ID_KEY"

        fun newInstance(messageId: Int): DeleteConfirmationDialogFragment {
            return DeleteConfirmationDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(MESSAGE_ID_KEY, messageId)
                }
            }
        }
    }

    interface Listener {
        fun onDeleteConfirmed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as? Listener
        val messageId = arguments?.getInt(MESSAGE_ID_KEY).orZero()

        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_delete_confirmation, null, false)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.text_view_message.setText(messageId)

        view.button_delete.setOnClickListener {
            listener?.onDeleteConfirmed()
            dialog.dismiss()
        }

        view.button_cancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}