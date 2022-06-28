package com.ramble.ramblewallet.base

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.network.ApiRetrofit
import com.ramble.ramblewallet.network.ApiService
import com.ramble.ramblewallet.utils.ActivityManager
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.addTo
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {

    var mApiService: ApiService = ApiRetrofit.getInstance().apiService

    @JvmField
    val onPauseComposite = CompositeDisposable()

    @JvmField
    val onStopComposite = CompositeDisposable()

    @JvmField
    val onDestroyComposite = CompositeDisposable()

    private var mNetWorkStateReceiver: NetWorkStateReceiver? = null
    lateinit var mLoadingPopupView: LoadingPopupView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        //沉浸式状态栏处理
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_FFFFFF)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //状态栏黑色字体
        RxBus.eventObservable()
            .subscribe(
                {
                    try {
                        onRxBus(it)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, { it.printStackTrace() }
            )
            .addTo(onDestroyComposite)
        mLoadingPopupView = XPopup.Builder(this).asLoading().setTitle(getString(R.string.loading))
        ActivityManager.getAppManager().addActivity(this)
        Log.i("ramble",javaClass.simpleName)
    }

    override fun onResume() {
        super.onResume()
        if (mNetWorkStateReceiver == null) {
            mNetWorkStateReceiver = NetWorkStateReceiver()
        }
        var filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mNetWorkStateReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mNetWorkStateReceiver)
    }

    protected fun setLoadingTitle(string: String){
        mLoadingPopupView.setTitle(string)
    }

    protected open fun showLoadingDialog() {
        mLoadingPopupView.show()
    }

    protected open fun dismissLoadingDialog() {
        if (mLoadingPopupView.isShow) {
            mLoadingPopupView.dismiss()
        }
    }

    open fun onRxBus(event: RxBus.Event) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!this.isDestroyed) {
            onPauseComposite.dispose()
            onStopComposite.dispose()
            onDestroyComposite.dispose()
        }
        dismissLoadingDialog()
        ActivityManager.getAppManager().finishActivity(this)
    }

}