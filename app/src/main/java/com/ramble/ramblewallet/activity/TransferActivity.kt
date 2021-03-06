package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
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
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import cn.hutool.core.util.StrUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.*
import com.ramble.ramblewallet.blockchain.IAddressActivateListener
import com.ramble.ramblewallet.blockchain.ITransferListener
import com.ramble.ramblewallet.blockchain.bitcoin.TransferBTCUtils.transferBTC
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.blockchain.ethereum.TransferETHUtils.transferETH
import com.ramble.ramblewallet.blockchain.ethereum.TransferETHUtils.transferETHToken
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.blockchain.solana.TransferSOLUtils.setAllowUniversalAccessFromFileURLs
import com.ramble.ramblewallet.blockchain.solana.TransferSOLUtils.transferSOL
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils
import com.ramble.ramblewallet.blockchain.solana.solanatokentransfer.WebViewJavascriptBridge
import com.ramble.ramblewallet.blockchain.tron.TransferTRXUtils.*
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityTransferBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.network.getBtcMinerConfigUrl
import com.ramble.ramblewallet.network.getEthMinerConfigUrl
import com.ramble.ramblewallet.network.reportTransferUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.inputWatch
import com.ramble.ramblewallet.utils.StringUtils.strAddComma
import com.ramble.ramblewallet.wight.FingerprintDialogFragment
import com.ramble.ramblewallet.wight.FingerprintDialogFragment.OnFingerprintSetting
import org.bitcoinj.core.UTXO
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger
import javax.crypto.Cipher

class TransferActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTransferBinding
    private var transferBalance: BigDecimal = BigDecimal("0")
    private var transferUnit: String? = ""
    private lateinit var currencyUnit: String
    private lateinit var currencySymbol: String
    private lateinit var walletSelleted: Wallet
    private lateinit var tokenBean: MainTokenBean
    private lateinit var bridge: WebViewJavascriptBridge
    private lateinit var gas: BigDecimal
    private var gasLimit: String? = ""
    private var fastGasPrice: String? = ""
    private var slowGasPrice: String? = ""
    private var gasPrice: String? = ""
    private var btcFee: String? = ""
    private var btcFeeFast: String? = ""
    private var btcFeeSlow: String? = ""
    private var isCustom = false
    private var transferReceiverAddress: String? = null
    private var isFinger = false
    private var cipher: Cipher? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)
        tokenBean = intent.getSerializableExtra(ARG_PARAM2) as MainTokenBean
        transferUnit = if (tokenBean.title.contains("-")) {
            val index = tokenBean.title.indexOf("-")
            tokenBean.title.substring(index + 1, tokenBean.title.length)
        } else {
            tokenBean.title
        }
        initClick()
        initWebView()
    }

    private fun initWebView() {
        val webview = findViewById<WebView>(R.id.web_view_token_transfer)
        setAllowUniversalAccessFromFileURLs(webview)
        bridge = WebViewJavascriptBridge(this, webview)
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                bridge.injectJavascript()
            }

            override fun onPageFinished(view: WebView, url: String) {}
        }
        webview.loadUrl("file:///android_asset/index.html")
    }

    override fun onResume() {
        super.onResume()
        isFinger = SharedPreferencesUtils.getSecurityBoolean(this, ISFINGERPRINT_KEY, false)
        initData()
        if (walletSelleted.walletType == 2) {
            binding.edtInputQuantity.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(24))
            EditTextTools(binding.edtInputQuantity, 24, 6)
        } else {
            if (tokenBean.isToken) {
                binding.edtInputQuantity.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(24))
                EditTextTools(binding.edtInputQuantity, 24, 6)
            } else {
                binding.edtInputQuantity.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(26))
                EditTextTools(binding.edtInputQuantity, 26, 8)
            }
        }
        binding.edtInputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //??????????????????????????????
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //??????????????????????????????
            }
        })
    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_RESS_TRANS_SCAN -> {
                binding.edtReceiverAddress.text = event.data()
            }
            Pie.EVENT_TRANS_ADDRESS -> {
                transferReceiverAddress = event.data()
                binding.edtReceiverAddress.setText(transferReceiverAddress)
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
                binding.edtInputQuantity.setText(DecimalFormatUtil.format(tokenBean.balance, 8))
            }
            R.id.btn_confirm -> {
                if (confirmValidForTransfer()) return
                transactionConfirmationDialog()
            }
        }
    }

    private fun confirmValidForTransfer(): Boolean {
        val amount = binding.edtInputQuantity.text.trim().toString()
        if (BigDecimal(amount).compareTo(tokenBean.balance) == 1) {
            ToastUtils.showToastFree(this, getString(R.string.balance_insufficient))
            return true
        }
        when (walletSelleted.walletType) { //?????????|1:ETH|2:TRX|3:BTC|4:SOL
            WALLET_TYPE_ETH-> {
                if (!WalletETHUtils.isEthValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
            }
            WALLET_TYPE_TRX -> {
                if (!WalletTRXUtils.isTrxValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
                if (tokenBean.isToken) {
                    isAddressActivateToken(
                        binding.edtReceiverAddress.text.toString(),
                        tokenBean.contractAddress,
                        object :IAddressActivateListener{
                            override fun onAddressActivate(boolean: Boolean) {
                                isTrxAddressActivate(boolean)
                            }
                        }
                    )
                } else {
                    isAddressActivate(binding.edtReceiverAddress.text.toString(),object :IAddressActivateListener{
                        override fun onAddressActivate(boolean: Boolean) {
                            isTrxAddressActivate(boolean)
                        }

                    })
                }
            }
            WALLET_TYPE_BTC -> {
                if (!WalletBTCUtils.isBtcValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
                if (BigDecimal(amount).compareTo(BigDecimal("0.0001")) == -1) {
                    ToastUtils.showToastFree(
                        this,
                        String.format(getString(R.string.btc_minimum_amount_prompt), "0.0001")
                    )
                    return true
                }
            }
            WALLET_TYPE_SOL -> {
                if (!WalletSOLUtils.isSolValidAddress(binding.edtReceiverAddress.text.toString())) {
                    ToastUtils.showToastFree(this, getString(R.string.address_already_err))
                    return true
                }
                if (BigDecimal(amount).compareTo(BigDecimal("0.000001")) == -1) {
                    ToastUtils.showToastFree(
                        this,
                        String.format(getString(R.string.btc_minimum_amount_prompt), "0.000001")
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
            WALLET_TYPE_ETH -> {
                showEthDialog()
            }
            WALLET_TYPE_BTC -> {
                showBtcDialog()
            }
        }
    }

    private fun btnIsClick() {
        binding.btnConfirm.isEnabled = ((binding.edtReceiverAddress.text.trim().toString().isNotEmpty())
                && (binding.edtInputQuantity.text.trim().toString().isNotEmpty()))
    }

    private fun setFingerprint() {
        if (ToolUtils.supportFingerprint(this)) {
            ToolUtils.initKey() //???????????????????????????key
            //????????????Cipher??????
            cipher = ToolUtils.initCipher()
        }
        cipher?.let { showFingerPrintDialog(it) }
    }

    private fun showFingerPrintDialog(cipher: Cipher) {
        val dialogFragment = FingerprintDialogFragment()
        dialogFragment.setCipher(cipher)
        dialogFragment.show(supportFragmentManager, "fingerprint")
        dialogFragment.setOnFingerprintSetting(OnFingerprintSetting { isSucceed ->
            if (isSucceed) {
                ToastUtils.showToastFree(this, getString(R.string.fingerprint_success))
                transfer()
            } else {
                ToastUtils.showToastFree(this, getString(R.string.fingerprint_failed))
            }
        })
    }

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
        currencyUnit = SharedPreferencesUtils.getSecurityString(this, CURRENCY, USD)
        if (SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
            binding.tvWalletAddress.text = walletSelleted.address
            binding.tvWalletName.text = walletSelleted.walletName
        }

        when (currencyUnit) {
            CNY -> currencySymbol = "???"
            HKD -> currencySymbol = "HK$"
            USD -> currencySymbol = "$"
        }

        when (walletSelleted.walletType) {  //?????????|1:ETH|2:TRX|3:BTC
            1 -> {
                initEthMinerFee()
            }
            3 -> {
                initBtcMiner()
            }
        }

        binding.edtReceiverAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //??????????????????????????????
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //??????????????????????????????
            }
        })
        binding.edtInputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //??????????????????????????????
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //??????????????????????????????
            }
        })

        when (walletSelleted.walletType) {  //?????????|1:ETH|2:TRX|3:BTC
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
            4 -> {
                binding.clMinerFee.visibility = View.GONE
                binding.edtReceiverAddress.hint = "SOL" + getString(R.string.address)
            }
        }

        binding.tvTransferTitle.text = tokenBean.title + " " + getString(R.string.transfer)
        binding.tvQuantityBalance.text = getString(R.string.transfer_balance) +
                " " + DecimalFormatUtil.format(tokenBean.balance, 8) + " " + transferUnit
    }

    @SuppressLint("CheckResult")
    private fun initBtcMiner() {
        mApiService.getBtcMinerConfig(
            BtcMinerConfig.Req().toApiRequest(getBtcMinerConfigUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        btcFeeFast = data.fastestFee
                        btcFeeSlow = data.generalFee

                        //??????
                        btcFee = btcFeeFast
                        if (!btcFee.isNullOrEmpty()) {
                            setBtcMinerFee()
                        }
                    }
                }
            }, {
            }
        )
    }

    @SuppressLint("CheckResult")
    private fun initEthMinerFee() {
        mApiService.getEthMinerConfig(
            EthMinerConfig.Req(tokenBean.title).toApiRequest(getEthMinerConfigUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data ->
                        gasLimit = data.gasLimit
                        fastGasPrice = data.fastGasPrice
                        slowGasPrice = data.slowGasPrice

                        //??????
                        gasPrice = fastGasPrice
                        setEthMinerFee()
                    }
                }
            }, {
            }
        )
    }

    fun isTrxAddressActivate(isAddressActivate: Boolean) {
        postUI {
            if (!isAddressActivate) {
                binding.tvNoActivateTips.visibility = View.VISIBLE
                if (tokenBean.isToken) {
                    binding.tvNoActivateTips.setText(R.string.trx_address_no_activate_token)
                } else {
                    binding.tvNoActivateTips.setText(R.string.trx_address_no_activate)
                }
            } else {
                binding.tvNoActivateTips.visibility = View.GONE
            }
        }
    }

    private fun setEthMinerFee() {
        gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit)).divide(BigDecimal("1000000000"))
        binding.tvMinerFeeValue.text = "${DecimalFormatUtil.format(gas, 8)} ETH"
        binding.tvMinerFeeValueConvert.text = "???${currencyUnit} ${currencySymbol}${
            DecimalFormatUtil.format(BigDecimal(tokenBean.unitPrice).multiply(gas), 8)
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
        binding.tvMinerFeeValueConvert.text = "???${currencyUnit} ${currencySymbol}${
            DecimalFormatUtil.format(
                BigDecimal(tokenBean.unitPrice).multiply(
                    BigDecimal(btcFee).multiply(BigDecimal("72"))
                        .divide(BigDecimal("1000000000"))
                ), 8
            )
        }"
        binding.tvTips.visibility = View.INVISIBLE
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
                if (isFinger) {//??????????????????
                    setFingerprint()
                } else {
                    passwordConfirmationDialog()
                }
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
            tvTransferTile.text = tokenBean.title + getString(R.string.transfer)
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
                    after: Int,
                ) {
                    //??????????????????????????????
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //??????????????????????????????
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
        showLoadingDialog()
        val amount = binding.edtInputQuantity.text.trim().toString()
        when (walletSelleted.walletType) {  //?????????|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE
            1 -> {
                if (tokenBean.isToken) {
                    transferETHToken(
                        walletSelleted.address,
                        transferReceiverAddress,
                        tokenBean.contractAddress,
                        walletSelleted.privateKey,
                        BigDecimal(amount).multiply(BigDecimal("10").pow(6)).toBigInteger(),
                        BigInteger(gasPrice).multiply(BigInteger("10").pow(9)), //GWEI ??? WEI
                        BigInteger(gasLimit),
                        object :ITransferListener{
                            override fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
                                transferSuccess(transactionHash, utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }
                        }
                    )
                } else {
                    transferETH(
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        amount,
                        BigInteger(gasPrice).multiply(BigInteger("10").pow(9)), //GWEI ??? WEI
                        BigInteger(gasLimit),
                        binding.edtInputTransferRemarks.text.trim().toString(),
                        object :ITransferListener{
                            override fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
                                transferSuccess(transactionHash, utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }
                        }
                    )
                }
            }
            2 -> {
                if (tokenBean.isToken) {
                    transferTRXToken(
                        walletSelleted.address,
                        transferReceiverAddress,
                        tokenBean.contractAddress,
                        walletSelleted.privateKey,
                        BigDecimal(amount).multiply(BigDecimal("10").pow(6)).toBigInteger(),
                        binding.edtInputTransferRemarks.text.trim().toString(),
                        object :ITransferListener{
                            override fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
                                transferSuccess(transactionHash, utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }
                        }
                    )
                } else {
                    transferTRX(
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        BigDecimal(amount).multiply(BigDecimal("10").pow(6)),
                        binding.edtInputTransferRemarks.text.trim().toString(),
                        object :ITransferListener{
                            override fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
                                transferSuccess(transactionHash, utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }
                        }
                    )
                }
            }
            3 -> {
                if (!tokenBean.isToken) {
                    transferBTC(
                        walletSelleted.address,
                        transferReceiverAddress,
                        walletSelleted.privateKey,
                        BigDecimal(amount).multiply(BigDecimal("10").pow(8)),
                        btcFee,object: ITransferListener{
                            override fun onTransferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
                                transferSuccess(transactionHash,utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }

                        }

                    )
                }
            }
            4 -> {
                if (tokenBean.isToken) {
                    val data = HashMap<String, String>()
                    data["toPublicKey"] = transferReceiverAddress!!
                    data["mintAuthority"] = tokenBean.contractAddress
                    data["endpoint"] = "https://api.mainnet-beta.solana.com"
                    data["amount"] = BigDecimal(amount).multiply(BigDecimal("10").pow(6)).toString()
                    data["secretKey"] = walletSelleted.privateKey
                    bridge.call("solanaTokenTransfer", data) { map ->
                        val json = JSONObject(map)
                        val transactionHash = json.optString("tx")
                        if (transactionHash.isNotEmpty()) {
                            postUI {
                                transactionFinishConfirmDialog(transactionHash)
                            }
                            putTransAddress(transactionHash, null)
                        } else {
                            postUI {
                                ToastUtils.showToastFree(
                                    this,
                                    getString(R.string.transaction_failed)
                                )
                            }
                        }
                    }
                } else {
                    transferSOL(
                        walletSelleted.address,
                        transferReceiverAddress!!,
                        walletSelleted.privateKey,
                        BigDecimal(amount).multiply(BigDecimal("10").pow(9)),
                        object :ITransferListener{
                            override fun onTransferSuccess(
                                transactionHash: String,
                                utxos: MutableList<UTXO>?,
                            ) {
                                transferSuccess(transactionHash, utxos)
                            }

                            override fun onTransferFail(errorMessage: String) {
                                transferFail(errorMessage)
                            }
                        }
                    )
                }
            }
        }
    }

    fun transferSuccess(transactionHash: String, utxos: MutableList<UTXO>?) {
        postUI {
            if (transactionHash.isNotEmpty()) {
                transactionFinishConfirmDialog(transactionHash)
                putTransAddress(transactionHash, utxos)
            } else {
                ToastUtils.showToastFree(this, getString(R.string.transaction_failed))
            }
            dismissLoadingDialog()
        }

    }

    var putAddressTimes = 0

    /***
     *????????????????????????
     */
    @SuppressLint("CheckResult")
    private fun putTransAddress(hash: String, utxos: MutableList<UTXO>?) {
        var inputs: ArrayList<ReportTransferInfo.InRecord>? = arrayListOf()//BTC????????????
        var outputs: ArrayList<ReportTransferInfo.InRecord>? = arrayListOf()//BTC????????????
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
                    BigDecimal(
                        btcFee
                    ).multiply(BigDecimal("72")).divide(BigDecimal("10").pow(9)), 8
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
                if (it.code() != 1) {
                    if (putAddressTimes < 3) {
                        putTransAddress(hash, utxos)
                        putAddressTimes++
                    }
                }
            }, {
            }
        )
    }

    fun transferFail(errorMessage: String) {
        postUI {
            dismissLoadingDialog()
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
                finish()
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
        //????????????
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //???????????????????????????????????????????????????
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog?????????
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun dialogTheme(window: Window) {
        //????????????
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //???????????????????????????????????????????????????
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog?????????
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
            val params = window.attributes //????????????
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog?????????
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_transfer_gas_price_fast).text =
                "($fastGasPrice GWEI)"
            window.findViewById<TextView>(R.id.tv_transfer_gas_price_slow).text =
                "($slowGasPrice GWEI)"

            window.findViewById<TextView>(R.id.miner_fee_gas_price).text = fastGasPrice
            window.findViewById<TextView>(R.id.miner_fee_limit_title).text = "21000"

            gas = BigDecimal(gasPrice).multiply(BigDecimal(gasLimit))
                .divide(BigDecimal("10").pow(9))
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(fastGasPrice).multiply(BigDecimal(gasLimit))
                            .divide(BigDecimal("10").pow(9)), 8
                    )
                } ETH"
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(slowGasPrice).multiply(BigDecimal(gasLimit))
                            .divide(BigDecimal("10").pow(9)), 8
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
                if (tokenBean.isToken) {
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
            || (BigDecimal(gasLimit) > BigDecimal("10").pow(5))
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
            val params = window.attributes //????????????
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog?????????
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
                            .divide(BigDecimal("10").pow(9)), 8
                    )
                } BTC"
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                "${
                    DecimalFormatUtil.format(
                        BigDecimal(btcFeeSlow).multiply(BigDecimal("72"))
                            .divide(BigDecimal("10").pow(9)), 8
                    )
                } BTC"

            onClickTransferFast(window)
            onClickTransferSlow(window)
            onClickTransferCustom(window)

            inputWatch(window.findViewById(R.id.miner_fee_gas_price), 250, 1)

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