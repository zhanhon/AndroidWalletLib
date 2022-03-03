package com.ramble.ramblewallet.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class Fragment : Fragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory
        protected set

    var reusedView: View? = null
        protected set

    var isViewCreated = false
        private set

    var isLazyDone = false
        private set

    private var isLazyLocked = true

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        if (!view.isClickable) {
            view.isClickable = true
        }
        actualLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        reusedView = null
    }

    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        actualLoad()
    }

    private fun actualLoad() {
        if (userVisibleHint && isViewCreated && !isLazyDone) {
            isLazyLocked = false
            isLazyDone = true
            actualLazyLoad()
        }
    }

    /**
     * overwrite the method to do lazy load
     */
    @CallSuper
    protected open fun actualLazyLoad() {
        if (isLazyLocked) {
            throw IllegalStateException("isLazyLocked = true")
        }
    }

    open fun onBackPressed(): Boolean = false
}