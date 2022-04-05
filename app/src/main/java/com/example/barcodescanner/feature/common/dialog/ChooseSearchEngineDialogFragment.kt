package com.example.barcodescanner.feature.common.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.barcodescanner.R
import com.example.barcodescanner.model.SearchEngine

class ChooseSearchEngineDialogFragment : DialogFragment() {

    companion object {
        private val ITEMS = arrayOf(
            SearchEngine.BING,
            SearchEngine.DUCK_DUCK_GO,
            SearchEngine.GOOGLE,
            SearchEngine.QWANT,
            SearchEngine.STARTPAGE,
            SearchEngine.YAHOO,
            SearchEngine.YANDEX
        )
    }

    interface Listener {
        fun onSearchEngineSelected(searchEngine: SearchEngine)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = requireActivity() as? Listener

        val items = arrayOf(
            getString(R.string.activity_choose_search_engine_bing),
            getString(R.string.activity_choose_search_engine_duck_duck_go),
            getString(R.string.activity_choose_search_engine_google),
            getString(R.string.activity_choose_search_engine_qwant),
            getString(R.string.activity_choose_search_engine_startpage),
            getString(R.string.activity_choose_search_engine_yahoo),
            getString(R.string.activity_choose_search_engine_yandex)
        )

        val dialog = AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
            .setItems(items) { _, itemClicked ->
                val searchEngine = ITEMS[itemClicked]
                listener?.onSearchEngineSelected(searchEngine)
            }
            .setNegativeButton(R.string.activity_barcode_choose_search_engine_dialog_negative_button) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        return dialog
    }
}