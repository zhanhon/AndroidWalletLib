package com.ramble.ramblewallet.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
import com.ramble.ramblewallet.blockchain.ethereum.TransferETHUtils
import com.ramble.ramblewallet.blockchain.ethereum.TransferETHUtils.getBalanceETH
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityMainEthBinding
import com.ramble.ramblewallet.network.ApiResponse
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import com.ramble.ramblewallet.utils.TimeUtils.dateToAllTokenBean
import java.math.BigDecimal


class MainETHActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainEthBinding
    private var mainETHTokenBean: ArrayList<MainTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private lateinit var currencyUnit: String
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
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
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
        if (SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
            if (SharedPreferencesUtils.getSecurityString(
                    this,
                    TOKEN_INFO_NO,
                    ""
                ).isNotEmpty()
            ) {
                myAllToken = Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                    object : TypeToken<ArrayList<AllTokenBean>>() {}.type
                )
                var allAddress: ArrayList<String> = arrayListOf()
                myAllToken.forEach {
                    allAddress.add(it.myCurrency)
                }
                if (!allAddress.contains(walletSelleted.address)) {
                    myAllToken.add(dateToAllTokenBean(walletSelleted.address))
                    SharedPreferencesUtils.saveSecurityString(
                        this,
                        TOKEN_INFO_NO,
                        Gson().toJson(myAllToken)
                    )
                }

            } else {
                myAllToken.add(dateToAllTokenBean(walletSelleted.address))
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    TOKEN_INFO_NO,
                    Gson().toJson(myAllToken)
                )
            }
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

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true)
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {
        val lang = TimeUtils.dateToLang(this)
        var redList: ArrayList<Page.Record> = arrayListOf()
        var records2: ArrayList<Page.Record> = arrayListOf()
        var req = Page.Req(1, 1000, lang)
        mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    redPointEthHandle(it, redList, records2, lang)
                }
            }, {
            }
        )
    }

    private fun redPointEthHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>,
        lang: Int
    ) {
        var records21 = records2
        it.data()?.let { data ->
            var read: ArrayList<Int> =
                if (SharedPreferencesUtils.getSecurityString(this, READ_ID_NEW, "").isNotEmpty()) {
                    Gson().fromJson(
                        SharedPreferencesUtils.getSecurityString(this, READ_ID_NEW, ""),
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
            records21 = if (SharedPreferencesUtils.getSecurityString(
                    this,
                    STATION_INFO,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, STATION_INFO, ""),
                    object : TypeToken<ArrayList<Page.Record>>() {}.type
                )
            } else {
                arrayListOf()
            }
            redPointEthHandleSub(records21, redList, lang)
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
        lang: Int
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
                if (item.lang == lang) {
                    if (SharedPreferencesUtils.getSecurityString(
                            this,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        var read: ArrayList<Int> = Gson().fromJson(
                            SharedPreferencesUtils.getSecurityString(this, READ_ID, ""),
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
                    putExtra(ARG_PARAM1, "ETH")
                    putExtra(ARG_PARAM2, walletSelleted.address)
                })
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(
                        ARG_PARAM2, MainTokenBean(
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
                        ARG_PARAM2, MainTokenBean(
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
            R.id.ll_copy_address -> {
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

        binding.ivTokenManageClick.setOnClickListener(this)

        binding.ivBalanceRefresh.setOnClickListener(this)
        binding.ivEyes.setOnClickListener(this)
        binding.llCopyAddress.setOnClickListener(this)
        binding.tvNtf.setOnClickListener(this)

    }

    private fun initData() {
        currencyUnit = SharedPreferencesUtils.getSecurityString(this, CURRENCY, USD)
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
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
        if (!walletSelleted.isBackupAlready && !walletSelleted.mnemonic.isNullOrEmpty()) {
            showCommonDialog(this,
                title = getString(R.string.tips),
                titleContent = getString(R.string.mnemonic_no_backup_tips),
                btnCancel = getString(R.string.backup_later),
                btnConfirm = getString(R.string.backup_now),
                confirmListener = {
                    startActivity(Intent(this, WalletMoreOperateActivity::class.java).apply {
                        putExtra(ARG_PARAM1, Gson().toJson(walletSelleted))
                    })
                }
            )
        }
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

    @SuppressLint("CheckResult")
    private fun refreshData() {
        myAllToken = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
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
            }
            binding.lyPullRefresh.finishRefresh() //刷新完成
            cancelSyncAnimation()
        }, {
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
                if (adapter.getItem(position) is MainTokenBean) {
                    var symbol = (adapter.getItem(position) as MainTokenBean).title
                    startActivity(Intent(this, QueryActivity::class.java).apply {
                        putExtra(ARG_PARAM1, walletSelleted.address)
                        putExtra(ARG_PARAM2, adapter.getItem(position) as MainTokenBean)
                        putExtra(ARG_PARAM3, symbol)
                    })
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
                    MainTokenBean(
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
            var tokenBean = MainTokenBean(
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
            TransferETHUtils.getBalanceToken(walletSelleted.address, tokenBean,object :TransferETHUtils.BalanceGet{
                override fun onListener(tokenBalance: BigDecimal) {
                    tokenBean.balance = tokenBalance
                    totalBalance += tokenBalance.multiply(BigDecimal(tokenBean.unitPrice))
                    setBalanceETH(totalBalance)
                }
            })
        }
    }
}