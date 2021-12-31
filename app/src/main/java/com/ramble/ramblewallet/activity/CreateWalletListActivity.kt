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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityCreateWalletListBinding


class CreateWalletListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateWalletListBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_wallet_list)

        initClick()
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.rlEth.setOnClickListener(this)
        binding.rlTrx.setOnClickListener(this)
        binding.rlBtc.setOnClickListener(this)
    }

    private fun showDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_create_wallet_list)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes //设置属性
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog背景层
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_title).text = title

            window.findViewById<RelativeLayout>(R.id.rl_create_wallet).setOnClickListener {
                startActivity(Intent(this, CreateWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 1)
                })
                dialog.dismiss()
            }

            window.findViewById<RelativeLayout>(R.id.rl_contributing_words).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 1)
                })
                dialog.dismiss()
            }

            window.findViewById<RelativeLayout>(R.id.rl_secret_key).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 2)
                })
                dialog.dismiss()
            }

            window.findViewById<RelativeLayout>(R.id.rl_keystore).setOnClickListener {
                startActivity(Intent(this, RecoverWalletActivity::class.java).apply {
                    putExtra(ARG_PARAM1, 3)
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
                showDialog(binding.tvEthCurrencyName.text.toString())
            }
            R.id.rl_trx -> {
                showDialog(binding.tvTrxCurrencyName.text.toString())
            }
            R.id.rl_btc -> {
                showDialog(binding.tvBtcCurrencyName.text.toString())
            }
        }
    }
}