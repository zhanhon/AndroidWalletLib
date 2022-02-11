package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
import com.ramble.ramblewallet.databinding.ActivityTokenBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.item.AddTokenItem
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem

/***
 * 时间　: 2022/1/13 8:45
 * 作者　: potato
 * 描述　: 主链代币
 */
class TokenActivity : BaseActivity(), View.OnClickListener {
    private val adapter = RecyclerAdapter()
    private lateinit var binding: ActivityTokenBinding
    private var myStores: ArrayList<StoreInfo> = arrayListOf()
    private lateinit var tokenTitle: String

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token)
        tokenTitle = intent.getStringExtra(ARG_PARAM1)
        initView()
        initListener()


    }

    private fun initView() {
        binding.tvTokenTitle.text = tokenTitle + getString(R.string.token)

        LinearLayoutManager(this).apply {
            binding.rvTokenCurrency.layoutManager = this
        }
        binding.rvTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08).leftMargin(R.dimen.dp_54)
                .build()
        )
        binding.rvTokenCurrency.adapter = adapter
        if (SharedPreferencesUtils.getString(
                this,
                TOKEN_INFO_NO,
                ""
            ).isNotEmpty()
        ) {
            myStores = Gson().fromJson(
                SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
                object : TypeToken<ArrayList<StoreInfo>>() {}.type
            )
        } else {
            var r1 = StoreInfo()
            r1.id = 2396
            r1.symbol = "WETH"
            r1.contractAddress="0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
            var r2 = StoreInfo()
            r2.id = 3717
            r2.symbol = "WBTC"
            r2.contractAddress="0x2260fac5e5542a773aa44fbcfedf7c193bc2c599"
            var r3 = StoreInfo()
            r3.id = 4943
            r3.symbol = "DAI"
            r3.contractAddress="0x6b175474e89094c44da98b954eedeac495271d0f"
            var r4 = StoreInfo()
            r4.symbol = "USDC"
            r4.id = 3408
            r4.contractAddress="0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
            var r5 = StoreInfo()
            r5.symbol = "USDT"
            r5.id = 825
            r5.contractAddress="0xdac17f958d2ee523a2206206994597c13d831ec7"
            var r6 = StoreInfo()
            r6.symbol = "LINK"
            r6.id = 1975
            r6.contractAddress="0x514910771af9ca656af840dff83e8264ecf986ca"
            var r7 = StoreInfo()
            r7.symbol = "YFI"
            r7.id = 5864
            r7.contractAddress="0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e"
            var r8 = StoreInfo()
            r8.symbol = "UNI"
            r8.id = 7083
            r8.contractAddress="0x1f9840a85d5af5bf1d1762f925bdaddc4201f984"
            myStores.add(r1)
            myStores.add(r2)
            myStores.add(r3)
            myStores.add(r4)
            myStores.add(r5)
            myStores.add(r6)
            myStores.add(r7)
            myStores.add(r8)
            SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myStores))
        }
        ArrayList<SimpleRecyclerItem>().apply {
            myStores.forEach { o -> this.add(AddTokenItem(o as StoreInfo)) }
            adapter.addAll(this.toList())
        }

    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivTokenRight.setOnClickListener(this)
        binding.ivAddToken.setOnClickListener(this)
        adapter.onClickListener = this
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADD_TOKEN, Pie.EVENT_MINUS_TOKEN -> {
                myStores = arrayListOf()
                adapter.clear()
                myStores = Gson().fromJson(
                    SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<StoreInfo>>() {}.type
                )
                var isOpen = false
                myStores.forEach {
                    if (it.id == event.data<StoreInfo>().id) {
                        it.isMyToken = event.data<StoreInfo>().isMyToken
                        isOpen = true
                    }
                }
                if (!isOpen) {
                    myStores.add(event.data())
                }

                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myStores))
                ArrayList<SimpleRecyclerItem>().apply {
                    myStores.forEach { o -> this.add(AddTokenItem(o as StoreInfo)) }
                    adapter.addAll(this.toList())
                }
            }
            Pie.EVENT_DEL_TOKEN -> {
                myStores = arrayListOf()
                adapter.clear()
                myStores = Gson().fromJson(
                    SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<StoreInfo>>() {}.type
                )

                ArrayList<SimpleRecyclerItem>().apply {
                    myStores.forEach { o -> this.add(AddTokenItem(o as StoreInfo)) }
                    adapter.addAll(this.toList())
                }
            }

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_token_right -> {
                start(TokenManageActivity::class.java)
            }
            R.id.iv_add_token -> {
                start(AddTokenActivity::class.java)
            }
            R.id.add_view -> {
                val position = AdapterUtils.getHolder(v).adapterPosition
                val item = AdapterUtils.getHolder(v).getItem<AddTokenItem>().data
                if (item.isMyToken == 0) {
                    item.isMyToken = 1
                } else {
                    item.isMyToken = 0
                }
                myStores[position] = item
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myStores))
                adapter.notifyItemChanged(position)
            }
        }
    }
}