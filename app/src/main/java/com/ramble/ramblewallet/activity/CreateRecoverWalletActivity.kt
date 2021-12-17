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
import com.ramble.ramblewallet.constant.CN
import com.ramble.ramblewallet.constant.EN
import com.ramble.ramblewallet.constant.LANGUAGE
import com.ramble.ramblewallet.constant.TW
import com.ramble.ramblewallet.databinding.ActivityCreateRecoverWalletBinding
import com.ramble.ramblewallet.utils.LanguageSetting
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

class CreateRecoverWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateRecoverWalletBinding
    private lateinit var dialogLanguage: AlertDialog

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_recover_wallet)

        setLanguage()
        binding.tvMore.setTextColor(Color.parseColor("#000000"))
        val dra = resources.getDrawable(R.mipmap.ic_arrow_down)
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        dra.setBounds(0, 0, dra.minimumWidth, dra.minimumHeight)
        binding.tvMore.setCompoundDrawables(null, null, dra, null) //设置顶部图标
        dialogLanguage = AlertDialog.Builder(this).create()
        binding.tvMore.setOnClickListener { v: View? ->
            if (dialogLanguage != null) {
                if (dialogLanguage.isShowing) {
                    dialogLanguage.dismiss()
                } else {
                    safetyDialog()
                }
            } else {
                safetyDialog()
            }
        }

        binding.btnRecoverWallet.setOnClickListener {
            startActivity(Intent(this, RecoverWalletActivity::class.java))
        }

        binding.btnCreateWallet.setOnClickListener {
            startActivity(Intent(this, CreateWalletActivity::class.java))
        }

    }

    private fun setLanguage() {
        when (SharedPreferencesUtils.getString(this, LANGUAGE, CN)) {
            CN -> {
                binding.tvMore.text = getString(R.string.language_simplified_chinese)
                LanguageSetting.setLanguage(applicationContext, 1)
            }
            TW -> {
                binding.tvMore.text = getString(R.string.language_traditional_chinese)
                LanguageSetting.setLanguage(applicationContext, 2)
            }
            EN -> {
                binding.tvMore.text = getString(R.string.language_english)
                LanguageSetting.setLanguage(applicationContext, 3)
            }
        }
    }

    private fun safetyDialog() {
        dialogLanguage = AlertDialog.Builder(this).create()
        dialogLanguage.show()
        val window: Window? = dialogLanguage.window
        if (window != null) {
            window.setContentView(R.layout.language_layout)
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