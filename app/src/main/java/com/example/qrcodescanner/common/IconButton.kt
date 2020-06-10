package com.example.qrcodescanner.common

import com.example.qrcodescanner.R
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_icon_button.view.*


class IconButton : FrameLayout {
    private lateinit var view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val view = inflateView(context)
        if (attrs != null) {
            applyAttributes(context, view, attrs)
        }
    }

    private fun inflateView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(R.layout.layout_icon_button, this, true)
    }

    private fun applyAttributes(context: Context, view: View, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.IconButton).apply {
            showIcon(view, this)
            showText(view, this)
            recycle()
        }
    }

    private fun showIcon(view: View, attributes: TypedArray) {
        view.image_view.setImageDrawable(attributes.getDrawable(R.styleable.IconButton_icon))
    }

    private fun showText(view: View, attributes: TypedArray) {
        view.text_view.text = attributes.getString(R.styleable.IconButton_text).orEmpty()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}