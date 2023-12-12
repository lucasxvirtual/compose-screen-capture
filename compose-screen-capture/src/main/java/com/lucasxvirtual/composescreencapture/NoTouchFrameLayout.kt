package com.lucasxvirtual.composescreencapture

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

internal class NoTouchFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}
