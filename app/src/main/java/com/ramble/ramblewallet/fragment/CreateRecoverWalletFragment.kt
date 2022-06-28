package com.ramble.ramblewallet.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.CreateRecoverWalletActivity
import com.ramble.ramblewallet.activity.CreateWalletActivity
import com.ramble.ramblewallet.activity.RecoverWalletListActivity
import com.ramble.ramblewallet.base.BaseFragment
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.FragmentCreateRecoverWalletBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.utils.*

class CreateRecoverWalletFragment : BaseFragment() {
    private lateinit var binding: FragmentCreateRecoverWalletBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (reusedView == null) {
            binding = inflater.dataBinding(R.layout.fragment_create_recover_wallet, container)
            reusedView = binding.root
        }
        return reusedView
    }

    override fun actualLazyLoad() {
        super.actualLazyLoad()
        setLanguage()
        setOnClickListener()
    }

    fun setOnClickListener() {
        binding.tvMore.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        binding.btnRecoverWallet.setOnClickListener(this)
        binding.btnCreateWallet.setOnClickListener(this)
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getSecurityString(context, LANGUAGE, CN)) {
            CN -> {
                binding.tvMore.text = getString(R.string.language_simplified_chinese)
                LanguageSetting.setLanguage(context, 1)
            }
            TW -> {
                binding.tvMore.text = getString(R.string.language_traditional_chinese)
                LanguageSetting.setLanguage(context, 2)
            }
            EN -> {
                binding.tvMore.text = getString(R.string.language_english)
                LanguageSetting.setLanguage(context, 3)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_more, R.id.iv_more -> {
                safetyDialog()
            }
            R.id.btn_recover_wallet -> {
                startActivity(Intent(context, RecoverWalletListActivity::class.java))
            }
            R.id.btn_create_wallet -> {
                startActivity(Intent(context, CreateWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 100)
                    putExtra(ARG_PARAM2, 1)
                })
            }
        }
    }

    private fun safetyDialog() {
        showLanguageDialog(requireActivity(), cnListener = {
            SharedPreferencesUtils.saveSecurityString(context, LANGUAGE, CN)
            setLanguage()
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,CreateRecoverWalletFragment::class.toString())
        }, twListener = {
            SharedPreferencesUtils.saveSecurityString(context, LANGUAGE, TW)
            setLanguage()
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,CreateRecoverWalletFragment::class.toString())
        }, enListener = {
            SharedPreferencesUtils.saveSecurityString(context, LANGUAGE, EN)
            setLanguage()
            RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE,CreateRecoverWalletFragment::class.toString())
        })
    }


}