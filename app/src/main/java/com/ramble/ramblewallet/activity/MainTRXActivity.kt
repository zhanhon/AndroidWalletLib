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
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainTrxBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.TransferTrxUtils.balanceOfTrc20
import com.ramble.ramblewallet.tron.TransferTrxUtils.balanceOfTrx
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.utils.*
import java.math.BigDecimal

class MainTRXActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainTrxBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var isClickEyes = false
    private var animator: ObjectAnimator? = null
    private var saveTokenList: ArrayList<StoreInfo> = arrayListOf()
    private lateinit var walletSelleted: WalletETH
    private var trxBalance: BigDecimal = BigDecimal("0")
    private var tokenBalance: BigDecimal = BigDecimal("0")
    private var totalBalance: BigDecimal = BigDecimal("0")
    private var rateTRX: String? = ""
    private var unitPrice = ""

    //YING
    private var contractAddress = "TU9iBgEEv9qsc6m7EBPLJ3x5vSNKfyxWW5" //官方Nile测试节点YING

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_E11334)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_trx)
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
                    it.putSerializable(ARG_PARAM2, MainETHTokenBean(
                        "TRX",
                        "TRX",
                        trxBalance,
                        unitPrice,
                        currencyUnit,
                        null,
                        false
                    ))
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
                    putExtra(ARG_PARAM1, "TRX")
                    putExtra(ARG_PARAM2, walletSelleted.address)
                })
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, MainETHTokenBean(
                        "TRX",
                        "TRX",
                        trxBalance,
                        unitPrice,
                        currencyUnit,
                        null,
                        false
                    ))
                })
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
                })
            }
            R.id.iv_balance_refresh -> {
                startSyncAnimation()
                Handler().postDelayed({
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

        binding.ivBalanceRefresh.setOnClickListener(this)
        binding.ivEyes.setOnClickListener(this)
        binding.ivCopy.setOnClickListener(this)
        binding.tvNtf.setOnClickListener(this)

    }

    private fun initData() {
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, CNY)
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
            CNY -> binding.tvCurrencyUnit.text = "￥"
            HKD -> binding.tvCurrencyUnit.text = "HK$"
            USD -> binding.tvCurrencyUnit.text = "$"
        }
        if (WalletTRXUtils.isTrxValidAddress(walletSelleted.address)) {
            balanceOfTrx(this, walletSelleted.address)
            balanceOfTrc20(this, walletSelleted.address, contractAddress)
        }
        binding.tvTrxAddress.text = addressHandle(walletSelleted.address)
    }

    fun setTrxBalance(balance: BigDecimal) {
        trxBalance = balance
        refreshData()
    }

    fun setTokenBalance(balance: BigDecimal) {
        tokenBalance = balance
        refreshData()
    }

    private fun setBalanceTRX(balance: BigDecimal) {
        postUI {
            if (DecimalFormatUtil.format2.format(balance) == "0") {
                binding.tvBalanceTotal.text = ""
            } else {
                binding.tvBalanceTotal.text = DecimalFormatUtil.format2.format(balance)
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
                    putExtra(ARG_PARAM1, "TRX-$mainETHTokenBean.symbol")
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
        var list: ArrayList<String> = arrayListOf()
        list.add("TRX")
        list.add("YING")
        var req = StoreInfo.Req()
        req.list = list
        req.convertId = "2787,2792,2781" //人民币、港币、美元
        req.platformId = 1958 //BTC 1,ETH 1027,TRX 1958
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
                        if (storeInfo.symbol == "TRX") {
                            mainETHTokenBean.add(
                                MainETHTokenBean(
                                    "TRX",
                                    storeInfo.symbol,
                                    trxBalance,
                                    unitPrice,
                                    currencyUnit,
                                    null,
                                    false
                                )
                            )
                            totalBalance += trxBalance.multiply(BigDecimal(unitPrice))
                        } else {
                            mainETHTokenBean.add(
                                MainETHTokenBean(
                                    "TRX-${storeInfo.symbol}",
                                    storeInfo.symbol,
                                    if (storeInfo.symbol == "YING") tokenBalance else BigDecimal("0"),
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
                                if ((adapter.getItem(position) as MainETHTokenBean).symbol != "TRX") {
                                    showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean))
                                }
                            }
                        }
                    }
                    setBalanceTRX(totalBalance)
                }
            } else {
                println("-=-=-=->${it.message()}")
            }
            cancelSyncAnimation()
        }, {
            println("-=-=-=->${it.printStackTrace()}")
            cancelSyncAnimation()
        })
    }
}