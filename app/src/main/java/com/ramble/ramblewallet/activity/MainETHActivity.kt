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
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainEthBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils
import com.ramble.ramblewallet.ethereum.TransferEthUtils.getBalanceETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import java.math.BigDecimal

class MainETHActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainEthBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var isClickEyes = false
    private var animator: ObjectAnimator? = null
    private var saveTokenList: ArrayList<StoreInfo> = arrayListOf()
    private lateinit var walletSelleted: Wallet
    private var ethBalance: BigDecimal = BigDecimal("0.00000000")
    private var tokenBalance: BigDecimal = BigDecimal("0.00000000")
    private var totalBalance: BigDecimal = BigDecimal("0.00")
    private var rateETH: String? = ""
    private var unitPrice = ""


    //DAI:4 0x16aFDD5dfE386052766b798bFA37DAec4b81155a      新：0x0B6558D0C0e06aAdf34428137b7eFF5B9da5974A
    //private var contractAddress = "0xb319d1A045ffe108D14195F7C5d60Be220436a34" //测试节点ERC-USDT:6合约地址
    private var contractAddress = "0x40a90Ade8BB2BdF8858b424ccbb90012373e8a41"; //新

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
        redPoint()
    }

    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {
        if (SharedPreferencesUtils.getString(this, READ_ID_NEW, "")
                .isNotEmpty()
        ) {
            var lang = when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
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
            var redList: ArrayList<Page.Record> = arrayListOf()
            var records2: ArrayList<Page.Record> = arrayListOf()
            var req = Page.Req(1, 1000, lang)
            mApiService.getNotice(
                req.toApiRequest(noticeInfoUrl)
            ).applyIo().subscribe(
                {
                    if (it.code() == 1) {
                        it.data()?.let { data ->
                            println("==================>getTransferInfo:${data}")

                            data.records.forEach { item ->

                                if (SharedPreferencesUtils.String2SceneList(
                                        SharedPreferencesUtils.getString(
                                            this,
                                            READ_ID_NEW,
                                            ""
                                        )
                                    ).contains(item.id)
                                ) {
                                    item.isRead = 1
                                } else {
                                    item.isRead = 0
                                    redList.add(item)
                                }
                            }
                            records2 = if (SharedPreferencesUtils.getString(
                                    this,
                                    STATION_INFO,
                                    ""
                                ).isNotEmpty()
                            ) {
                                SharedPreferencesUtils.String2SceneList(
                                    SharedPreferencesUtils.getString(
                                        this,
                                        STATION_INFO,
                                        ""
                                    )
                                ) as ArrayList<Page.Record>

                            } else {
                                arrayListOf()
                            }
                            if (records2.isNotEmpty()) {

                                records2.forEach { item ->
                                    if (SharedPreferencesUtils.getString(
                                            this,
                                            READ_ID,
                                            ""
                                        ).isNotEmpty()
                                    ) {
                                        if (SharedPreferencesUtils.String2SceneList(
                                                SharedPreferencesUtils.getString(
                                                    this,
                                                    READ_ID,
                                                    ""
                                                )
                                            ).contains(item.id)
                                        ) {
                                            item.isRead = 1
                                        } else {
                                            item.isRead = 0
                                            redList.add(item)
                                        }

                                    } else {
                                        item.isRead = 0
                                        redList.add(item)
                                    }

                                }


                            }
                            if (redList.isNotEmpty()) {
                                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center_red)
                            } else {
                                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center)
                            }
                        }
                    } else {
                        println("==================>getTransferInfo1:${it.message()}")
                    }

                }, {

                    println("==================>getTransferInfo1:${it.printStackTrace()}")
                }
            )
        } else {
            binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center_red)
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
                    putExtra(
                        ARG_PARAM2, MainETHTokenBean(
                            "ETH",
                            "ETH",
                            ethBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            false
                        )
                    )
                })
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
                    putExtra(
                        ARG_PARAM2, MainETHTokenBean(
                            "ETH",
                            "ETH",
                            ethBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            false
                        )
                    )
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
                    binding.tvBalanceTotal.text =
                        strAddComma(DecimalFormatUtil.format2.format(totalBalance))
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
            R.id.tv_ntf -> {
                toastDefault(getString(R.string.coming_soon))
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
        binding.tvNtf.setOnClickListener(this)

    }

    private fun initData() {
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, USD)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        binding.tvWalletName.text = walletSelleted.walletName
        when (currencyUnit) {
            CNY -> binding.tvCurrencyUnit.text = "￥"
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
            if (DecimalFormatUtil.format2.format(balance) == "0") {
                binding.tvBalanceTotal.text = "0"
            } else {
                binding.tvBalanceTotal.text = strAddComma(DecimalFormatUtil.format2.format(balance))
            }
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

            tvTokenTitle.text = mainETHTokenBean.symbol

            tvTransfer.setOnClickListener { v1: View? ->
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, mainETHTokenBean)
                })
                dialog.dismiss()
            }
            tvGathering.setOnClickListener { v1: View? ->
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "ETH-${mainETHTokenBean.symbol}")
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

    @SuppressLint("CheckResult")
    private fun refreshData() {
        val tokenInfo = SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, "")
        var list: ArrayList<String> = arrayListOf()
        list.add("ETH")
        if (tokenInfo.isNotEmpty()) {
            saveTokenList =
                Gson().fromJson(tokenInfo, object : TypeToken<ArrayList<StoreInfo>>() {}.type)
            val list = saveTokenList.iterator()
            list.forEach {
                if (it.isMyToken == 0) {
                    list.remove()
                }
            }
        }
        saveTokenList.forEach {
            list.add(it.symbol)
        }
        var req = StoreInfo.Req()
        req.list = list
        req.convertId = "2787,2792,2781" //人民币、港币、美元
        req.platformId = 1027 //BTC 1,ETH 1027,TRX 1958
        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                it.data()?.let { data ->
                    mainETHTokenBean.clear()
                    totalBalance = BigDecimal("0.00")
                    data.forEach { storeInfo ->
                        storeInfo.quote.forEach { quote ->
                            if (quote.symbol == currencyUnit) {
                                unitPrice = quote.price
                            }
                        }
                        if (storeInfo.symbol == "ETH") {
                            mainETHTokenBean.add(
                                MainETHTokenBean(
                                    "ETH",
                                    storeInfo.symbol,
                                    ethBalance,
                                    unitPrice,
                                    currencyUnit,
                                    null,
                                    false
                                )
                            )
                            rateETH = unitPrice
                            totalBalance += ethBalance.multiply(BigDecimal(unitPrice))
                        } else {
                            mainETHTokenBean.add(
                                MainETHTokenBean(
                                    "ETH-${storeInfo.symbol}",
                                    storeInfo.symbol,
                                    if (storeInfo.symbol == "TESTERC") tokenBalance else BigDecimal(
                                        "0"
                                    ),
                                    unitPrice,
                                    currencyUnit,
                                    contractAddress,
                                    true
                                )
                            )
                            totalBalance += tokenBalance.multiply(BigDecimal(unitPrice))
                        }
                        mainAdapter = MainAdapter(mainETHTokenBean)
                        binding.rvCurrency.adapter = mainAdapter
                        mainAdapter.setOnItemClickListener { adapter, view, position ->
                            if (adapter.getItem(position) is MainETHTokenBean) {
                                if ((adapter.getItem(position) as MainETHTokenBean).symbol != "ETH") {
                                    showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean))
                                }
                            }
                        }
                    }
                    setBalanceETH(totalBalance)
                }
            } else {
                println("-=-=-=->ETH${it.message()}")
            }
            cancelSyncAnimation()
        }, {
            println("-=-=-=->ETH${it.printStackTrace()}")
            cancelSyncAnimation()
        })
    }
}