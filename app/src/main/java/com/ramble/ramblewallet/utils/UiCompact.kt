@file:JvmName("UiCompact")
@file:Suppress("DEPRECATION")

package com.ramble.ramblewallet.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.ramble.ramblewallet.base.Fragment


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

fun <T> T.postUI(action: () -> Unit) {

    // Fragment
    if (this is Fragment) {
        val fragment = this
        if (!fragment.isAdded) return

        val activity = fragment.activity ?: return
        if (activity.isFinishing) return

        activity.runOnUiThread(action)
        return
    }

    // Activity
    if (this is Activity) {
        if (this.isFinishing) return

        this.runOnUiThread(action)
        return
    }

    // 主线程
    if (Looper.getMainLooper() === Looper.myLooper()) {
        action()
        return
    }

    // 子线程，使用handler
    Handler().post { action() }
}