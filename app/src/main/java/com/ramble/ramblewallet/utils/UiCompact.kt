@file:JvmName("UiCompact")
@file:Suppress("DEPRECATION")

package com.ramble.ramblewallet.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.*
import android.widget.Toast
import com.ramble.ramblewallet.R


fun View.asyncAnimator(): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 720f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 1500
        start()
    }
}

fun Context.toastDefault(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}