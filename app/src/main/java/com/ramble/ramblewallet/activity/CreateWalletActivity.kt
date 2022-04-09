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
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityCreateWalletBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.ToastUtils

class CreateWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateWalletBinding
    private var walletType = 1 //链类型|1:ETH|2:TRX|3:BTC|4:SOL|5:DOGE|100:BTC、ETH、TRX、SOL、DOGE
    private var walletSource = 1
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
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
        if (SharedPreferencesUtils.getString(this, WALLETINFO, "").isNotEmpty()) {
            saveWalletList =
                Gson().fromJson(
                    SharedPreferencesUtils.getString(this, WALLETINFO, ""),
                    object : TypeToken<ArrayList<Wallet>>() {}.type
                )
        }
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
                //暂时不需要实现此方法
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //暂时不需要实现此方法
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnIsClick() {
        if ((binding.edtWalletPassword.text.isNotEmpty())
            && (binding.edtPasswordConfirm.text.isNotEmpty())
            && (binding.edtWalletPassword.text.length >= 6)
            && (binding.edtWalletPassword.text.trim()
                .toString() == binding.edtPasswordConfirm.text.trim().toString())
        ) {
            binding.btnConfirm.isEnabled = true
            binding.btnConfirm.background = getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
            binding.btnConfirm.isEnabled = false
            binding.btnConfirm.background = getDrawable(R.drawable.shape_gray_bottom_btn)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
                    putExtra(ARG_PARAM1, binding.edtWalletName.text.toString())
                    putExtra(ARG_PARAM2, binding.edtWalletPassword.text.toString())
                    putExtra(ARG_PARAM3, walletType)
                    putExtra(ARG_PARAM4, false)
                })
            }
        }
    }
}