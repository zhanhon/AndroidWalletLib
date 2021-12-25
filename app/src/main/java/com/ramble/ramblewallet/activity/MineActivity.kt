package com.ramble.ramblewallet.activity


import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.constant.ARG_PARAM4
import com.ramble.ramblewallet.databinding.ActivityMineBinding
import com.ramble.ramblewallet.helper.start

/***
 * 我的管理页面
 */
class MineActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMineBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine)
        initView()
        initListener()
    }
    /***
     * View初始化
     */
    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.personal_management)
        binding.incManageWallet.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.manage_wallet)
        binding.incManageWallet.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_manag)
        binding.incAddressBook.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.address_book)
        binding.incAddressBook.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_address_book)
        binding.incTransactionQuery.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.transaction_query)
        binding.incTransactionQuery.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_transaction_query)
//        binding.incTransactionQuery.isVisible=false
        binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.multi_language)
        binding.incMultiLanguage.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_multi_language)
        binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_subtitle).text =  getString(R.string.language_simplified_chinese)
        binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.currency_unit)
        binding.incCurrencyUnit.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_currency)
        binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_subtitle).text =getString(R.string.cny_dollar)
        binding.incHelpFeedback.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.help_feedback)
        binding.incHelpFeedback.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_help_feedback)
        binding.incServiceAgreement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.service_agreement)
        binding.incServiceAgreement.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_service)
        binding.incPrivacyStatement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.privacy_statement)
        binding.incPrivacyStatement.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_privacy_statement)
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.about_us)
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_icon).setImageResource(R.drawable.ic_about)
        binding.incAboutUs.findViewById<ImageView>(R.id.iv_mine_next).visibility=View.INVISIBLE
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_subtitle).text ="v2.1"
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
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
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
                    it.putString(ARG_PARAM4, getString(R.string.service_agreement))
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
                    it.putString(ARG_PARAM4, getString(R.string.privacy_statement))
                })
            }
            R.id.inc_manage_wallet -> {
                start(ManageWalletActivity::class.java)
            }
            R.id.inc_address_book -> {
                start(AddressBookActivity::class.java)
            }
            R.id.inc_help_feedback->{
                start(HelpActivity::class.java)
            }
            R.id.inc_transaction_query->{
                start(TransactionQueryActivity::class.java)
            }
            R.id.inc_multi_language->{//多语言
                start(UpdateLauguageActivity::class.java, Bundle().also {
                    it.putInt(ARG_PARAM1, 2)
                })
            }
            R.id.inc_currency_unit->{//货币
                start(UpdateCurrencyActivity::class.java, Bundle().also {
                    it.putInt(ARG_PARAM1, 2)
                })
            }
        }
    }
}