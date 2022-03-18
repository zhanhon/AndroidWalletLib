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
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.LanguageSetting.setLanguage
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import java.util.*

class WelcomeActivity : BaseActivity() {

    private lateinit var wallet: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var times = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        skipConfirmHandle()
        Handler().postDelayed({
            if (SharedPreferencesUtils.getString(this, WALLETSELECTED, "").isEmpty()) {
                startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
            } else {
                wallet =
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
                        object : TypeToken<Wallet>() {}.type
                    )
                when (wallet.walletType) {
                    1 -> {
                        startActivity(Intent(this, MainETHActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this, MainTRXActivity::class.java))
                    }
                    3 -> {
                        startActivity(Intent(this, MainBTCActivity::class.java))
                    }
                }
            }
        }, 3000)

    }

    /***
     *上传友盟DEVICE_TOKEN失效问题
     *
     */

    private fun skipConfirmHandle() {
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
            var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            saveWalletList.forEach {
                detailsList.add(AddressReport.DetailsList(it.address, 0, it.walletType))
            }
            if (detailsList.size > 0) {
                putAddress(detailsList)
            }
        }
    }

    /***
     *上传友盟DEVICE_TOKEN失效问题
     */
    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    if (times < 3) {
                        putAddress(detailsList)
                        times++
                    }
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }


    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        val fristShow = SharedPreferencesUtils.getBoolean(this, FIRSTLANGUAGE, true)
        if (fristShow) {//第一次
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
                setLanguage(this, 1)
            }
            TW -> {
                setLanguage(this, 2)
            }
            EN -> {
                setLanguage(this, 3)
            }
        }
    }

}