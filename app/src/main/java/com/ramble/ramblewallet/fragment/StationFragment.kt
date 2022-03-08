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
    private var list = mutableListOf<Any?>()
    private var saveList: ArrayList<Int> = arrayListOf()
    private var records: ArrayList<Page.Record> = arrayListOf()
    private var saveTokenList: ArrayList<StationItem> = arrayListOf()
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

            //刷新的监听事件
            binding.lyPullRefresh.setOnRefreshListener {
                binding.lyPullRefresh.finishRefresh() //刷新完成
                ProgressItem.addTo(adapter)
                reFreshData()
            }

            binding.recycler.adapter = adapter
            reusedView = binding.root
        }
        return reusedView
    }


    private fun reFreshData() {
        records = if (SharedPreferencesUtils.getString(
                myActivity,
                STATION_INFO,
                ""
            ).isNotEmpty()
        ) {
            SharedPreferencesUtils.string2SceneList(
                SharedPreferencesUtils.getString(
                    myActivity,
                    STATION_INFO,
                    ""
                )
            ) as ArrayList<Page.Record>

        } else {
            arrayListOf()
        }
        loadData()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun loadData() {

        if (records.isNotEmpty()) {
            ArrayList<SimpleRecyclerItem>().apply {
                records.forEach { item ->
                    add(StationItem(dataCheck(item)))
                }

                forEach {
                    if (it is StationItem) {
                        it.isEditable = isShowCheck
                        it.isChecked = isShowALLCheck
                    }
                }
                adapter.replaceAll(this.toList())

            }

        }

        apply(adapter.itemCount)
        onLoaded()

    }

    private fun dataCheck(item: Page.Record): Page.Record {
        if (SharedPreferencesUtils.getString(myActivity, READ_ID, "").isNotEmpty()) {
            if (SharedPreferencesUtils.string2SceneList(
                    SharedPreferencesUtils.getString(
                        myActivity,
                        READ_ID,
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

    private fun onLoaded() {
        ProgressItem.removeFrom(adapter)
    }


    override fun actualLazyLoad() {
        super.actualLazyLoad()
        reFreshData()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.item_msg_notic -> {
                if (isShowCheck) {
                    return
                }
                val itemBean = AdapterUtils.getHolder(v).getItem<StationItem>().data
                list = if (SharedPreferencesUtils.getString(
                        myActivity,
                        READ_ID,
                        ""
                    ).isNotEmpty()
                ) {
                    SharedPreferencesUtils.string2SceneList(
                        SharedPreferencesUtils.getString(
                            myActivity,
                            READ_ID,
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
                var addId = SharedPreferencesUtils.sceneList2String(list)
                itemBean.isRead = 1
                SharedPreferencesUtils.saveString(myActivity, READ_ID, addId)
                adapter.notifyItemChanged(AdapterUtils.getHolder(v).adapterPosition)
                start2(MsgDetailsActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, itemBean.title)
                    it.putString(ARG_PARAM2, itemBean.content)
                    it.putString(ARG_PARAM3, itemBean.createTime)
                    it.putInt(ARG_PARAM4, 2)
                })


            }
        }
    }


    private fun apply(count: Int) {
        binding.txtEmpty.isVisible = count == 0
        binding.lyPullRefresh.isVisible = count > 0
    }

    private fun setIDlist() {
        if (saveList.size > 0) {
            saveList.clear()
        }
        adapter.all.forEach {
            if (it is StationItem && it.isChecked) {
                saveList.add(it.data.id)
                saveTokenList.add(it)
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


    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_CHECK_MSG -> {
                passStatus(event.data())
            }
            Pie.EVENT_DELETE_MSG -> {
                setIDlist()
                if (saveList.isNotEmpty()) {
                    saveTokenList.forEach {
                        adapter.remove(it)
                    }
                    adapter.notifyDataSetChanged()
                    var list = records.iterator()
                    list.forEach {
                        if (saveList.contains(it.id)) {
                            list.remove()
                        }
                    }
                    var addId = SharedPreferencesUtils.sceneList2String(records)
                    SharedPreferencesUtils.saveString(myActivity, STATION_INFO, addId)
                    apply(adapter.itemCount)
                }
                passStatus(event.data())
            }
            else -> return
        }
    }


    private fun passStatus(isedit: Boolean) {
        setAdapterEditable(isedit)
    }

    companion object {
        @JvmStatic
        fun newInstance(): StationFragment = StationFragment()

    }

}
