package com.example.barcodescanner.extension

import android.widget.ScrollView
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter

fun ScrollView.makeSmoothScrollable() {
    VerticalOverScrollBounceEffectDecorator(
        ScrollViewOverScrollDecorAdapter(this),
        3f,
        VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
        VerticalOverScrollBounceEffectDecorator.DEFAULT_DECELERATE_FACTOR
    )
}