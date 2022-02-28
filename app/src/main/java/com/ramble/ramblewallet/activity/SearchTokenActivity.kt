package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivitySearchTokenBinding
import com.ramble.ramblewallet.item.AddTokenItem
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import java.util.*

/**
 * 时间　: 2022/1/14 9:25
 * 作者　: potato
 * 描述　:搜索页面
 */
class SearchTokenActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchTokenBinding
    private val adapter = RecyclerAdapter()
    private var myDataBeansMyAssets: ArrayList<StoreInfo> = arrayListOf()
    private var lastString = ""
    private lateinit var wallet: Wallet

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_token)
        initView()
        initListener()
    }

    private fun initView() {
        LinearLayoutManager(this).apply {
            binding.rvTokenManageCurrency.layoutManager = this
        }
        binding.rvTokenManageCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        binding.rvTokenManageCurrency.adapter = adapter
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.search.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
        adapter.onClickListener = this
    }

    @SuppressLint("CheckResult")
    private fun searchData(condition: String) {
        myDataBeansMyAssets = Gson().fromJson(
            SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
            object : TypeToken<ArrayList<StoreInfo>>() {}.type
        )
        var list = myDataBeansMyAssets.iterator()
        list.forEach {
            if (it.isMyToken == 0) {
                list.remove()
            }
        }
        wallet =
            Gson().fromJson(
                SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )

        var req = StoreInfo.Req()
        req.condition = "symbol"
        req.symbol = condition
        req.platformId = when (wallet.walletType) {
            2 -> {
                1958
            }
            3 -> {
                1
            }
            else -> {
                1027
            }
        }

        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                adapter.clear()
                it.data()?.let { data ->
                    data.forEach { info ->
                        myDataBeansMyAssets.forEach { bean ->
                            if (info.id == bean.id) {
                                info.isMyToken = 1
                            }
                        }
                    }

                    ArrayList<SimpleRecyclerItem>().apply {
                        data.forEach { o -> this.add(AddTokenItem(o)) }
                        adapter.replaceAll(this.toList())
                    }
                }
                apply(adapter.itemCount)
            } else {
                println("==================>getTransferInfo1:${it.message()}")
            }
        }, {
            println("==================>getTransferInfo1:${it.printStackTrace()}")
        }
        )
    }

    private fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.rvTokenManageCurrency.isVisible = count > 0
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back, R.id.tv_clear -> {
                finish()
            }
            R.id.search -> {//搜索代币
                if (binding.etSearch.text.toString()
                        .isNullOrEmpty() || lastString == binding.etSearch.text.toString().trim()
                ) return
                lastString = binding.etSearch.text.toString().trim()
                searchData(lastString)
            }
            R.id.iv_delete -> {
                binding.etSearch.text = null
            }

            R.id.add_view -> {
                val position = AdapterUtils.getHolder(v).adapterPosition
                val item = AdapterUtils.getHolder(v).getItem<AddTokenItem>().data
                if (item.isMyToken == 0) {
                    item.isMyToken = 1
                } else {
                    item.isMyToken = 0
                }
                adapter.notifyItemChanged(position)
                RxBus.emitEvent(Pie.EVENT_MINUS_TOKEN, item)
            }
        }
    }
}