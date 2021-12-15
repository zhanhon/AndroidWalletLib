package com.ramble.ramblewallet.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 时间　: 2021/12/15 16:48
 * 作者　: potato
 * 描述　:
 */
@JvmOverloads
fun <T : ViewDataBinding> LayoutInflater.dataBinding(@LayoutRes layoutRes: Int, container: ViewGroup? = null): T =
    DataBindingUtil.inflate(this, layoutRes, container, false)



@ColorInt
fun Context.toColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)