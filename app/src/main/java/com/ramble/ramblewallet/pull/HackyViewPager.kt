package com.ramble.ramblewallet.pull

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * PhotoView ViewPager
 * @link https://github.com/chrisbanes/PhotoView/blob/master/sample/src/main/java/com/github/chrisbanes/photoview/sample/HackyViewPager.java
 */
class HackyViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
