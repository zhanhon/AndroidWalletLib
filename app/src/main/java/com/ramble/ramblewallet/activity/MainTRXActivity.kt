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
import com.ramble.ramblewallet.databinding.ActivityMainTrxBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils.getBalanceETH
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.rateInfoUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import java.math.BigDecimal

class MainTRXActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainTrxBinding
    private var mainETHTokenBean: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter
    private var rateBean: List<RateBeen> = arrayListOf()
    private lateinit var currencyUnit: String
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var isClickEyes = false
    private var animator: ObjectAnimator? = null
    private var saveTokenList: ArrayList<StoreInfo> = arrayListOf()
    private lateinit var walletSelleted: WalletETH

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_E11334)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_trx)
        initClick()
        initData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADDRESS_TRANS_SCAN -> {
                start(TransferActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, event.data())
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
                    putExtra(ARG_PARAM2, "TRX")
                    putExtra(ARG_PARAM3, false)
                })
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
                })
            }
            R.id.iv_token_manage_click -> {
                startActivity(Intent(this, TokenActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "TRX")
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
                    binding.tvBalanceTotal.text = "1212323"
                    isClickEyes = false
                } else {
                    binding.ivEyes.background = getDrawable(R.drawable.vector_home_address_close)
                    binding.tvBalanceTotal.text = "******"
                    isClickEyes = true
                }
            }
            R.id.iv_copy -> {
                ClipboardUtils.copy(binding.tvTrxAddress.text.toString())
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
        binding.tvBalanceTotal.text = getBalanceETH(walletSelleted.address).toPlainString()
        binding.tvTrxAddress.text = addressHandle(walletSelleted.address)
    }

    private fun addressHandle(str: String): String? {
        val subStr1 = str.substring(0, 6)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 4, strLength)
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
                startActivity(Intent(this, TransferActivity::class.java).apply {
                    putExtra(ARG_PARAM2, tokenName)
                    putExtra(ARG_PARAM3, true)
                })
                dialog.dismiss()
            }
            tvGathering.setOnClickListener { v1: View? ->
                startActivity(Intent(this, GatheringActivity::class.java).apply {
                    putExtra(ARG_PARAM1, "TRX")
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
        mApiService.getRateInfo(EmptyReq().toApiRequest(rateInfoUrl))
            .applyIo().subscribe(
                {
                    if (it.code() == 1) {
                        it.data()?.let { data ->
                            rateBean = data
                            mainETHTokenBean.clear()
                            if (rateBean.isNotEmpty()) {
                                rateBean.forEach { //标题：ETH
                                    if (it.currencyType == "TRX") {
                                        mainETHTokenBean.add(
                                            MainETHTokenBean(
                                                it.currencyType,
                                                BigDecimal(10.12123),
                                                BigDecimal(it.rateUsd),
                                                currencyUnit,
                                                BigDecimal(it.change)
                                            )
                                        )
                                    }
                                }
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
                                if (saveTokenList.isNotEmpty()) {
                                    rateBean.forEach { rateBean ->
                                        saveTokenList.forEach { saveToken ->
                                            if (saveToken.name == rateBean.currencyType) {
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
                                        if ((adapter.getItem(position) as MainETHTokenBean).name != "TRX") {
                                            showTransferGatheringDialog((adapter.getItem(position) as MainETHTokenBean).name)
                                        }
                                    }
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