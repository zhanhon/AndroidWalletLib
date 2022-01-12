package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MyAssetsAdapter
import com.ramble.ramblewallet.adapter.RecommendTokenAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.databinding.ActivityAddTokenBinding
import com.ramble.ramblewallet.item.AddTokenItem
import com.ramble.ramblewallet.item.Help
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem

class AddTokenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddTokenBinding

    private val adapter = RecyclerAdapter()
    private var myDataBeansMyAssets: ArrayList<MyDataBean> = arrayListOf()
    private  val recommendTokenAdapter=RecyclerAdapter()
    private var myDataBeansRecommendToken: ArrayList<StoreInfo> = arrayListOf()
    private var isSpread = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_token)
        initView()
        initListener()
    }

    private fun initView() {
        myDataBeansMyAssets.add(MyDataBean(1, "TFT", ""))
        myDataBeansMyAssets.add(MyDataBean(2, "WBTC", ""))
        myDataBeansMyAssets.add(MyDataBean(3, "DAI", ""))
        myDataBeansMyAssets.add(MyDataBean(4, "USDC", ""))
        myDataBeansMyAssets.add(MyDataBean(5, "USDT", ""))
        myDataBeansMyAssets.add(MyDataBean(6, "LINK", ""))
        myDataBeansMyAssets.add(MyDataBean(7, "YFI", ""))
        myDataBeansMyAssets.add(MyDataBean(8, "UNI", ""))
       // myAssetsAdapter = MyAssetsAdapter(myDataBeansMyAssets)
        LinearLayoutManager(this).apply {
            binding.rvMyTokenCurrency.layoutManager = this
        }
        binding.rvMyTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_04)
                .build()
        )
        binding.rvMyTokenCurrency.adapter = adapter


        LinearLayoutManager(this).apply {
            binding.rvTokenManageCurrency.layoutManager = this
        }
        binding.rvTokenManageCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_04)
                .build()
        )
        binding.rvTokenManageCurrency.adapter = recommendTokenAdapter
        var r1=StoreInfo()
        r1.name="TFT"
        var r2=StoreInfo()
        r2.name="WBTC"
        var r3=StoreInfo()
        r3.name="DAI"
        var r4=StoreInfo()
        r4.name="USDC"
        var r5=StoreInfo()
        r5.name="USDT"
        var r6=StoreInfo()
        r6.name="LINK"
        var r7=StoreInfo()
        r7.name="YFI"
        var r8=StoreInfo()
        r8.name="UNI"
        myDataBeansRecommendToken.add(r1)
        myDataBeansRecommendToken.add(r2)
        myDataBeansRecommendToken.add(r3)
        myDataBeansRecommendToken.add(r4)
        myDataBeansRecommendToken.add(r5)
        myDataBeansRecommendToken.add(r6)
        myDataBeansRecommendToken.add(r7)
        myDataBeansRecommendToken.add(r8)
        ArrayList<SimpleRecyclerItem>().apply {
            myDataBeansRecommendToken.forEach { o -> this.add(AddTokenItem(o)) }
            recommendTokenAdapter.addAll(this.toList())
        }

    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivTokenManage.setOnClickListener(this)
        binding.llMyTokenCurrencyConstriction.setOnClickListener(this)
        binding.search.setOnClickListener(this)
    }

    @SuppressLint("CheckResult")
    private fun searchData(condition:String){
        var req= StoreInfo.Req()
        req.condition=condition
        req.symbol="ETH"
        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                it.data()?.let { data ->
                    ArrayList<SimpleRecyclerItem>().apply {
                        data.forEach { o -> this.add(AddTokenItem(o)) }
                        recommendTokenAdapter.addAll(this.toList())
                    }
                }

            } else {
                println("==================>getTransferInfo1:${it.message()}")
            }
        }, {
            println("==================>getTransferInfo1:${it.printStackTrace()}")
        }
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.search->{//搜索代币
                if (binding.etSearch.text.toString().isNullOrEmpty())return
                searchData(binding.etSearch.text.toString())
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