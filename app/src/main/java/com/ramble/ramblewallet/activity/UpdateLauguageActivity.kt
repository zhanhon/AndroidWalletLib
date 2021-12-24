package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityUpdateLauguageBinding
import com.ramble.ramblewallet.helper.getExtras

/**
 * 时间　: 2021/12/24 17:10
 * 作者　: potato
 * 描述　:
 */
class UpdateLauguageActivity : BaseActivity(), View.OnClickListener{
    private lateinit var binding: ActivityUpdateLauguageBinding
    private var type=1
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_lauguage)
        initView()
        initListener()
    }

    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.up_data_language)
        type=getExtras().getInt(ARG_PARAM1,1)
        when(type){
            1->{
                binding.checkHk.setImageResource(R.drawable.ic_delete_selected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_unselected)
            }
            2->{
                binding.checkHk.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_selected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_unselected)
            }
            3->{
                binding.checkHk.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_selected)
            }
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.llCny.setOnClickListener(this)
        binding.llHk.setOnClickListener(this)
        binding.llUsd.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.ll_hk -> {
                binding.checkHk.setImageResource(R.drawable.ic_delete_selected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_unselected)
            }
            R.id.ll_cny -> {
                binding.checkHk.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_selected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_unselected)
            }
            R.id.ll_usd -> {
                binding.checkHk.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkCny.setImageResource(R.drawable.ic_delete_unselected)
                binding.checkUsd.setImageResource(R.drawable.ic_delete_selected)
            }
            R.id.btn_next -> finish()
        }
    }
}