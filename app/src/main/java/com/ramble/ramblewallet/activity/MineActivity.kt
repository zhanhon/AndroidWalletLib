package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.language.MultiLanguages
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMineBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.ApiResponse
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.TimeUtils.dateToLang

/***
 * 我的管理页面
 */
class MineActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMineBinding
    private lateinit var language: String
    private lateinit var currency: String
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var times = 0
    var redList: ArrayList<Page.Record> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine)
        initListener()
    }

    /***
     * View初始化
     */
    private fun initView() {
        language = SharedPreferencesUtils.getSecurityString(this, LANGUAGE, CN)
        currency = SharedPreferencesUtils.getSecurityString(this, CURRENCY, USD)
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
            getString(R.string.forgot_password)
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
        binding.clearText.text = getString(R.string.clear_cache)
        binding.incFingerPrint.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.fingerprint_transaction)
        binding.incFingerPrint.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_finger_print)
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
        binding.clearText.setOnClickListener(this)
        binding.incFingerPrint.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        initView()
        redPoint()
    }


    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {
        val lang = dateToLang(this)
        val redList: ArrayList<Page.Record> = arrayListOf()
        val records2: ArrayList<Page.Record> = arrayListOf()
        val req = Page.Req(1, 1000, lang)
        mApiService.getNotice(req.toApiRequest(noticeInfoUrl)).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    redPointMineHandle(it, redList, records2, lang)
                }
            }, {
            }
        )
    }

    private fun redPointMineHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>,
        lang: Int,

        ) {
        var records21 = records2
        it.data()?.let { data ->
            val read: ArrayList<Int> =
                if (SharedPreferencesUtils.getSecurityString(this, READ_ID_NEW, "").isNotEmpty()) {
                    Gson().fromJson(
                        SharedPreferencesUtils.getSecurityString(this, READ_ID_NEW, ""),
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
            records21 = if (SharedPreferencesUtils.getSecurityString(
                    this,
                    STATION_INFO,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, STATION_INFO, ""),
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
        lang: Int,
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
                if (item.lang == lang) {
                    if (SharedPreferencesUtils.getSecurityString(
                            this,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        val read: ArrayList<Int> = Gson().fromJson(
                            SharedPreferencesUtils.getSecurityString(this, READ_ID, ""),
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
            R.id.iv_back -> {
                finish()
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
                start(WalletManageActivity::class.java)
            }
            R.id.inc_address_book -> {
                start(AddressBookActivity::class.java, Bundle().also {
                    it.putBoolean(ARG_PARAM1, false)
                })
            }
            R.id.inc_help_feedback -> {
                startActivity(Intent(this, MMWebViewActivity::class.java).apply {
                    putExtra(ARG_TITLE, getString(R.string.forgot_password))
                    if (getLanguageType() == 1){//中文
                        data = Uri.parse(baseChatServerUrl + FORGOT_PASSWORD_CN)
                    }else{
                        data = Uri.parse(baseChatServerUrl + FORGOT_PASSWORD_EN)
                    }
                })
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
            R.id.clear_text -> {
                ToastUtils.showToastFree(this, getString(R.string.clear_suc))
            }
        }
    }

    fun getLanguageType(): Int{
        val languages: Int = SharedPreferencesUtils.getLanguages()
        if (languages == 0) {
            val locale = MultiLanguages.getAppLanguage()
            if (locale.language.contains("zh")) {
                //中文
                return 1
            } else {
                return 2
            }
        } else {
            return languages
        }
    }

    /***
     *切换语言重新上传地址
     */
    private fun skipConfirmHandle() {
        val walletInfo = SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "")
        if (walletInfo.isNotEmpty()) {
            saveWalletList = Gson().fromJson(walletInfo, object : TypeToken<ArrayList<Wallet>>() {}.type)
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
     *切换语言重新上传地址网络请求
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
                    if (times < 3) {
                        putAddress(detailsList)
                        times++
                    }
                }
            }, {
                it.printStackTrace()
            }
        )
    }

    private fun languageDialog() {
        showLanguageDialog(this, cnListener = {
            SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, CN)
            setLanguage()
            onResume()
        }, twListener = {
            SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, TW)
            setLanguage()
            onResume()
        }, enListener = {
            SharedPreferencesUtils.saveSecurityString(this, LANGUAGE, EN)
            setLanguage()
            onResume()
        })
    }

    private fun currencyDialog() {
        showCurrencyDialog(this, cnyListener = {
            SharedPreferencesUtils.saveSecurityString(this, CURRENCY, CNY)
            binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = CNY
        }, hkdListener = {
            SharedPreferencesUtils.saveSecurityString(this, CURRENCY, HKD)
            binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = HKD
        }, usdListener = {
            SharedPreferencesUtils.saveSecurityString(this, CURRENCY, USD)
            binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text = USD
        })
    }


    private fun setLanguage() {
        when (SharedPreferencesUtils.getSecurityString(this, LANGUAGE, CN)) {
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