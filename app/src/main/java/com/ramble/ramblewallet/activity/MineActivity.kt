package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityMineBinding

class MineActivity : BaseActivity() {

    private lateinit var binding: ActivityMineBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mine)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvMineTitle.text = getString(R.string.personal_management)
        binding.incManageWallet.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.manage_wallet)
        binding.incAddressBook.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.address_book)
        binding.incTransactionQuery.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.transaction_query)
        binding.incMultiLanguage.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.multi_language)
        binding.incCurrencyUnit.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.currency_unit)
        binding.incHelpFeedback.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.help_feedback)
        binding.incServiceAgreement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.service_agreement)
        binding.incPrivacyStatement.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.privacy_statement)
        binding.incAboutUs.findViewById<TextView>(R.id.tv_mine_title).text =
            getString(R.string.about_us)


        binding.incManageWallet.setOnClickListener {
            startActivity(Intent(this, ManageWalletActivity::class.java))
        }
        binding.incAddressBook.setOnClickListener {
            startActivity(Intent(this, AddressBookActivity::class.java))
        }
    }
}