package com.ramble.ramblewallet.base

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.addTo
import io.reactivex.disposables.CompositeDisposable

/**
 * 时间　: 2021/12/20 17:40
 * 作者　: potato
 * 描述　:
 */
abstract class BaseFragment  : Fragment(), View.OnClickListener {
    @JvmField
    protected val onPauseComposite = CompositeDisposable()

    @JvmField
    protected val onStopComposite = CompositeDisposable()

    @JvmField
    protected val onDestroyComposite = CompositeDisposable()

    protected val onDetachComposite = CompositeDisposable()

    protected var lock = false

    override fun actualLazyLoad() {
        super.actualLazyLoad()
        RxBus.eventObservable()
            .subscribe(
                {
                    try {
                        onRxBus(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    it.printStackTrace()
                }
            )
            .addTo(onDestroyComposite)
    }

    @CallSuper
    override fun onAttach(context: Context) {
//        viewModelFactory = MyApp.appComponent.viewModelFactory()
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        onDetachComposite.clear()
    }

    override fun onPause() {
        super.onPause()
        onPauseComposite.clear()
    }

    override fun onStop() {
        super.onStop()
        onStopComposite.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDetachComposite.dispose()
        onPauseComposite.dispose()
        onStopComposite.dispose()
        onDestroyComposite.dispose()
    }

    open fun onRxBus(event: RxBus.Event) {
    }

}