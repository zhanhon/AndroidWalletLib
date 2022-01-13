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
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.ethereum.WalletETHUtils
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.tron.WalletTRXUtils
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.toastDefault

class RecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var walletType = 0 //链类型|0:BTC|1:ETH|2:TRX
    private var chooseMode = 0 //选择方式|1:助记词|2:私钥|3:keystore
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()


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
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.edtWalletPassword.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.edtPasswordConfirm.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.edtContributingWords.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterTextChanged(s: Editable?) {
                btnIsClick()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnIsClick() {
        if ((binding.edtWalletName.text.isNotEmpty())
            && (binding.edtWalletPassword.text.isNotEmpty())
            && (binding.edtPasswordConfirm.text.isNotEmpty())
            && (binding.edtContributingWords.text.isNotEmpty())
            && (binding.edtWalletPassword.text.length >= 6)
            && (binding.edtWalletPassword.text.trim() == binding.edtPasswordConfirm.text.trim())
        ) {
            binding.btnConfirm.background = getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnConfirm.background = getDrawable(R.drawable.shape_gray_bottom_btn)
        }
    }

    private fun initData() {
        walletType = intent.getIntExtra(ARG_PARAM1, 0)
        chooseMode = intent.getIntExtra(ARG_PARAM2, 0)
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
                    toastDefault(getString(R.string.please_input_wallet_name))
                    return
                }
                if (binding.edtWalletPassword.text.isEmpty()) {
                    toastDefault(getString(R.string.please_input_password))
                    return
                }
                if (binding.edtWalletPassword.text.length < 6) {
                    toastDefault(getString(R.string.password_less_six_tips))
                    return
                }
                if (binding.edtWalletPassword.text.trim() != binding.edtPasswordConfirm.text.trim()) {
                    toastDefault(getString(R.string.different_password))
                    return
                }
                when (walletType) {
                    1 -> { //以太坊
                        when (chooseMode) {
                            1 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_mnemonic_words))
                                    return
                                }
                                recoverWalletETH(1)
                            }
                            2 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_secret_key))
                                    return
                                }
                                recoverWalletETH(2)
                            }
                            3 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_keystore))
                                    return
                                }
                                recoverWalletETH(3)
                            }
                        }
                    }
                    2 -> { //波场
                        when (chooseMode) {
                            1 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_mnemonic_words))
                                    return
                                }
                                recoverWalletTRX(1)
                            }
                            2 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_secret_key))
                                    return
                                }
                                recoverWalletTRX(2)
                            }
                            3 -> {
                                if (binding.edtContributingWords.text.isEmpty()) {
                                    toastDefault(getString(R.string.input_keystore))
                                    return
                                }
                                recoverWalletTRX(3)
                            }
                        }
                    }
                    0 -> { //比特币

                    }
                }
            }
        }
    }

    private fun recoverWalletETH(chooseMode: Int) {
        lateinit var walletETH: WalletETH
        when (chooseMode) {
            1 -> {
                walletETH = WalletETHUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
            2 -> {
                walletETH = WalletETHUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
            3 -> {
                walletETH = WalletETHUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
        }
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<WalletETH>>() {}.type
                )
        }
        saveWalletList.add(walletETH)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        putAddress(walletETH, 1)
        if (isEthValidAddress(walletETH.address)) {
            startActivity(Intent(this, MainETHActivity::class.java))
        } else {
            toastDefault(getString(R.string.input_correct_mnemonic_words))
        }
    }

    private fun recoverWalletTRX(chooseMode: Int) {
        lateinit var walletTRX: WalletETH
        when (chooseMode) {
            1 -> {
                walletTRX = WalletTRXUtils.generateWalletByMnemonic(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
            2 -> {
                walletTRX = WalletTRXUtils.generateWalletByPrivateKey(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
            3 -> {
                walletTRX = WalletTRXUtils.generateWalletByKeyStore(
                    binding.edtWalletName.text.trim().toString(),
                    binding.edtWalletPassword.text.trim().toString(),
                    binding.edtContributingWords.text.toString()
                )
            }
        }
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<WalletETH>>() {}.type
                )
        }
        saveWalletList.add(walletTRX)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        putAddress(walletTRX, 2)
        startActivity(Intent(this, MainETHActivity::class.java))
        if (isTrxValidAddress(walletTRX.address)) {
            startActivity(Intent(this, MainETHActivity::class.java))
        } else {
            toastDefault(getString(R.string.input_correct_mnemonic_words))
        }
    }

    private fun putAddress(wallet: WalletETH, walletType: Int) {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        when (walletType) {
            1 -> {
                detailsList.add(
                    AddressReport.DetailsList(
                        wallet.address,
                        1
                    )
                ) //链类型|0:ETC|1:ETH|2:TRON
            }
            2 -> {
                detailsList.add(
                    AddressReport.DetailsList(
                        wallet.address,
                        2
                    )
                ) //链类型|0:ETC|1:ETH|2:TRON
            }
        }
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    putAddress(wallet, walletType)
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }

}