package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.TokenManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.TokenManageBean
import com.ramble.ramblewallet.constant.SELECTED_TOKENS
import com.ramble.ramblewallet.databinding.ActivityTokenManageBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

class TokenManageActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTokenManageBinding
    private lateinit var tokenManageAdapter: TokenManageAdapter
    private var tokenManageBean: ArrayList<TokenManageBean> = arrayListOf()
    private var saveTokenList: ArrayList<String> = arrayListOf()
    private lateinit var saveTokenListJson: String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token_manage)
        saveTokenList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, SELECTED_TOKENS, ""),
            object : TypeToken<ArrayList<String>>() {}.type
        )

        saveTokenListJson = SharedPreferencesUtils.getString(this, SELECTED_TOKENS, "")
        //初始化推荐代币
        tokenManageBean.add(
            TokenManageBean(
                0,
                "TFT",
                if (saveTokenListJson.contains("TFT")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                1,
                "WBTC",
                if (saveTokenListJson.contains("WBTC")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                2,
                "DAI",
                if (saveTokenListJson.contains("DAI")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                3,
                "USDC",
                if (saveTokenListJson.contains("USDC")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                4,
                "USDT",
                if (saveTokenListJson.contains("USDT")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                5,
                "LINK",
                if (saveTokenListJson.contains("LINK")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                6,
                "YFI",
                if (saveTokenListJson.contains("YFI")) 1 else 0
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                7,
                "UNI",
                if (saveTokenListJson.contains("UNI")) 1 else 0
            )
        )
        tokenManageAdapter = TokenManageAdapter(tokenManageBean, false)
        binding.rvTokenManageCurrency.adapter = tokenManageAdapter

        tokenManageAdapter.addChildClickViewIds(R.id.iv_token_status)
        tokenManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (adapter.getItem(position) is TokenManageBean) {
                if ((adapter.getItem(position) as TokenManageBean).status == 0) {
                    tokenManageBean[position] = TokenManageBean(
                        position,
                        (adapter.getItem(position) as TokenManageBean).name,
                        1
                    )
                    tokenManageAdapter.notifyItemChanged(position)
                    saveTokenList.add((adapter.getItem(position) as TokenManageBean).name)
                } else {
                    tokenManageBean[position] = TokenManageBean(
                        position,
                        (adapter.getItem(position) as TokenManageBean).name,
                        0
                    )
                    tokenManageAdapter.notifyItemChanged(position)
                    saveTokenList.remove((adapter.getItem(position) as TokenManageBean).name)
                }
            }
            SharedPreferencesUtils.saveString(this, SELECTED_TOKENS, Gson().toJson(saveTokenList))
        }


        binding.ivBack.setOnClickListener(this)
        binding.ivDeleteToken.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_delete_token -> {
                tokenManageAdapter = TokenManageAdapter(tokenManageBean, true)
                binding.rvTokenManageCurrency.adapter = tokenManageAdapter
            }
        }
    }
}