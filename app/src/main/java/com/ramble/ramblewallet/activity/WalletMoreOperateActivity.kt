package com.ramble.ramblewallet.activity

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import com.ramble.ramblewallet.utils.toastDefault

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
                inputPasswordDialog(getString(R.string.edit_wallet))
            }
            R.id.rl_secret_key_backups -> {
                inputPasswordDialog(getString(R.string.secret_key_backup))
            }
            R.id.rl_keystore_backups -> {
                inputPasswordDialog(getString(R.string.keystore_backup))
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

    private fun inputPasswordDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_input_password)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            val edtWalletPassword = window.findViewById<TextView>(R.id.edt_wallet_password)
            edtWalletPassword.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletPassword.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (edtWalletPassword.text.trim().toString() == walletCurrent.walletPassword) {
                    when (title) {
                        getString(R.string.edit_wallet) -> {
                            editWalletDialog(title)
                        }
                        getString(R.string.secret_key_backup) -> {
                            secretKeyDialog(title)
                        }
                        getString(R.string.keystore_backup) -> {
                            keystoreDialog(title)
                        }
                    }
                    dialog.dismiss()
                } else {
                    toastDefault(getString(R.string.input_correct_wallet_password))
                }
            }
        }
    }

    private fun editWalletDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_edit_wallet)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
            }

        }
    }

    private fun secretKeyDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_secret_key)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            window.findViewById<EditText>(R.id.edt_secret_key)
                .setText(walletCurrent.privateKey.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.privateKey.toString())
            }
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun keystoreDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_keystore)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            window.findViewById<EditText>(R.id.edt_keystore)
                .setText(walletCurrent.keystore.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.keystore.toString())
            }
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_generate_qr_code).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_generate_qr_code).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
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