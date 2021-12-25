package com.ramble.ramblewallet.model

import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ramble.ramblewallet.helper.hideKeyboard

/**
 * 时间　: 2021/12/25 15:48
 * 作者　: potato
 * 描述　:
 */
abstract class Activity : AppCompatActivity() {

    lateinit var viewModelFactory: ViewModelProvider.Factory
        protected set

    @CallSuper
    override fun finish() {
        super.finish()
        this.hideKeyboard()
    }

}