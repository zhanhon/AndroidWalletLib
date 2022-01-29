package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.tvTokenTitle.text = tokenTitle

        LinearLayoutManager(this).apply {
            binding.rvTokenCurrency.layoutManager = this
        }
        binding.rvTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        binding.rvTokenCurrency.adapter = adapter
        if (SharedPreferencesUtils.getString(
                this,
                TOKEN_INFO_NO,
                ""
            ).isNotEmpty()
        ) {
            myStores = SharedPreferencesUtils.String2SceneList(
                SharedPreferencesUtils.getString(
                    this,
                    TOKEN_INFO_NO,
                    ""
                )
            ) as ArrayList<StoreInfo>
        } else {
            var r1 = StoreInfo()
            r1.id = 1
            r1.name = "TFT"
            var r2 = StoreInfo()
            r2.id = 2
            r2.name = "WBTC"
            var r3 = StoreInfo()
            r3.id = 3
            r3.name = "DAI"
            var r4 = StoreInfo()
            r4.name = "USDC"
            r4.id = 4
            var r5 = StoreInfo()
            r5.name = "USDT"
            r5.id = 5
            var r6 = StoreInfo()
            r6.name = "LINK"
            r6.id = 6
            var r7 = StoreInfo()
            r7.name = "YFI"
            r7.id = 7
            var r8 = StoreInfo()
            r8.name = "UNI"
            r8.id = 8
            myStores.add(r1)
            myStores.add(r2)
            myStores.add(r3)
            myStores.add(r4)
            myStores.add(r5)
            myStores.add(r6)
            myStores.add(r7)
            myStores.add(r8)
            var addId = SharedPreferencesUtils.SceneList2String(myStores)
            SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, addId)
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
                myStores = SharedPreferencesUtils.String2SceneList(
                    SharedPreferencesUtils.getString(
                        this,
                        TOKEN_INFO_NO,
                        ""
                    )
                ) as ArrayList<StoreInfo>
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
                var addId = SharedPreferencesUtils.SceneList2String(myStores)
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, addId)
                ArrayList<SimpleRecyclerItem>().apply {
                    myStores.forEach { o -> this.add(AddTokenItem(o as StoreInfo)) }
                    adapter.addAll(this.toList())
                }
            }
            Pie.EVENT_DEL_TOKEN -> {
                myStores = arrayListOf()
                adapter.clear()
                myStores = SharedPreferencesUtils.String2SceneList(
                    SharedPreferencesUtils.getString(
                        this,
                        TOKEN_INFO_NO,
                        ""
                    )
                ) as ArrayList<StoreInfo>

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
                var addId = SharedPreferencesUtils.SceneList2String(myStores)
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, addId)
                adapter.notifyItemChanged(position)
            }
        }
    }
}