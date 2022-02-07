package com.ramble.ramblewallet.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.constant.TOKEN_INFO_NO
import com.ramble.ramblewallet.custom.ItemTouchDelegate
import com.ramble.ramblewallet.custom.ItemTouchHelperCallback
import com.ramble.ramblewallet.custom.ItemTouchHelperImpl
import com.ramble.ramblewallet.databinding.ActivityTokenManageBinding
import com.ramble.ramblewallet.item.TokenManageItem
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.wight.adapter.AdapterUtils
import com.ramble.ramblewallet.wight.adapter.QuickItemDecoration
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import java.util.*

/***
 * 时间　: 2022/1/13 8:45
 * 作者　: potato
 * 描述　: 代币管理
 */
class TokenManageActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTokenManageBinding

    private var myStores: ArrayList<StoreInfo> = arrayListOf()
    private var saveTokenList: ArrayList<TokenManageItem> = arrayListOf()
    private var saveList: ArrayList<Int> = arrayListOf()
    private lateinit var itemTouchHelper: ItemTouchHelperImpl
    private val adapter = RecyclerAdapter()
    private var isShowCheck: Boolean = false
    private var isFirst: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token_manage)

        initView()
        initListener()


    }

    private fun initView() {
        //初始化推荐代币
        myStores = Gson().fromJson(
            SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
            object : TypeToken<ArrayList<StoreInfo>>() {}.type
        )
        LinearLayoutManager(this).apply {
            binding.rvTokenManageCurrency.layoutManager = this
        }
        binding.rvTokenManageCurrency.addItemDecoration(
            QuickItemDecoration.builder(this)
                .color(R.color.driver_gray, R.dimen.dp_08)
                .build()
        )
        binding.rvTokenManageCurrency.adapter = adapter
        ArrayList<SimpleRecyclerItem>().apply {
            myStores.forEach { o -> this.add(TokenManageItem(o)) }
            adapter.addAll(this.toList())
        }
        // 实现拖拽
        val itemTouchCallback = ItemTouchHelperCallback(object : ItemTouchDelegate {
            override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
                if (myStores.size > 1 && srcPosition < myStores.size && targetPosition < myStores.size) {
                    // 更换数据源中的数据Item的位置
                    Collections.swap(myStores, srcPosition, targetPosition)
                    // 更新UI中的Item的位置，主要是给用户看到交互效果
                    adapter.notifyItemMoved(srcPosition, targetPosition)
                    return true
                }
                return false
            }

            override fun uiOnDragging(viewHolder: RecyclerView.ViewHolder?) {
                viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#22000000"))
            }

            override fun uiOnClearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        })
        itemTouchHelper = ItemTouchHelperImpl(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTokenManageCurrency)
        itemTouchHelper.setDragEnable(true)

    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivDeleteToken.setOnClickListener(this)
        adapter.onClickListener = this
    }


    private fun setAdapterEditable(isEditable: Boolean) {
        isShowCheck = isEditable
        adapter.all.forEach {
            if (it is TokenManageItem) {
                it.isEditable = isEditable
            }
        }
        adapter.notifyDataSetChanged()
//        adapter.notifyItemRangeChanged(0, adapter.itemCount, isEditable)
    }

    private fun setIDlist() {
        if (saveList.size > 0) {
            saveList.clear()
        }
        adapter.all.forEach {
            if (it is TokenManageItem) {
                if (it.isChecked) {
                    saveList.add(it.data.id)
                    saveTokenList.add(it)
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_delete_token -> {
                isShowCheck = true
                if (!isFirst) {
                    setAdapterEditable(isShowCheck)
                    isFirst = true
                } else {
                    setIDlist()
                    if (saveList.isNotEmpty()) {
                        saveTokenList.forEach {
                            adapter.remove(it)
                        }
                        adapter.notifyDataSetChanged()
                        var list = myStores.iterator()
                        list.forEach {
                            if (saveList.contains(it.id)) {
                                list.remove()
                            }
                        }
                        SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myStores))
                        RxBus.emitEvent(Pie.EVENT_DEL_TOKEN, saveList)
                    }
                }
            }
            R.id.iv_token_status -> {
                if (isShowCheck) return
                val position = AdapterUtils.getHolder(v).adapterPosition
                val item = AdapterUtils.getHolder(v).getItem<TokenManageItem>().data
                if (item.isMyToken == 0) {
                    item.isMyToken = 1
                } else {
                    item.isMyToken = 0
                }
                myStores[position] = item
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myStores))
                adapter.notifyItemChanged(position)
                RxBus.emitEvent(Pie.EVENT_MINUS_TOKEN, item)

            }

        }
    }
}