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
import android.view.*
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
import com.ramble.ramblewallet.bitcoin.TransferBTCUtils.balanceOfBtc
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainBtcBinding
import com.ramble.ramblewallet.network.ApiResponse
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import java.math.BigDecimal

class MainBTCActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBtcBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var isClickEyes = false
    private var animator: ObjectAnimator? = null
    private lateinit var walletSelleted: Wallet
    private var btcBalance: BigDecimal = BigDecimal("0")
    private var totalBalance: BigDecimal = BigDecimal("0")
    private var unitPrice = ""

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_CA7C14)
        //设置状态栏字体颜色，true:代表黑色，false代表白色
        StateUtils.setLightStatusBar(this, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_btc)
        binding.lyPullRefresh.setOnRefreshListener {
            initData()
        }
        initClick()
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_PUSH_MSG -> {
                redPoint()
            }
            else -> return
        }
    }


    override fun onResume() {
        super.onResume()
        initData()
        redPoint()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {

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
                    redPointBtcHandle(it, redList, records2,lang)
                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }

            }, {

                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )
    }

    private fun redPointBtcHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>,
        lang:Int
    ) {
        var records21 = records2
        it.data()?.let { data ->
            println("==================>getTransferInfo:${data}")
            var read: ArrayList<Int> =  if (SharedPreferencesUtils.getString(this, READ_ID_NEW, "").isNotEmpty()) {
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, READ_ID_NEW, ""),
                    object : TypeToken<ArrayList<Int>>() {}.type
                )
            } else {
                arrayListOf()
            }
            data.records.forEach { item ->

                if (read.contains(item.id)
                ) {
                    item.isRead = 1
                } else {
                    item.isRead = 0
                    redList.add(item)
                }
            }
            records21 = if (SharedPreferencesUtils.getString(
                    this,
                    STATION_INFO,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, STATION_INFO, ""),
                    object : TypeToken<ArrayList<Page.Record>>() {}.type
                )

            } else {
                arrayListOf()
            }
            redPointEthHandleSub(records21, redList,lang)
            if (redList.isNotEmpty()) {
                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center_red)
            } else {
                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center)
            }
        }
    }

    private fun redPointEthHandleSub(
        records21: ArrayList<Page.Record>,
        redList: ArrayList<Page.Record>,
        lang:Int
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
                if (item.lang == lang) {
                    if (SharedPreferencesUtils.getString(
                            this,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        var read: ArrayList<Int> = Gson().fromJson(
                            SharedPreferencesUtils.getString(this, READ_ID, ""),
                            object : TypeToken<ArrayList<Int>>() {}.type
                        )
                        if (read.contains(item.id)
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
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnMenu -> {
                startActivity(Intent(this, WalletManageActivity::class.java).apply {
                    putExtra(ARG_PARAM1, false)
                })
            }
            R.id.iv_notice_top -> {
                startActivity(Intent(this, MessageCenterActivity::class.java))
            }
            R.id.iv_mine_top -> {
                startActivity(Intent(this, MineActivity::class.java))
            }
            R.id.iv_gathering_top, R.id.ll_gathering -> {
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "BTC")
                    putExtra(ARG_PARAM2, walletSelleted.address)
                })
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(
                        ARG_PARAM2, MainETHTokenBean(
                            "BTC",
                            "BTC",
                            btcBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            0,
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
                            "BTC",
                            "BTC",
                            btcBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            0,
                            false
                        )
                    )
                })
            }
            R.id.iv_balance_refresh -> {
                startSyncAnimation()
                Handler().postDelayed({
                    initData()
                }, 2000)
            }
            R.id.iv_eyes -> {
                if (isClickEyes) {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_open)
                    binding.tvBalanceTotal.text =
                        strAddComma(DecimalFormatUtil.format(totalBalance, 2))
                    isClickEyes = false
                } else {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_close)
                    binding.tvBalanceTotal.text = "******"
                    isClickEyes = true
                }
            }
            R.id.iv_copy -> {
                ClipboardUtils.copy(walletSelleted.address, this)
            }
            R.id.tv_ntf -> {
                ToastUtils.showToastFree(this, getString(R.string.coming_soon))
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
                    //binding.txtTitle.visibility = View.INVISIBLE
                    isShow = true
                } else if (isShow) {
                    binding.toolbarLeft.visibility = View.GONE
                    //binding.txtTitle.visibility = View.VISIBLE
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
        if (WalletBTCUtils.isBtcValidAddress(walletSelleted.address)) {
            balanceOfBtc(this, walletSelleted.address)
        }
        binding.tvBtcAddress.text = addressHandle(walletSelleted.address)
    }

    fun setBtcBalance(balance: BigDecimal) {
        btcBalance = balance
        refreshData()
    }

    private fun setBalanceBTC(balance: BigDecimal) {
        postUI {
            if (DecimalFormatUtil.format(balance, 2) == "0") {
                binding.tvBalanceTotal.text = "0"
            } else {
                binding.tvBalanceTotal.text = strAddComma(DecimalFormatUtil.format(balance, 2))
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

            tvTransfer.setOnClickListener {
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, mainETHTokenBean)
                })
                dialog.dismiss()
            }
            tvGathering.setOnClickListener {
                if (mainETHTokenBean.symbol == "BTC") {
                    startActivity(Intent(this, GatheringActivity::class.java).apply {
                        putExtra(ARG_PARAM1, "BTC")
                        putExtra(ARG_PARAM2, walletSelleted.address)
                    })
                } else {
                    startActivity(Intent(this, GatheringActivity::class.java).apply {
                        putExtra(ARG_PARAM1, "BTC-${mainETHTokenBean.symbol}")
                        putExtra(ARG_PARAM2, walletSelleted.address)
                    })
                }
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
        list.add("BTC")
        //list.add("USDT")   暂时不用Omni-USDT
        var req = StoreInfo.Req()
        req.list = list
        req.convertId = "2781,2787,2792" //美元、人民币、港币
        req.platformId = 1 //BTC 1,ETH 1027,TRX 1958
        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                btcHomeDataHandle(it)
            } else {
                println("-=-=-=->BTC${it.message()}")
            }
            binding.lyPullRefresh.finishRefresh() //刷新完成
            cancelSyncAnimation()
        }, {
            println("-=-=-=->BTC${it.printStackTrace()}")
            binding.lyPullRefresh.finishRefresh() //刷新完成
            cancelSyncAnimation()
        })
    }

    private fun btcHomeDataHandle(it: ApiResponse<List<StoreInfo>>) {
        it.data()?.let { data ->
            mainETHTokenBean.clear()
            totalBalance = BigDecimal("0.00")
            data.forEach { storeInfo ->
                storeInfo.quote.forEach { quote ->
                    if (quote.symbol == currencyUnit) {
                        unitPrice = quote.price
                    }
                }
                if (storeInfo.symbol == "BTC") {
                    mainETHTokenBean.add(
                        MainETHTokenBean(
                            "BTC",
                            storeInfo.symbol,
                            btcBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            0,
                            false
                        )
                    )
                    totalBalance += btcBalance.multiply(BigDecimal(unitPrice))
                }
                mainAdapter = MainAdapter(mainETHTokenBean)
                binding.rvCurrency.adapter = mainAdapter
                mainAdapter.setOnItemClickListener { adapter, _, position ->
                    if (adapter.getItem(position) is MainETHTokenBean) {
                        showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean))
                    }
                }
            }
            setBalanceBTC(totalBalance)
        }
    }
}