package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
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
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.wight.ProgressItem
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import java.util.*


/**
 * 时间　: 2021/12/15 14:29
 * 作者　: potato
 * 描述　:公告
 */
class ProclamationFragment : BaseFragment() {
    private lateinit var binding: FragmentProclamationBinding
    lateinit var myActivity: MessageCenterActivity
        private set
    var isShowCheck: Boolean = false
    var isShowALLCheck: Boolean = false
    private var list = mutableListOf<Any?>()
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
                if (currentPage < totalPage) {
                    binding.lyPullRefresh.finishLoadMore() //加载完成
                    ProgressItem.addTo(adapter)
                    currentPage += 1
                    loadData()
                } else {
                    binding.lyPullRefresh.finishLoadMoreWithNoMoreData()
                }
            }
            binding.recycler.adapter = adapter
            reusedView = binding.root
        }
        return reusedView
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        var lang = when (SharedPreferencesUtils.getString(myActivity, LANGUAGE, CN)) {
            CN -> {
                1
            }
            TW -> {
                2
            }
            else -> {
                3
            }
        }
        var req = Page.Req(currentPage, 20, lang)
        myActivity.mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        totalPage = data.totalPage
                        println("==================>getTransferInfo:${data}")
                        dataItem(data)
                        apply(adapter.itemCount)
                        onLoaded()
                    }

                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }

            }, {
                onLoaded()
                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )

    }

    private fun dataItem(data: Page) {
        ArrayList<SimpleRecyclerItem>().apply {
            data.records.forEach { item ->
                add(StationItem(dataCheck(item)))
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
    }

    private fun dataCheck(item: Page.Record): Page.Record {
        if (SharedPreferencesUtils.getString(myActivity, READ_ID_NEW, "")
                .isNotEmpty()
        ) {
            if (SharedPreferencesUtils.String2SceneList(
                    SharedPreferencesUtils.getString(
                        myActivity,
                        READ_ID_NEW,
                        ""
                    )
                ).contains(item.id)
            ) {
                item.isRead = 1
            } else {
                item.isRead = 0
            }

        } else {
            item.isRead = 0
        }
        return item
    }


    private fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.lyPullRefresh.isVisible = count > 0
    }

    private fun onLoaded() {
        ProgressItem.removeFrom(adapter)
    }

    private fun reFreshData() {
        currentPage = 1
        totalPage = 1
        loadData()
    }

    override fun onResume() {
        super.onResume()

    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        reFreshData()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_msg_notic -> {
                val itemBean = AdapterUtils.getHolder(v).getItem<StationItem>().data
                list = if (SharedPreferencesUtils.getString(
                        myActivity,
                        READ_ID_NEW,
                        ""
                    ).isNotEmpty()
                ) {
                    SharedPreferencesUtils.String2SceneList(
                        SharedPreferencesUtils.getString(
                            myActivity,
                            READ_ID_NEW,
                            ""
                        )
                    )
                } else {
                    mutableListOf()
                }
                if (list.isNotEmpty()) {
                    if (!list.contains(itemBean.id)) {
                        list.add(itemBean.id)
                    }
                } else {
                    list.add(itemBean.id)
                }
                var addId = SharedPreferencesUtils.SceneList2String(list)
                SharedPreferencesUtils.saveString(myActivity, READ_ID_NEW, addId)
                itemBean.isRead = 1
                adapter.notifyItemChanged(AdapterUtils.getHolder(v).adapterPosition)
                start2(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM3, itemBean.createTime)
                    it.putInt(ARG_PARAM4, 22)
                })

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProclamationFragment = ProclamationFragment()

    }


}