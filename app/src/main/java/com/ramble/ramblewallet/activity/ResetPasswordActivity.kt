package com.ramble.ramblewallet.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.databinding.ActivityResetPasswordBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.ToastUtils

class ResetPasswordActivity : BaseActivity(),View.OnClickListener{
    private lateinit var binding: ActivityResetPasswordBinding
    val idWalletList = arrayListOf<Wallet>()
    var saveWalletList = arrayListOf<Wallet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset_password)
        initView()
        initData()

    }

    private fun initData() {
        binding.laTitleAll.ivBack.setOnClickListener(this)
        binding.laTitleAll.tvAllTitle.text = getString(R.string.reset_id_wallet)
        binding.btnConfirm.setOnClickListener(this)
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )

        saveWalletList.forEach {
            if (it.isIdWallet) {
                idWalletList.add(it)
            }
        }




    }

    private fun initView() {
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
        binding.btnConfirm.isEnabled = (
                (binding.edtWalletPassword.text.isNotEmpty())
                && (binding.edtWalletPassword.text.length >= 6)
                && (binding.edtPasswordConfirm.text.isNotEmpty())
                && (binding.edtPasswordConfirm.text.length >= 6)
                )
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.iv_back -> {
                finish()
            }

            R.id.btn_confirm -> {
                val mnemonic = binding.etMnemonic.text.toString()
                val password = binding.edtWalletPassword.text.trim().toString()
                if (mnemonic.isEmpty()){
                    ToastUtils.showToastFree(this,getString(R.string.reset_password_et_mnemonic_tip))
                    return
                }
                if (password != binding.edtPasswordConfirm.text.trim().toString()) {
                    ToastUtils.showToastFree(this, getString(R.string.different_password))
                    return
                }
                idWalletList.forEach {idWallet ->
                    if (mnemonic != idWallet.mnemonicList[0] && mnemonic != idWallet.mnemonicList[1]){
                        ToastUtils.showToastFree(this,getString(R.string.reset_password_mnemonic_error))
                        return
                    }
                    saveWalletList.forEach{
                        if (idWallet.address == it.address){
                            it.walletPassword = password
                        }
                    }
                }
                SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
                ToastUtils.showToastFree(this,getString(R.string.reset_success))
                finish()
            }

        }
    }


}