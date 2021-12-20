package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityTransactionQueryBinding
import com.ramble.ramblewallet.fragment.TransactionQueryFragment
import com.ramble.ramblewallet.wight.adapter.FragmentPagerAdapter2

/**
 * 时间　: 2021/12/17 11:11
 * 作者　: potato
 * 描述　: 交易查询
 */
class TransactionQueryActivity : BaseActivity(), View.OnClickListener{
    private lateinit var adapter: MyAdapter
    private lateinit var binding: ActivityTransactionQueryBinding


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_query)
        initView()
        initListener()

    }

    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.transaction_query)
        adapter = MyAdapter(supportFragmentManager,this)
        binding.pager.adapter = adapter
        binding.layoutTab.setViewPager(binding.pager)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivMineRight.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.iv_mine_right -> {

            }
        }
    }

    class MyAdapter(fm: FragmentManager,activity: TransactionQueryActivity) : FragmentPagerAdapter2(fm) {
        private val titles: Array<String> = arrayOf(activity.getString(R.string.transaction_query_all), activity.getString(R.string.transaction_query_chu), activity.getString(R.string.transaction_query_ru),activity.getString(R.string.transaction_query_fail))
        override fun getItem(position: Int): Fragment {
            val gameType = when (position) {
                0 -> 1
                1 -> 4
                2 -> 3
                3 -> 2
                else -> 1
            }
            return TransactionQueryFragment.newInstance(gameType)
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}