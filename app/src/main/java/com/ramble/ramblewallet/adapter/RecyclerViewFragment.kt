package com.ramble.ramblewallet.adapter

import android.os.Bundle
import android.view.View

import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.base.Fragment
import com.ramble.ramblewallet.wight.adapter.OnDataSetChanged
import com.ramble.ramblewallet.wight.adapter.RecyclerAdapter

/**
 * 时间　: 2021/12/15 19:03
 * 作者　: potato
 * 描述　:
 */
abstract class RecyclerViewFragment : BaseFragment(), QMUIPullRefreshLayout.OnPullListener,
    OnDataSetChanged {
    protected val adapter = RecyclerAdapter()
    protected open var currentPage = 0
    protected var totalPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.adapter.onClickListener = this
        this.adapter.onDataSetChanged = this
    }

    override fun onMoveTarget(offset: Int) {
    }

    override fun onMoveRefreshView(offset: Int) {
    }

    override fun onRefresh() {
    }

    override fun apply(count: Int) {
    }

}