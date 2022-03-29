package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityCreateRecoverWalletBinding
import com.ramble.ramblewallet.utils.LanguageSetting
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

class CreateRecoverWalletActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateRecoverWalletBinding
    private lateinit var dialogLanguage: AlertDialog

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_recover_wallet)
        setLanguage()
        setOnClickListener()
    }

    fun setOnClickListener() {
        binding.tvMore.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        binding.btnRecoverWallet.setOnClickListener(this)
        binding.btnCreateWallet.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_more, R.id.iv_more -> {
                safetyDialog()
            }
            R.id.btn_recover_wallet -> {
                startActivity(Intent(this, RecoverWalletListActivity::class.java))
            }
            R.id.btn_create_wallet -> {
                startActivity(Intent(this, CreateWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 4)
                    putExtra(ARG_PARAM2, 1)
                })
            }
        }
    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                binding.tvMore.text = getString(R.string.language_simplified_chinese)
                LanguageSetting.setLanguage(this, 1)
            }
            TW -> {
                binding.tvMore.text = getString(R.string.language_traditional_chinese)
                LanguageSetting.setLanguage(this, 2)
            }
            EN -> {
                binding.tvMore.text = getString(R.string.language_english)
                LanguageSetting.setLanguage(this, 3)
            }
        }
    }

    private fun safetyDialog() {
        dialogLanguage = AlertDialog.Builder(this).create()
        dialogLanguage.show()
        val window: Window? = dialogLanguage.window
        if (window != null) {
            window.setContentView(R.layout.dialog_language)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.findViewById<View>(R.id.tv_language1).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, CN)
                setLanguage()
                dialogLanguage.dismiss()

                val it = Intent(this, CreateRecoverWalletActivity::class.java)
                //清空任务栈确保当前打开activit为前台任务栈栈顶
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
            window.findViewById<View>(R.id.tv_language2).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, TW)
                setLanguage()
                dialogLanguage.dismiss()

                val it = Intent(this, CreateRecoverWalletActivity::class.java)
                //清空任务栈确保当前打开activit为前台任务栈栈顶
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
            window.findViewById<View>(R.id.tv_language3).setOnClickListener { v1: View? ->
                SharedPreferencesUtils.saveString(this, LANGUAGE, EN)
                setLanguage()
                dialogLanguage.dismiss()

                val it = Intent(this, CreateRecoverWalletActivity::class.java)
                //清空任务栈确保当前打开activit为前台任务栈栈顶
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }

            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params
            //点击空白处不关闭dialog
            dialogLanguage.show()
        }
    }

}