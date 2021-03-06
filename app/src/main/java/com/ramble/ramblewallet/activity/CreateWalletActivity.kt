package com.ramble.ramblewallet.activity

import android.content.Intent
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
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.databinding.ActivityCreateWalletBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.ToastUtils

class CreateWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateWalletBinding
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE|100:BTC、ETH、TRX、SOL、DOGE
    private var walletSource = 1
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_wallet)
        walletType = intent.getIntExtra(ARG_PARAM1, 1)
        walletSource = intent.getIntExtra(ARG_PARAM2, 1)
        initClick()
        when (walletSource) {
            1 -> {
                binding.tvCreateWalletTitle.text = getString(R.string.create_wallet)
            }
            2 -> {
                binding.tvCreateWalletTitle.text = getString(R.string.new_wallet)
            }
        }
        if (SharedPreferencesUtils.getSecurityString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
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
                //暂时不需要实现此方法
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //暂时不需要实现此方法
            }
        })
    }

    private fun btnIsClick() {
        binding.btnConfirm.isEnabled = ((binding.edtWalletPassword.text.isNotEmpty())
                && (binding.edtPasswordConfirm.text.isNotEmpty())
                && (binding.edtWalletPassword.text.length >= 6)
                && (binding.edtWalletPassword.text.trim()
            .toString() == binding.edtPasswordConfirm.text.trim().toString()))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.btn_confirm -> {
                if (saveWalletList.size > 0) {
                    saveWalletList.forEach {
                        if (it.walletName == binding.edtWalletName.text.toString()) {
                            ToastUtils.showToastFree(this, getString(R.string.repeat_wallet_tips))
                            return
                        }
                    }
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
                startActivity(Intent(this, MnemonicActivity::class.java).apply {
                    putExtra(
                        ARG_PARAM1, Wallet(
                            binding.edtWalletName.text.trim().toString(),
                            binding.edtWalletPassword.text.toString(),
                            "", "", "", "", walletType, null
                        )
                    )
                })
            }
        }
    }
}