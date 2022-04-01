package com.ramble.ramblewallet.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityDealDetailBinding
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.item.AddressTansItem
import com.ramble.ramblewallet.item.AddressTansRuItem
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.TimeUtils
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem


/**
 * 时间　: 2021/12/20 14:42
 * 作者　: potato
 * 描述　: 交易详情
 */
class DealDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDealDetailBinding
    private var trans: QueryTransferRecord.Record? = null
    private val adapter = RecyclerAdapter()
    private val adapterRu = RecyclerAdapter()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_detail)
        initView()
        initListener()

    }


    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.details)
        trans = getExtras().getSerializable(ARG_PARAM1) as QueryTransferRecord.Record?
        binding.rvCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        LinearLayoutManager(this).apply {
            binding.rvCurrency.layoutManager = this
        }
        binding.rvCurrency.adapter = adapter

        binding.rvCurrencyAddress.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        LinearLayoutManager(this).apply {
            binding.rvCurrencyAddress.layoutManager = this
        }
        binding.rvCurrencyAddress.adapter = adapterRu

        binding.minerFees.text = trans?.miner
        binding.minerFeesUsd.text = trans?.minerUnit

        when (trans?.addressType) {
            3 -> {
                ArrayList<SimpleRecyclerItem>().apply {
                    trans?.inputs?.forEach { o -> this.add(AddressTansItem(o)) }
                    adapter.replaceAll(this.toList())
                }

                ArrayList<SimpleRecyclerItem>().apply {
                    trans?.outputs?.forEach { o -> this.add(AddressTansRuItem(o)) }
                    adapterRu.replaceAll(this.toList())
                }

            }
            else -> {
                ArrayList<SimpleRecyclerItem>().apply {
                    var o = QueryTransferRecord.InRecord()
                    o.address = trans?.fromAddress.toString()
                    this.add(AddressTansItem(o))
                    adapter.replaceAll(this.toList())
                }

                ArrayList<SimpleRecyclerItem>().apply {
                    var o = QueryTransferRecord.InRecord()
                    o.address = trans?.toAddress.toString()
                    this.add(AddressTansRuItem(o))
                    adapterRu.replaceAll(this.toList())
                }

            }
        }

//        binding.mark.text = trans?.remark
        binding.transactionCode.text = trans?.txHash
        binding.blockNumber.text = trans?.blockNumber
        binding.tvStatus.text = trans?.statusDesc
        binding.tvTime.text =
            TimeUtils.dateToWeek(this, trans?.createTime) + "  " + trans?.createTime
        when (trans?.status) {
            1 -> {
                binding.ivStatus.setImageResource(R.drawable.ic_transing)
                initTransView()
            }
            2 -> {
                binding.ivStatus.setImageResource(R.drawable.ic_success)
                initTransView()
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
    private fun initTransView(){
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

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
        binding.numberCopy.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)
        adapter.onClickListener = this
        adapterRu.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.add_copy -> {
                val itemBean = AdapterUtils.getHolder(v).getItem<AddressTansRuItem>().data
                ClipboardUtils.copy(itemBean.address, this)
            }
            R.id.pay_copy -> {
                val itemBean = AdapterUtils.getHolder(v).getItem<AddressTansItem>().data
                ClipboardUtils.copy(itemBean.address, this)
            }
            R.id.number_copy -> {
                ClipboardUtils.copy(binding.transactionCode.text.toString(), this)
            }
            R.id.btn_detail -> {
              var   urlString = when (trans?.addressType){
                    1->{
                        "https://etherscan.io/tx/${trans?.txHash}"
                    }
                    2->{
                        "https://tronscan.org/#/transaction/${trans?.txHash}"
                    }
                    3->{
                        "https://btcscan.org/tx/${trans?.txHash}"
                    }
                    else->""
                }
                var intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse(urlString)
                startActivity(intent)
            }
        }
    }
}