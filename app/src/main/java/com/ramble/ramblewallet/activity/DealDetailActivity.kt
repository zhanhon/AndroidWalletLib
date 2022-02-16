package com.ramble.ramblewallet.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityDealDetailBinding
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.TimeUtils


/**
 * 时间　: 2021/12/20 14:42
 * 作者　: potato
 * 描述　: 交易详情
 */
class DealDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDealDetailBinding
    private var trans: QueryTransferRecord.Record? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_detail)
        initView()
        initListener()

    }


    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.details)
        trans = getExtras().getSerializable(ARG_PARAM1) as QueryTransferRecord.Record?
        binding.minerFees.text = trans?.miner
        binding.minerFeesUsd.text = trans?.minerUnit
        binding.tvToAddress.text = trans?.fromAddress
        binding.tvFromAddress.text = trans?.toAddress
        binding.mark.text = trans?.remark
        binding.transactionCode.text = trans?.txHash
        binding.blockNumber.text = trans?.blockNumber
        binding.tvStatus.text = trans?.statusDesc
        binding.tvTime.text = TimeUtils.dateToWeek(trans?.createTime) + "  " + trans?.createTime
        when (trans?.status) {
            1 -> {
                binding.ivStatus.setImageResource(R.drawable.ic_transing)
            }
            2 -> {
                binding.ivStatus.setImageResource(R.drawable.ic_success)
                when (trans?.transferType) {
                    2 -> {//转入
                        binding.tvMoneyCount.text = "+" + trans?.amount
                        binding.tvMoneyType.text = trans?.unit
                        binding.tvMoneyCount.setTextColor(Color.parseColor("#009474"))
                        binding.tvMoneyType.setTextColor(Color.parseColor("#009474"))
                    }
                    1 -> {//转出
                        binding.tvMoneyCount.text = "-" + trans?.amount
                        binding.tvMoneyType.text = trans?.unit
                        binding.tvMoneyCount.setTextColor(Color.parseColor("#e11334"))
                        binding.tvMoneyType.setTextColor(Color.parseColor("#e11334"))
                    }
                }
            }
            3 -> {
                binding.ivStatus.setImageResource(R.drawable.ic_fail)
                binding.tvMoneyCount.text = trans?.amount
                binding.tvMoneyType.text = trans?.unit
                binding.tvMoneyCount.setTextColor(Color.parseColor("#333333"))
                binding.tvMoneyType.setTextColor(Color.parseColor("#333333"))
            }
        }

    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
        binding.addCopy.setOnClickListener(this)
        binding.payCopy.setOnClickListener(this)
        binding.numberCopy.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_mine_right -> {

            }
            R.id.add_copy -> {
                ClipboardUtils.copy(binding.tvFromAddress.text.toString())
            }
            R.id.pay_copy -> {
                ClipboardUtils.copy(binding.tvToAddress.text.toString())
            }
            R.id.number_copy -> {
                ClipboardUtils.copy(binding.transactionCode.text.toString())
            }
            R.id.btn_detail -> {
                var intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse("https://etherscan.io/tx/${trans?.txHash}")
                startActivity(intent)
            }
        }
    }
}