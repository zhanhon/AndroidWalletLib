package com.ramble.ramblewallet.pull

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class NoSmoothScrollViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, false)
    }
}