package com.ramble.ramblewallet

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.ramble.ramblewallet.constant.setupArchLibrary
import com.ramble.ramblewallet.helper.MyPreferences
import com.ramble.ramblewallet.helper.PushHelper
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils

/**
 * 时间　: 2021/12/15 9:50
 * 作者　: potato
 * 描述　:应用程序类
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        initUmengSDK()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        setupArchLibrary(
            this,
            BuildConfig.DEBUG,
            BuildConfig.APPLICATION_ID,
            0
        )
    }


    /**
     * 初始化友盟SDK
     */
    private fun initUmengSDK() {
        //日志开关
        UMConfigure.setLogEnabled(true)
        //预初始化
        PushHelper.preInit(this)
        //是否同意隐私政策
        val agreed: Boolean = MyPreferences.getInstance(this).hasAgreePrivacyAgreement()
        if (!agreed) {
            return
        }
        val isMainProcess = UMUtils.isMainProgress(this)
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            Thread { PushHelper.init(applicationContext) }.start()
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(applicationContext)
        }
    }

    companion object {
        lateinit var sInstance: Application
    }

}