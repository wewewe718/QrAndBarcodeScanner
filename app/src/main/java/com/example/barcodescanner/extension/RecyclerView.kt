package com.example.barcodescanner.extension

import androidx.recyclerview.widget.RecyclerView
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter

fun RecyclerView.makeSmoothScrollable() {
    VerticalOverScrollBounceEffectDecorator(
        RecyclerViewOverScrollDecorAdapter(this),
        3f,
        VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
        VerticalOverScrollBounceEffectDecorator.DEFAULT_DECELERATE_FACTOR
    )
}