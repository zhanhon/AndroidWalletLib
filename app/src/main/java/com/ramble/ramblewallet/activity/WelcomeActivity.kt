package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.helper.MyPreferences
import com.ramble.ramblewallet.push.UmInitConfig
import com.ramble.ramblewallet.utils.LanguageSetting
import com.ramble.ramblewallet.utils.LanguageSetting.setLanguage
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import java.util.*

class WelcomeActivity : BaseActivity() {

    private lateinit var wallet: WalletETH

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
            if (SharedPreferencesUtils.getString(this, WALLETSELECTED, "").isEmpty()) {
                startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
            } else {
                wallet =
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
                        object : TypeToken<WalletETH>() {}.type
                    )
                when (wallet.walletType) {
                    1 -> {
                        startActivity(Intent(this, MainETHActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this, MainTRXActivity::class.java))
                    }
                    0 -> {
                        startActivity(Intent(this, MainBTCActivity::class.java))
                    }
                }
            }
        }, 3000)

    }

    private fun showAgreement() {
        //用户点击隐私协议同意按钮后，初始化PushSDK
        MyPreferences.getInstance(applicationContext).setAgreePrivacyAgreement(true)
        UMConfigure.submitPolicyGrantResult(applicationContext, true)
        /*** 友盟sdk正式初始化 */

        UmInitConfig.umInit(this@WelcomeActivity)
        //推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
        PushAgent.getInstance(this@WelcomeActivity).onAppStart()
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        val fristShow = SharedPreferencesUtils.getBoolean(this, FIRSTLANGUAGE, true)
        if(fristShow){//第一次
            SharedPreferencesUtils.saveBoolean(this, FIRSTLANGUAGE, false)
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
        } else {
            setLanguage()
        }
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                setLanguage(applicationContext, 1)
            }
            TW -> {
                setLanguage(applicationContext, 2)
            }
            EN -> {
                setLanguage(applicationContext, 3)
            }
        }
    }

}