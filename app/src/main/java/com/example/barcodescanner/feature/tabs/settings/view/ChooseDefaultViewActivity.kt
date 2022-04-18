package com.example.barcodescanner.feature.tabs.settings.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.barcodescanner.R
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.DefaultView
import com.example.barcodescanner.usecase.Settings
import kotlinx.android.synthetic.main.activity_choose_default_view.*

class ChooseDefaultViewActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseDefaultViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val buttons by unsafeLazy {
        listOf(button_scan_view, button_create_view, button_history_view, button_settings_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_default_view)
        supportEdgeToEdge()
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        showInitialSettings()
        handleSettingsChanged()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun showInitialSettings() {
        val view = settings.defaultView
        button_scan_view.isChecked = view == DefaultView.SCAN
        button_create_view.isChecked = view == DefaultView.CREATE
        button_history_view.isChecked = view == DefaultView.HISTORY
        button_settings_view.isChecked = view == DefaultView.SETTINGS
    }

    private fun handleSettingsChanged() {
        button_scan_view.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_scan_view)
            settings.defaultView = DefaultView.SCAN
        }

        button_create_view.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_create_view)
            settings.defaultView = DefaultView.CREATE
        }

        button_history_view.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_history_view)
            settings.defaultView = DefaultView.HISTORY
        }

        button_settings_view.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_settings_view)
            settings.defaultView = DefaultView.SETTINGS
        }
    }

    private fun uncheckOtherButtons(checkedButton: View) {
        buttons.forEach { button ->
            if (checkedButton !== button) {
                button.isChecked = false
            }
        }
    }
}