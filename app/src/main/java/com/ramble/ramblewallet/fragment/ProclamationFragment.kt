package com.ramble.ramblewallet.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.MessageCenterActivity
import com.ramble.ramblewallet.adapter.RecyclerViewFragment
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils


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

                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }else{
                        load()
                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter
            reusedView = binding.root
        }
        return reusedView
    }

    override fun onResume() {
        super.onResume()
//        if ((lock || currentPage > 0) && adapter.isEmpty) {
//            setEmptyView()
//        }
    }

    private fun load() {
        lock = true
//        model.getAnnouncementPage(Page.Req(currentPage + 1, Pie.PAGE_SIZE)).subscribe(
//            {
//                if (it.status()) {
//                    currentPage++
//                    it.body.data?.let { page ->
//                        totalPage = page.totalPage
//                        if ((page.records.isEmpty() && page.pageNo <= 1)) {
//                            onLoaded()
//                            setEmptyView()
//                            return@let
//                        }
//                        ArrayList<SimpleRecyclerItem>().apply {
//                            page.records.forEach { item -> add(NoticeItem(item)) }
//                            if (page.pageNo == 1) {
//                                adapter.replaceAll(this.toList())
//                            } else {
//                                adapter.addAll(this.toList())
//                            }
//                        }
//                    }
//                } else {
//                    toastDefault(it.message())
//                }
//                onLoaded()
//            }, {
//                onLoaded()
//                it.toast()
//            }
//        ).addTo(onDestroyComposite)
    }

    private fun onLoaded() {
//        myActivity.dismissLoading()
        lock = false
        binding.pullToRefresh.finishRefresh()
        ProgressItem.removeFrom(adapter)
    }

    override fun onRefresh() {
        if (lock) {
            binding.pullToRefresh.finishRefresh()
            return
        }
        currentPage = 0
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
                val holder = AdapterUtils.getHolder(v)
//                val item = holder.getItem<NoticeItem>()
//                RxViewModel.liveEvent.postValue(RxBus.Event(FLAG1, item.data))
//                this.startMsgDetailsFragment(8)

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProclamationFragment = ProclamationFragment()

    }


}