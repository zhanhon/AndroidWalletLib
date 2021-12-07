package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding

class RecoverWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityRecoverWalletBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_wallet)

        binding.imvBack.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}