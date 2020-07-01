package com.example.barcodescanner.feature.tabs.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.layout_settings_button.view.*

class SettingsButton : FrameLayout {
    private lateinit var view: View

    var hint: String
        get() = view.text_view_hint.text.toString()
        set(value) {
            view.text_view_hint.apply {
                text = value
                isVisible = text.isNullOrEmpty().not()
            }
        }

    var isChecked: Boolean
        get() = view.switch_button.isChecked
        set(value) { view.switch_button.isChecked = value }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val view = inflateView(context)
        if (attrs != null) {
            applyAttributes(context, view, attrs)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        text_view_text.isEnabled = enabled
    }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        view.switch_button.setOnCheckedChangeListener { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    private fun inflateView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.layout_settings_button, this, true)
        return view
    }

    private fun applyAttributes(context: Context, view: View, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SettingsButton).apply {
            showText(view, this)
            showHint(view, this)
            showSwitch(view, this)
            showArrow(view, this)
            showDelimiter(view, this)
            recycle()
        }
    }

    private fun showText(view: View, attributes: TypedArray) {
        view.text_view_text.text = attributes.getString(R.styleable.SettingsButton_text).orEmpty()
    }

    private fun showHint(view: View, attributes: TypedArray) {
        hint = attributes.getString(R.styleable.SettingsButton_hint).orEmpty()
    }

    private fun showSwitch(view: View, attributes: TypedArray) {
        view.switch_button.isVisible = attributes.getBoolean(R.styleable.SettingsButton_isSwitchVisible, true)
    }

    private fun showArrow(view: View, attributes: TypedArray) {
        view.image_view_arrow.isVisible = attributes.getBoolean(R.styleable.SettingsButton_isArrowVisible, false)
    }

    private fun showDelimiter(view: View, attributes: TypedArray) {
        view.delimiter.isInvisible = attributes.getBoolean(R.styleable.SettingsButton_isDelimiterVisible, true).not()
    }
}