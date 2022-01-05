package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityCreateWalletBinding
import com.ramble.ramblewallet.utils.toastDefault

class CreateWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateWalletBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_wallet)

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {

            if (binding.edtWalletName.text.isEmpty()) {
                toastDefault("钱包名不能为空")
                return@setOnClickListener
            }
            if (binding.edtWalletPassword.text.trim() != binding.edtPasswordConfirm.text.trim()) {
                toastDefault("两次密码不一致")
                return@setOnClickListener
            }

            startActivity(Intent(this, ContributingWordsActivity::class.java).apply {
                putExtra(ARG_PARAM1, binding.edtWalletName.text.toString())
                putExtra(ARG_PARAM2, binding.edtWalletPassword.text.toString())
            })
        }
    }
}