package com.ramble.ramblewallet.fragment

import android.app.Activity
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.CURRENCY_TRAN
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.TopNoticeDialogBinding
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import razerdp.basepopup.BasePopupWindow

/**
 * 时间　: 2022/3/18 10:37
 * 作者　: potato
 * 描述　:历史记录上部弹窗
 */
class TransPopupRecord(val mContext: Activity) : BasePopupWindow(mContext) {

    private lateinit var binding: TopNoticeDialogBinding

    override fun onCreateContentView(): View {
        val view = createPopupById(R.layout.top_notice_dialog)
        binding = DataBindingUtil.bind(view.findViewById(R.id.trans_item))!!
        var wallet: Wallet = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(mContext, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        var currencyUnit = SharedPreferencesUtils.getSecurityString(mContext, CURRENCY_TRAN, "")
        if (currencyUnit.isNotEmpty()) {
            when (currencyUnit) {
                "ETH" -> {
                    binding.ivEth.visibility = View.VISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                "BTC" -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.VISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                "TRX" -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.VISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                "SOL" -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.VISIBLE
                }
            }
        } else {
            when (wallet.walletType) {
                1 -> {
                    binding.ivEth.visibility = View.VISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                2 -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.VISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                3 -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.VISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.INVISIBLE
                }
                4 -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                    binding.ivSola.visibility = View.VISIBLE
                }
            }
        }

        binding.rlEth.setOnClickListener {
            SharedPreferencesUtils.saveSecurityString(mContext, CURRENCY_TRAN, "ETH")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 1)
            dismiss()
        }
        binding.rlBtc.setOnClickListener {
            SharedPreferencesUtils.saveSecurityString(mContext, CURRENCY_TRAN, "BTC")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 0)
            dismiss()
        }
        binding.rlTrx.setOnClickListener {
            SharedPreferencesUtils.saveSecurityString(mContext, CURRENCY_TRAN, "TRX")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 2)
            dismiss()
        }
        binding.rlSola.setOnClickListener {
            SharedPreferencesUtils.saveSecurityString(mContext, CURRENCY_TRAN, "SOL")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 3)
            dismiss()
        }
        return view
    }

    override fun onDismiss() {
        super.onDismiss()
        RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 5)
    }
}