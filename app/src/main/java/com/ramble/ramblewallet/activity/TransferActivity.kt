package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.EthMinerConfig
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityTransferBinding
import com.ramble.ramblewallet.ethereum.TransferEthUtils.*
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.getEthMinerConfigUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.TransferTrxUtils.*
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.utils.*
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

    private var isCustom = false
    private lateinit var walletSelleted: WalletETH
    private lateinit var transferTitle: String
    private var transferReceiverAddress: String? = null
    private var isToken: Boolean = false

    //DAI:4 0x16aFDD5dfE386052766b798bFA37DAec4b81155a
    private var contractETHAddress =
        "0xb319d1A045ffe108D14195F7C5d60Be220436a34" //测试节点ERC-USDT:6合约地址
    //private var contractAddress = "0xd95371A0550cF47aB9519105300707a82b0640Db" //开发节点ERC-USDT合约地址

    private var contractTRXAddress = "TU9iBgEEv9qsc6m7EBPLJ3x5vSNKfyxWW5" //YING 合约地址；nile

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)
        transferReceiverAddress = intent.getStringExtra(ARG_PARAM1)
        transferTitle = intent.getStringExtra(ARG_PARAM2)
        isToken = intent.getBooleanExtra(ARG_PARAM3, false)
        rate = intent.getStringExtra(ARG_PARAM4)
        if (transferTitle.contains("-"))  {
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
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADDRESS_TRANS_SCAN -> {
                binding.edtReceiverAddress.text = event.data()

            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_transfer_scan -> {
                start(ScanActivity::class.java, Bundle().also {
                    it.putInt(ARG_PARAM1, 2)
                })
            }
            R.id.iv_address_book -> {
                startActivity(Intent(this, AddressBookActivity::class.java).apply {
                    putExtra(ARG_PARAM1, true)
                    putExtra(ARG_PARAM2, transferTitle)
                    putExtra(ARG_PARAM3, isToken)
                    putExtra(ARG_PARAM4, rate)
                })
            }
            R.id.cl_miner_fee -> {
                showDialog()
            }
            R.id.tv_select_all -> {
                binding.edtInputQuantity.setText(DecimalFormatUtil.format8.format(transferBalance))
            }
            R.id.btn_confirm -> {
                transactionConfirmationDialog()
            }
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
        currencyUnit = SharedPreferencesUtils.getString(this, CURRENCY, CNY)
        if (SharedPreferencesUtils.getString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
                object : TypeToken<WalletETH>() {}.type
            )
            binding.tvWalletAddress.text = walletSelleted.address
            binding.tvWalletName.text = walletSelleted.walletName
        }

        when (currencyUnit) {
            CNY -> currencySymbol = "￥"
            HKD -> currencySymbol = "HK$"
            USD -> currencySymbol = "$"
        }

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
                        setMinerFee()
                    }
                } else {
                    println("-=-=-=->ETH:${it.message()}")
                }
            }, {
                println("-=-=-=->ETH:${it.printStackTrace()}")
            }
        )

        when (walletSelleted.walletType) { //链类型|0:BTC|1:ETH|2:TRX
            1 -> {
                binding.clMinerFee.visibility = View.VISIBLE
            }
            2 -> {
                binding.clMinerFee.visibility = View.GONE
            }
            0 -> {

            }
        }

        binding.tvTransferTitle.text = transferTitle + " " + getString(R.string.transfer)

        when (walletSelleted.walletType) {
            1 -> {
                if (WalletETHUtils.isEthValidAddress(walletSelleted.address)) {
                    if (isToken) {
                        Thread {
                            transferBalance =
                                getBalanceToken(walletSelleted.address, contractETHAddress)
                            if (transferBalance != BigDecimal("0.000000")) {
                                setBalance(transferBalance)
                            }
                        }.start()
                    } else {
                        Thread {
                            transferBalance = getBalanceETH(walletSelleted.address)
                            if (transferBalance != BigDecimal("0.00000000")) {
                                setBalance(transferBalance)
                            }
                        }.start()
                    }
                }
            }
            2 -> {
                if (WalletTRXUtils.isTrxValidAddress(walletSelleted.address)) {
                    if (isToken) {
                        balanceOfTrc20(
                            this,
                            walletSelleted.address,
                            contractTRXAddress
                        )
                    } else {
                        balanceOfTrx(this, walletSelleted.address)
                    }
                }
            }
            0 -> {

            }
        }
    }

    fun setTrxBalance(balance: BigDecimal) {
        transferBalance = balance
        setBalance(transferBalance)
    }

    fun setTokenBalance(balance: BigDecimal) {
        transferBalance = balance
        setBalance(transferBalance)
    }

    private fun setMinerFee() {
        gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit)).divide(BigDecimal("1000000000"))
        when (walletSelleted.walletType) {
            1 -> {
                binding.tvMinerFeeValue.text = "${DecimalFormatUtil.format8.format(gas)} ETH"
                binding.tvMinerFeeValueConvert.text = "≈${currencySymbol}${
                    DecimalFormatUtil.format2.format(BigDecimal(rate).multiply(gas))
                }"
            }
            2 -> {

            }
            0 -> {

            }
        }
        binding.tvTips.text = "$gasPrice GWEI * GAS ($gasLimit)"
    }

    private fun setBalance(balance: BigDecimal) {
        postUI {
            binding.tvQuantityBalance.text =
                getString(R.string.transfer_balance) + " " + DecimalFormatUtil.format8.format(
                    balance
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
                tvAmount.text = DecimalFormatUtil.format8.format(
                    BigDecimal(binding.edtInputQuantity.text.trim().toString())
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
                    if (edtWalletPassword.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            val btnConfirm = window.findViewById<Button>(R.id.btn_confirm)
            btnConfirm.setOnClickListener {
                if (edtWalletPassword.text.trim().toString() == walletSelleted.walletPassword) {
                    transferReceiverAddress = binding.edtReceiverAddress.text.toString()
                    transfer()
                    dialog.dismiss()
                } else {
                    toastDefault(getString(R.string.input_correct_wallet_password))
                }
            }

        }
    }

    private fun transfer() {
        when (walletSelleted.walletType) { //链类型|0:BTC|1:ETH|2:TRX
            1 -> {
                if (isToken) {
                    transferETHToken( //暂时是USDT合约
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        contractETHAddress,
                        walletSelleted.privateKey,
                        BigDecimal(binding.edtInputQuantity.text.trim().toString()).multiply(
                            BigDecimal("1000000")
                        ).toBigInteger(),
                        (BigInteger(gasPrice).multiply(BigInteger("100000000"))), //GWEI → WEI
                        BigInteger(gasLimit),
                        binding.edtInputTransferRemarks.text.trim().toString()
                    )
                } else {
                    transferETH(
                        this,
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        binding.edtInputQuantity.text.trim().toString(),
                        BigInteger(gasPrice), //GWEI → WEI
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
                        contractTRXAddress,
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
            0 -> {

            }
        }
    }

    fun transferSuccess(transactionHash: String) {
        postUI {
            transactionFinishConfirmDialog(transactionHash)
        }
    }

    fun transferFail(strFail: String) {
        postUI {
            toastDefault(getString(R.string.failure_transaction))
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
    private fun showDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_miner_fee)
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

            when (walletSelleted.walletType) {
                1 -> {
                    gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit))
                        .divide(BigDecimal("1000000000"))
                    window.findViewById<TextView>(R.id.tv_transfer_gas_fast).text =
                        "${
                            DecimalFormatUtil.format8.format(
                                BigDecimal(fastGasPrice).multiply(BigDecimal(gasLimit))
                                    .divide(BigDecimal("1000000000"))
                            )
                        } ETH"
                    window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                        "${
                            DecimalFormatUtil.format8.format(
                                BigDecimal(slowGasPrice).multiply(BigDecimal(gasLimit))
                                    .divide(BigDecimal("1000000000"))
                            )
                        } ETH"
                }
                2 -> {

                }
                0 -> {

                }
            }

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
                setMinerFee()
                dialog.dismiss()
                if ((BigDecimal(gasPrice) < BigDecimal(slowGasPrice))
                    || (BigDecimal(gasLimit) < BigDecimal("21000"))
                ) {
                    transactionFailDialog(getString(R.string.miner_fee_low_tips))
                    return@setOnClickListener
                }
                if ((BigDecimal(gasPrice) > BigDecimal(fastGasPrice))
                    || (BigDecimal(gasLimit) < BigDecimal("1000000"))
                ) {
                    transactionFailDialog(getString(R.string.miner_fee_high_tips))
                    return@setOnClickListener
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
            isCustom = false
        }
    }

}