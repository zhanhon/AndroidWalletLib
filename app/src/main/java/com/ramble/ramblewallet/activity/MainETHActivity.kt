package com.ramble.ramblewallet.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.EmptyReq
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.RateBeen
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainEthBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalleETHManager.getTokenBalance
import com.ramble.ramblewallet.network.rateInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.asyncAnimator
import java.math.BigDecimal

class MainETHActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainEthBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private var rateBean: List<RateBeen> = arrayListOf()
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var isClickEyes = false
    private var saveTokenList: ArrayList<String> = arrayListOf()
    private var animator: ObjectAnimator? = null

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_078DC2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_eth)
        saveTokenList.add("ETH")
        SharedPreferencesUtils.saveString(this, SELECTED_TOKENS, Gson().toJson(saveTokenList))
        saveTokenList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, SELECTED_TOKENS, ""),
            object : TypeToken<ArrayList<String>>() {}.type
        )
        rateBean = Gson().fromJson(
            SharedPreferencesUtils.getString(this, RATEINFO, ""),
            object : TypeToken<ArrayList<RateBeen>>() {}.type
        )
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, RMB)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )


        binding.scroll01.post { binding.scroll01.fullScroll(ScrollView.FOCUS_UP) } //初始值
        findViewById<RelativeLayout>(R.id.toolbar).setBackgroundResource(0)
        binding.scroll01.setOnScrollChangeListener { view, x, y, oldx, oldy ->
            if (y > 64) {
                binding.txtTitle.visibility = View.INVISIBLE
                binding.linearBtns.visibility = View.INVISIBLE
                binding.relBtns.visibility = View.VISIBLE
                binding.card01.visibility = View.VISIBLE
                binding.toolbar.background = getDrawable(R.mipmap.ic_home_bg_eth_small)
            } else {
                binding.txtTitle.visibility = View.VISIBLE
                binding.linearBtns.visibility = View.VISIBLE
                binding.relBtns.visibility = View.INVISIBLE
                binding.card01.visibility = View.GONE
                binding.toolbar.setBackgroundResource(0)
            }
        }

        binding.tvWalletName.text = saveWalletList[0].walletName
        when (currencyUnit) {
            RMB -> binding.tvCurrencyUnit.text = "￥"
            HKD -> binding.tvCurrencyUnit.text = "HK$"
            USD -> binding.tvCurrencyUnit.text = "$"
        }
        Thread {
            binding.tvBalanceTotal.text = getTokenBalance(saveWalletList[0].address)
        }
        binding.tvEthAddress.text = getSubStr(saveWalletList[0].address)

        setOnClickListener()
    }

    private fun getSubStr(str: String): String? {
        val subStr1 = str.substring(0, 6)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 4, strLength)
        return "$subStr1...$subStr2"
    }

    private fun startSyncAnimation() {
        if (animator != null) {
            return
        }
        animator = binding.ivBalanceRefresh1.asyncAnimator()
        animator = binding.ivBalanceRefresh2.asyncAnimator()
    }

    private fun cancelSyncAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setOnClickListener() {
        binding.btnMenu.setOnClickListener(this)
        binding.ivNoticeTop.setOnClickListener(this)
        binding.ivMineTop.setOnClickListener(this)

        binding.ivGatheringTop.setOnClickListener(this)
        binding.llGathering.setOnClickListener(this)

        binding.ivTransferTop.setOnClickListener(this)
        binding.llTransfer.setOnClickListener(this)

        binding.ivScanTop.setOnClickListener(this)
        binding.llScan.setOnClickListener(this)

        binding.ivTokenManageClick.setOnClickListener(this)
        binding.ivTokenManageClick01.setOnClickListener(this)

        binding.ivBalanceRefresh1.setOnClickListener(this)
        binding.ivBalanceRefresh2.setOnClickListener(this)
        binding.ivEyes.setOnClickListener(this)
        binding.ivCopy.setOnClickListener(this)

    }

    private fun showTransferGatheringDialog(tokenName: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_transfer_gathering)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvTokenTitle = window.findViewById<TextView>(R.id.tv_currency_title)
            val tvTransfer = window.findViewById<TextView>(R.id.tv_transfer)
            val tvGathering = window.findViewById<TextView>(R.id.tv_gathering)

            tvTokenTitle.text = tokenName

            tvTransfer.setOnClickListener { v1: View? ->
                startActivity(Intent(this, TransferActivity::class.java))
                dialog.dismiss()
            }
            tvGathering.setOnClickListener { v1: View? ->
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH")
                    putExtra(ARG_PARAM2, saveWalletList[0].address)
                })
                dialog.dismiss()
            }

            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params
            //点击空白处不关闭dialog
            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnMenu -> {
                startActivity(Intent(this, WalletManageActivity::class.java))
            }
            R.id.iv_notice_top -> {
                startActivity(Intent(this, MessageCenterActivity::class.java))
            }
            R.id.iv_mine_top -> {
                startActivity(Intent(this, MineActivity::class.java))
            }
            R.id.iv_gathering_top, R.id.ll_gathering -> {
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH")
                    putExtra(ARG_PARAM2, saveWalletList[0].address)
                })
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java))
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java))
            }
            R.id.iv_token_manage_click, R.id.iv_token_manage_click_01 -> {
                startActivity(Intent(this, TokenActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH")
                })
            }
            R.id.iv_balance_refresh_1, R.id.iv_balance_refresh_2 -> {
                startSyncAnimation()
                Handler().postDelayed({
                    refreshData()
                }, 2000)
            }
            R.id.iv_eyes -> {
                if (isClickEyes) {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_open)
                    binding.tvBalanceTotal.text = "1212323"
                    isClickEyes = false
                } else {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_close)
                    binding.tvBalanceTotal.text = "******"
                    isClickEyes = true
                }
            }
            R.id.iv_copy -> {
                ClipboardUtils.copy(binding.tvEthAddress.text.toString())
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun refreshData() {
        mApiService.getRateInfo(EmptyReq().toApiRequest(rateInfoUrl))
            .applyIo().subscribe(
                {
                    if (it.code() == 1) {
                        it.data()?.let { data ->
                            SharedPreferencesUtils.saveString(
                                this,
                                RATEINFO,
                                Gson().toJson(data)
                            )
                            println("-=-=-=->${Gson().toJson(data)}")
                            mainETHTokenBean.clear()
                            saveTokenList = Gson().fromJson(
                                SharedPreferencesUtils.getString(this, SELECTED_TOKENS, ""),
                                object : TypeToken<ArrayList<String>>() {}.type
                            )
                            if (rateBean.isNotEmpty() && saveTokenList.isNotEmpty()) {
                                saveTokenList.forEach { saveToken ->
                                    rateBean.forEach { rateBean ->
                                        if (saveToken == rateBean.currencyType) {
                                            mainETHTokenBean.add(
                                                MainETHTokenBean(
                                                    rateBean.currencyType,
                                                    BigDecimal(10.12123),
                                                    BigDecimal(rateBean.rateUsd),
                                                    currencyUnit,
                                                    BigDecimal(rateBean.change)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            mainAdapter = MainAdapter(mainETHTokenBean)
                            binding.rvCurrency.adapter = mainAdapter
                            mainAdapter.setOnItemClickListener { adapter, view, position ->
                                if (adapter.getItem(position) is MainETHTokenBean) {
                                    showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean).name)
                                }
                            }
                        }
                    } else {
                        println("-=-=-=->${it.message()}")
                    }
                    cancelSyncAnimation()
                }, {
                    cancelSyncAnimation()
                    println("-=-=-=->${it.printStackTrace()}")
                }
            )
    }

}