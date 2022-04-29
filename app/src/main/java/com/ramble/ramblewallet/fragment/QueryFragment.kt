package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.DealDetailActivity
import com.ramble.ramblewallet.activity.QueryActivity
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.FragmentTransactionQueryBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.TransferItem
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.network.transferInfoUrl
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.TimeUtils.dateToCurrency
import com.ramble.ramblewallet.utils.TimeUtils.dateToWalletTypeInt
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

/**
 * 时间　: 2022/4/18 14:21
 * 作者　: potato
 * 描述　:
 */
class QueryFragment : BaseFragment(),
    RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: FragmentTransactionQueryBinding
    lateinit var myActivity: QueryActivity
        private set
    private var gameType: Int = 1
    private var status: Int? = null
    private var transferType: Int? = null
    private var endTime: Long? = null
    private var startTime: Long? = null
    private var dateType: String? = null
    private var currentPage = 1
    private var totalPage = 1
    private val adapter = RecyclerAdapter()
    private lateinit var wallet: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var address = ""
    private var addressType = 0
    private var currencyUnit = ""
    private var changeCurrencyType = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = activity as QueryActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameType = it.getInt(ARG_PARAM1, 1)
        }

        adapter.onClickListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (reusedView == null) {
            binding = inflater.dataBinding(R.layout.fragment_transaction_query, container)
            binding.lyPullRefresh.setRefreshHeader(ClassicsHeader(myActivity))
            binding.lyPullRefresh.setRefreshFooter(ClassicsFooter(myActivity))
            //刷新的监听事件
            binding.lyPullRefresh.setOnRefreshListener {
                binding.lyPullRefresh.finishRefresh() //刷新完成
                ProgressItem.addTo(adapter)
                reFreshData()
            }
            //加载的监听事件
            binding.lyPullRefresh.setOnLoadMoreListener {
                if (currentPage < totalPage) {
                    binding.lyPullRefresh.finishLoadMore() //加载完成
                    ProgressItem.addTo(adapter)
                    currentPage += 1
                    dataCheck()
                    loadData()
                } else {
                    binding.lyPullRefresh.finishLoadMoreWithNoMoreData()
                }
            }
            binding.tableHeader.check(R.id.all)

            binding.recycler.adapter = adapter
            binding.tableHeader.setOnCheckedChangeListener(this)
            reusedView = binding.root
        }
        return reusedView
    }


    private fun reFreshData() {
        currentPage = 1
        totalPage = 1
        dataCheck()
        loadData()
    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        reFreshData()
    }

    private fun dataCheck() {
        status = when (gameType) {
            2 -> 3
            else -> null
        }
        transferType = when (gameType) { //交易类型|1:转出|2:转入|其它null
            4 -> 1
            3 -> 2
            else -> null
        }
        changeCurrencyType = dateToCurrency(myActivity)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(myActivity, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        wallet =
            Gson().fromJson(
                SharedPreferencesUtils.getString(myActivity, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
        currencyUnit = SharedPreferencesUtils.getString(activity, CURRENCY_TRAN, "")
        addressType = if (currencyUnit.isNotEmpty()) {
            dateToWalletTypeInt(currencyUnit)
        } else {
            wallet.walletType
        }
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        address =SharedPreferencesUtils.getString(myActivity, TOKEN_DB_NO, "")
        var contractAddress: String? = null
        var isToken: Int? = null
        if (address.isEmpty()) {
            address = wallet.address
        } else {
            contractAddress = address
            address = wallet.address
        }
        val req = QueryTransferRecord.Req(
            dateType,
            currentPage,
            100,
            address,
            addressType,
            changeCurrencyType,
            contractAddress,
            endTime,
            1,
            startTime,
            status,
            transferType
        )
        myActivity.mApiService.getTransferInfo(
            req.toApiRequest(transferInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        totalPage = data.totalPage
                        println("==================>getTransferInfo:${data}")
                        ArrayList<SimpleRecyclerItem>().apply {
                            data.records.forEach { item -> add(TransferItem(myActivity, item)) }
                            if (data.pageNo == 1) {
                                adapter.replaceAll(this.toList())
                            } else {
                                adapter.addAll(this.toList())
                            }
                        }
                        apply(adapter.itemCount)
                        onLoaded()
                    }

                } else {
                    apply(0)
                }
            }, {
                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )

    }


    private fun onLoaded() {
        ProgressItem.removeFrom(adapter)
    }

    companion object {
        @JvmStatic
        fun newInstance(gameType: Int): QueryFragment {
            return QueryFragment().apply {
                arguments = Bundle().also {
                    it.putInt(ARG_PARAM1, gameType)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_transfer -> {//转账详情
                val itemBean = AdapterUtils.getHolder(v).getItem<TransferItem>().data
                start2(DealDetailActivity::class.java, Bundle().also {
                    it.putSerializable(ARG_PARAM1, itemBean)
                })
            }
        }
    }

    private fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.lyPullRefresh.isVisible = count > 0
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.all -> {
                dateType = null
                reFreshData()
            }
            R.id.week -> {
                dateType = "WW"
                reFreshData()
            }
            R.id.month -> {
                dateType = "MM"
                reFreshData()
            }
            R.id.year -> {
                dateType = "YY"
                reFreshData()
            }
        }
    }

}