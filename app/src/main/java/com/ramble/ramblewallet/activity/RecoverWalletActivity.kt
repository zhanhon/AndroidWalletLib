package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding
import com.ramble.ramblewallet.ethereum.WalleETHManager
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.applyIo
import com.ramble.ramblewallet.utils.toastDefault

class RecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var chooseMode = 0
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private var isErrorContributingWords = true
    private var isErrorSecretKey = true
    private var isErrorKeystore = true

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
        chooseMode = intent.getIntExtra(ARG_PARAM1, 0)
        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
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
                when (chooseMode) {
                    1 -> {
                        if (binding.edtContributingWords.text.isEmpty()) {
                            toastDefault(getString(R.string.input_mnemonic_words))
                            return
                        }
//                        saveWalletList.forEach {
//                            if (it.mnemonic == binding.edtContributingWords.text.toString().trim()) {
//                                isErrorContributingWords = false
//                            }
//                        }
//                        if (isErrorContributingWords) {
//                            Toast.makeText(this, getString(R.string.input_correct_mnemonic_words), Toast.LENGTH_SHORT).show()
//                            return
//                        }
                        //1、助记词生成keystore
                        var walletETHKeyStore: WalletETH =
                            WalleETHManager.generateWalletKeystore(binding.edtWalletPassword.text.trim().toString(),
                                binding.edtContributingWords.text.toString())
                        walletETHKeyStore.walletName = binding.edtWalletName.text.trim().toString()
                        walletETHKeyStore.walletPassword = binding.edtWalletPassword.text.trim().toString()
                        walletETHKeyStore.walletType = 1 //链类型|0:BTC|1:ETH|2:TRX
                        println("-=-=-=->wallestETHAddress:${walletETHKeyStore.address}")
                        println("-=-=-=->walletETHMnemonic:${walletETHKeyStore.mnemonic}")
                        println("-=-=-=->walletETHPrivateKey:${walletETHKeyStore.privateKey}")
                        println("-=-=-=->walletETHKeystore:${walletETHKeyStore.keystore}")

                        putAddress(walletETHKeyStore)

                        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
                            saveWalletList =
                                Gson().fromJson(
                                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                                    object : TypeToken<ArrayList<WalletETH>>() {}.type
                                )
                        }
                        saveWalletList.add(walletETHKeyStore)
                        println("-=-=-=->walletJson:${Gson().toJson(saveWalletList)}")
                        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))


                        //2、之后地址校验
                        var isValidSuccess = WalleETHManager.isETHValidAddress(walletETHKeyStore.address)
                        if (isValidSuccess) {
                            println("-=-=-=->isValidSuccess:$isValidSuccess")
                            startActivity(Intent(this, MainETHActivity::class.java))
                        } else {
                            Toast.makeText(this, getString(R.string.input_correct_mnemonic_words), Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    2 -> {
                        if (binding.edtContributingWords.text.isEmpty()) {
                            toastDefault(getString(R.string.input_secret_key))
                            return
                        }
//                        saveWalletList.forEach {
//                            if (it.privateKey == binding.edtContributingWords.text.toString().trim()) {
//                                isErrorSecretKey = false
//                            }
//                        }
//                        if (isErrorSecretKey) {
//                            Toast.makeText(this, getString(R.string.input_correct_secret_key), Toast.LENGTH_SHORT).show()
//                            return
//                        }
                    }
                    3 -> {
                        if (binding.edtContributingWords.text.isEmpty()) {
                            toastDefault(getString(R.string.input_keystore))
                            return
                        }
//                        saveWalletList.forEach {
//                            if (it.keystore == binding.edtContributingWords.text.toString().trim()) {
//                                isErrorKeystore = false
//                            }
//                        }
//                        if (isErrorKeystore) {
//                            Toast.makeText(this, getString(R.string.input_correct_keystore), Toast.LENGTH_SHORT).show()
//                            return
//                        }
                    }
                }
            }
        }
    }

    private fun putAddress(walletETHKeyStore: WalletETH) {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        detailsList.add(AddressReport.DetailsList(walletETHKeyStore.address, 1)) //链类型|0:ETC|1:ETH|2:TRON
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    putAddress(walletETHKeyStore)
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }
}