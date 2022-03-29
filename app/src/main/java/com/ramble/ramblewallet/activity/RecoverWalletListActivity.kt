package com.ramble.ramblewallet.activity

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityRecoverWalletListBinding

class RecoverWalletListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRecoverWalletListBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recover_wallet_list)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener(this)
        binding.rlEth.setOnClickListener(this)
        binding.rlTrx.setOnClickListener(this)
        binding.rlBtc.setOnClickListener(this)
    }

    private fun showDialogList(walletType: Int) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_recover_wallet_list)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes //设置属性
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog背景层
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_title).text = getString(R.string.import_wallet)

            when (walletType) {
                1, 2 -> {
                    window.findViewById<LinearLayout>(R.id.rl_keystore).visibility = View.VISIBLE
                }
                3 -> {
                    window.findViewById<LinearLayout>(R.id.rl_keystore).visibility = View.GONE
                }
            }

            window.findViewById<LinearLayout>(R.id.rl_contributing_words).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, walletType)
                    putExtra(ARG_PARAM2, 1)
                })
                dialog.dismiss()
            }

            window.findViewById<LinearLayout>(R.id.rl_secret_key).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, walletType)
                    putExtra(ARG_PARAM2, 2)
                })
                dialog.dismiss()
            }

            window.findViewById<LinearLayout>(R.id.rl_keystore).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, walletType)
                    putExtra(ARG_PARAM2, 3)
                })
                dialog.dismiss()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.rl_eth -> {
                showDialogList(1)
            }
            R.id.rl_trx -> {
                showDialogList(2)
            }
            R.id.rl_btc -> {
                showDialogList(3)
            }
        }
    }
}