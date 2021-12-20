package com.ramble.ramblewallet.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.TransactionQueryActivity
import com.ramble.ramblewallet.adapter.RecyclerViewFragment
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.databinding.FragmentTransactionQueryBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.OnDataSetChanged

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
//                        load()
                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter
            binding.tableHeader.check(R.id.all)
            reusedView = binding.root
        }
//        binding.tvCancel.setOnClickListener(this)
//        binding.tvDelete.setOnClickListener(this)
        return reusedView
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

    }
}