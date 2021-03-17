package com.example.barcodescanner.feature.tabs.scan.file

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MyCropImageView : CropImageView {
    private val touches = PublishSubject.create<MotionEvent>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.also { touches.onNext(it) }
        return super.onTouchEvent(event)
    }

    override fun rotateImage(degrees: RotateDegrees?) {
        if (drawable != null) {
            super.rotateImage(degrees)
        }
    }

    fun touches(): Observable<MotionEvent> {
        return touches
    }
}