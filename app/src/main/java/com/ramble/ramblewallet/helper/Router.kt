@file:JvmName("Router")

package com.ramble.ramblewallet.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.constant.REQUEST_CODE_1029
import com.ramble.ramblewallet.utils.Glide4Engine
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType

/**
 * 选择相片
 */
fun Activity.startMatisseActivity() {
    Matisse.from(this)
        .choose(MimeType.ofImage(), true)
        .countable(false)
        .maxSelectable(1)
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        .thumbnailScale(0.85f)
        .imageEngine(Glide4Engine())
        .capture(false)
        .theme(R.style.MyMatisse)
        .forResult(REQUEST_CODE_1029)
}

@JvmOverloads
fun Activity.start(to: Class<*>, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this, to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivity(intent)
}

@JvmOverloads
fun Fragment.start2(to: Class<*>, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this.requireActivity(), to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivity(intent)
}

fun Activity.getExtras(): Bundle = this.intent.extras!!
