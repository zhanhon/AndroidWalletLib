package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.MessageCenterActivity
import com.ramble.ramblewallet.activity.MsgDetailsActivity
import com.ramble.ramblewallet.adapter.RecyclerViewFragment
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM4
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.StationItem
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import java.util.ArrayList


/**
 * 时间　: 2021/12/15 14:29
 * 作者　: potato
 * 描述　:公告
 */
class ProclamationFragment : RecyclerViewFragment(), QMUIPullRefreshLayout.OnPullListener {
    private lateinit var binding: FragmentProclamationBinding
    lateinit var myActivity: MessageCenterActivity
        private set
    private lateinit var endless: EndlessRecyclerViewScrollListener
    var isShowCheck: Boolean = false
    var isShowALLCheck: Boolean = false

    override fun onAttach(context: Context) {
        myActivity = activity as MessageCenterActivity
        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.onClickListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (reusedView == null) {
            binding = inflater.dataBinding(R.layout.fragment_proclamation, container)
            binding.pullToRefresh.setOnPullListener(this)
            LinearLayoutManager(myActivity).apply {
                binding.recycler.layoutManager = this
                endless = EndlessRecyclerViewScrollListener(this) { _, _ ->
                    if (!lock && currentPage < totalPage) {
                        lock = true
                        ProgressItem.addTo(adapter)
                        currentPage += 1
                        load()
                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }else{

                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter
            reusedView = binding.root
        }
        return reusedView
    }

    @SuppressLint("CheckResult")
    private fun load() {
        lock = true
        var req = Page.Req(currentPage, 2, 1)
        myActivity.mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        totalPage = data.totalPage
                        println("==================>getTransferInfo:${data}")
                        ArrayList<SimpleRecyclerItem>().apply {
                            data.records.forEach { item -> add(StationItem(item)) }
                            if (data.pageNo == 1) {
                                forEach {
                                    if (it is StationItem) {
                                        it.isEditable = isShowCheck
                                        it.isChecked = isShowALLCheck
                                    }
                                }
                                adapter.replaceAll(this.toList())
                            } else {
                                forEach {
                                    if (it is StationItem) {
                                        it.isEditable = isShowCheck
                                        it.isChecked = isShowALLCheck
                                    }
                                }
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
    override fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.pullToRefresh.isVisible = count > 0
    }
    private fun onLoaded() {
        lock = false
        binding.pullToRefresh.finishRefresh()
        ProgressItem.removeFrom(adapter)
    }

    override fun onRefresh() {
        if (lock) {
            binding.pullToRefresh.finishRefresh()
            return
        }
        currentPage = 1
        totalPage = 1
        endless.reset()
        load()
    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        binding.pullToRefresh.setToRefreshDirectly()
    }


    private fun sendToServer() {

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_msg_notic -> {
                val itemBean = AdapterUtils.getHolder(v).getItem<StationItem>().data
                start2(MsgDetailsActivity::class.java,Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM4, "消息详情")
                })

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProclamationFragment = ProclamationFragment()

    }


}