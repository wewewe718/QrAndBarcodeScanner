package com.example.barcodescanner.feature.tabs.settings.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.barcodescanner.R
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.common.view.SettingsRadioButton
import com.example.barcodescanner.model.SearchEngine
import kotlinx.android.synthetic.main.activity_choose_search_engine.*

class ChooseSearchEngineActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseSearchEngineActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val buttons by unsafeLazy {
        listOf(button_none, button_ask_every_time, button_bing, button_duck_duck_go, button_google, button_qwant, button_startpage, button_yahoo, button_yandex)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_search_engine)
        supportEdgeToEdge()
        initToolbar()
        showInitialValue()
        handleSettingsChanged()
    }

    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun showInitialValue() {
        when (settings.searchEngine) {
            SearchEngine.NONE -> button_none.isChecked = true
            SearchEngine.ASK_EVERY_TIME -> button_ask_every_time.isChecked = true
            SearchEngine.BING -> button_bing.isChecked = true
            SearchEngine.DUCK_DUCK_GO -> button_duck_duck_go.isChecked = true
            SearchEngine.GOOGLE -> button_google.isChecked = true
            SearchEngine.QWANT -> button_qwant.isChecked = true
            SearchEngine.STARTPAGE -> button_startpage.isChecked = true
            SearchEngine.YAHOO -> button_yahoo.isChecked = true
            SearchEngine.YANDEX -> button_yandex.isChecked = true
        }
    }

    private fun handleSettingsChanged() {
        button_none.setCheckedChangedListener(SearchEngine.NONE)
        button_ask_every_time.setCheckedChangedListener(SearchEngine.ASK_EVERY_TIME)
        button_bing.setCheckedChangedListener(SearchEngine.BING)
        button_duck_duck_go.setCheckedChangedListener(SearchEngine.DUCK_DUCK_GO)
        button_google.setCheckedChangedListener(SearchEngine.GOOGLE)
        button_qwant.setCheckedChangedListener(SearchEngine.QWANT)
        button_startpage.setCheckedChangedListener(SearchEngine.STARTPAGE)
        button_yahoo.setCheckedChangedListener(SearchEngine.YAHOO)
        button_yandex.setCheckedChangedListener(SearchEngine.YANDEX)
    }

    private fun SettingsRadioButton.setCheckedChangedListener(searchEngine: SearchEngine) {
        setCheckedChangedListener { isChecked ->
            if (isChecked) {
                uncheckOtherButtons(this)
                settings.searchEngine = searchEngine
            }
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
