package com.ramble.ramblewallet.activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
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
    private var isSpread = false
    private var lastString = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_token)
        initView()
        initListener()
    }

    private fun initView() {
        LinearLayoutManager(this).apply {
            binding.rvMyTokenCurrency.layoutManager = this
        }
        binding.rvMyTokenCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        binding.rvMyTokenCurrency.adapter = adapter


        LinearLayoutManager(this).apply {
            binding.rvTokenManageCurrency.layoutManager = this
        }
        binding.rvTokenManageCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        binding.rvTokenManageCurrency.adapter = recommendTokenAdapter
        myDataBeansRecommendToken = SharedPreferencesUtils.String2SceneList(
            SharedPreferencesUtils.getString(
                this,
                TOKEN_INFO_NO,
                ""
            )
        ) as ArrayList<StoreInfo>
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
                if (o.isMyToken == 1) {
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
                findExistMsgItem(obj.data.name)?.let {
                    duplicates.add(obj)
                }
            }
        }
        duplicates.forEach { data.remove(it) }
    }

    private fun findExistMsgItem(id: String): AddTokenItem? {
        recommendTokenAdapter.all.forEach {
            if (it is AddTokenItem && it.data.name == id) {
                return it
            }
        }
        return null
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_MINUS_TOKEN -> {
                myDataBeansMyAssets = arrayListOf()
                adapter.clear()
                recommendTokenAdapter.clear()
                myDataBeansMyAssets = SharedPreferencesUtils.String2SceneList(
                    SharedPreferencesUtils.getString(
                        this,
                        TOKEN_INFO_NO,
                        ""
                    )
                ) as ArrayList<StoreInfo>
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
                var addId = SharedPreferencesUtils.SceneList2String(myDataBeansMyAssets)
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, addId)

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
                        if (o.isMyToken == 1) {
                            this.add(UnAddTokenItem(o))
                        }
                    }
                    adapter.addAll(this.toList())
                }
            }
            Pie.EVENT_DEL_TOKEN -> {
                myDataBeansMyAssets = arrayListOf()
                adapter.clear()
                recommendTokenAdapter.clear()
                myDataBeansMyAssets = SharedPreferencesUtils.String2SceneList(
                    SharedPreferencesUtils.getString(
                        this,
                        TOKEN_INFO_NO,
                        ""
                    )
                ) as ArrayList<StoreInfo>

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
                        if (o.isMyToken == 1) {
                            this.add(UnAddTokenItem(o))
                        }
                    }
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
            R.id.search -> {//搜索代币
                start(SearchTokenActivity::class.java)

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
            R.id.add_view -> {
                when (val item = AdapterUtils.getHolder(v).getItem<SimpleRecyclerItem>()) {
                    is AddTokenItem -> {//增加代币到我的
                        myDataBeansMyAssets.clear()
                        item.data.isMyToken = 1
                        myDataBeansMyAssets.add(item.data)
                        recommendTokenAdapter.remove(item)
                        recommendTokenAdapter.notifyDataSetChanged()
                        ArrayList<SimpleRecyclerItem>().apply {
                            myDataBeansMyAssets.forEach { o -> this.add(UnAddTokenItem(o)) }
                            trimDuplicate(this)
                            adapter.addAll(this.toList())
                        }
                        RxBus.emitEvent(Pie.EVENT_ADD_TOKEN, item.data)
                    }
                    is UnAddTokenItem -> {//减到代币到我的
                        myDataBeansMyAssets.clear()
                        item.data.isMyToken = 0
                        RxBus.emitEvent(Pie.EVENT_ADD_TOKEN, item.data)
                        myDataBeansMyAssets.add(item.data)
                        adapter.remove(item)
                        adapter.notifyDataSetChanged()
                        ArrayList<SimpleRecyclerItem>().apply {
                            myDataBeansMyAssets.forEach { o -> this.add(AddTokenItem(o)) }
                            trimDuplicate(this)
                            recommendTokenAdapter.addAll(this.toList())
                        }
                    }
                }

            }
        }
    }
}