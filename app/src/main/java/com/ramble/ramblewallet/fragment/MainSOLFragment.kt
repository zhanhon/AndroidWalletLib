package com.ramble.ramblewallet.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.*
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.bean.MainTokenBean
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.bean.event.EventRefeshPopup
import com.ramble.ramblewallet.blockchain.IBalanceListener
import com.ramble.ramblewallet.blockchain.solana.TransferSOLUtils
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils.Companion.isSolValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.FragmentMainCurrencyBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.network.ApiResponse
import com.ramble.ramblewallet.network.getStoreUrl
import com.ramble.ramblewallet.network.noticeInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class MainSOLFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentMainCurrencyBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var currencyUnit: String
    private lateinit var walletSelleted: Wallet
    private var mainTokenBean: ArrayList<MainTokenBean> = arrayListOf()
    private var solBalance: BigDecimal = BigDecimal("0")
    private var tokenBalance: BigDecimal = BigDecimal("0")
    private var totalBalance: BigDecimal = BigDecimal("0")
    private var unitPrice = ""
    private var isRefreshPopup = false

    //SPL-USDT
    private var contractAddress = "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB" //正式链

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (reusedView == null) {
            binding = inflater.dataBinding(R.layout.fragment_main_currency, container)
            reusedView = binding.root
        }
        return reusedView
    }

    override fun actualLazyLoad() {
        super.actualLazyLoad()
        binding.lyPullRefresh.setOnRefreshListener {
            initData()
            initAssetsData()
        }
        initClick()
    }

    override fun onResume() {
        super.onResume()
        initData()
        initAssetsData()
        redPoint()
    }


    /***
     * 未读消息红点展示
     */
    @SuppressLint("CheckResult")
    private fun redPoint() {
        val lang = TimeUtils.dateToLang(requireContext())
        var redList: ArrayList<Page.Record> = arrayListOf()
        var records2: ArrayList<Page.Record> = arrayListOf()
        var req = Page.Req(1, 1000, lang)
        mApiService.getNotice(
            req.toApiRequest(noticeInfoUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    redPointHandle(it, redList, records2, lang)
                }
            }, {
            }
        )
    }

    private fun redPointHandle(
        it: ApiResponse<Page>,
        redList: ArrayList<Page.Record>,
        records2: ArrayList<Page.Record>,
        lang: Int
    ) {
        var records21 = records2
        it.data()?.let { data ->
            var read: ArrayList<Int> =
                if (SharedPreferencesUtils.getSecurityString(context, READ_ID_NEW, "").isNotEmpty()) {
                    Gson().fromJson(
                        SharedPreferencesUtils.getSecurityString(context, READ_ID_NEW, ""),
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
                    context,
                    STATION_INFO,
                    ""
                ).isNotEmpty()
            ) {
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(context, STATION_INFO, ""),
                    object : TypeToken<ArrayList<Page.Record>>() {}.type
                )

            } else {
                arrayListOf()
            }
            redPointHandleSub(records21, redList, lang)
            RxBus.emitEvent(Pie.EVENT_MSG_NUM,read.size)
            if (redList.isNotEmpty()) {//未读
                binding.vBillPoint.visibility = View.VISIBLE
            } else {
                binding.vBillPoint.visibility = View.GONE
            }
        }
    }

    private fun redPointHandleSub(
        records21: ArrayList<Page.Record>,
        redList: ArrayList<Page.Record>,
        lang: Int
    ) {
        if (records21.isNotEmpty()) {
            records21.forEach { item ->
                if (item.lang == lang) {
                    if (SharedPreferencesUtils.getSecurityString(
                            context,
                            READ_ID,
                            ""
                        ).isNotEmpty()
                    ) {
                        var read: ArrayList<Int> = Gson().fromJson(
                            SharedPreferencesUtils.getSecurityString(context, READ_ID, ""),
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

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_PUSH_MSG -> {
                redPoint()
            }
            Pie.EVENT_REFRESH_POPUP -> {
                val eventRefeshPopup: EventRefeshPopup = event.data()
                isRefreshPopup = eventRefeshPopup.isVisibleToUser && eventRefeshPopup.sClass == this::class.java.name
                initData()
                if (isRefreshPopup) {
                    redPoint()
                }
            }
            else -> return
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_mine -> {
                startActivity(Intent(context, MineActivity::class.java))
            }
            R.id.iv_wallet_more -> {
                startActivity(Intent(context, WalletMoreOperateActivity::class.java).apply {
                    putExtra(ARG_PARAM1, Gson().toJson(walletSelleted))
                })
            }
            R.id.fl_bell -> {
                startActivity(Intent(context, MessageCenterActivity::class.java))
            }
            R.id.btnMenu -> {
                startActivity(Intent(context, WalletManageActivity::class.java).apply {
                    putExtra(ARG_PARAM1, false)
                })
            }
            R.id.iv_notice_top -> {
                startActivity(Intent(context, MessageCenterActivity::class.java))
            }
            R.id.iv_mine_top -> {
                startActivity(Intent(context, MineActivity::class.java))
            }
            R.id.ll_gathering -> {
                startActivity(Intent(context, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "SOL")
                    putExtra(ARG_PARAM2, walletSelleted.address)
                })
            }
            R.id.ll_transfer -> {
                startActivity(Intent(context, TransferActivity::class.java).apply {
                    putExtra(
                        ARG_PARAM2, MainTokenBean(
                            "SOL",
                            "SOL",
                            solBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            0,
                            false
                        )
                    )
                })
            }
            R.id.iv_scan_top -> {
                startActivity(Intent(context, ScanActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
                    putExtra(
                        ARG_PARAM2, MainTokenBean(
                            "SOL",
                            "SOL",
                            solBalance,
                            unitPrice,
                            currencyUnit,
                            null,
                            0,
                            false
                        )
                    )
                })
            }
            R.id.ll_copy_address -> {
                ClipboardUtils.copy(walletSelleted.address, context)
            }
        }
    }

    private fun initClick() {
        binding.rlCurrencyBg.setBackgroundResource(R.mipmap.qb_card_sol)
        binding.ivTokenManageClick.visibility = View.GONE

        binding.flBell.setOnClickListener(this)
        binding.ivMine.setOnClickListener(this)
        binding.ivWalletMore.setOnClickListener(this)
        binding.btnMenu.setOnClickListener(this)
        binding.llGathering.setOnClickListener(this)
        binding.llTransfer.setOnClickListener(this)
        binding.ivScanTop.setOnClickListener(this)
        binding.llCopyAddress.setOnClickListener(this)
        mainAdapter = MainAdapter(mainTokenBean)
        binding.rvCurrency.layoutManager = LinearLayoutManager(context)
        binding.rvCurrency.adapter = mainAdapter
        mainAdapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) is MainTokenBean) {
                val symbol = (adapter.getItem(position) as MainTokenBean).title
                startActivity(Intent(context, QueryActivity::class.java).apply {
                    putExtra(ARG_PARAM1, walletSelleted.address)
                    putExtra(ARG_PARAM2, adapter.getItem(position) as MainTokenBean)
                    putExtra(ARG_PARAM3, symbol)
                })
            }
        }
    }

    private fun initData() {
        binding.tvNetAddress.text = "SOL"
        currencyUnit = SharedPreferencesUtils.getSecurityString(context, CURRENCY, USD)
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(context, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        binding.tvWalletName.text = walletSelleted.walletName
        when (currencyUnit) {
            CNY -> binding.tvCurrencyUnit.text = "￥"
            HKD -> binding.tvCurrencyUnit.text = "HK$"
            USD -> binding.tvCurrencyUnit.text = "$"
        }

        binding.tvAddress.text = addressHandle(walletSelleted.address)
        mnemonicBackupTips()
    }

    /**
     * 助记词备份提示
     */
    private fun mnemonicBackupTips(){
        if (isRefreshPopup && !walletSelleted.isBackupAlready && !walletSelleted.mnemonic.isNullOrEmpty()) {
            showCommonXPopup(requireContext(),
                title = getString(R.string.tips),
                titleContent = getString(R.string.mnemonic_no_backup_tips),
                btnCancel = getString(R.string.backup_later),
                btnConfirm = getString(R.string.backup_now),
                confirmListener = {
                    startActivity(Intent(context, WalletMoreOperateActivity::class.java).apply {
                        putExtra(ARG_PARAM1, Gson().toJson(walletSelleted))
                    })
                }
            )
        }
    }

    //资产
    fun initAssetsData(){
        if (isSolValidAddress(walletSelleted.address)) {
            lifecycleScope.launch() {
                getPrice()
                tokenBalance()
                setBalanceSOL()
            }
        }
    }

    /**
     * 币种价格
     */
    @SuppressLint("CheckResult")
    fun getPrice(){
        mainTokenBean.clear()
        totalBalance = BigDecimal("0.00")
        mainTokenBean.add(
            MainTokenBean(
                "SOL",
                "SOL",
                solBalance,
                "0",
                currencyUnit,
                null,
                0,
                false
            )
        )
        mainTokenBean.add(
            MainTokenBean(
                "SOL-USDT",
                "USDT",
                tokenBalance,
                "0",
                currencyUnit,
                contractAddress,
                0,
                true
            )
        )
        val list: ArrayList<String> = arrayListOf()
        mainTokenBean.forEach {
            list.add(it.symbol)
        }
        val req = StoreInfo.Req()
        req.list = list
        req.convertId = "2781,2787,2792" //美元、人民币、港币
        req.platformId = 5426//BTC:1, ETH:1027, TRX:1958, SOL:5426
        mApiService.getStore(req.toApiRequest(getStoreUrl)).applyIo().subscribe({apiResponse->
            if (apiResponse.code() == 1) {
                apiResponse.data()?.let {
                    it.forEach {storeInfo->
                        storeInfo.quote.forEach { quote ->
                            if (quote.symbol == currencyUnit) {
                                storeInfo.price = quote.price
                            }
                        }
                        mainTokenBean.forEach { tokenBean->
                            if (storeInfo.symbol == tokenBean.symbol) {
                                tokenBean.unitPrice = storeInfo.price
                                tokenBean.decimalPoints = storeInfo.decimalPoints
                                if (storeInfo.symbol == "SOL"){
                                    unitPrice = storeInfo.price
                                }
                            }
                        }
                    }
//                    for (i in it.indices){
//                        val tokenBean = mainTokenBean.get(i)
//                        val storeInfo: StoreInfo = it.get(i)
//                        storeInfo.quote.forEach { quote ->
//                            if (quote.symbol == currencyUnit) {
//                                storeInfo.price = quote.price
//                            }
//                        }
//                        if (storeInfo.symbol == tokenBean.symbol) {
//                            tokenBean.unitPrice = storeInfo.price
//                            tokenBean.decimalPoints = storeInfo.decimalPoints
//                            if (storeInfo.symbol == "SOL"){
//                                unitPrice = storeInfo.price
//                            }
//                        }
//                    }
                }
                mainAdapter.setList(mainTokenBean)
            }
        }, {
            mainAdapter.setList(mainTokenBean)
        })
    }


    /**
     * 获取代币余额
     */
    suspend fun tokenBalance() = withContext(Dispatchers.IO){
        val copy = ArrayList(mainTokenBean)
        copy.forEach {tokenBean ->
            if (tokenBean.symbol == "SOL") {
                TransferSOLUtils.getSOLBalance(walletSelleted.address,object :IBalanceListener{
                    override fun onBalance(bigDecimal: BigDecimal) {
                        solBalance = bigDecimal
                        tokenBean.balance = bigDecimal
                    }
                })
                return@forEach
            }
            TransferSOLUtils.getsSOLTokenBalance(walletSelleted.address, tokenBean.contractAddress,object :IBalanceListener{
                override fun onBalance(bigDecimal: BigDecimal) {
                    tokenBalance = bigDecimal
                    tokenBean.balance = bigDecimal
                }
            })
        }
    }

    private fun setBalanceSOL() {
        mainAdapter.setList(mainTokenBean)
        binding.lyPullRefresh.finishRefresh() //刷新完成

        mainTokenBean.forEach {
            totalBalance += it.balance.multiply(BigDecimal(it.unitPrice))
        }

        if (DecimalFormatUtil.format(totalBalance, 2) == "0") {
            binding.tvBalanceTotal.text = "0"
        } else {
            binding.tvBalanceTotal.text = strAddComma(DecimalFormatUtil.format(totalBalance, 2))
        }
    }

    private fun addressHandle(str: String): String {
        val subStr1 = str.substring(0, 10)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 6, strLength)
        return "$subStr1...$subStr2"
    }




}