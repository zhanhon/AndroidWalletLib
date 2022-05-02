package com.ramble.ramblewallet.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY_ALL
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY_COMMON
import com.ramble.ramblewallet.databinding.ActivityFingerPrintBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils
import com.ramble.ramblewallet.utils.ToolUtils
import com.ramble.ramblewallet.utils.showCommonDialog

/**
 * 时间　: 2022/4/18 10:26
 * 作者　: potato
 * 描述　: 指纹交易
 */
class FingerPrintActivity : BaseActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivityFingerPrintBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finger_print)
        initView()
        initListener()
    }

    private fun initView() {
        binding.tvMineTitle.text = getString(R.string.fingerprint_transaction)
        binding.toggleAll.isChecked =
            SharedPreferencesUtils.getSecurityBoolean(this, ISFINGERPRINT_KEY_ALL, false)
        binding.toggleCommon.isChecked =
            SharedPreferencesUtils.getSecurityBoolean(this, ISFINGERPRINT_KEY_COMMON, false)
        binding.toggleTrans.isChecked =
            SharedPreferencesUtils.getSecurityBoolean(this, ISFINGERPRINT_KEY, false)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.toggleAll.setOnCheckedChangeListener(this)
        binding.toggleCommon.setOnCheckedChangeListener(this)
        binding.toggleTrans.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.toggle_all -> {
                if (isChecked) {
                    confirmTipsDialog(buttonView, ISFINGERPRINT_KEY_ALL)
                } else {
                    SharedPreferencesUtils.saveSecurityBoolean(this, ISFINGERPRINT_KEY_ALL, isChecked)
                }
            }
            R.id.toggle_common -> {
                if (isChecked) {
                    confirmTipsDialog(buttonView, ISFINGERPRINT_KEY_COMMON)
                } else {
                    SharedPreferencesUtils.saveSecurityBoolean(this, ISFINGERPRINT_KEY_COMMON, isChecked)
                }
            }
            R.id.toggle_trans -> {
                if (isChecked) {
                    confirmTipsDialog(buttonView, ISFINGERPRINT_KEY)
                } else {
                    SharedPreferencesUtils.saveSecurityBoolean(this, ISFINGERPRINT_KEY, isChecked)
                }
            }
        }
    }

    private fun confirmTipsDialog(buttonView: CompoundButton, key: String) {
        showCommonDialog(this, getString(R.string.fingerprint_toggle_text), confirmListener = {
            SharedPreferencesUtils.saveSecurityBoolean(this, key, true)
            ToolUtils.supportFingerprint(this)
        }, tvcListener = {
            buttonView.isChecked = false
        }, btcListener = {
            buttonView.isChecked = false
        })
    }
}