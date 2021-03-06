package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.network.AppUtils
import com.ramble.ramblewallet.network.getAppVersion
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.update.AppVersion
import com.ramble.ramblewallet.update.UpdateUtils
import com.ramble.ramblewallet.utils.LanguageSetting.setLanguage
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class WelcomeActivity : BaseActivity() {

    private lateinit var wallet: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var putAddressTimes = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_welcome)
        skipConfirmHandle()
        isForcedUpdatingShow()
    }

    @SuppressLint("CheckResult")
    private fun isForcedUpdatingShow() {
        GlobalScope.launch {
            mApiService.appVersion(AppVersion.Req().toApiRequest(getAppVersion)).subscribe({
                if (it.code() == 1 && it.data()!!.version != null) {
                    if (it.data()!!.version!! != AppUtils.getVersionName()) {
                        if (it.data()!!.forcedUpdatingShow == 1) {//????????????
                            RxBus.emitEvent(Pie.EVENT_PUSH_FOC_UP, it.data()!!)
                            return@subscribe
                        }
                        if (it.data()!!.floatingWindowShow == 1) {//?????????
                            RxBus.emitEvent(Pie.EVENT_PUSH_FOC, it.data()!!)
                        }
                    } else {
                        RxBus.emitEvent(Pie.EVENT_PUSH_JUMP, it.data()!!)
                    }
                } else {
                    RxBus.emitEvent(Pie.EVENT_PUSH_JUMP, it.data()!!)
                }
            }, {
                RxBus.emitEvent(Pie.EVENT_PUSH_JUMP, "1")
            })
        }
    }

    /***
     * ????????????
     */
    private fun forcedUpDataDialog(version: AppVersion) {
        val title = version.date + " " + version.version + getString(R.string.update_connect)
        checkAppVersion(version, title, true)
    }

    /***
     * ????????????
     */
    private fun upDataDialog(version: AppVersion) {
        val title = version.date + " " + version.version + getString(R.string.update_connect)
        checkAppVersion(version, title, false)
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_PUSH_FOC -> {
                upDataDialog(event.data())
            }
            Pie.EVENT_PUSH_FOC_UP -> {
                forcedUpDataDialog(event.data())
            }
            Pie.EVENT_PUSH_JUMP -> {
                startActivityJun()
            }
            else -> return
        }
    }

    private fun checkAppVersion(version: AppVersion, title: String, isFoce: Boolean) {
        UpdateUtils().checkUpdate(version, title, isFoce, true)
    }

    /***
     * ????????????
     */
    private fun startActivityJun() {
        Handler().postDelayed({
            if (SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, "").isEmpty()) {
                startActivity(Intent(this, CreateRecoverWalletActivity::class.java))
            } else {
                wallet =
                    Gson().fromJson(
                        SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
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
                    4 -> {
                        startActivity(Intent(this, MainSOLActivity::class.java))
                    }
                }
            }
        }, 3000)
    }

    /***
     *????????????DEVICE_TOKEN????????????
     *
     */

    private fun skipConfirmHandle() {
        if (SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
            val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
            saveWalletList.forEach {
                detailsList.add(AddressReport.DetailsList(it.address, 0, it.walletType))
            }
            if (detailsList.size > 0) {
                putAddress(detailsList)
            }
        }
    }

    /***
     *????????????DEVICE_TOKEN????????????
     */
    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getSecurityString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getSecurityString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() != 1) {
                    if (putAddressTimes < 3) {
                        putAddress(detailsList)
                        putAddressTimes++
                    }
                }
            }, {
            }
        )
    }


    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        val fristShow = SharedPreferencesUtils.getSecurityBoolean(this, FIRSTLANGUAGE, true)
        if (fristShow) {//?????????
            SharedPreferencesUtils.saveSecurityBoolean(this, FIRSTLANGUAGE, false)
            //??????????????????????????????????????????app????????????
            val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0]
            } else {
                Resources.getSystem().configuration.locale
            }
            when (locale.country) {
                "CN" -> {
                    SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, CN)
                }
                "TW" -> {
                    SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, TW)
                }
                else -> {
                    SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, EN)
                }
            }
        } else {
            setLanguage()
        }
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getSecurityString(this, LANGUAGE, CN)) {
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