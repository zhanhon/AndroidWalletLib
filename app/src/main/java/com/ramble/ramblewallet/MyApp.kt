package com.ramble.ramblewallet

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.fragment.*
import com.ramble.ramblewallet.push.UmInitConfig
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent



class MyApp{


    lateinit var sInstance: Application



    fun initData(
        context: Application,
        mAppProcessName: String,
        serverUrls: MutableList<String>,
        chatServerUrl: String
    ){
        sInstance = context
        AndroidThreeTen.init(context)
        setupArchLibrary(context, BuildConfig.DEBUG, mAppProcessName, 0, serverUrls,chatServerUrl)
        initUmengSDK(context)
    }


    /**
     * 初始化友盟SDK
     */
    private fun initUmengSDK(context: Context) {
        //日志开关
        UMConfigure.setLogEnabled(true)
        //预初始化

        UmInitConfig.preInit(context)
        UmInitConfig.umInit(context)
        PushAgent.getInstance(context).onAppStart()
    }

    /***
     * 进入主页
     */
    fun startActivityJun(context: Context):String? {
        val selected = SharedPreferencesUtils.getSecurityString(context, WALLETSELECTED, "")
        if (selected.isEmpty()) {
            return CreateRecoverWalletFragment::class.toString()
        } else {
            val wallet: Wallet = Gson().fromJson(selected, object : TypeToken<Wallet>() {}.type)
            var sClass: String? = null
            when (wallet.walletType) {
                WALLET_TYPE_ETH -> {
                    sClass =  MainETHFragment::class.toString()
                }
                WALLET_TYPE_TRX -> {
                    sClass =  MainTRXFragment::class.toString()
                }
                WALLET_TYPE_BTC -> {
                    sClass =  MainBTCFragment::class.toString()
                }
                WALLET_TYPE_SOL -> {
                    sClass =  MainSOLFragment::class.toString()
                }
            }
            return sClass
        }
    }

    fun getMapFragment(): Map<String,Fragment>{
        val map = HashMap<String,Fragment>()
        map.put(CreateRecoverWalletFragment::class.toString(), CreateRecoverWalletFragment())
        map.put(MainBTCFragment::class.toString(), MainBTCFragment())
        map.put(MainETHFragment::class.toString(), MainETHFragment())
        map.put(MainTRXFragment::class.toString(), MainTRXFragment())
        map.put(MainSOLFragment::class.toString(), MainSOLFragment())
        return map
    }

    companion object {
        val getInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MyApp()
        }
    }



}