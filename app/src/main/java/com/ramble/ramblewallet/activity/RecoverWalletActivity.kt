package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding
import com.ramble.ramblewallet.ethereum.MnemonicUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.ethereum.utils.ChineseSimplified
import com.ramble.ramblewallet.ethereum.utils.ChineseTraditional
import com.ramble.ramblewallet.ethereum.utils.English
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.StringUtils.*
import com.ramble.ramblewallet.utils.ToastUtils
import com.ramble.ramblewallet.utils.applyIo

class RecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4：BTC、ETH、TRX
    private var chooseMode = 0 //选择方式|1:助记词|2:私钥|3:keystore
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var mnemonic: ArrayList<String> = arrayListOf()
    private var walletSelleted: Wallet? = null
    private var times = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_wallet)
        initClick()
        initData()
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
        binding.edtWalletName.addTextChangedListener(object : TextWatcher {
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
        binding.edtWalletPassword.addTextChangedListener(object : TextWatcher {
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
        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("-=-=->${s}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("-=-=->${s}")
            }
        })
        binding.edtContributingWords.addTextChangedListener(object : TextWatcher {
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnIsClick() {
        if ((binding.edtWalletName.text.trim().toString().isNotEmpty())
            && (binding.edtWalletPassword.text.trim().toString().isNotEmpty())
            && (binding.edtPasswordConfirm.text.trim().toString().isNotEmpty())
            && (binding.edtContributingWords.text.trim().toString().isNotEmpty())
            && (binding.edtWalletPassword.text.trim().toString().length >= 6)
            && (binding.edtWalletPassword.text.trim().toString()
                    == binding.edtPasswordConfirm.text.trim().toString())
        ) {
            binding.btnConfirm.isEnabled = true
            binding.btnConfirm.background = getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnConfirm.isEnabled = false
            binding.btnConfirm.background = getDrawable(R.drawable.shape_gray_bottom_btn)
        }
    }

    private fun initData() {
        walletType = intent.getIntExtra(ARG_PARAM1, 1)
        chooseMode = intent.getIntExtra(ARG_PARAM2, 0)
        if (SharedPreferencesUtils.getString(this, WALLETSELECTED, "").isNotEmpty()) {
            walletSelleted = Gson().fromJson(
                SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
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

                when (walletType) {
                    1 -> { //以太坊
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
                        }
                    }
                    2 -> { //波场
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
                    3 -> { //比特币
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
                }
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
        lateinit var walletETH: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                walletETH = WalletETHUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                walletETH = WalletETHUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
            3 -> {
                walletETH = WalletETHUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
        saveWalletList.add(walletETH)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        if (walletETH.address.isNotEmpty()) {
            if (isEthValidAddress(walletETH.address)) {
                putAddress(walletETH, 1)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletETH))
                startActivity(Intent(this, MainETHActivity::class.java))
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
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_mnemonic_words))
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
        var mmemoic = binding.edtContributingWords.text.toString()
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
        lateinit var walletTRX: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                walletTRX = WalletTRXUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
            3 -> {
                walletTRX = WalletTRXUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
        saveWalletList.add(walletTRX)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        if (walletTRX.address.isNotEmpty()) {
            if (isTrxValidAddress(walletTRX.address)) {
                putAddress(walletTRX, 2)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletTRX))
                startActivity(Intent(this, MainTRXActivity::class.java))
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
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_mnemonic_words))
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
        lateinit var walletBTC: Wallet
        when (chooseMode) {
            1 -> {
                dictionaryMnemonic()
                walletBTC = WalletBTCUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    mnemonicList[0],
                    mnemonicList
                )
            }
            2 -> {
                walletBTC = WalletBTCUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString(),
                    mnemonicList
                )
            }
        }
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
        saveWalletList.add(walletBTC)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        if (walletBTC.address.isNotEmpty()) {
            if (isBtcValidAddress(walletBTC.address)) {
                putAddress(walletBTC, 3)
                SharedPreferencesUtils.saveString(this, WALLETSELECTED, Gson().toJson(walletBTC))
                startActivity(Intent(this, MainBTCActivity::class.java))
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
        } else {
            when (chooseMode) {
                1 -> {
                    ToastUtils.showToastFree(this, getString(R.string.input_correct_mnemonic_words))
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
            AddressReport.DetailsList(
                wallet.address,
                0,
                walletType
            )
        ) //链类型|1:ETH|2:TRX|3:ETC
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress-recover:${data}") }
                } else {
                    if (times < 3) {
                        putAddress(wallet, walletType)
                        times++
                    }
                    println("-=-=-=->putAddress-recover:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress-recover:${it.printStackTrace()}")
            }
        )
    }

}