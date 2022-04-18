package com.ramble.ramblewallet.activity

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView

import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY_ALL
import com.ramble.ramblewallet.constant.ISFINGERPRINT_KEY_COMMON
import com.ramble.ramblewallet.databinding.ActivityFingerPrintBinding
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

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
            SharedPreferencesUtils.getBoolean(this, ISFINGERPRINT_KEY_ALL, false)
        binding.toggleCommon.isChecked =
            SharedPreferencesUtils.getBoolean(this, ISFINGERPRINT_KEY_COMMON, false)
        binding.toggleTrans.isChecked =
            SharedPreferencesUtils.getBoolean(this, ISFINGERPRINT_KEY, false)
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
                    confirmTipsDialog(buttonView,ISFINGERPRINT_KEY_ALL)
                } else {
                    SharedPreferencesUtils.saveBoolean(this, ISFINGERPRINT_KEY_ALL, isChecked)
                }
            }
            R.id.toggle_common ->{
                if (isChecked) {
                    confirmTipsDialog(buttonView,ISFINGERPRINT_KEY_COMMON)
                } else {
                    SharedPreferencesUtils.saveBoolean(this, ISFINGERPRINT_KEY_COMMON, isChecked)
                }
            }
            R.id.toggle_trans -> {
                if (isChecked) {
                    confirmTipsDialog(buttonView,ISFINGERPRINT_KEY)
                } else {
                    SharedPreferencesUtils.saveBoolean(this, ISFINGERPRINT_KEY, isChecked)
                }
            }
        }
    }

    private fun confirmTipsDialog(buttonView: CompoundButton,key: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_delete_confirm_tips)
            dialogCenterTheme(window)
            window.findViewById<TextView>(R.id.tv_content).text =
                getString(R.string.fingerprint_toggle_text)
            window.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
                buttonView.isChecked=false
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
                buttonView.isChecked=false
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                SharedPreferencesUtils.saveBoolean(this, key, true)
                dialog.dismiss()
            }
        }
    }

    private fun dialogCenterTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}