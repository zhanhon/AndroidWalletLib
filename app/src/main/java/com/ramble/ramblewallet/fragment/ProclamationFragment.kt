package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.MessageCenterActivity
import com.ramble.ramblewallet.activity.MsgDetailsActivity
import com.ramble.ramblewallet.adapter.RecyclerViewFragment
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.FragmentProclamationBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start2
import com.ramble.ramblewallet.item.StationItem
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.pull.EndlessRecyclerViewScrollListener
import com.ramble.ramblewallet.pull.QMUIPullRefreshLayout
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
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
    private  var list= mutableListOf<Any?>()

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
        var req = Page.Req(currentPage, 1, 1)
        myActivity.mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        totalPage = data.totalPage
                        println("==================>getTransferInfo:${data}")
                        ArrayList<SimpleRecyclerItem>().apply {
                            data.records.forEach { item ->
                                if (SharedPreferencesUtils.getString(myActivity, READ_ID_NEW, "").isNotEmpty()) {
                                    if (  SharedPreferencesUtils.String2SceneList(
                                            SharedPreferencesUtils.getString(
                                                myActivity,
                                                READ_ID_NEW,
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

                    }

                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }
                onLoaded()
            }, {
                onLoaded()
                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )

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
                list = if ( SharedPreferencesUtils.getString(
                        myActivity,
                        READ_ID_NEW,
                        ""
                    ).isNotEmpty()){
                    SharedPreferencesUtils.String2SceneList(
                        SharedPreferencesUtils.getString(
                            myActivity,
                            READ_ID_NEW,
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
                SharedPreferencesUtils.saveString(myActivity, READ_ID_NEW, addId)
                itemBean.isRead=1
                adapter.notifyItemChanged(AdapterUtils.getHolder(v).adapterPosition)
                start2(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM3, itemBean.createTime)
                })

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProclamationFragment = ProclamationFragment()

    }


}