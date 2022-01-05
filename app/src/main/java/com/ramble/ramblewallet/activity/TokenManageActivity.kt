package com.ramble.ramblewallet.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.TokenManageAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.TokenManageBean
import com.ramble.ramblewallet.constant.SELECTED_TOKENS
import com.ramble.ramblewallet.custom.ItemTouchDelegate
import com.ramble.ramblewallet.custom.ItemTouchHelperCallback
import com.ramble.ramblewallet.custom.ItemTouchHelperImpl
import com.ramble.ramblewallet.databinding.ActivityTokenManageBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import java.util.*

class TokenManageActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTokenManageBinding
    private lateinit var tokenManageAdapter: TokenManageAdapter
    private var tokenManageBean: ArrayList<TokenManageBean> = arrayListOf()
    private var saveTokenList: ArrayList<String> = arrayListOf()
    private lateinit var saveTokenListJson: String
    private lateinit var itemTouchHelper: ItemTouchHelperImpl

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_token_manage)
        saveTokenList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, SELECTED_TOKENS, ""),
            object : TypeToken<ArrayList<String>>() {}.type
        )
        saveTokenListJson = SharedPreferencesUtils.getString(this, SELECTED_TOKENS, "")
        //初始化推荐代币
        tokenManageBean.add(
            TokenManageBean(
                0,
                "TFT",
                if (saveTokenListJson.contains("TFT")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                1,
                "WBTC",
                if (saveTokenListJson.contains("WBTC")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                2,
                "DAI",
                if (saveTokenListJson.contains("DAI")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                3,
                "USDC",
                if (saveTokenListJson.contains("USDC")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                4,
                "USDT",
                if (saveTokenListJson.contains("USDT")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                5,
                "LINK",
                if (saveTokenListJson.contains("LINK")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                6,
                "YFI",
                if (saveTokenListJson.contains("YFI")) 1 else 0,
                false
            )
        )
        tokenManageBean.add(
            TokenManageBean(
                7,
                "UNI",
                if (saveTokenListJson.contains("UNI")) 1 else 0,
                false
            )
        )
        loadData(false)
        // 实现拖拽
        val itemTouchCallback = ItemTouchHelperCallback(object : ItemTouchDelegate {
            override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
                if (tokenManageBean.size > 1 && srcPosition < tokenManageBean.size && targetPosition < tokenManageBean.size) {
                    // 更换数据源中的数据Item的位置
                    Collections.swap(tokenManageBean, srcPosition, targetPosition)
                    // 更新UI中的Item的位置，主要是给用户看到交互效果
                    tokenManageAdapter.notifyItemMoved(srcPosition, targetPosition)
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

        binding.ivBack.setOnClickListener(this)
        binding.ivDeleteToken.setOnClickListener(this)
    }

    private fun loadData(isNeedDelete: Boolean) {
        tokenManageAdapter = TokenManageAdapter(tokenManageBean, isNeedDelete)
        binding.rvTokenManageCurrency.adapter = tokenManageAdapter

        tokenManageAdapter.addChildClickViewIds(R.id.iv_token_status)
        tokenManageAdapter.addChildClickViewIds(R.id.cl_delete)
        tokenManageAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (adapter.getItem(position) is TokenManageBean) {
                when (view.id) {
                    R.id.iv_token_status -> {
                        if ((adapter.getItem(position) as TokenManageBean).status == 0) {
                            tokenManageBean[position] = TokenManageBean(
                                position,
                                (adapter.getItem(position) as TokenManageBean).name,
                                1,
                                false
                            )
                            tokenManageAdapter.notifyItemChanged(position)
                            saveTokenList.add((adapter.getItem(position) as TokenManageBean).name)
                        } else {
                            tokenManageBean[position] = TokenManageBean(
                                position,
                                (adapter.getItem(position) as TokenManageBean).name,
                                0,
                                false
                            )
                            tokenManageAdapter.notifyItemChanged(position)
                            saveTokenList.remove((adapter.getItem(position) as TokenManageBean).name)
                        }
                        SharedPreferencesUtils.saveString(
                            this,
                            SELECTED_TOKENS,
                            Gson().toJson(saveTokenList)
                        )
                    }
                    R.id.cl_delete -> {
                        if ((adapter.getItem(position) as TokenManageBean).isClickDelete) {
                            tokenManageBean[position] = TokenManageBean(
                                position,
                                (adapter.getItem(position) as TokenManageBean).name,
                                0,
                                false
                            )
                            tokenManageAdapter.notifyItemChanged(position)
                        } else {
                            tokenManageBean[position] = TokenManageBean(
                                position,
                                (adapter.getItem(position) as TokenManageBean).name,
                                0,
                                true
                            )
                            tokenManageAdapter.notifyItemChanged(position)
                        }
                    }
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
                loadData(true)
            }
        }
    }
}