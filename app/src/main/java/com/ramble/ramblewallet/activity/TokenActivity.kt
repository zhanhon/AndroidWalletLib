package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.TokenAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityTokenBinding

class TokenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTokenBinding
    private lateinit var tokenAdapter: TokenAdapter
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var tokenTitle: String

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token)
        tokenTitle = intent.getStringExtra(ARG_PARAM1)

        binding.tvTokenTitle.text = tokenTitle


        myDataBeans.add(MyDataBean(1, "TFT", ""))
        myDataBeans.add(MyDataBean(2, "WBTC", ""))
        myDataBeans.add(MyDataBean(3, "DAI", ""))
        myDataBeans.add(MyDataBean(4, "USDC", ""))
        myDataBeans.add(MyDataBean(5, "USDT", ""))
        myDataBeans.add(MyDataBean(6, "LINK", ""))
        myDataBeans.add(MyDataBean(7, "YFI", ""))
        myDataBeans.add(MyDataBean(8, "UNI", ""))
        tokenAdapter = TokenAdapter(myDataBeans)
        binding.rvTokenCurrency.adapter = tokenAdapter

        binding.ivBack.setOnClickListener(this)
        binding.ivTokenRight.setOnClickListener(this)
        binding.ivAddToken.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_token_right -> {
                startActivity(Intent(this, TokenManageActivity::class.java))
            }
            R.id.iv_add_token -> {
                startActivity(Intent(this, AddTokenActivity::class.java))
            }
        }
    }
}