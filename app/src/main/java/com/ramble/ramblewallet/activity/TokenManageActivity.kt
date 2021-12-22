package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.TokenManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityTokenManageBinding

class TokenManageActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTokenManageBinding
    private lateinit var tokenManageAdapter: TokenManageAdapter
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token_manage)

        myDataBeans.add(MyDataBean(1, "TFT", ""))
        myDataBeans.add(MyDataBean(2, "WBTC", ""))
        myDataBeans.add(MyDataBean(3, "DAI", ""))
        myDataBeans.add(MyDataBean(4, "USDC", ""))
        myDataBeans.add(MyDataBean(5, "USDT", ""))
        myDataBeans.add(MyDataBean(6, "LINK", ""))
        myDataBeans.add(MyDataBean(7, "YFI", ""))
        myDataBeans.add(MyDataBean(8, "UNI", ""))
        tokenManageAdapter = TokenManageAdapter(myDataBeans, false)
        binding.rvTokenManageCurrency.adapter = tokenManageAdapter

        binding.ivBack.setOnClickListener(this)
        binding.ivDeleteToken.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_delete_token -> {
                tokenManageAdapter = TokenManageAdapter(myDataBeans, true)
                binding.rvTokenManageCurrency.adapter = tokenManageAdapter
            }
        }
    }
}