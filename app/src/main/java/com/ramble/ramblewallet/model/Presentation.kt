package com.ramble.ramblewallet.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

/**
 * 时间　: 2021/12/25 15:46
 * 作者　: potato
 * 描述　:
 */

@JvmOverloads
fun <T : ViewDataBinding> LayoutInflater.dataBinding(@LayoutRes layoutRes: Int, container: ViewGroup? = null): T =
    DataBindingUtil.inflate(this, layoutRes, container, false)

/**
 * lazy init, so we use delegate
 */
fun <T : android.app.Activity, R : ViewDataBinding> dataBinding(@LayoutRes layoutRes: Int) =
    ContentView<T, R>(layoutRes)

/**
 * lazy init, so we use delegate
 */
//fun <T : AppCompatDialogFragment, R : ViewModel> dialogFragmentViewModel(clz: Class<R>) =
//    DialogFragmentViewModel<T, R>(clz)

/**
 * lazy init, so we use delegate
 */
fun <T : Activity, R : ViewModel> activityViewModel(clz: Class<R>) = ActivityViewModel<T, R>(clz)

/**
 * lazy init, so we use delegate
 */
//fun <T : Fragment, R : ViewModel> fragmentViewModel(clz: Class<R>) = FragmentViewModel<T, R>(clz)