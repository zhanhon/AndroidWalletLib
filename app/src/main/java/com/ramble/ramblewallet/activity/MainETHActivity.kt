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
import android.text.InputFilter
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
import com.ramble.ramblewallet.bean.*
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainEthBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils
import com.ramble.ramblewallet.ethereum.TransferEthUtils.getBalanceETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.network.ApiResponse
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
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()
    private lateinit var walletSelleted: Wallet
    private var ethBalance: BigDecimal = BigDecimal("0")
    private var totalBalance: BigDecimal = BigDecimal("0.00")
    private var unitPriceETH = ""

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_078DC2)
        //设置状态栏字体颜色，true:代表黑色，false代表白色
        StateUtils.setLightStatusBar(this, false)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_eth)
        initView()
        binding.lyPullRefresh.setOnRefreshListener {
            initData()
        }
        initClick()
    }

    /***
     * 数据初始化
     */
    private fun initView() {
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        if (SharedPreferencesUtils.getString(
                this,
                TOKEN_INFO_NO,
                ""
            ).isNotEmpty()
        ) {
            myAllToken = Gson().fromJson(
                SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
                object : TypeToken<ArrayList<AllTokenBean>>() {}.type
            )
            var allAddress: ArrayList<String> = arrayListOf()
            myAllToken.forEach {
                allAddress.add(it.myCurrency)
            }
            if (!allAddress.contains(walletSelleted.address)) {
                var allToken = AllTokenBean()
                var myStores: ArrayList<StoreInfo> = arrayListOf()
                var r1 = StoreInfo()
                r1.id = 2396
                r1.symbol = "WETH"
                r1.contractAddress = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
                var r2 = StoreInfo()
                r2.id = 3717
                r2.symbol = "WBTC"
                r2.contractAddress = "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599"
                var r3 = StoreInfo()
                r3.id = 4943
                r3.symbol = "DAI"
                r3.contractAddress = "0x6b175474e89094c44da98b954eedeac495271d0f"
                var r4 = StoreInfo()
                r4.symbol = "USDC"
                r4.id = 3408
                r4.contractAddress = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
                var r5 = StoreInfo()
                r5.symbol = "USDT"
                r5.id = 825
                r5.contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7"
                r5.isMyToken = 2
                var r6 = StoreInfo()
                r6.symbol = "LINK"
                r6.id = 1975
                r6.contractAddress = "0x514910771af9ca656af840dff83e8264ecf986ca"
                var r7 = StoreInfo()
                r7.symbol = "YFI"
                r7.id = 5864
                r7.contractAddress = "0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e"
                var r8 = StoreInfo()
                r8.symbol = "UNI"
                r8.id = 7083
                r8.contractAddress = "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984"
                myStores.add(r5)
                myStores.add(r2)
                myStores.add(r3)
                myStores.add(r4)
                myStores.add(r8)
                myStores.add(r6)
                myStores.add(r1)
                myStores.add(r7)
                allToken.storeInfos = myStores
                allToken.myCurrency = walletSelleted.address
                myAllToken.add(allToken)
                SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myAllToken))
            }

        } else {
            var allToken = AllTokenBean()
            var myStores: ArrayList<StoreInfo> = arrayListOf()
            var r1 = StoreInfo()
            r1.id = 2396
            r1.symbol = "WETH"
            r1.contractAddress = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
            var r2 = StoreInfo()
            r2.id = 3717
            r2.symbol = "WBTC"
            r2.contractAddress = "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599"
            var r3 = StoreInfo()
            r3.id = 4943
            r3.symbol = "DAI"
            r3.contractAddress = "0x6b175474e89094c44da98b954eedeac495271d0f"
            var r4 = StoreInfo()
            r4.symbol = "USDC"
            r4.id = 3408
            r4.contractAddress = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
            var r5 = StoreInfo()
            r5.symbol = "USDT"
            r5.id = 825
            r5.contractAddress = "0xdac17f958d2ee523a2206206994597c13d831ec7"
            r5.isMyToken = 2
            var r6 = StoreInfo()
            r6.symbol = "LINK"
            r6.id = 1975
            r6.contractAddress = "0x514910771af9ca656af840dff83e8264ecf986ca"
            var r7 = StoreInfo()
            r7.symbol = "YFI"
            r7.id = 5864
            r7.contractAddress = "0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e"
            var r8 = StoreInfo()
            r8.symbol = "UNI"
            r8.id = 7083
            r8.contractAddress = "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984"
            myStores.add(r5)
            myStores.add(r2)
            myStores.add(r3)
            myStores.add(r4)
            myStores.add(r8)
            myStores.add(r6)
            myStores.add(r1)
            myStores.add(r7)
            allToken.storeInfos = myStores
            allToken.myCurrency = walletSelleted.address
            myAllToken.add(allToken)
            SharedPreferencesUtils.saveString(this, TOKEN_INFO_NO, Gson().toJson(myAllToken))
        }
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
                    redPointEthHandle(it, redList, records2)
                } else {
                    println("==================>getTransferInfo1:${it.message()}")
                }

            }, {

                println("==================>getTransferInfo1:${it.printStackTrace()}")
            }
        )
    }

    private fun redPointEthHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>
    ) {
        var records21 = records2
        it.data()?.let { data ->
            println("==================>getTransferInfo:${data}")

            data.records.forEach { item ->
                var read: ArrayList<Int> = Gson().fromJson(
                    SharedPreferencesUtils.getString(this, READ_ID_NEW, ""),
                    object : TypeToken<ArrayList<Int>>() {}.type
                )
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
            redPointEthHandleSub(records21, redList)
            if (redList.isNotEmpty()) {
                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center_red)
            } else {
                binding.ivNoticeTop.setImageResource(R.drawable.vector_message_center)
            }
        }
    }

    private fun redPointEthHandleSub(
        records21: ArrayList<Page.Record>,
        redList: ArrayList<Page.Record>
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
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
                            unitPriceETH,
                            currencyUnit,
                            null,
                            18,
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
                            unitPriceETH,
                            currencyUnit,
                            null,
                            18,
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
                refreshData()
            }.start()
        }
        binding.tvEthAddress.text = addressHandle(walletSelleted.address)
    }

    private fun setBalanceETH(balance: BigDecimal) {
        if (DecimalFormatUtil.format(balance, 2) == "0") {
            binding.tvBalanceTotal.text = "0"
        } else {
            binding.tvBalanceTotal.text = strAddComma(DecimalFormatUtil.format(balance, 2))
            binding.tvBalanceTotal.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(18))
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
                if (mainETHTokenBean.symbol == "ETH") {
                    startActivity(Intent(this, GatheringActivity::class.java).apply {
                        putExtra(ARG_PARAM1, "ETH")
                        putExtra(ARG_PARAM2, walletSelleted.address)
                    })
                } else {
                    startActivity(Intent(this, GatheringActivity::class.java).apply {
                        putExtra(ARG_PARAM1, "ETH-${mainETHTokenBean.symbol}")
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
        myAllToken = Gson().fromJson(
            SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
            object : TypeToken<ArrayList<AllTokenBean>>() {}.type
        )
        myAllToken.forEach {
            if (it.myCurrency == walletSelleted.address) {
                saveTokenList = it.storeInfos
            }
        }

        var list: ArrayList<String> = arrayListOf()
        list.add("ETH")
        saveTokenList.forEach {
            if (it.isMyToken != 0) {
                list.add(it.symbol)
            }
        }
        var req = StoreInfo.Req()
        req.list = list
        req.convertId = "2781,2787,2792" //美元、人民币、港币
        req.platformId = 1027 //BTC 1,ETH 1027,TRX 1958
        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({
            if (it.code() == 1) {
                ethHomeDataHandle(it)
            } else {
                println("-=-=-=->ETH${it.message()}")
            }
            binding.lyPullRefresh.finishRefresh() //刷新完成
            cancelSyncAnimation()
        }, {
            println("-=-=-=->ETH${it.printStackTrace()}")
            binding.lyPullRefresh.finishRefresh() //刷新完成
            cancelSyncAnimation()
        })
    }

    private fun ethHomeDataHandle(it: ApiResponse<List<StoreInfo>>) {
        it.data()?.let { data ->
            mainETHTokenBean.clear()
            totalBalance = BigDecimal("0.00")
            ethHomeDataHandleSub(data)
            mainAdapter = MainAdapter(mainETHTokenBean)
            binding.rvCurrency.adapter = mainAdapter
            mainAdapter.setOnItemClickListener { adapter, _, position ->
                if (adapter.getItem(position) is MainETHTokenBean) {
                    showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean))
                }
            }
            setBalanceETH(totalBalance)
        }
    }

    private fun ethHomeDataHandleSub(data: List<StoreInfo>) {
        data.forEach { storeInfo ->
            storeInfo.quote.forEach { quote ->
                if (quote.symbol == currencyUnit) {
                    storeInfo.price = quote.price
                }
            }
            if (storeInfo.symbol == "ETH") {
                unitPriceETH = storeInfo.price
                mainETHTokenBean.add(
                    MainETHTokenBean(
                        "ETH",
                        storeInfo.symbol,
                        ethBalance,
                        unitPriceETH,
                        currencyUnit,
                        null,
                        18,
                        false
                    )
                )
                totalBalance += ethBalance.multiply(BigDecimal(unitPriceETH))
            }
        }
        data.forEach { storeInfo ->
            if ((storeInfo.symbol == "UNI") && (storeInfo.contractAddress != "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984")) {
                return@forEach
            }
            if (storeInfo.symbol == "ETH") {
                return@forEach
            }
            var tokenBean = MainETHTokenBean(
                "ETH-${storeInfo.symbol}",
                storeInfo.symbol,
                BigDecimal("0"),
                storeInfo.price,
                currencyUnit,
                storeInfo.contractAddress,
                storeInfo.decimalPoints,
                true
            )
            mainETHTokenBean.add(tokenBean)
            Thread {
                TransferEthUtils.getBalanceToken(walletSelleted.address, tokenBean)
            }.start()
            TransferEthUtils().setOnListener { tokenBean, tokenBalance ->
                postUI {
                    tokenBean.balance = tokenBalance
                    totalBalance += tokenBalance.multiply(BigDecimal(tokenBean.unitPrice))
                    mainAdapter.notifyDataSetChanged()
                    setBalanceETH(totalBalance)
                }
            }
        }
    }
}