package com.ramble.ramblewallet.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
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
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
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
            binding.pullToRefresh.setOnPullListener(this)
            LinearLayoutManager(myActivity).apply {
                binding.recycler.layoutManager = this
                endless = EndlessRecyclerViewScrollListener(this) { _, _ ->
                    if (!lock && currentPage < totalPage) {
                        lock = true
                        ProgressItem.addTo(adapter)
                        loadData()
                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter
            binding.tableHeader.check(R.id.all)
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
    private fun loadData() {
        lock = true
        currentPage++
        var page= QueryTransferRecord()
        page.pageNo=1
        page.pageSize=20
        page.totalCount=20
        page.totalPage=1
        var list= arrayListOf<QueryTransferRecord.Record>()
        var v1= QueryTransferRecord.Record()
        v1.createTime= "11111111111111111"
        v1.thirdGameName="0x457496574569845675896576586"
        v1.actionStatus=1
        v1.blockStatus=1
        list.add(v1)
        var v2= QueryTransferRecord.Record()
        v2.createTime= "11111111111111111"
        v2.thirdGameName="0x457496574569845675896576586"
        v2.actionStatus=1
        v2.blockStatus=2
        v2.userReceivedWashBetAmount=111.00
        v2.validBetAmount=222.00
        list.add(v2)
        var v3= QueryTransferRecord.Record()
        v3.createTime= "11111111111111111"
        v3.thirdGameName="0x457496574569845675896576586"
        v3.actionStatus=1
        v3.blockStatus=3
        v3.userReceivedWashBetAmount=111.00
        v3.validBetAmount=222.00
        list.add(v3)
        var v4= QueryTransferRecord.Record()
        v4.createTime= "11111111111111111"
        v4.thirdGameName="0x457496574569845675896576586"
        v4.actionStatus=2
        v4.blockStatus=1
        list.add(v4)
        var v5= QueryTransferRecord.Record()
        v5.createTime= "11111111111111111"
        v5.thirdGameName="0x457496574569845675896576586"
        v5.actionStatus=2
        v5.blockStatus=2
        v5.userReceivedWashBetAmount=111.00
        v5.validBetAmount=222.00
        list.add(v5)
        var v6= QueryTransferRecord.Record()
        v6.createTime= "11111111111111111"
        v6.thirdGameName="0x457496574569845675896576586"
        v6.actionStatus=2
        v6.blockStatus=3
        v6.userReceivedWashBetAmount=111.00
        v6.validBetAmount=222.00
        list.add(v6)

        page.records=list
        ArrayList<SimpleRecyclerItem>().apply {
            page.records.forEach { item -> add(TransferItem(item)) }
            if (page.pageNo == 1) {
                adapter.replaceAll(this.toList())
            } else {
                adapter.addAll(this.toList())
            }
        }
        onLoaded()
        totalPage=1
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
           when(checkedId){
               R.id.all->{

               }
               R.id.week->{

               }
               R.id.month->{

               }
               R.id.year->{

               }
           }
    }
}