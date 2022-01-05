@file:JvmName("UiCompact")
@file:Suppress("DEPRECATION")

package com.ramble.ramblewallet.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.*


fun View.asyncAnimator(): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 720f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 1500
        start()
    }
}
