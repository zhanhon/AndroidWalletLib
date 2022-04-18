package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.databinding.ActivityQueryBinding
import com.ramble.ramblewallet.fragment.QueryFragment
import com.ramble.ramblewallet.wight.adapter.FragmentPagerAdapter2


/**
 * 时间　: 2022/4/18 13:50
 * 作者　: potato
 * 描述　: 代币历史记录
 */
class QueryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapter: MyAdapter
    private lateinit var binding: ActivityQueryBinding
    private lateinit var tokenBean: MainETHTokenBean
    private lateinit var countAddress:String
    private lateinit var symbol:String

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_query)
        countAddress = intent.getStringExtra(ARG_PARAM1).toString()
        tokenBean = intent.getSerializableExtra(ARG_PARAM2) as MainETHTokenBean
        symbol = intent.getStringExtra(ARG_PARAM3).toString()
        initView()
        initListener()
    }

    private fun initView() {
        adapter = MyAdapter(supportFragmentManager, this, tokenBean.contractAddress)
        binding.pager.adapter = adapter
        binding.layoutTab.setViewPager(binding.pager)
        binding.tvMineTitle.text = tokenBean.symbol
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.tvTransfer.setOnClickListener(this)
        binding.tvGathering.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_transfer->{
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, tokenBean)
                })
            }
            R.id.tv_gathering->{
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, symbol)
                    putExtra(ARG_PARAM2, countAddress)
                })
            }
        }
    }

    class MyAdapter(fm: FragmentManager, activity: QueryActivity, address: String) :
        FragmentPagerAdapter2(fm) {
        private val address=address
        private val titles: Array<String> = arrayOf(
            activity.getString(R.string.all),
            activity.getString(R.string.cash_out),
            activity.getString(R.string.cash_in),
            activity.getString(R.string.fail)
        )

        override fun getItem(position: Int): Fragment {
            val gameType = when (position) {
                0 -> 1
                1 -> 4
                2 -> 3
                3 -> 2
                else -> 1
            }
            return QueryFragment.newInstance(gameType, address)
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}