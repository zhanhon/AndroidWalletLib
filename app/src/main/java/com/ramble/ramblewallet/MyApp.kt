package com.ramble.ramblewallet

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ramble.ramblewallet.constant.setupArchLibrary
import com.ramble.ramblewallet.helper.MyPreferences
import com.ramble.ramblewallet.push.UmInitConfig
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent


/**
 * 时间　: 2021/12/15 9:50
 * 作者　: potato
 * 描述　:应用程序类
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        AndroidThreeTen.init(this)
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

        UmInitConfig.preInit(this)
        UmInitConfig.umInit(this)
        PushAgent.getInstance(this).onAppStart()
    }

    companion object {
        lateinit var sInstance: Application
    }

}