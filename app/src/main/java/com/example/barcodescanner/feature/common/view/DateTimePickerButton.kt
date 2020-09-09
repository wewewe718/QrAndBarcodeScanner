package com.example.barcodescanner.feature.common.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.formatOrNull
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import kotlinx.android.synthetic.main.layout_date_time_picker_button.view.*
import java.text.SimpleDateFormat
import java.util.*

class DateTimePickerButton : FrameLayout {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    private val view: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_date_time_picker_button, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.DateTimePickerButton).apply {
            showHint(this)
            recycle()
        }

        view.setOnClickListener {
            showDateTimePickerDialog()
        }

        showDateTime()
    }

    var dateTime: Long = System.currentTimeMillis()
        set(value) {
            field = value
            showDateTime()
        }

    private fun showHint(attributes: TypedArray) {
        view.text_view_hint.text = attributes.getString(R.styleable.DateTimePickerButton_hint).orEmpty()
    }

    private fun showDateTimePickerDialog() {
        SingleDateAndTimePickerDialog.Builder(context)
            .backgroundColor(context.resources.getColor(R.color.date_time_picker_dialog_background_color))
            .title(view.text_view_hint.text.toString())
            .mainColor(context.resources.getColor(R.color.blue))
            .listener { newDateTime ->
                dateTime = newDateTime.time
                showDateTime()
            }
            .display()
    }

    private fun showDateTime() {
        view.text_view_date_time.text = dateFormatter.formatOrNull(dateTime).orEmpty()
    }
}