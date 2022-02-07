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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.EmptyReq
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.RateBeen
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainEthBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils
import com.ramble.ramblewallet.ethereum.TransferEthUtils.getBalanceETH
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.rateInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import java.math.BigDecimal

class MainETHActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainEthBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private var rateBean: List<RateBeen> = arrayListOf()
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var isClickEyes = false
    private var animator: ObjectAnimator? = null
    private var saveTokenList: ArrayList<StoreInfo> = arrayListOf()
    private lateinit var walletSelleted: WalletETH
    private var ethBalance: BigDecimal = BigDecimal("0.00000000")
    private var tokenBalance: BigDecimal = BigDecimal("0.00000000")
    private var totalBalance: BigDecimal = BigDecimal("0.00000000")
    private var rateETH: String? = ""
    private var rateToken: String? = ""

    //DAI:4 0x16aFDD5dfE386052766b798bFA37DAec4b81155a
    private var contractAddress = "0xb319d1A045ffe108D14195F7C5d60Be220436a34" //测试节点ERC-USDT:6合约地址

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_078DC2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_eth)
        initClick()
    }

    override fun onResume() {
        super.onResume()
        initData()
        refreshData()
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADDRESS_TRANS_SCAN -> {
                start(TransferActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, event.data())
                    it.putString(ARG_PARAM2, "ETH")
                    it.putBoolean(ARG_PARAM3, false)
                    it.putString(ARG_PARAM4, rateETH)
                })
            }
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
                    putExtra(ARG_PARAM2, walletSelleted.address)
                })
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, "ETH")
                    putExtra(ARG_PARAM3, false)
                    putExtra(ARG_PARAM4, rateETH)
                })
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
                })
            }
            R.id.iv_token_manage_click -> {
                startActivity(Intent(this, TokenActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH")
                })
            }
            R.id.iv_balance_refresh -> {
                startSyncAnimation()
                Handler().postDelayed({
                    initData()
                    refreshData()
                }, 2000)
            }
            R.id.iv_eyes -> {
                if (isClickEyes) {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_open)
                    binding.tvBalanceTotal.text = DecimalFormatUtil.format2.format(totalBalance)
                    isClickEyes = false
                } else {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_close)
                    binding.tvBalanceTotal.text = "******"
                    isClickEyes = true
                }
            }
            R.id.iv_copy -> {
                ClipboardUtils.copy(walletSelleted.address)
            }
        }
    }

    private fun initClick() {
        binding.appbarLayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbarLeft.visibility = View.VISIBLE
                    binding.txtTitle.visibility = View.INVISIBLE
                    isShow = true
                } else if (isShow) {
                    binding.toolbarLeft.visibility = View.GONE
                    binding.txtTitle.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })

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

        binding.ivBalanceRefresh.setOnClickListener(this)
        binding.ivEyes.setOnClickListener(this)
        binding.ivCopy.setOnClickListener(this)

    }

    private fun initData() {
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, RMB)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<WalletETH>() {}.type
        )
        binding.tvWalletName.text = walletSelleted.walletName
        when (currencyUnit) {
            RMB -> binding.tvCurrencyUnit.text = "￥"
            HKD -> binding.tvCurrencyUnit.text = "HK$"
            USD -> binding.tvCurrencyUnit.text = "$"
        }
        if (WalletETHUtils.isEthValidAddress(walletSelleted.address)) {
            Thread {
                ethBalance = getBalanceETH(walletSelleted.address)
                if (ethBalance != BigDecimal("0.00000000")) {
                    refreshData()
                }
                tokenBalance =
                    TransferEthUtils.getBalanceToken(walletSelleted.address, contractAddress)
                if (tokenBalance != BigDecimal("0.000000")) {
                    refreshData()
                }
            }.start()
        }
        binding.tvEthAddress.text = addressHandle(walletSelleted.address)
    }

    private fun setBalanceETH(balance: BigDecimal) {
        postUI {
            binding.tvBalanceTotal.text = DecimalFormatUtil.format2.format(balance)
        }
    }

    private fun addressHandle(str: String): String? {
        val subStr1 = str.substring(0, 10)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 6, strLength)
        return "$subStr1...$subStr2"
    }

    private fun startSyncAnimation() {
        if (animator != null) {
            return
        }
        animator = binding.ivBalanceRefresh.asyncAnimator()
    }

    private fun cancelSyncAnimation() {
        animator?.cancel()
        animator = null
    }

    private fun showTransferGatheringDialog(mainETHTokenBean: MainETHTokenBean) {
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

            tvTokenTitle.text = mainETHTokenBean.name

            tvTransfer.setOnClickListener { v1: View? ->
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, "ETH-${mainETHTokenBean.name}")
                    putExtra(ARG_PARAM3, true)
                    putExtra(ARG_PARAM4, rateETH)
                })
                dialog.dismiss()
            }
            tvGathering.setOnClickListener { v1: View? ->
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH-${mainETHTokenBean.name}")
                    putExtra(ARG_PARAM2, walletSelleted.address)
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

    private var ethLegal = BigDecimal("0.00")
    private var tokenLegal = BigDecimal("0.00")

    @SuppressLint("CheckResult")
    private fun refreshData() {
        mApiService.getRateInfo(EmptyReq().toApiRequest(rateInfoUrl))
            .applyIo().subscribe(
                {
                    if (it.code() == 1) {
                        it.data()?.let { data ->
                            rateBean = data
                            mainETHTokenBean.clear()
                            if (rateBean.isNotEmpty()) {
                                rateBean.forEach { //标题：ETH
                                    if (it.currencyType == "ETH") {
                                        when (currencyUnit) {
                                            RMB -> rateETH = it.rateCny
                                            HKD -> rateETH = it.rateHkd
                                            USD -> rateETH = it.rateUsd
                                        }
                                        ethLegal = ethBalance.multiply(BigDecimal(rateETH))
                                        mainETHTokenBean.add(
                                            MainETHTokenBean(
                                                it.currencyType,
                                                ethBalance,
                                                BigDecimal(rateETH),
                                                currencyUnit,
                                                BigDecimal(it.change)
                                            )
                                        )
                                    }
                                }
                                if (SharedPreferencesUtils.getString(
                                        this,
                                        TOKEN_INFO_NO,
                                        ""
                                    ).isNotEmpty()
                                ) {
                                    saveTokenList = SharedPreferencesUtils.String2SceneList(
                                        SharedPreferencesUtils.getString(
                                            this,
                                            TOKEN_INFO_NO,
                                            ""
                                        )
                                    ) as ArrayList<StoreInfo>
                                    val list = saveTokenList.iterator()
                                    list.forEach {
                                        if (it.isMyToken == 0) {
                                            list.remove()
                                        }
                                    }
                                    tokenLegal = BigDecimal("0.00")
                                    rateBean.forEach { rateBean ->
                                        saveTokenList.forEach { saveToken ->
                                            if (saveToken.name == rateBean.currencyType) {
                                                when (currencyUnit) {
                                                    RMB -> rateToken = rateBean.rateCny
                                                    HKD -> rateToken = rateBean.rateHkd
                                                    USD -> rateToken = rateBean.rateUsd
                                                }
                                                tokenLegal =
                                                    tokenBalance.multiply(BigDecimal(rateToken))
                                                mainETHTokenBean.add(
                                                    MainETHTokenBean(
                                                        rateBean.currencyType,
                                                        tokenBalance,
                                                        BigDecimal(rateToken),
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
                                        if ((adapter.getItem(position) as MainETHTokenBean).name != "ETH") {
                                            showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean))
                                        }
                                    }
                                }
                            }
                        }
                        if ((ethBalance != BigDecimal("0.00000000")) && (tokenBalance != BigDecimal(
                                "0.000000"
                            ))
                        ) {
                            totalBalance = ethLegal.add(tokenLegal)
                            setBalanceETH(totalBalance)
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