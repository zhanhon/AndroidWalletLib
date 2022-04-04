package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import cn.hutool.core.util.StrUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.*
import com.ramble.ramblewallet.bitcoin.TransferBTCUtils.balanceOfBtc
import com.ramble.ramblewallet.bitcoin.TransferBTCUtils.transferBTC
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityTransferBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils.*
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.*
import com.ramble.ramblewallet.tron.TransferTrxUtils.*
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.inputWatch
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import org.bitcoinj.core.UTXO
import java.math.BigDecimal
import java.math.BigInteger


class TransferActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTransferBinding
    private var transferBalance: BigDecimal = BigDecimal("0.00")
    private var transferUnit: String? = ""
    private lateinit var currencyUnit: String
    private lateinit var currencySymbol: String
    private var gasLimit: String? = ""
    private var fastGasPrice: String? = ""
    private var slowGasPrice: String? = ""
    private var gasPrice: String? = ""
    private var rate: String? = ""
    private var gas: BigDecimal = BigDecimal("0.00")
    private var btcFee: String? = ""
    private var btcFeeFast: String? = ""
    private var btcFeeSlow: String? = ""
    private var times = 0
    private var isCustom = false
    private lateinit var walletSelleted: Wallet
    private lateinit var transferTitle: String
    private var transferReceiverAddress: String? = null
    private var isToken: Boolean = false
    private lateinit var tokenBean: MainETHTokenBean

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)
        transferReceiverAddress = intent.getStringExtra(ARG_PARAM1)
        tokenBean = intent.getSerializableExtra(ARG_PARAM2) as MainETHTokenBean
        rate = tokenBean.unitPrice
        isToken = tokenBean.isToken
        transferTitle = tokenBean.title

        if (transferTitle.contains("-")) {
            val index = transferTitle.indexOf("-")
            transferUnit = transferTitle.substring(index + 1, transferTitle.length)
        } else {
            transferUnit = transferTitle
        }

        binding.edtReceiverAddress.setText(transferReceiverAddress)
        initClick()
    }

    override fun onResume() {
        super.onResume()
        initData()
        if (walletSelleted.walletType == 2) {
            binding.edtInputQuantity.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(24))
            EditTextTools(binding.edtInputQuantity, 24, 6)
        } else {
            binding.edtInputQuantity.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(26))
            EditTextTools(binding.edtInputQuantity, 26, 8)
        }
        binding.edtInputQuantity.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //暂时不需要实现此方法
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //暂时不需要实现此方法
            }
        })
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_RESS_TRANS_SCAN -> {
                binding.edtReceiverAddress.text = event.data()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                backChoosePurpose()
            }
            R.id.iv_transfer_scan -> {
                start(ScanActivity::class.java, Bundle().also {
                    it.putInt(ARG_PARAM1, 2)
                    it.putSerializable(ARG_PARAM2, tokenBean)
                })
            }
            R.id.iv_address_book -> {
                startActivity(Intent(this, AddressBookActivity::class.java).apply {
                    putExtra(ARG_PARAM1, true)
                    putExtra(ARG_PARAM2, tokenBean)
                })
            }
            R.id.cl_miner_fee -> {
                showMinerFeeDialog()
            }
            R.id.tv_select_all -> {
                binding.edtInputQuantity.setText(DecimalFormatUtil.format(transferBalance, 8))
            }
            R.id.btn_confirm -> {
                if (confirmValidForTransfer()) return
                transactionConfirmationDialog()
            }
        }
    }

    private fun confirmValidForTransfer(): Boolean {
        if (BigDecimal(binding.edtInputQuantity.text.trim().toString()).compareTo(
                transferBalance
            ) == 1
        ) {
            ToastUtils.showToastFree(this, getString(R.string.balance_insufficient))
            return true
        }
        when (walletSelleted.walletType) { //链类型|1:ETH|2:TRX|3:BTC
            1 -> {
                if (!WalletETHUtils.isEthValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
            }
            2 -> {
                if (!WalletTRXUtils.isTrxValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
                if (isToken) {
                    isAddressActivateToken(
                        this,
                        binding.edtReceiverAddress.text.toString(),
                        tokenBean.contractAddress
                    )
                } else {
                    isAddressActivate(this, binding.edtReceiverAddress.text.toString())
                }
            }
            3 -> {
                if (!WalletBTCUtils.isBtcValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
                if (BigDecimal(binding.edtInputQuantity.text.toString()).compareTo(
                        BigDecimal("0.0001")
                    ) == -1
                ) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.btc_minimum_amount_prompt)
                    )
                    return true
                }
            }
        }
        if (StrUtil.equalsIgnoreCase(
                binding.tvWalletAddress.text.toString(),
                binding.edtReceiverAddress.text.toString()
            )
        ) {
            ToastUtils.showToastFree(this, getString(R.string.repeat_address))
            return true
        }
        return false
    }

    private fun showMinerFeeDialog() {
        when (walletSelleted.walletType) {
            1 -> {
                showEthDialog()
            }
            3 -> {
                showBtcDialog()
            }
        }
    }

    private fun backChoosePurpose() {
        when (walletSelleted.walletType) {
            1 -> {
                start(MainETHActivity::class.java)
            }
            2 -> {
                start(MainTRXActivity::class.java)
            }
            3 -> {
                start(MainBTCActivity::class.java)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnIsClick() {
        if ((binding.edtReceiverAddress.text.trim().toString().isNotEmpty())
            && (binding.edtInputQuantity.text.trim().toString().isNotEmpty())
        ) {
            binding.btnConfirm.isEnabled = true
            binding.btnConfirm.background = getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnConfirm.isEnabled = false
            binding.btnConfirm.background = getDrawable(R.drawable.shape_gray_bottom_btn)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.ivTransferScan.setOnClickListener(this)
        binding.ivAddressBook.setOnClickListener(this)
        binding.clMinerFee.setOnClickListener(this)
        binding.tvSelectAll.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    private fun initData() {
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, USD)
        if (SharedPreferencesUtils.getString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
            binding.tvWalletAddress.text = walletSelleted.address
            binding.tvWalletName.text = walletSelleted.walletName
        }

        when (currencyUnit) {
            CNY -> currencySymbol = "￥"
            HKD -> currencySymbol = "HK$"
            USD -> currencySymbol = "$"
        }

        when (walletSelleted.walletType) {  //链类型|1:ETH|2:TRX|3:BTC
            1 -> {
                initEthMinerFee()
            }
            3 -> {
                initBtcMiner()
            }
        }


        binding.edtReceiverAddress.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //暂时不需要实现此方法
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //暂时不需要实现此方法
            }
        })
        binding.edtInputQuantity.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //暂时不需要实现此方法
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //暂时不需要实现此方法
            }
        })

        when (walletSelleted.walletType) {  //链类型|1:ETH|2:TRX|3:BTC
            1 -> {
                binding.clMinerFee.visibility = View.VISIBLE
                binding.edtReceiverAddress.hint = "ETH" + getString(R.string.address)
            }
            2 -> {
                binding.clMinerFee.visibility = View.GONE
                binding.edtReceiverAddress.hint = "TRX" + getString(R.string.address)
            }
            3 -> {
                binding.clMinerFee.visibility = View.VISIBLE
                binding.edtReceiverAddress.hint = "BTC" + getString(R.string.address)
            }
        }

        binding.tvTransferTitle.text = transferTitle + " " + getString(R.string.transfer)
        binding.tvQuantityBalance.text =
            getString(R.string.transfer_balance) + " " + "0" + " " + transferUnit
        initBalanceForTransfer()
    }

    private fun initBalanceForTransfer() {
        when (walletSelleted.walletType) {
            1 -> {
                if (WalletETHUtils.isEthValidAddress(walletSelleted.address)) {
                    initBalanceForEth()
                }
            }
            2 -> {
                if (WalletTRXUtils.isTrxValidAddress(walletSelleted.address)) {
                    initBalanceForTrx()
                }
            }
            3 -> {
                if (WalletBTCUtils.isBtcValidAddress(walletSelleted.address) && (!isToken)) {
                    balanceOfBtc(this, walletSelleted.address)
                }
            }
        }
    }

    private fun initBalanceForTrx() {
        if (isToken) {
            balanceOfTrc20(
                this,
                walletSelleted.address,
                tokenBean.contractAddress
            )
        } else {
            balanceOfTrx(this, walletSelleted.address)
        }
    }

    private fun initBalanceForEth() {
        if (isToken) {
            Thread {
                transferBalance =
                    getBalanceToken(walletSelleted.address, tokenBean)
                if (transferBalance != BigDecimal("0")) {
                    setBalanceView(transferBalance)
                }
            }.start()
        } else {
            Thread {
                transferBalance = getBalanceETH(walletSelleted.address)
                if (transferBalance != BigDecimal("0")) {
                    setBalanceView(transferBalance)
                }
            }.start()
        }
    }

    private fun initBtcMiner() {
        mApiService.getBtcMinerConfig(
            BtcMinerConfig.Req().toApiRequest(getBtcMinerConfigUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        btcFeeFast = data.fastestFee
                        btcFeeSlow = data.generalFee

                        //默认
                        btcFee = btcFeeFast
                        if (!btcFee.isNullOrEmpty()) {
                            setBtcMinerFee()
                        }
                    }
                } else {
                    println("-=-=-=->BTC:${it.message()}")
                }
            }, {
                println("-=-=-=->BTC:${it.printStackTrace()}")
            }
        )
    }

    private fun initEthMinerFee() {
        mApiService.getEthMinerConfig(
            EthMinerConfig.Req(transferTitle).toApiRequest(getEthMinerConfigUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        gasLimit = data.gasLimit
                        fastGasPrice = data.fastGasPrice
                        slowGasPrice = data.slowGasPrice

                        //默认
                        gasPrice = fastGasPrice
                        setEthMinerFee()
                    }
                } else {
                    println("-=-=-=->ETH:${it.message()}")
                }
            }, {
                println("-=-=-=->ETH:${it.printStackTrace()}")
            }
        )
    }

    fun isTrxAddressActivate(isAddressActivate: Boolean) {
        postUI {
            if (!isAddressActivate) {
                binding.tvNoActivateTips.visibility = View.VISIBLE
                if (isToken) {
                    binding.tvNoActivateTips.setText(R.string.trx_address_no_activate_token)
                } else {
                    binding.tvNoActivateTips.setText(R.string.trx_address_no_activate)
                }
            } else {
                binding.tvNoActivateTips.visibility = View.GONE
            }
        }
    }

    fun setBalance(balance: BigDecimal) {
        transferBalance = balance
        setBalanceView(transferBalance)
    }

    private fun setEthMinerFee() {
        gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit)).divide(BigDecimal("1000000000"))
        binding.tvMinerFeeValue.text = "${DecimalFormatUtil.format(gas, 8)} ETH"
        binding.tvMinerFeeValueConvert.text = "≈${currencyUnit} ${currencySymbol}${
            DecimalFormatUtil.format(BigDecimal(rate).multiply(gas), 8)
        }"
        binding.tvTips.text = "$gasPrice Gwei * Gas Limit (${strAddComma(gasLimit)})"
    }

    private fun setBtcMinerFee() {
        binding.tvMinerFeeValue.text = "${
            DecimalFormatUtil.format(
                BigDecimal(btcFee).multiply(BigDecimal("72"))
                    .divide(BigDecimal("1000000000")), 8
            )
        } BTC"
        binding.tvMinerFeeValueConvert.text = "≈${currencyUnit} ${currencySymbol}${
            DecimalFormatUtil.format(
                BigDecimal(rate).multiply(
                    BigDecimal(btcFee).multiply(BigDecimal("72"))
                        .divide(BigDecimal("1000000000"))
                ), 8
            )
        }"
        binding.tvTips.visibility = View.INVISIBLE
    }

    private fun setBalanceView(balance: BigDecimal) {
        postUI {
            binding.tvQuantityBalance.text =
                getString(R.string.transfer_balance) + " " + DecimalFormatUtil.format(
                    balance, 8
                ) + " " + transferUnit
        }
    }


    private fun transactionConfirmationDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_transaction_confirmation)
            dialogTheme(window)

            val tvAmount = window.findViewById<TextView>(R.id.tv_amount)
            if (binding.edtInputQuantity.text.trim().toString().isNotEmpty()) {
                tvAmount.text = DecimalFormatUtil.format(
                    BigDecimal(binding.edtInputQuantity.text.trim().toString()), 8
                ) + " " + transferUnit
            }
            val edtSendingAddress = window.findViewById<EditText>(R.id.edt_sending_address)
            edtSendingAddress.setText(binding.tvWalletAddress.text.trim().toString())
            val edtReceivingAdreess = window.findViewById<EditText>(R.id.edt_receiving_address)
            edtReceivingAdreess.setText(binding.edtReceiverAddress.text.trim().toString())
            val btnNext = window.findViewById<Button>(R.id.btn_confirm)
            btnNext.setOnClickListener {
                passwordConfirmationDialog()
                dialog.dismiss()
            }

        }
    }

    private fun passwordConfirmationDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_password_confirmation)
            dialogTheme(window)

            val tvTransferTile = window.findViewById<TextView>(R.id.tv_transfer_tile)
            tvTransferTile.text = transferTitle + getString(R.string.transfer)
            val edtWalletPassword = window.findViewById<TextView>(R.id.edt_wallet_password)
            edtWalletPassword.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if ((edtWalletPassword.text.isNotEmpty())
                        && (edtWalletPassword.text.length >= 6)
                    ) {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = true
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = false
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //暂时不需要实现此方法
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //暂时不需要实现此方法
                }
            })
            val btnConfirm = window.findViewById<Button>(R.id.btn_confirm)
            btnConfirm.setOnClickListener {
                if (edtWalletPassword.text.trim().toString() == walletSelleted.walletPassword) {
                    transferReceiverAddress = binding.edtReceiverAddress.text.toString()
                    transfer()
                    dialog.dismiss()
                } else {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_wallet_password)
                    )
                }
            }

        }
    }

    private fun transfer() {
        when (walletSelleted.walletType) {  //链类型|1:ETH|2:TRX|3:BTC
            1 -> {
                if (isToken) {
                    transferETHToken( //暂时是USDT合约
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        tokenBean.contractAddress,
                        walletSelleted.privateKey,
                        BigDecimal(binding.edtInputQuantity.text.trim().toString()).multiply(
                            BigDecimal("1000000")
                        ).toBigInteger(),
                        (BigInteger(gasPrice).multiply(BigInteger("1000000000"))), //GWEI → WEI
                        BigInteger(gasLimit)
                    )
                } else {
                    transferETH(
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        binding.edtInputQuantity.text.trim().toString(),
                        (BigInteger(gasPrice).multiply(BigInteger("1000000000"))), //GWEI → WEI
                        BigInteger(gasLimit),
                        binding.edtInputTransferRemarks.text.trim().toString()
                    )
                }
            }
            2 -> {
                if (isToken) {
                    transferTRXToken(
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        tokenBean.contractAddress,
                        walletSelleted.privateKey,
                        BigDecimal(binding.edtInputQuantity.text.trim().toString()).multiply(
                            BigDecimal("1000000")
                        ).toBigInteger(),
                        binding.edtInputTransferRemarks.text.trim().toString()
                    )
                } else {
                    transferTRX(
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        BigDecimal(binding.edtInputQuantity.text.trim().toString()).multiply(
                            BigDecimal("1000000")
                        ),
                        binding.edtInputTransferRemarks.text.trim().toString()
                    )
                }
            }
            3 -> {
                if (!isToken) {
                    println("-=-=-=-=->btcFee：${btcFee}")
                    transferBTC(
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        BigDecimal(binding.edtInputQuantity.text.trim().toString()).multiply(
                            BigDecimal("100000000")
                        ),
                        btcFee
                    )
                }
            }
        }
    }

    fun transferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
        println("-=-=-=-=->transactionUTXO:${Gson().toJson(utxos)}")
        println("-=-=-=-=->transactionHash:${transactionHash}")
        postUI {
            transactionFinishConfirmDialog(transactionHash)
        }
        if (transactionHash.isNotEmpty()){
            putTransAddress(transactionHash, utxos)
        }
    }

    /***
     *上报转出交易记录
     */
    @SuppressLint("CheckResult")
    private fun putTransAddress(hash: String, utxos: MutableList<UTXO>?) {
        var inputs: ArrayList<ReportTransferInfo.InRecord>? = arrayListOf()//BTC转出信息
        var outputs: ArrayList<ReportTransferInfo.InRecord>? = arrayListOf()//BTC转出信息
        var edtReceiverAddress = binding.edtReceiverAddress.text.trim().toString()
        var edtInputQuantity = binding.edtInputQuantity.text.trim().toString()
        var tvWalletAddress = binding.tvWalletAddress.text.trim().toString()
        if (walletSelleted.walletType == 3) {
            var a = ReportTransferInfo.InRecord(
                edtReceiverAddress,
                edtInputQuantity,
                0
            )
            inputs?.add(a)
            var tferb = (transferBalance - BigDecimal(
                DecimalFormatUtil.format(
                    BigDecimal(btcFee).multiply(BigDecimal("72")).divide(BigDecimal("1000000000")),
                    8
                )
            ) - BigDecimal(edtInputQuantity)).toString()
            var a1 = ReportTransferInfo.InRecord(
                tvWalletAddress,
                tferb,
                1
            )
            inputs?.add(a1)
            utxos?.forEach { utxo ->
                outputs?.add(
                    ReportTransferInfo.InRecord(
                        utxo.address,
                        BigDecimal(utxo.value.toString()).divide(BigDecimal("10").pow(8))
                            .toString(),
                        utxo.index.toInt()
                    )
                )
            }
        }
        mApiService.reportTransferRecord(
            ReportTransferInfo.Req(
                walletSelleted.walletType,
                edtInputQuantity,
                tokenBean.contractAddress,
                tokenBean.symbol,
                tvWalletAddress,
                gasLimit,
                gasPrice,
                inputs?.toList(),
                outputs?.toList(),
                edtReceiverAddress,
                hash
            )
                .toApiRequest(reportTransferUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    if (times < 3) {
                        putTransAddress(hash, utxos)
                        times++
                    }
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }


    fun transferFail(strFail: String) {
        postUI {
            ToastUtils.showToastFree(this, getString(R.string.transaction_failed))
        }
    }

    private fun transactionFinishConfirmDialog(transactionHash: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_transfer_confirm)
            dialogCenterTheme(window)

            window.findViewById<TextView>(R.id.tv_transaction_code).text = transactionHash
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this, TransactionQueryActivity::class.java))
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun transactionFailDialog(content: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_transfer_fail)
            dialogCenterTheme(window)

            window.findViewById<TextView>(R.id.tv_content).text = content
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun dialogCenterTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun dialogTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun showEthDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_eth_miner_fee)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes //设置属性
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog背景层
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_transfer_gas_price_fast).text =
                "($fastGasPrice GWEI)"
            window.findViewById<TextView>(R.id.tv_transfer_gas_price_slow).text =
                "($slowGasPrice GWEI)"

            window.findViewById<TextView>(R.id.miner_fee_gas_price).text = fastGasPrice
            window.findViewById<TextView>(R.id.miner_fee_limit_title).text = "21000"

            gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit))
                .divide(BigDecimal("1000000000"))
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(fastGasPrice).multiply(BigDecimal(gasLimit))
                            .divide(BigDecimal("1000000000")), 8
                    )
                } ETH"
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(slowGasPrice).multiply(BigDecimal(gasLimit))
                            .divide(BigDecimal("1000000000")), 8
                    )
                } ETH"

            onClickTransferFast(window)
            onClickTransferSlow(window)
            onClickTransferCustom(window)

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (isCustom) {
                    gasPrice =
                        window.findViewById<EditText>(R.id.miner_fee_gas_price).text.toString()
                    gasLimit =
                        window.findViewById<EditText>(R.id.miner_fee_limit_title).text.toString()
                }
                setEthMinerFee()
                dialog.dismiss()
                if (isToken) {
                    tokenFeeTipsLogic()
                } else {
                    mainFeeTipsLogic()
                }
            }
        }
    }

    private fun mainFeeTipsLogic() {
        if ((BigDecimal(gasPrice) <= BigDecimal(slowGasPrice).multiply(BigDecimal("0.9")))
            || (BigDecimal(gasLimit) < BigDecimal("21000"))
        ) {
            transactionFailDialog(getString(R.string.miner_fee_low_tips))
            return
        }
        if ((BigDecimal(gasPrice) >= BigDecimal(fastGasPrice).multiply(BigDecimal("1.2")))
            || (BigDecimal(gasLimit) > BigDecimal("26000"))
        ) {
            transactionFailDialog(getString(R.string.miner_fee_high_tips))
            return
        }
    }

    private fun tokenFeeTipsLogic() {
        if ((BigDecimal(gasPrice) < BigDecimal("60"))
            || (BigDecimal(gasLimit) < BigDecimal("62000"))
        ) {
            transactionFailDialog(getString(R.string.miner_fee_low_tips))
            return
        }
        if ((BigDecimal(gasPrice) > BigDecimal("300"))
            || (BigDecimal(gasLimit) > BigDecimal("100000"))
        ) {
            transactionFailDialog(getString(R.string.miner_fee_high_tips))
            return
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun showBtcDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_btc_miner_fee)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes //设置属性
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog背景层
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_transfer_gas_price_fast).text =
                "($btcFeeFast sat)"
            window.findViewById<TextView>(R.id.tv_transfer_gas_price_slow).text =
                "($btcFeeSlow sat)"

            btcFee = btcFeeFast
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(btcFeeFast).multiply(BigDecimal("72"))
                            .divide(BigDecimal("1000000000")), 8
                    )
                } BTC"
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(btcFeeSlow).multiply(BigDecimal("72"))
                            .divide(BigDecimal("1000000000")), 8
                    )
                } BTC"

            onClickTransferFast(window)
            onClickTransferSlow(window)
            onClickTransferCustom(window)

            inputWatch(window.findViewById<EditText>(R.id.miner_fee_gas_price), 250, 1)

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (isCustom) {
                    btcFee = window.findViewById<EditText>(R.id.miner_fee_gas_price).text.toString()
                }
                if (!btcFee.isNullOrEmpty()) {
                    setBtcMinerFee()
                    dialog.dismiss()
                    if (BigDecimal(btcFee) < BigDecimal(btcFeeSlow)) {
                        transactionFailDialog(getString(R.string.miner_fee_low_tips))
                        return@setOnClickListener
                    }
                    if (BigDecimal(btcFee) > BigDecimal(btcFeeFast)) {
                        transactionFailDialog(getString(R.string.miner_fee_high_tips))
                        return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun onClickTransferCustom(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_custom).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.VISIBLE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)


            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            isCustom = true
        }
    }

    private fun onClickTransferSlow(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_slow).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.GONE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_green_bottom_btn)

            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_FFFFFF))

            gasPrice = slowGasPrice
            btcFee = btcFeeSlow
            isCustom = false
        }
    }

    private fun onClickTransferFast(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_fast).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.GONE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_green_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)

            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_FFFFFF))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))

            gasPrice = fastGasPrice
            btcFee = btcFeeFast
            isCustom = false
        }
    }


}