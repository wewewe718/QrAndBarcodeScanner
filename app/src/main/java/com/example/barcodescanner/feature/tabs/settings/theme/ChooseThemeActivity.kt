package com.example.barcodescanner.feature.tabs.settings.theme

import android.content.*
import android.os.*
import android.util.*
import android.view.*
import androidx.appcompat.app.*
import com.example.barcodescanner.*
import com.example.barcodescanner.di.*
import com.example.barcodescanner.feature.*
import kotlinx.android.synthetic.main.activity_choose_theme.*

class ChooseThemeActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseThemeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val buttons by lazy {
        listOf(button_system_theme, button_light_theme, button_dark_theme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_theme)
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        showInitialSettings()
        handleSettingsChanged()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun showInitialSettings() {
        Log.d("InitialState", settings.theme.toString())
        val theme = settings.theme
        button_system_theme.isChecked = theme == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED || theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        button_light_theme.isChecked = theme == AppCompatDelegate.MODE_NIGHT_NO
        button_dark_theme.isChecked = theme == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun handleSettingsChanged() {
        button_system_theme.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_system_theme)
            settings.theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        button_light_theme.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_light_theme)
            settings.theme = AppCompatDelegate.MODE_NIGHT_NO
        }

        button_dark_theme.setCheckedChangedListener { isChecked ->
            if (isChecked.not()) {
                return@setCheckedChangedListener
            }

            uncheckOtherButtons(button_dark_theme)
            settings.theme = AppCompatDelegate.MODE_NIGHT_YES
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