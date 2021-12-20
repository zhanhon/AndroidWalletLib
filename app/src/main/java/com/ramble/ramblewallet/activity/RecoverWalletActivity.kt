package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletBinding

class RecoverWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityRecoverWalletBinding
    private var chooseMode = 0

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

        initData()
    }

    private fun initData() {
        chooseMode = intent.getIntExtra(ARG_PARAM1, 0)
        when (chooseMode) {
            1 -> {
                binding.tvTitle.text = getString(R.string.import_mnemonic_words)
                binding.tvContributingWords.text = getString(R.string.contributing_words)
                binding.edtContributingWords.hint = getString(R.string.input_mnemonic_words)
            }
            2 -> {
                binding.tvTitle.text = getString(R.string.import_secret_key)
                binding.tvContributingWords.text = getString(R.string.secret_key)
                binding.edtContributingWords.hint = getString(R.string.input_secret_key)
            }
            3 -> {
                binding.tvTitle.text = getString(R.string.import_keystore)
                binding.tvContributingWords.text = "Keystore"
                binding.edtContributingWords.hint = getString(R.string.input_keystore)
            }
        }
    }
}