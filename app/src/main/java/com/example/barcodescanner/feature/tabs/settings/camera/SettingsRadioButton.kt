package com.example.barcodescanner.feature.tabs.settings.camera

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.layout_settings_radio_button.view.*

class SettingsRadioButton : FrameLayout {
    private lateinit var view: View

    var isChecked: Boolean
        get() = view.radio_button.isChecked
        set(value) { view.radio_button.isChecked = value }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val view = inflateView(context)
        if (attrs != null) {
            applyAttributes(context, view, attrs)
        }
    }

    fun setCheckedChangedListener(listener: ((Boolean) -> Unit)?) {
        view.radio_button.setOnCheckedChangeListener { _, isChecked ->
            listener?.invoke(isChecked)
        }
    }

    private fun inflateView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.layout_settings_radio_button, this, true)
        return view
    }

    private fun applyAttributes(context: Context, view: View, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SettingsRadioButton).apply {
            showText(view, this)
            showDelimiter(view, this)
            recycle()
        }
    }

    private fun showText(view: View, attributes: TypedArray) {
        view.text_view_text.text = attributes.getString(R.styleable.SettingsRadioButton_text).orEmpty()
    }

    private fun showDelimiter(view: View, attributes: TypedArray) {
        view.delimiter.isInvisible = attributes.getBoolean(R.styleable.SettingsRadioButton_isDelimiterVisible, true).not()
    }
}