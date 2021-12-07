package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityCreateRecoverWalletBinding

class CreateRecoverWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateRecoverWalletBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_recover_wallet)

        binding.btnRecoverWallet.setOnClickListener {
            startActivity(Intent(this, RecoverWalletActivity::class.java))
        }

        binding.btnCreateWallet.setOnClickListener {
            startActivity(Intent(this, CreateWalletActivity::class.java))
        }

    }
}