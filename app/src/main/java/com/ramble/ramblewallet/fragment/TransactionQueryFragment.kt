package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.DealDetailActivity
import com.ramble.ramblewallet.activity.TransactionQueryActivity
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.FragmentTransactionQueryBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.TransferItem
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.network.transferInfoUrl
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.toJdk7Date
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.ArrayList

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
                if (currentPage<totalPage){
                    binding.lyPullRefresh.finishLoadMore() //加载完成
                    ProgressItem.addTo(adapter)
                    currentPage += 1
                    loadData()
                }else{
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

    override fun onResume() {
        super.onResume()
//        reFreshData()
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
        status=  when(gameType){
            2->3
            else->null
        }
        transferType=when(gameType){ //交易类型|1:转出|2:转入|其它null
            4->1
            3->2
            else->null
        }
        var req = QueryTransferRecord.Req(
            currentPage,
            2,
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
                        totalPage = data.totalPage
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