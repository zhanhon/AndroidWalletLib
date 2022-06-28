package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.blockchain.ethereum.MnemonicUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.blockchain.ethereum.utils.ChineseSimplified
import com.ramble.ramblewallet.blockchain.ethereum.utils.ChineseTraditional
import com.ramble.ramblewallet.blockchain.ethereum.utils.English
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils
import com.ramble.ramblewallet.blockchain.solana.WalletSOLUtils.Companion.isSolValidAddress
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding
import com.ramble.ramblewallet.fragment.MainBTCFragment
import com.ramble.ramblewallet.fragment.MainETHFragment
import com.ramble.ramblewallet.fragment.MainSOLFragment
import com.ramble.ramblewallet.fragment.MainTRXFragment
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.utils.StringUtils.*

class RecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE|100:BTC、ETH、TRX、SOL、DOGE
    private var chooseMode = 0 //选择方式|1:助记词|2:私钥|3:keystore
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var mnemonic: ArrayList<String> = arrayListOf()
    private var walletSelleted: Wallet? = null
    private var putAddressTimes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_wallet)
        initClick()
        initData()
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
        binding.edtWalletName.addTextChangedListener(object : TextWatcher {
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
        binding.edtWalletPassword.addTextChangedListener(object : TextWatcher {
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
        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.edtContributingWords.addTextChangedListener(object : TextWatcher {
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

    private fun btnIsClick() {
        binding.btnConfirm.isEnabled = ((binding.edtWalletName.text.trim().toString().isNotEmpty())
                && (binding.edtWalletPassword.text.trim().toString().isNotEmpty())
                && (binding.edtPasswordConfirm.text.trim().toString().isNotEmpty())
                && (binding.edtContributingWords.text.trim().toString().isNotEmpty())
                && (binding.edtWalletPassword.text.trim().toString().length >= 6)
                && (binding.edtWalletPassword.text.trim().toString()
                == binding.edtPasswordConfirm.text.trim().toString()))
    }

    private fun initData() {
        walletType = intent.getIntExtra(ARG_PARAM1, 1)
        chooseMode = intent.getIntExtra(ARG_PARAM2, 0)
        if (SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
                object : TypeToken<Wallet>() {}.type
            )
        }
        when (chooseMode) {
            1 -> {
                binding.tvRecoverTitle.text = getString(R.string.import_mnemonic_words)
                binding.tvContributingWords.text = getString(R.string.contributing_words)
                binding.edtContributingWords.hint = getString(R.string.input_mnemonic_words)
            }
            2 -> {
                binding.tvRecoverTitle.text = getString(R.string.import_secret_key)
                binding.tvContributingWords.text = getString(R.string.secret_key)
                binding.edtContributingWords.hint = getString(R.string.input_secret_key)
            }
            3 -> {
                binding.tvRecoverTitle.text = getString(R.string.import_keystore)
                binding.tvContributingWords.text = "Keystore"
                binding.edtContributingWords.hint = getString(R.string.input_keystore)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.btn_confirm -> {
                btnConfirmValid()
            }
        }
    }

    private fun btnConfirmValid() {
        if (binding.edtWalletName.text.isEmpty()) {
            ToastUtils.showToastFree(this, getString(R.string.please_input_wallet_name))
            return
        }
        if (binding.edtWalletPassword.text.isEmpty()) {
            ToastUtils.showToastFree(this, getString(R.string.please_input_password))
            return
        }
        if (binding.edtWalletPassword.text.length < 6) {
            ToastUtils.showToastFree(this, getString(R.string.password_less_six_tips))
            return
        }
        if (binding.edtWalletPassword.text.trim()
                .toString() != binding.edtPasswordConfirm.text.trim().toString()
        ) {
            ToastUtils.showToastFree(this, getString(R.string.different_password))
            return
        }
        if (SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
        when (walletType) {
            1 -> { //以太坊
                ethValidHandle()
            }
            2 -> { //波场
                trxValidHandle()
            }
            3 -> { //比特币
                btcValidHandle()
            }
            4 -> { //索拉纳币
                solValidHandle()
            }
        }
    }

    private fun solValidHandle() {
        when (chooseMode) {
            1 -> {
                if (validMnemonic()) return
                recoverWalletSOL(1)
            }
            2 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_secret_key)
                    )
                    return
                }
                if (binding.edtContributingWords.text.length < 32) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_secret_key)
                    )
                    return
                }
                recoverWalletSOL(2)
            }
        }
    }

    private fun btcValidHandle() {
        when (chooseMode) {
            1 -> {
                if (validMnemonic()) return
                recoverWalletBTC(1)
            }
            2 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_secret_key)
                    )
                    return
                }
                if (binding.edtContributingWords.text.length < 32) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_secret_key)
                    )
                    return
                }
                recoverWalletBTC(2)
            }
        }
    }

    private fun trxValidHandle() {
        when (chooseMode) {
            1 -> {
                if (validMnemonic()) return
                recoverWalletTRX(1)
            }
            2 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_secret_key)
                    )
                    return
                }
                if (binding.edtContributingWords.text.length < 32) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_secret_key)
                    )
                    return
                }
                recoverWalletTRX(2)
            }
            3 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_keystore)
                    )
                    return
                }
                recoverWalletTRX(3)
            }
        }
    }

    private fun ethValidHandle() {
        when (chooseMode) {
            1 -> {
                if (validMnemonic()) return
                recoverWalletETH(1)
            }
            2 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_secret_key)
                    )
                    return
                }
                if (binding.edtContributingWords.text.length < 32) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_secret_key)
                    )
                    return
                }
                recoverWalletETH(2)
            }
            3 -> {
                if (binding.edtContributingWords.text.isEmpty()) {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_keystore)
                    )
                    return
                }
                recoverWalletETH(3)
            }
            4 -> {
            }
        }
    }

    private fun validMnemonic(): Boolean {
        if (binding.edtContributingWords.text.isEmpty()) {
            ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
            return true
        }
        var str = binding.edtContributingWords.text.toString().replace(" ", "")
        if (isHasLowerChar(str) || isChinese(str)) {
            if (!binding.edtContributingWords.text.trim().toString().contains(" ")) {
                ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
                return true
            }
            mnemonic =
                binding.edtContributingWords.text.trim().toString().split(" ") as ArrayList<String>
            if (mnemonic.size != 12) {
                ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
                return true
            }
            mnemonic.forEach {
                if (isHasLowerChar(str) && !English.words.contains(it)) {
                    ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
                    return true
                }
                if (isChinese(str) && !ChineseSimplified.words.contains(it)
                    && !ChineseTraditional.words.contains(it)
                ) {
                    ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
                    return true
                }
            }
        } else {
            ToastUtils.showToastFree(this, getString(R.string.input_mnemonic_words))
            return true
        }
        return false
    }

    private var mnemonicList: ArrayList<String> = arrayListOf()
    private var mnemonicList1: ArrayList<String> = arrayListOf()
    private var mnemonicList2: ArrayList<String> = arrayListOf()
    private var mnemonicOther: String = ""
    private var mnemonicIndexList: ArrayList<Int> = arrayListOf()
    private fun recoverWalletETH(chooseMode: Int) {
        lateinit var wallet: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                wallet = WalletETHUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                wallet = WalletETHUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
            3 -> {
                wallet = WalletETHUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (isEthValidAddress(wallet.address)) {
            var needUpdateBean: Wallet? = null
            saveWalletList.forEach {
                if (it.address == wallet.address){//已存在重置密码
                    needUpdateBean = it
                }
            }
            if (needUpdateBean != null){
                needUpdateBean!!.walletPassword = binding.edtWalletPassword.text.trim().toString()
                ToastUtils.showToastFree(this,getString(R.string.reset_success))
            }else{
                saveWalletList.add(wallet)
                putAddress(wallet, 1)
            }
            SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(wallet))
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainETHFragment::class.toString())
            finish()
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_mnemonic_words)
                    )
                }
                2 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_secret_key))
                }
                3 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_keystore))
                }
            }
        }
    }

    private fun dictionaryMnemonic() {
        val mmemoic = binding.edtContributingWords.text.toString()
        mnemonicList1 = mmemoic.split(" ") as ArrayList<String>
        if (isHasLowerChar(mmemoic)) {
            mnemonicList.add(mmemoic)
            mnemonicList1.forEach {
                English.words.forEachIndexed { index, element ->
                    if (it == element) {
                        mnemonicIndexList.add(index)
                    }
                }
            }
            mnemonicIndexList.forEach {
                ChineseSimplified.words.forEachIndexed { index, element ->
                    if (it == index) {
                        mnemonicList2.add(element)
                    }
                }
            }
            mnemonicList2.forEach {
                mnemonicOther = "$mnemonicOther $it"
            }
            if (mnemonicOther != "") {
                mnemonicList.add(mnemonicOther.trim())
            } else {
                mnemonicList.add(MnemonicUtils.generateMnemonicEnglishChinese()[1])
            }
        }
        if (isContainChinese(mmemoic)) {
            mnemonicList1.forEach {
                ChineseSimplified.words.forEachIndexed { index, element ->
                    if (it == element) {
                        mnemonicIndexList.add(index)
                    }
                }
            }
            mnemonicIndexList.forEach {
                English.words.forEachIndexed { index, element ->
                    if (it == index) {
                        mnemonicList2.add(element)
                    }
                }
            }
            mnemonicList2.forEach {
                mnemonicOther = "$mnemonicOther $it"
            }
            if (mnemonicOther != "") {
                mnemonicList.add(mnemonicOther.trim())
            } else {
                mnemonicList.add(MnemonicUtils.generateMnemonicEnglishChinese()[0])
            }
            mnemonicList.add(mmemoic)
        }
    }

    private fun recoverWalletTRX(chooseMode: Int) {
        lateinit var wallet: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                wallet = WalletTRXUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                wallet = WalletTRXUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
            3 -> {
                wallet = WalletTRXUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (isTrxValidAddress(wallet.address)) {
            var needUpdateBean: Wallet? = null
            saveWalletList.forEach {
                if (it.address == wallet.address){//已存在重置密码
                    needUpdateBean = it
                }
            }
            if (needUpdateBean != null){
                if (needUpdateBean!!.isIdWallet){
                    //  身份钱包
                    ToastUtils.showToastFree(this,getString(R.string.reset_password_is_id))
                }else{
                    needUpdateBean!!.walletPassword = binding.edtWalletPassword.text.trim().toString()
                    ToastUtils.showToastFree(this,getString(R.string.reset_success))
                }
            }else{
                saveWalletList.add(wallet)
                putAddress(wallet, 2)
            }
            SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(wallet))
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainTRXFragment::class.toString())
            finish()
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_mnemonic_words)
                    )
                }
                2 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_secret_key))
                }
                3 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_keystore))
                }
            }
        }
    }

    private fun recoverWalletBTC(chooseMode: Int) {
        lateinit var wallet: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                wallet = WalletBTCUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                wallet = WalletBTCUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (saveWalletList.isEmpty()) {
            wallet.index = 0
        } else {
            wallet.index = saveWalletList[0].index + 1
        }
        if (isBtcValidAddress(wallet.address)) {
            var needUpdateBean: Wallet? = null
            saveWalletList.forEach {
                if (it.address == wallet.address){//已存在重置密码
                    needUpdateBean = it
                }
            }
            if (needUpdateBean != null){
                needUpdateBean!!.walletPassword = binding.edtWalletPassword.text.trim().toString()
                ToastUtils.showToastFree(this,getString(R.string.reset_success))
            }else{
                saveWalletList.add(wallet)
                putAddress(wallet, WALLET_TYPE_BTC)
            }
            SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))

            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(wallet))
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainBTCFragment::class.toString())
            finish()
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_mnemonic_words)
                    )
                }
                2 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_secret_key))
                }
            }
        }
    }

    private fun recoverWalletSOL(chooseMode: Int) {
        lateinit var walletSOL: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                walletSOL = WalletSOLUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                walletSOL = WalletSOLUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (isSolValidAddress(walletSOL.address)) {
            var needUpdateBean: Wallet? = null
            saveWalletList.forEach {
                if (it.address == walletSOL.address){//已存在重置密码
                    needUpdateBean = it
                }
            }
            if (needUpdateBean != null){
                needUpdateBean!!.walletPassword = binding.edtWalletPassword.text.trim().toString()
                ToastUtils.showToastFree(this,getString(R.string.reset_success))
            }else{
                saveWalletList.add(walletSOL)
                putAddress(walletSOL, 4)
            }
            SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
            SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(walletSOL))
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,MainSOLFragment::class.toString())
            finish()
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_mnemonic_words)
                    )
                }
                2 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_secret_key))
                }
            }
        }
    }

    private fun putAddress(wallet: Wallet, walletType: Int) {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(
            AddressReport.DetailsList(wallet.address, 0, walletType)
        ) //链类型|1:ETH|2:TRX|3:ETC
        val languageCode = SharedPreferencesUtils.getSecurityString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getSecurityString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() != 1) {
                    if (putAddressTimes < 3) {
                        putAddress(wallet, walletType)
                        putAddressTimes++
                    }
                }
            }, {
            }
        )
    }

}