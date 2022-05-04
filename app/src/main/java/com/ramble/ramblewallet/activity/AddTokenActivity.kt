package com.ramble.ramblewallet.activity


import android.content.Intent
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
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityAddTokenBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.item.AddTokenItem
import com.ramble.ramblewallet.item.UnAddTokenItem
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
 * 描述　: 新增代币
 */
class AddTokenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddTokenBinding
    private val adapter = RecyclerAdapter()
    private var myDataBeansMyAssets: ArrayList<StoreInfo> = arrayListOf()
    private val recommendTokenAdapter = RecyclerAdapter()
    private var myDataBeansRecommendToken: ArrayList<StoreInfo> = arrayListOf()
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()
    private lateinit var walletSelleted: Wallet
    private var isSpread = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_token)
        initView()
        initListener()
    }

    private fun initView() {
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        LinearLayoutManager(this).apply {
            binding.rvMyTokenCurrency.layoutManager = this
        }
        binding.rvMyTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08).leftMargin(R.dimen.dp_54)
                .build()
        )
        binding.rvMyTokenCurrency.adapter = adapter


        LinearLayoutManager(this).apply {
            binding.rvTokenManageCurrency.layoutManager = this
        }
        binding.rvTokenManageCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08).leftMargin(R.dimen.dp_54)
                .build()
        )
        binding.rvTokenManageCurrency.adapter = recommendTokenAdapter
        myAllToken = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
            object : TypeToken<ArrayList<AllTokenBean>>() {}.type
        )
        myAllToken.forEach {
            if (it.myCurrency == walletSelleted.address) {
                myDataBeansRecommendToken = it.storeInfos
            }
        }

        ArrayList<SimpleRecyclerItem>().apply {
            myDataBeansRecommendToken.forEach { o ->
                if (o.isMyToken == 0) {
                    this.add(AddTokenItem(o))
                }
            }
            trimDuplicate(this)
            recommendTokenAdapter.addAll(this.toList())
        }
        ArrayList<SimpleRecyclerItem>().apply {
            myDataBeansRecommendToken.forEach { o ->
                if (o.isMyToken == 1 || o.isMyToken == 2) {
                    this.add(UnAddTokenItem(o))
                }
            }
            adapter.addAll(this.toList())
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivTokenManage.setOnClickListener(this)
        binding.llMyTokenCurrencyConstriction.setOnClickListener(this)
        binding.search.setOnClickListener(this)
        recommendTokenAdapter.onClickListener = this
        adapter.onClickListener = this
    }


    /***
     * 过滤重复信息
     */
    private fun trimDuplicate(data: ArrayList<SimpleRecyclerItem>) {
        val duplicates = ArrayList<SimpleRecyclerItem>()
        data.forEach { obj ->
            if (obj is AddTokenItem) {
                findExistMsgItem(obj.data.symbol)?.let {
                    duplicates.add(obj)
                }
            }
        }
        duplicates.forEach { data.remove(it) }
    }

    private fun findExistMsgItem(id: String): AddTokenItem? {
        recommendTokenAdapter.all.forEach {
            if (it is AddTokenItem && it.data.symbol == id) {
                return it
            }
        }
        return null
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)

        when (event.id()) {
            Pie.EVENT_MINUS_TOKEN, Pie.EVENT_ADD_TOKEN -> {
                myDataBeansMyAssets = arrayListOf()
                adapter.clear()
                recommendTokenAdapter.clear()
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        myDataBeansMyAssets = it.storeInfos
                    }
                }
                var isOpen = false
                myDataBeansMyAssets.forEach {
                    if (it.id == event.data<StoreInfo>().id) {
                        it.isMyToken = event.data<StoreInfo>().isMyToken
                        isOpen = true
                    }
                }
                if (!isOpen) {
                    myDataBeansMyAssets.add(event.data())
                }
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        it.storeInfos = myDataBeansMyAssets
                    }
                }
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    TOKEN_INFO_NO,
                    Gson().toJson(myAllToken)
                )
                dataCheck()
            }
            Pie.EVENT_DEL_TOKEN -> {
                myDataBeansMyAssets = arrayListOf()
                adapter.clear()
                recommendTokenAdapter.clear()
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                myAllToken.forEach {
                    if (it.myCurrency == walletSelleted.address) {
                        myDataBeansMyAssets = it.storeInfos
                    }
                }
                dataCheck()
            }
        }
    }

    private fun dataCheck() {
        ArrayList<SimpleRecyclerItem>().apply {
            myDataBeansMyAssets.forEach { o ->
                if (o.isMyToken == 0) {
                    this.add(AddTokenItem(o))
                }
            }
            trimDuplicate(this)
            recommendTokenAdapter.addAll(this.toList())
        }
        ArrayList<SimpleRecyclerItem>().apply {
            myDataBeansMyAssets.forEach { o ->
                if (o.isMyToken == 1 || o.isMyToken == 2) {
                    this.add(UnAddTokenItem(o))
                }
            }
            adapter.addAll(this.toList())
        }

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.search -> {//搜索代币
                start(SearchTokenActivity::class.java)

            }
            R.id.iv_token_manage -> {
                startActivity(Intent(this, TokenManageActivity::class.java))
            }
            R.id.ll_my_token_currency_constriction -> {
                if (isSpread) {
                    binding.v1.visibility = View.VISIBLE
                    binding.v2.visibility = View.VISIBLE
                    binding.guView.visibility = View.VISIBLE
                    binding.rvMyTokenCurrency.visibility = View.VISIBLE
                    binding.tvMyTokenCurrencyConstriction.text = getString(R.string.pack_up)
                    binding.ivMyTokenCurrencyConstriction.setBackgroundResource(R.drawable.vector_solid_triangle_up)
                    isSpread = false
                } else {
                    binding.v1.visibility = View.GONE
                    binding.v2.visibility = View.GONE
                    binding.rvMyTokenCurrency.visibility = View.GONE
                    binding.guView.visibility = View.GONE
                    binding.tvMyTokenCurrencyConstriction.text = getString(R.string.spread)
                    binding.ivMyTokenCurrencyConstriction.setBackgroundResource(R.drawable.vector_solid_triangle_down)
                    isSpread = true
                }
            }
            R.id.add_view -> {
                when (val item = AdapterUtils.getHolder(v).getItem<SimpleRecyclerItem>()) {
                    is AddTokenItem -> {//增加代币到我的
                        if (item.data.isMyToken == 2) {
                            return
                        }
                        myDataBeansMyAssets.clear()
                        item.data.isMyToken = 1
                        RxBus.emitEvent(Pie.EVENT_ADD_TOKEN, item.data)
                    }
                    is UnAddTokenItem -> {//减到代币到我的
                        if (item.data.isMyToken == 2) {
                            return
                        }
                        myDataBeansMyAssets.clear()
                        item.data.isMyToken = 0
                        RxBus.emitEvent(Pie.EVENT_ADD_TOKEN, item.data)
                    }
                }
            }
        }
    }
}