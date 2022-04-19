package com.ramble.ramblewallet.activity


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.BuildConfig
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMineBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.*
import com.ramble.ramblewallet.update.AppVersion
import com.ramble.ramblewallet.update.UpdateUtils
import com.ramble.ramblewallet.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/***
 * 我的管理页面
 */
class MineActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMineBinding
    private lateinit var language: String
    private lateinit var currency: String
    private lateinit var walletSelleted: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var times = 0
    var redList: ArrayList<Page.Record> = arrayListOf()
    var records2: ArrayList<Page.Record> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine)
        initListener()
    }

    /***
     * View初始化
     */
    private fun initView() {
        language = SharedPreferencesUtils.getString(this, LANGUAGE, CN)
        currency = SharedPreferencesUtils.getString(this, CURRENCY, USD)
        binding.tvMineTitle.text = getString(R.string.personal_management)
        binding.incManageWallet.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.wallet_management)
        binding.incManageWallet.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_manag)
        binding.incAddressBook.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.address_page)
        binding.incAddressBook.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_address_book)
        binding.incTransactionQuery.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.transaction_query)
        binding.incTransactionQuery.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_transaction_query)
        binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.multi_language)
        binding.incMultiLanguage.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_multi_language)
        binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
            when (language) {
                TW -> getString(R.string.language_traditional_chinese)
                EN -> getString(R.string.language_english)
                else -> getString(R.string.language_simplified_chinese)
            }
        binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.currency_unit)
        binding.incCurrencyUnit.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_currency)
        binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text =
            when (currency) {
                HKD -> "HKD"
                USD -> "USD"
                else -> "CNY"

            }
        binding.incHelpFeedback.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.help_feedback)
        binding.incHelpFeedback.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_help_feedback)
        binding.incServiceAgreement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.service_agreement)
        binding.incServiceAgreement.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_service)
        binding.incPrivacyStatement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.privacy_statement)
        binding.incPrivacyStatement.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_privacy_statement)
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.about_us)
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_about)
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_next)
            .setImageResource(R.drawable.ic_mine_cycle)
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_subtitle).text =
            BuildConfig.VERSION_NAME
        binding.clearText.text = getString(R.string.clear_cache)
        binding.incFingerPrint.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.fingerprint_transaction)
        binding.incFingerPrint.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_finger_print)
    }


    @SuppressLint("CheckResult")
    private fun checkVersion() {
        mApiService.appVersion(AppVersion.Req().toApiRequest(faqInfoUrl)).subscribe({
            if (it.code() == 1) {
                GlobalScope.launch {
//                    delay(800)
                    checkAppVersion(it.data()!!)
                }

            }
        }, {
        })
    }

    private fun checkAppVersion(version: AppVersion) {
        if (version.isNeedUpdate == 1) {
            UpdateUtils().checkUpdate(version, false)
//            if (version.isForceUpdate == 1) {
////                start(ForceUpdateActivity::class.java)
//            } else if (version.isPopPrompt == 1 && MMKVManager.getVersionIgnore() != version.version) {
//                UpdateUtils().checkUpdate(version,false)
//            }
        }
    }

    /****
     * 事件监听
     */
    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
        binding.incServiceAgreement.setOnClickListener(this)
        binding.incPrivacyStatement.setOnClickListener(this)
        binding.incManageWallet.setOnClickListener(this)
        binding.incAddressBook.setOnClickListener(this)
        binding.incHelpFeedback.setOnClickListener(this)
        binding.incTransactionQuery.setOnClickListener(this)
        binding.incMultiLanguage.setOnClickListener(this)
        binding.incCurrencyUnit.setOnClickListener(this)
        binding.incAboutUs.setOnClickListener(this)
        binding.clearText.setOnClickListener(this)
        binding.incFingerPrint.setOnClickListener(this)
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_next).setOnClickListener {
            checkVersion()
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        redPoint()
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
    }


    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {
        var lang = when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                1
            }
            TW -> {
                2
            }
            else -> {
                3
            }
        }
        var redList: ArrayList<Page.Record> = arrayListOf()
        var records2: ArrayList<Page.Record> = arrayListOf()
        var req = Page.Req(1, 1000, lang)
        mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    redPointMineHandle(it, redList, records2, lang)
                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }

            }, {

                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )
    }

    private fun redPointMineHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>,
        lang: Int

    ) {
        var records21 = records2
        it.data()?.let { data ->
            println("==================>getTransferInfo:${data}")
            var read: ArrayList<Int> =
                if (SharedPreferencesUtils.getString(this, READ_ID_NEW, "").isNotEmpty()) {
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(this, READ_ID_NEW, ""),
                        object : TypeToken<ArrayList<Int>>() {}.type
                    )
                } else {
                    arrayListOf()
                }
            data.records.forEach { item ->
                if (read.contains(item.id)
                ) {
                    item.isRead = 1
                } else {
                    item.isRead = 0
                    redList.add(item)
                }
            }
            records21 = if (SharedPreferencesUtils.getString(
                    this,
                    STATION_INFO,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, STATION_INFO, ""),
                    object : TypeToken<ArrayList<Page.Record>>() {}.type
                )

            } else {
                arrayListOf()
            }
            redPointMineHandleSub(records21, redList, lang)
            if (redList.isNotEmpty()) {
                binding.ivMineRight.setImageResource(R.drawable.ic_bell_unread)
            } else {
                binding.ivMineRight.setImageResource(R.drawable.ic_bell_read)
            }
        }
    }

    private fun redPointMineHandleSub(
        records21: ArrayList<Page.Record>,
        redList: ArrayList<Page.Record>,
        lang: Int
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
                if (item.lang == lang) {
                    if (SharedPreferencesUtils.getString(
                            this,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        var read: ArrayList<Int> = Gson().fromJson(
                            SharedPreferencesUtils.getString(this, READ_ID, ""),
                            object : TypeToken<ArrayList<Int>>() {}.type
                        )
                        if (read.contains(item.id)
                        ) {
                            item.isRead = 1
                        } else {
                            item.isRead = 0
                            redList.add(item)
                        }
                    } else {
                        item.isRead = 0
                        redList.add(item)
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        when (walletSelleted.walletType) {
            1 -> {
                start(MainETHActivity::class.java)
            }
            2 -> {
                start(MainTRXActivity::class.java)
            }
            3 -> {
                start(MainBTCActivity::class.java)
            }
        }
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_PUSH_MSG -> {
                onResume()
            }
            else -> return
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> { //区分不同钱包进行跳转
                when (walletSelleted.walletType) {
                    1 -> {
                        start(MainETHActivity::class.java)
                    }
                    2 -> {
                        start(MainTRXActivity::class.java)
                    }
                    3 -> {
                        start(MainBTCActivity::class.java)
                    }
                }
            }
            R.id.iv_mine_right -> {
                start(MessageCenterActivity::class.java)
            }
            R.id.inc_service_agreement -> {
                start(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, getString(R.string.service_agreement))
                    it.putString(ARG_PARAM2, "")
                    it.putString(ARG_PARAM3, "")
                    it.putInt(ARG_PARAM4, 4)
                })
            }
            R.id.inc_privacy_statement -> {
                start(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, getString(R.string.privacy_statement))
                    it.putString(ARG_PARAM2, "")
                    it.putString(ARG_PARAM3, "")
                    it.putInt(ARG_PARAM4, 3)
                })
            }
            R.id.inc_manage_wallet -> {
                start(WalletManageActivity::class.java, Bundle().also {
                    it.putBoolean(ARG_PARAM1, true)
                })
            }
            R.id.inc_address_book -> {
                start(AddressBookActivity::class.java, Bundle().also {
                    it.putBoolean(ARG_PARAM1, false)
                })
            }
            R.id.inc_help_feedback -> {
                start(HelpActivity::class.java)
            }
            R.id.inc_transaction_query -> {
                start(TransactionQueryActivity::class.java)
            }
            R.id.inc_finger_print -> {
                start(FingerPrintActivity::class.java)
            }
            R.id.inc_multi_language -> {//多语言
                languageDialog()
            }
            R.id.inc_currency_unit -> {//货币
                currencyDialog()
            }
            R.id.inc_about_us -> {
//                start(HelpActivity::class.java)
            }
            R.id.clear_text -> {
                ToastUtils.showToastFree(this, getString(R.string.clear_suc))
            }
        }
    }

    /***
     *切换语言重新上传地址
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
     *切换语言重新上传地址网络请求
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

    private fun languageDialog() {
        var dialogLanguage = AlertDialog.Builder(this).create()
        dialogLanguage.show()
        val window: Window? = dialogLanguage.window
        if (window != null) {
            window.setContentView(R.layout.dialog_language)

            window.findViewById<View>(R.id.tv_language1).setOnClickListener {
                SharedPreferencesUtils.saveString(this, LANGUAGE, CN)
                setLanguage()
                onResume()
                dialogLanguage.dismiss()

            }
            window.findViewById<View>(R.id.tv_language2).setOnClickListener {
                SharedPreferencesUtils.saveString(this, LANGUAGE, TW)
                setLanguage()
                onResume()
                dialogLanguage.dismiss()

            }
            window.findViewById<View>(R.id.tv_language3).setOnClickListener {
                SharedPreferencesUtils.saveString(this, LANGUAGE, EN)
                setLanguage()
                onResume()
                dialogLanguage.dismiss()

            }

            dialogTheme(window)
//            dialogLanguage.show()
        }
    }

    private fun currencyDialog() {
        var dialogCurrency = AlertDialog.Builder(this).create()
        dialogCurrency.show()
        val window: Window? = dialogCurrency.window
        if (window != null) {
            window.setContentView(R.layout.dialog_currency)

            window.findViewById<View>(R.id.tv_language1).setOnClickListener {
                SharedPreferencesUtils.saveString(this, CURRENCY, CNY)
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = CNY
                dialogCurrency.dismiss()
            }
            window.findViewById<View>(R.id.tv_language2).setOnClickListener {
                SharedPreferencesUtils.saveString(this, CURRENCY, HKD)
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = HKD
                dialogCurrency.dismiss()
            }
            window.findViewById<View>(R.id.tv_language3).setOnClickListener {
                SharedPreferencesUtils.saveString(this, CURRENCY, USD)
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = USD
                dialogCurrency.dismiss()
            }

            dialogTheme(window)
//            dialogCurrency.show()
        }
    }

    private fun dialogTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.language_simplified_chinese)
                LanguageSetting.setLanguage(this, 1)
            }
            TW -> {
                binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.language_traditional_chinese)
                LanguageSetting.setLanguage(this, 2)
            }
            EN -> {

                binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.language_english)
                LanguageSetting.setLanguage(this, 3)
            }
        }
        times = 0
        skipConfirmHandle()
    }
}