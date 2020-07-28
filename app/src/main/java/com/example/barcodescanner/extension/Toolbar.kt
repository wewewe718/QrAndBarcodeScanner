package com.example.barcodescanner.extension

import android.widget.ScrollView
import androidx.appcompat.widget.Toolbar

fun Toolbar.setupElevationChange(scrollView: ScrollView) {
    scrollView.viewTreeObserver.addOnScrollChangedListener {
    }
}