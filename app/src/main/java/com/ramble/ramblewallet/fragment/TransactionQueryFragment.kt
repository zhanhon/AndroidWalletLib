package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.DealDetailActivity
import com.ramble.ramblewallet.activity.TransactionQueryActivity
import com.ramble.ramblewallet.adapter.RecyclerViewFragment
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.FragmentTransactionQueryBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.TransferItem
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.network.transferInfoUrl
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.toJdk7Date
import com.ramble.ramblewallet.utils.yyyy_mm_dd_hh_mm_ss
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import java.util.ArrayList

/**
 * 时间　: 2021/12/17 13:30
 * 作者　: potato
 * 描述　:
 */
class TransactionQueryFragment : RecyclerViewFragment(), QMUIPullRefreshLayout.OnPullListener,
    RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: FragmentTransactionQueryBinding
    lateinit var myActivity: TransactionQueryActivity
        private set
    private var gameType: Int = 1
    private lateinit var endless: EndlessRecyclerViewScrollListener
    private lateinit var chooseDay: LocalDateTime
    private lateinit var startDay: LocalDateTime
    private var status: Int? = null
    private var transferType: Int? = null
    private var endTime: Long? = null
    private var startTime: Long? = null
    private var isAll = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = activity as TransactionQueryActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameType = it.getInt(ARG_PARAM1, 1)
        }
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
            binding.pullToRefresh.setOnPullListener(this)
            binding.tableHeader.check(R.id.all)
            isAll = true
            LinearLayoutManager(myActivity).apply {
                binding.recycler.layoutManager = this
                endless = EndlessRecyclerViewScrollListener(this) { _, _ ->
                    if (!lock && currentPage < totalPage) {
                        lock = true
                        ProgressItem.addTo(adapter)
                        currentPage += 1
                        loadData()
                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter

            binding.tableHeader.setOnCheckedChangeListener(this)
            reusedView = binding.root
        }
//        binding.tvCancel.setOnClickListener(this)
//        binding.tvDelete.setOnClickListener(this)
        return reusedView
    }

    override fun onRefresh() {
        init()
    }

    private fun init() {
        if (lock) {
            binding.pullToRefresh.finishRefresh()
            return
        }
        currentPage = 0
        totalPage = 1
        endless.reset()
        loadData()
    }

    override fun actualLazyLoad() {
        super.actualLazyLoad()
        binding.pullToRefresh.setToRefreshDirectly()
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
        var req = QueryTransferRecord.Req(
            1,
            20,
            "0x90d51f90fdf0722f1d621820ca9f45547221fdd9",
            1,
            1,
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
                        println("==================>getTransferInfo:${data}")
                        ArrayList<SimpleRecyclerItem>().apply {
                            data.records.forEach { item -> add(TransferItem(item)) }
                            if (data.pageNo == 1) {
                                adapter.replaceAll(this.toList())
                            } else {
                                adapter.addAll(this.toList())
                            }
                        }
                        apply(adapter.itemCount)
                        onLoaded()
                        totalPage = 1
                    }

                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }
            }, {
                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )

    }

    private fun onLoaded() {
//        myActivity.dismissLoading()
        lock = false
        binding.pullToRefresh.finishRefresh()
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
                start2(DealDetailActivity::class.java)
            }
        }
    }

    override fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.pullToRefresh.isVisible = count > 0
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.all -> {
                isAll = true
                loadData()
            }
            R.id.week -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusDays(-7).toLocalDate(), LocalTime.MIN)
                loadData()
            }
            R.id.month -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusMonths(-1).toLocalDate(), LocalTime.MIN)
                loadData()
            }
            R.id.year -> {
                isAll = false
                startDay = LocalDateTime.of(chooseDay.plusYears(-1).toLocalDate(), LocalTime.MIN)
                loadData()
            }
        }
    }
}