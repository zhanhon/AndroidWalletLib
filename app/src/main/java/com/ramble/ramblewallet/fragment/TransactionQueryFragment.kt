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
import com.ramble.ramblewallet.activity.TransactionQueryActivity
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
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.*

/**
 * 时间　: 2021/12/17 13:30
 * 作者　: potato
 * 描述　:
 */
class TransactionQueryFragment : BaseFragment(),
    RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: FragmentTransactionQueryBinding
    lateinit var myActivity: TransactionQueryActivity
        private set
    private var gameType: Int = 1
    private lateinit var chooseDay: LocalDateTime
    private lateinit var startDay: LocalDateTime
    private var status: Int? = null
    private var transferType: Int? = null
    private var endTime: Long? = null
    private var startTime: Long? = null
    private var isAll = false
    private var currentPage = 1
    private var totalPage = 1
    private val adapter = RecyclerAdapter()
    private lateinit var wallet: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var address = ""
    private var addressType = 0
    private var currencyUnit = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = activity as TransactionQueryActivity
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
            chooseDay = LocalDateTime.now()
            chooseDay = LocalDateTime.of(chooseDay.toLocalDate(), LocalTime.MAX)
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
                    loadData()
                } else {
                    binding.lyPullRefresh.finishLoadMoreWithNoMoreData()
                }
            }
            binding.tableHeader.check(R.id.all)
            isAll = true
            binding.recycler.adapter = adapter
            binding.tableHeader.setOnCheckedChangeListener(this)
            reusedView = binding.root
        }
        return reusedView
    }


    private fun reFreshData() {
        currentPage = 1
        totalPage = 1
        loadData()
    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        reFreshData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        if (isAll) {
            endTime = null
            startTime = null
        } else {
            endTime = chooseDay.toJdk7Date().time
            startTime = startDay.toJdk7Date().time
        }
        status = when (gameType) {
            2 -> 3
            else -> null
        }
        transferType = when (gameType) { //交易类型|1:转出|2:转入|其它null
            4 -> 1
            3 -> 2
            else -> null
        }

        var changeCurrencyType =
            when (SharedPreferencesUtils.getString(myActivity, CURRENCY, USD)) {
                CNY -> {
                    2
                }
                HKD -> {
                    3
                }
                else -> {
                    1
                }
            }

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
            when (currencyUnit) {
                "ETH" -> 1
                "BTC" -> 3
                else -> 2
            }
        } else {
            when (wallet.walletType) {
                3 -> 3
                1 -> 1
                else -> 2
            }
        }


        address = saveData(saveWalletList)
//           "tb1qlg08ptrd0mff0tzxcg6fas2wzy6dzqxprmum9c"
//        saveData(saveWalletList)
//        "0x90d51f90fdf0722f1d621820ca9f45547221fdd9"
        var req = QueryTransferRecord.Req(
            currentPage,
            20,
            address,
            addressType,
            changeCurrencyType,
            endTime,
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

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_TRAN_TYPE -> {
                reFreshData()
            }
        }
    }

    private fun saveData(list: ArrayList<Wallet>): String {
        var sb = StringBuffer()
        if (currencyUnit.isNotEmpty()) {
            var walletType = when (currencyUnit) {
                "ETH" -> 1
                "BTC" -> 3
                else -> 2
            }
            list.forEach {
                if (walletType == it.walletType) {
                    sb.append(it.address).append(",")
                }
            }
        } else {
            list.forEach {
                if (wallet.walletType == it.walletType) {
                    sb.append(it.address).append(",")
                }
            }
        }

        var addStr =
            if (sb.isNotEmpty()) sb.deleteCharAt(sb.length - 1).toString() else sb.toString()
        return addStr
    }

    private fun onLoaded() {
        ProgressItem.removeFrom(adapter)
    }

    companion object {
        @JvmStatic
        fun newInstance(gameType: Int): TransactionQueryFragment {
            return TransactionQueryFragment().apply {
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
                isAll = true
                reFreshData()
            }
            R.id.week -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusDays(-7).toLocalDate(), LocalTime.MIN)
                reFreshData()
            }
            R.id.month -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusMonths(-1).toLocalDate(), LocalTime.MIN)
                reFreshData()
            }
            R.id.year -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusYears(-1).toLocalDate(), LocalTime.MIN)
                reFreshData()
            }
        }
    }
}