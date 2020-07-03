package com.example.barcodescanner.feature.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.layout_icon_button.view.*


class IconButton : FrameLayout {
    private lateinit var view: View

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val view = inflateView(context)
        if (attrs != null) {
            applyAttributes(context, view, attrs)
        }
    }

    private fun inflateView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.layout_icon_button, this, true)
        return view
    }

    private fun applyAttributes(context: Context, view: View, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.IconButton).apply {
            showIcon(view, this)
            showIconBackgroundColor(view, this)
            showText(view, this)
            showDelimiter(view, this)
            recycle()
        }
    }

    private fun showIcon(view: View, attributes: TypedArray) {
        view.image_view_schema.setImageDrawable(attributes.getDrawable(R.styleable.IconButton_icon))
    }

    private fun showIconBackgroundColor(view: View, attributes: TypedArray) {
        val color = attributes.getColor(R.styleable.IconButton_iconBackground, view.context.resources.getColor(R.color.green))
        (view.layout_image.background.mutate() as GradientDrawable).setColor(color)
    }

    private fun showText(view: View, attributes: TypedArray) {
        view.text_view.text = attributes.getString(R.styleable.IconButton_text).orEmpty()
    }

    private fun showDelimiter(view: View, attributes: TypedArray) {
        view.delimiter.isInvisible = attributes.getBoolean(R.styleable.IconButton_isDelimiterVisible, true).not()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        view.image_view_schema.isEnabled = enabled
        view.text_view.isEnabled = enabled
    }
}