package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding
import com.ramble.ramblewallet.eth.Wallet
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

class RecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var chooseMode = 0
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private var isErrorContributingWords = true
    private var isErrorSecretKey = true
    private var isErrorKeystore = true

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_wallet)
        setOnClickListener()
        initData()
    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
    }

    private fun initData() {
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
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
                var walletName = binding.edtWalletName.text.toString().trim()
                var walletPassword = binding.edtWalletPassword.text.toString().trim()
                var walletPasswordConfirm = binding.edtPasswordConfirm.text.toString().trim()
                var walletContributingWords = binding.edtContributingWords.text.toString().trim()

                if (walletPassword != walletPasswordConfirm) {
                    Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show()
                    return
                }
                when (chooseMode) {
                    1 -> {
                        saveWalletList.forEach {
                            if (it.mnemonic == walletContributingWords) {
                                isErrorContributingWords = false
                            }
                        }
                        if (isErrorContributingWords) {
                            Toast.makeText(this, "请输入正确的助记词", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    2 -> {
                        saveWalletList.forEach {
                            if (it.privateKey == walletContributingWords) {
                                isErrorSecretKey = false
                            }
                        }
                        if (isErrorSecretKey) {
                            Toast.makeText(this, "请输入正确的密钥", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    3 -> {
                        saveWalletList.forEach {
                            if (it.keystore == walletContributingWords) {
                                isErrorKeystore = false
                            }
                        }
                        if (isErrorKeystore) {
                            Toast.makeText(this, "请输入正确正确的keystore", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                }
                startActivity(Intent(this, MainETHActivity::class.java))
            }
        }
    }
}