package com.ramble.ramblewallet.activity


import android.app.AlertDialog
import android.content.Intent
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
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMineBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.utils.LanguageSetting
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

/***
 * 我的管理页面
 */
class MineActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMineBinding
    private lateinit var language: String
    private lateinit var currency: String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine)
        language = SharedPreferencesUtils.getString(this, LANGUAGE, CN)
        currency = SharedPreferencesUtils.getString(this, CURRENCY, RMB)
        initView()
        initListener()
    }

    /***
     * View初始化
     */
    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.personal_management)
        binding.incManageWallet.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.wallet_management)
        binding.incManageWallet.findViewById<ImageView>(R.id.iv_mine_icon)
            .setImageResource(R.drawable.ic_manag)
        binding.incAddressBook.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.address_book)
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
                HKD -> getString(R.string.hk_dollar)
                USD -> getString(R.string.usd_dollar)
                else -> getString(R.string.cny_dollar)

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
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_next).visibility = View.INVISIBLE
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_subtitle).text = "v2.1"
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
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> { //区分不同钱包进行跳转
                startActivity(Intent(this, MainETHActivity::class.java))
            }
            R.id.iv_mine_right -> {
                start(MessageCenterActivity::class.java)
            }
            R.id.inc_service_agreement -> {
                start(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, getString(R.string.service_agreement))
                    it.putString(
                        ARG_PARAM2,
                        "我是服务协议，我要打10个，我是服务协议，我要打10个，我是服务协议，我要打10个，我是服务协议，我要打10个，我是服务协议，我要打10个，我是服务协议，我要打10个"
                    )
                    it.putString(ARG_PARAM3, "1111111111111111111111111")
                    it.putInt(ARG_PARAM4, 4)
                })
            }
            R.id.inc_privacy_statement -> {
                start(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, getString(R.string.privacy_statement))
                    it.putString(
                        ARG_PARAM2,
                        "我是隐私声明，我要打10个，我是隐私声明，我要打10个，我是隐私声明，我要打10个，我是隐私声明，我要打10个，我是隐私声明，我要打10个，我是隐私声明，我要打10个"
                    )
                    it.putString(ARG_PARAM3, "1111111111111111111111111")
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
                start(HelpActivity::class.java)
            }
            R.id.inc_transaction_query -> {
                start(TransactionQueryActivity::class.java)
            }
            R.id.inc_multi_language -> {//多语言
                languageDialog()
            }
            R.id.inc_currency_unit -> {//货币
                currencyDialog()
            }
            R.id.inc_about_us -> {

            }
        }
    }

    private fun languageDialog() {
        var dialogLanguage = AlertDialog.Builder(this).create()
        dialogLanguage.show()
        val window: Window? = dialogLanguage.window
        if (window != null) {
            window.setContentView(R.layout.dialog_language)

            window.findViewById<View>(R.id.tv_language1).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, CN)
                setLanguage()
                dialogLanguage.dismiss()
                startActivity(Intent(this, MineActivity::class.java))
            }
            window.findViewById<View>(R.id.tv_language2).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, TW)
                setLanguage()
                dialogLanguage.dismiss()
                startActivity(Intent(this, MineActivity::class.java))
            }
            window.findViewById<View>(R.id.tv_language3).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, EN)
                setLanguage()
                dialogLanguage.dismiss()
                startActivity(Intent(this, MineActivity::class.java))
            }

            dialogTheme(window)
            dialogLanguage.show()
        }
    }

    private fun currencyDialog() {
        var dialogCurrency = AlertDialog.Builder(this).create()
        dialogCurrency.show()
        val window: Window? = dialogCurrency.window
        if (window != null) {
            window.setContentView(R.layout.dialog_currency)

            window.findViewById<View>(R.id.tv_language1).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, CURRENCY, RMB)
                setCurrency()
                dialogCurrency.dismiss()
            }
            window.findViewById<View>(R.id.tv_language2).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, CURRENCY, HKD)
                setCurrency()
                dialogCurrency.dismiss()
            }
            window.findViewById<View>(R.id.tv_language3).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, CURRENCY, USD)
                setCurrency()
                dialogCurrency.dismiss()
            }

            dialogTheme(window)
            dialogCurrency.show()
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
                LanguageSetting.setLanguage(applicationContext, 1)
            }
            TW -> {
                binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.language_traditional_chinese)
                LanguageSetting.setLanguage(applicationContext, 2)
            }
            EN -> {
                binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.language_english)
                LanguageSetting.setLanguage(applicationContext, 3)
            }
        }
    }

    private fun setCurrency() {
        when (SharedPreferencesUtils.getString(this, CURRENCY, RMB)) {
            RMB -> {
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.cny_dollar)
            }
            HKD -> {
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.hk_dollar)
            }
            USD -> {
                binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text =
                    getString(R.string.usd_dollar)
            }
        }
    }

}