package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.CURRENCY_TRAN
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityTransactionQueryBinding
import com.ramble.ramblewallet.fragment.TransPopupRecord
import com.ramble.ramblewallet.fragment.TransactionQueryFragment
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.wight.adapter.FragmentPagerAdapter2

/**
 * 时间　: 2021/12/17 11:11
 * 作者　: potato
 * 描述　: 交易查询
 */
class TransactionQueryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var adapter: MyAdapter
    private lateinit var binding: ActivityTransactionQueryBinding
    private var isSpread = false
    private lateinit var wallet: Wallet

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_query)
        initView()
        initListener()
    }

    private fun initView() {
        adapter = MyAdapter(supportFragmentManager, this)
        binding.pager.adapter = adapter
        binding.layoutTab.setViewPager(binding.pager)
        wallet = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        var currencyUnit = SharedPreferencesUtils.getSecurityString(this, CURRENCY_TRAN, "")
        if (currencyUnit.isNotEmpty()) {
            when (currencyUnit) {
                "ETH" -> {
                    binding.tvMyCurrency.text = "ETH"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_eth)
                }
                "BTC" -> {
                    binding.tvMyCurrency.text = "BTC"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_btc)
                }
                "TRX" -> {
                    binding.tvMyCurrency.text = "TRX"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_trx)
                }
                "SOL" -> {
                    binding.tvMyCurrency.text = "SOL"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_sola)
                }
            }
        } else {
            when (wallet.walletType) {
                1 -> {
                    binding.tvMyCurrency.text = "ETH"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_eth)
                }
                2 -> {
                    binding.tvMyCurrency.text = "TRX"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_trx)
                }
                3 -> {
                    binding.tvMyCurrency.text = "BTC"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_btc)
                }
                4 -> {
                    binding.tvMyCurrency.text = "SOL"
                    binding.tvMineTitle.text = getString(R.string.transaction_query_sola)
                }
            }
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.llMyCurrency.setOnClickListener(this)
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_TRAN_TYPE -> {
                when (event.data<Int>()) {
                    1 -> {
                        binding.tvMineTitle.text = getString(R.string.transaction_query_eth)
                        binding.tvMyCurrency.text = "ETH"
                    }
                    0 -> {
                        binding.tvMineTitle.text = getString(R.string.transaction_query_btc)
                        binding.tvMyCurrency.text = "BTC"
                    }
                    2 -> {
                        binding.tvMineTitle.text = getString(R.string.transaction_query_trx)
                        binding.tvMyCurrency.text = "TRX"
                    }
                    3 -> {
                        binding.tvMineTitle.text = getString(R.string.transaction_query_sola)
                        binding.tvMyCurrency.text = "SOL"
                    }
                }
                binding.ivMyCurrency.setBackgroundResource(R.drawable.vector_three_down)
                isSpread = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesUtils.saveSecurityString(this, CURRENCY_TRAN, "")
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.ll_my_currency -> {
                if (isSpread) {
                    binding.ivMyCurrency.setBackgroundResource(R.drawable.vector_three_down)
                    isSpread = false
                } else {
                    binding.ivMyCurrency.setBackgroundResource(R.drawable.vector_three_up)
                    isSpread = true
                    TransPopupRecord(this)
                        .setPopupGravity(Gravity.BOTTOM)
                        .setAlignBackground(true)
                        .showPopupWindow(binding.topView)
                }
            }
        }
    }

    class MyAdapter(fm: FragmentManager, activity: TransactionQueryActivity) :
        FragmentPagerAdapter2(fm) {
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
            return TransactionQueryFragment.newInstance(gameType)
        }

        override fun getCount(): Int = titles.size

        override fun getPageTitle(position: Int): CharSequence? = titles[position]
    }
}