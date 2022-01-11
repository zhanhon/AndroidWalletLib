package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityCreateWalletBinding
import com.ramble.ramblewallet.utils.toastDefault

class CreateWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateWalletBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_wallet)
        initClick()
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
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun btnIsClick() {
        if ((binding.edtWalletName.text.isNotEmpty())
            && (binding.edtWalletPassword.text.isNotEmpty())
            && (binding.edtPasswordConfirm.text.isNotEmpty())
            && (binding.edtWalletPassword.text.length >= 6)
            && (binding.edtWalletPassword.text.trim() == binding.edtPasswordConfirm.text.trim())
        ) {
            binding.btnConfirm.background = getDrawable(R.drawable.shape_green_bottom_btn)
        } else {
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
                startActivity(Intent(this, ContributingWordsActivity::class.java).apply {
                    putExtra(ARG_PARAM1, binding.edtWalletName.text.toString())
                    putExtra(ARG_PARAM2, binding.edtWalletPassword.text.toString())
                })
            }
        }
    }
}