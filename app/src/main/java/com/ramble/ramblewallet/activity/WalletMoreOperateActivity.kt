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
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.databinding.ActivityWalletMoreOperateBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.SharedPreferencesUtils

class WalletMoreOperateActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalletMoreOperateBinding
    private lateinit var walletCurrent: WalletETH
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_more_operate)
        walletCurrent = Gson().fromJson(
            intent.getStringExtra(ARG_PARAM1),
            object : TypeToken<WalletETH>() {}.type
        )
        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )

        initClick()
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.rlEditWallet.setOnClickListener(this)
        binding.rlContributingWordsBackups.setOnClickListener(this)
        binding.rlSecretKeyBackups.setOnClickListener(this)
        binding.rlKeystoreBackups.setOnClickListener(this)
        binding.tvDeleteWallet.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.rl_edit_wallet -> {
                editWalletDialog()
            }
            R.id.rl_contributing_words_backups -> {
                contributingWordsDialog()
            }
            R.id.rl_secret_key_backups -> {
                secretKeyDialog()
            }
            R.id.rl_keystore_backups -> {
                keystoreDialog()
            }
            R.id.tv_delete_wallet -> {
                val list = saveWalletList.iterator()
                list.forEach {
                    if (it.address == walletCurrent.address) {
                        list.remove()
                    }
                }
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                finish()
            }
        }
    }

    private fun editWalletDialog() {
        var dialogEditWallet = AlertDialog.Builder(this).create()
        dialogEditWallet.show()
        val window: Window? = dialogEditWallet.window
        if (window != null) {
            window.setContentView(R.layout.dialog_edit_wallet)
            dialogTheme(window)
        }
    }

    private fun contributingWordsDialog() {
        var dialogContributingWords = AlertDialog.Builder(this).create()
        dialogContributingWords.show()
        val window: Window? = dialogContributingWords.window
        if (window != null) {
            window.setContentView(R.layout.dialog_contributing_words)
            dialogTheme(window)
        }
    }

    private fun secretKeyDialog() {
        var dialogcSecretKey = AlertDialog.Builder(this).create()
        dialogcSecretKey.show()
        val window: Window? = dialogcSecretKey.window
        if (window != null) {
            window.setContentView(R.layout.dialog_secret_key)
            dialogTheme(window)

            window.findViewById<EditText>(R.id.edt_secret_key).setText(walletCurrent.privateKey.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.privateKey.toString())
            }
        }
    }

    private fun keystoreDialog() {
        var dialogKeystore = AlertDialog.Builder(this).create()
        dialogKeystore.show()
        val window: Window? = dialogKeystore.window
        if (window != null) {
            window.setContentView(R.layout.dialog_keystore)
            dialogTheme(window)

            window.findViewById<EditText>(R.id.edt_keystore).setText(walletCurrent.keystore.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener{
                ClipboardUtils.copy(walletCurrent.keystore.toString())
            }
        }
    }

    private fun dialogTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}