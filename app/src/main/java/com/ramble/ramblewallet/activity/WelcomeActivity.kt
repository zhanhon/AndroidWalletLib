package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.EmptyReq
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.helper.MyPreferences
import com.ramble.ramblewallet.helper.PushHelper
import com.ramble.ramblewallet.network.rateInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback
import java.util.*

class WelcomeActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        if (MyPreferences.getInstance(this).hasAgreePrivacyAgreement()) {
            PushAgent.getInstance(this).onAppStart()
            val deviceToken = PushAgent.getInstance(this).registrationId
            SharedPreferencesUtils.saveString(this, DEVICE_TOKEN, deviceToken)
        } else {
            showAgreement()
        }
        Handler().postDelayed({
            startActivity(Intent(this, MainETHActivity::class.java)) //暂时测试用
            //startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
        }, 3000)

    }

    private fun showAgreement() {
        //用户点击隐私协议同意按钮后，初始化PushSDK
        MyPreferences.getInstance(applicationContext).setAgreePrivacyAgreement(true)
        PushHelper.init(applicationContext)
        PushAgent.getInstance(applicationContext).register(object : UPushRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                runOnUiThread {
//                    val token = findViewById<TextView>(R.id.tv_device_token)
//                    token.text = deviceToken
                }
            }

            override fun onFailure(code: String, msg: String) {
                Log.d("MainActivity", "code:$code msg:$msg")
            }
        })

    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        //设置跟随手机系统语言进行设置app默认语言
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
        when (locale.country) {
            "CN" -> {
                SharedPreferencesUtils.saveString(this, LANGUAGE, CN)
            }
            "TW" -> {
                SharedPreferencesUtils.saveString(this, LANGUAGE, TW)
            }
            else -> {
                SharedPreferencesUtils.saveString(this, LANGUAGE, EN)
            }
        }

        Thread {
            while (true) {
                try {
                    mApiService.getRateInfo(EmptyReq().toApiRequest(rateInfoUrl))
                        .applyIo().subscribe(
                            {
                                if (it.code() == 1) {
                                    it.data()?.let { data ->
                                        SharedPreferencesUtils.saveString(
                                            this,
                                            RATEINFO,
                                            Gson().toJson(data)
                                        )
                                        println("-=-=-=->${Gson().toJson(data)}")
                                    }
                                } else {
                                    println("-=-=-=->${it.message()}")
                                }
                            }, {
                                println("-=-=-=->${it.printStackTrace()}")
                            }
                        )
                    Thread.sleep(10000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}