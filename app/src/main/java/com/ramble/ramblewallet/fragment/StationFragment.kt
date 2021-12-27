package com.ramble.ramblewallet.fragment

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
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.constant.ARG_PARAM4
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.StationItem
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import java.util.*

/**
 * 时间　: 2021/12/16 9:29
 * 作者　: potato
 * 描述　: 站内消息
 */
open class StationFragment : RecyclerViewFragment(), QMUIPullRefreshLayout.OnPullListener {
    private lateinit var binding: FragmentProclamationBinding
    lateinit var myActivity: MessageCenterActivity
        private set
    private lateinit var endless: EndlessRecyclerViewScrollListener
    var isShowCheck: Boolean = false
    var isShowALLCheck: Boolean = false
    var isEmpty: Boolean = false
    private var dlist: ArrayList<Int>? = ArrayList()

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
                        currentPage += 1
                        ProgressItem.addTo(adapter)
                        load()
                    }
                    if (currentPage >= totalPage) {
//                        toastDefault("已加载完所有数据！")
                    }
                }
                binding.recycler.addOnScrollListener(endless)
            }
            binding.recycler.adapter = adapter
            reusedView = binding.root
        }
//        binding.tvCancel.setOnClickListener(this)
//        binding.tvDelete.setOnClickListener(this)
        return reusedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isEmpty) {
            setEmptyView()
            return
        }
        setInitChecked()
    }

    override fun onResume() {
        super.onResume()
        if ((lock || currentPage > 0) && adapter.isEmpty) {
            setEmptyView()
        }
    }

    private fun load() {
        lock = true
        currentPage++
        var page=Page()
        page.pageNo=1
        page.pageSize=20
        page.totalCount=20
        page.totalPage=1
        var list= arrayListOf<Page.Record>()
        for (j in 0 until 20) {
            var v1= Page.Record()
            v1.content= "11111111111111111$j"
            v1.title="222222222222222$j"
            v1.isReaded=0
            list.add(v1)
        }
        page.records=list
        ArrayList<SimpleRecyclerItem>().apply {
            page.records.forEach { item -> add(StationItem(item)) }
            if (page.pageNo == 1) {
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
        totalPage=1
//        model.getUserLetterPage(Page.Req(currentPage + 1, Pie.PAGE_SIZE)).subscribe(
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
//                            page.records.forEach { item -> add(StationItem(item)) }
//                            if (page.pageNo == 1) {
//                                forEach {
//                                    if (it is StationItem) {
//                                        it.isEditable = isShowCheck
//                                        it.isChecked = isShowALLCheck
//                                    }
//                                }
//                                adapter.replaceAll(this.toList())
//                            } else {
//                                forEach {
//                                    if (it is StationItem) {
//                                        it.isEditable = isShowCheck
//                                        it.isChecked = isShowALLCheck
//                                    }
//                                }
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
        load()
    }

    override fun actualLazyLoad() {
        super.actualLazyLoad()
        binding.pullToRefresh.setToRefreshDirectly()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.tv_cancel -> {//全选
//                selectTvCancel()
//            }
//            R.id.tv_delete -> {//删除
//                setIDlist()
//                if (dlist!!.isEmpty()) {
//                    toastDefault("未选中")
//                    return
//                }
//                model.getUserLetterRemove(Remove.Req(dlist!!)).subscribe(
//                    {
//                        if (it.status()) {
//                            myActivity.dismissLoading()
//                            setInitChecked()
//                            init()//刷新
//                        } else {
//                            myActivity.dismissLoading()
//                            toastDefault(it.message())
//                        }
//
//                    }, {
//                        it.toast()
//                    }
//                ).addTo(onDestroyComposite)
//            }
            R.id.item_msg_notic -> {
                if (isShowCheck) {
//                    toastDefault("处于编辑状态不可查看")
                    return
                }
                val itemBean = AdapterUtils.getHolder(v).getItem<StationItem>().data
                start2(MsgDetailsActivity::class.java,Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM4, "消息详情")
                })
//                if (itemBean.isReaded==0){
//                    model.getUserLetterEdit(
//                        Edit.Req(
//                            itemBean.content,
//                            itemBean.createTime,
//                            itemBean.id,
//                            itemBean.isReaded,
//                            itemBean.isPopup,
//                            itemBean.isPush,
//                            itemBean.platformId,
//                            itemBean.title
//                        )
//                    ).subscribe(
//                        {
//                            if (it.status()) {
//                                LogUtils.eTag("editUserLetter",1111)
//                                myActivity.dismissLoading()
//                                init()
//
//                            } else {
//                                myActivity.dismissLoading()
//                                toastDefault(it.message())
//                            }
//                        }, {
//                            LogUtils.eTag("editUserLetter",it.message)
//                            it.printStackTrace()
//                        }
//                    ).addTo(onDestroyComposite)
//                }
//                RxViewModel.liveEvent.postValue(RxBus.Event(FLAG2, itemBean))
//                this.startMsgDetailsFragment(9)

            }
        }
    }

    override fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.pullToRefresh.isVisible = count > 0
    }

    private fun setIDlist() {
        if (dlist!!.size > 0) {
            dlist!!.clear()
        }
        adapter.all.forEach {
            if (it is StationItem) {
                if (it.isChecked) {
                    dlist!!.add(it.data.id)
                }
            }
        }
    }

//    private fun selectTvCancel() {
//        if (tv_cancel.text == "全  选") {
//            setAdapterALLChecked(true)
//            tv_cancel.text = "取消全选"
//        } else {
//            setAdapterALLChecked(false)
//            tv_cancel.text = "全  选"
//        }
//    }

    private fun setAdapterEditable(isEditable: Boolean) {
        isShowCheck = isEditable
        adapter.all.forEach {
            if (it is StationItem) {
                it.isEditable = isEditable
            }
        }
        adapter.notifyItemRangeChanged(0, adapter.itemCount, isEditable)
    }

    private fun setInitChecked() {
        adapter.all.forEach {
            if (it is StationItem) {
                it.isEditable = false
                it.isChecked = false
            }
        }
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }
    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_CHECK_MSG -> {
                passStatus(event.data())
            }
            else -> return
        }
    }
    private fun setAdapterALLChecked(isChecked: Boolean) {
        isShowALLCheck = isChecked
        adapter.all.forEach {
            if (it is StationItem) {
                it.isChecked = isChecked
            }
        }
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }

    private fun passStatus(isedit: Boolean) {
//        if (isedit) {
//            binding.layoutBottoom.visibility = View.VISIBLE
//        } else {
//            binding.layoutBottoom.visibility = View.GONE
//        }
        setAdapterEditable(isedit)
    }

    companion object {
        @JvmStatic
        fun newInstance(): StationFragment = StationFragment()

    }

    private fun setEmptyView() {
        isEmpty = true
//        binding.pullToRefresh.visibility = View.GONE
//        binding.emptyView.visibility = View.VISIBLE
//        binding.layoutBottoom.visibility = View.GONE
    }
}
