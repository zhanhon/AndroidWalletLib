package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
import com.ramble.ramblewallet.constant.WALLETSELECTED
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
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()
    private lateinit var walletSelleted: Wallet

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token)
        tokenTitle = intent.getStringExtra(ARG_PARAM1).toString()
        initView()
        initListener()
    }

    private fun initView() {
        binding.tvTokenTitle.text = tokenTitle + getString(R.string.token)
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        LinearLayoutManager(this).apply {
            binding.rvTokenCurrency.layoutManager = this
        }
        binding.rvTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08).leftMargin(R.dimen.dp_54)
                .build()
        )
        binding.rvTokenCurrency.adapter = adapter

        myAllToken = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
            object : TypeToken<ArrayList<AllTokenBean>>() {}.type
        )
        myAllToken.forEach {
            if (it.myCurrency == walletSelleted.address) {
                myStores = it.storeInfos
            }
        }
        ArrayList<SimpleRecyclerItem>().apply {
            myStores.forEach { o -> this.add(AddTokenItem(o)) }
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
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        myStores = it.storeInfos
                    }
                }
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
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        it.storeInfos = myStores
                    }
                }

                SharedPreferencesUtils.saveSecurityString(
                    this,
                    TOKEN_INFO_NO,
                    Gson().toJson(myAllToken)
                )
                ArrayList<SimpleRecyclerItem>().apply {
                    myStores.forEach { o -> this.add(AddTokenItem(o)) }
                    adapter.addAll(this.toList())
                }
            }
            Pie.EVENT_DEL_TOKEN -> {
                myStores = arrayListOf()
                adapter.clear()
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        myStores = it.storeInfos
                    }
                }

                ArrayList<SimpleRecyclerItem>().apply {
                    myStores.forEach { o -> this.add(AddTokenItem(o)) }
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
                when (item.isMyToken) {
                    0 -> {
                        item.isMyToken = 1
                    }
                    1 -> {
                        item.isMyToken = 0
                    }
                    else -> {
                        return
                    }
                }
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        myStores = it.storeInfos
                    }
                }
                myStores[position] = item
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        it.storeInfos = myStores
                    }
                }
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    TOKEN_INFO_NO,
                    Gson().toJson(myAllToken)
                )
                adapter.notifyItemChanged(position)
            }
        }
    }
}