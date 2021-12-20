package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityDealDetailBinding


/**
 * 时间　: 2021/12/20 14:42
 * 作者　: potato
 * 描述　: 交易详情
 */
class DealDetailActivity  : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDealDetailBinding
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_detail)
        initView()
        initListener()

    }



    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.transaction_details)

    }
    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_mine_right -> {

            }
        }
    }
}