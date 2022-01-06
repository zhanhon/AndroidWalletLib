package com.ramble.ramblewallet.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.MessageCenterActivity
import com.ramble.ramblewallet.activity.MsgDetailsActivity
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.StationItem
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import java.util.*

/**
 * 时间　: 2021/12/16 9:29
 * 作者　: potato
 * 描述　: 站内消息
 */
open class StationFragment : BaseFragment() {
    private lateinit var binding: FragmentProclamationBinding
    lateinit var myActivity: MessageCenterActivity
        private set
    var isShowCheck: Boolean = false
    var isShowALLCheck: Boolean = false
    var isEmpty: Boolean = false
    private var dlist: ArrayList<Int>? = ArrayList()
    private  var list= mutableListOf<Any?>()
    private var currentPage = 1
    private var totalPage = 1
    private val adapter = RecyclerAdapter()

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
                binding.lyPullRefresh.finishLoadMore() //加载完成
                ProgressItem.addTo(adapter)
                currentPage += 1
                loadData()

            }
            binding.recycler.adapter = adapter
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
        reFreshData()
    }

    private fun loadData() {
        lock = true
        currentPage++
        var page = Page()
        page.pageNo = 1
        page.pageSize = 20
        page.totalCount = 20
        page.totalPage = 1
        var list = arrayListOf<Page.Record>()

        var v1 = Page.Record()
        v1.content = "11111111111111111"
        v1.title = "222222222222222"
        v1.isRead = 0
        v1.id = 1
        list.add(v1)
        var v2 = Page.Record()
        v2.content = "11111111111111111"
        v2.title = "222222222222222"
        v2.isRead = 0
        v2.id = 2
        list.add(v2)
        var v3 = Page.Record()
        v3.content = "11111111111111111"
        v3.title = "222222222222222"
        v3.isRead = 0
        v3.id = 3
        list.add(v3)
        var v4 = Page.Record()
        v4.content = "11111111111111111"
        v4.title = "222222222222222"
        v4.isRead = 0
        v4.id = 4
        list.add(v4)
        var v5 = Page.Record()
        v5.content = "11111111111111111"
        v5.title = "222222222222222"
        v5.isRead = 0
        v5.id = 5
        list.add(v5)
        page.records = list
        ArrayList<SimpleRecyclerItem>().apply {
            page.records.forEach { item ->
                if (SharedPreferencesUtils.getString(myActivity, READ_ID, "").isNotEmpty()) {
                    if (  SharedPreferencesUtils.String2SceneList(
                            SharedPreferencesUtils.getString(
                                myActivity,
                                READ_ID,
                                ""
                            )
                        ).contains(item.id)){
                        item.isRead = 1
                    }else{
                        item.isRead = 0
                    }

                } else {
                    item.isRead = 0
                }
                add(StationItem(item))
            }
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
        totalPage = 1
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
        ProgressItem.removeFrom(adapter)
    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        reFreshData()
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
                 list = if ( SharedPreferencesUtils.getString(
                         myActivity,
                         READ_ID,
                         ""
                     ).isNotEmpty()){
                     SharedPreferencesUtils.String2SceneList(
                         SharedPreferencesUtils.getString(
                             myActivity,
                             READ_ID,
                             ""
                         )
                     )
                 }else{
                     mutableListOf()
                 }
                list.size
                if (list.isNotEmpty()){
                    if (!list.contains(itemBean.id)){
                        list.add(itemBean.id)
                    }
                }else{
                    list.add(itemBean.id)
                }
                list.size
                var addId = SharedPreferencesUtils.SceneList2String(list)
                itemBean.isRead=1
                SharedPreferencesUtils.saveString(myActivity, READ_ID, addId)
                adapter.notifyItemChanged(AdapterUtils.getHolder(v).adapterPosition)
                start2(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM3, itemBean.createTime)
                })


            }
        }
    }


    private fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.lyPullRefresh.isVisible = count > 0
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
        setAdapterEditable(isedit)
    }

    companion object {
        @JvmStatic
        fun newInstance(): StationFragment = StationFragment()

    }

}
