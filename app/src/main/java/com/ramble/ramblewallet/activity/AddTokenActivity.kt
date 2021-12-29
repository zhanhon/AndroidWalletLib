package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MyAssetsAdapter
import com.ramble.ramblewallet.adapter.RecommendTokenAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityAddTokenBinding

class AddTokenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddTokenBinding
    private lateinit var myAssetsAdapter: MyAssetsAdapter
    private var myDataBeansMyAssets: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var recommendTokenAdapter: RecommendTokenAdapter
    private var myDataBeansRecommendToken: ArrayList<MyDataBean> = arrayListOf()
    private var isSpread = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_token)
        binding.scrollAddToken.post { binding.scrollAddToken.fullScroll(ScrollView.FOCUS_UP) } //初始值

        myDataBeansMyAssets.add(MyDataBean(1, "TFT", ""))
        myDataBeansMyAssets.add(MyDataBean(2, "WBTC", ""))
        myDataBeansMyAssets.add(MyDataBean(3, "DAI", ""))
        myDataBeansMyAssets.add(MyDataBean(4, "USDC", ""))
        myDataBeansMyAssets.add(MyDataBean(5, "USDT", ""))
        myDataBeansMyAssets.add(MyDataBean(6, "LINK", ""))
        myDataBeansMyAssets.add(MyDataBean(7, "YFI", ""))
        myDataBeansMyAssets.add(MyDataBean(8, "UNI", ""))
        myAssetsAdapter = MyAssetsAdapter(myDataBeansMyAssets)
        binding.rvMyTokenCurrency.adapter = myAssetsAdapter


        myDataBeansRecommendToken.add(MyDataBean(1, "TFT", ""))
        myDataBeansRecommendToken.add(MyDataBean(2, "WBTC", ""))
        myDataBeansRecommendToken.add(MyDataBean(3, "DAI", ""))
        myDataBeansRecommendToken.add(MyDataBean(4, "USDC", ""))
        myDataBeansRecommendToken.add(MyDataBean(5, "USDT", ""))
        myDataBeansRecommendToken.add(MyDataBean(6, "LINK", ""))
        myDataBeansRecommendToken.add(MyDataBean(7, "YFI", ""))
        myDataBeansRecommendToken.add(MyDataBean(8, "UNI", ""))
        recommendTokenAdapter = RecommendTokenAdapter(myDataBeansRecommendToken)
        binding.rvTokenManageCurrency.adapter = recommendTokenAdapter


        binding.ivBack.setOnClickListener(this)
        binding.ivTokenManage.setOnClickListener(this)
        binding.llMyTokenCurrencyConstriction.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_token_manage -> {
                startActivity(Intent(this, TokenManageActivity::class.java))
            }
            R.id.ll_my_token_currency_constriction -> {
                if (isSpread) {
                    binding.rvMyTokenCurrency.visibility = View.GONE
                    binding.tvMyTokenCurrencyConstriction.text = getString(R.string.spread)
                    binding.ivMyTokenCurrencyConstriction.setBackgroundResource(R.drawable.vector_solid_triangle_down)
                    isSpread = false
                } else {
                    binding.rvMyTokenCurrency.visibility = View.VISIBLE
                    binding.tvMyTokenCurrencyConstriction.text = getString(R.string.pack_up)
                    binding.ivMyTokenCurrencyConstriction.setBackgroundResource(R.drawable.vector_solid_triangle_up)
                    isSpread = true
                }
            }
        }
    }
}