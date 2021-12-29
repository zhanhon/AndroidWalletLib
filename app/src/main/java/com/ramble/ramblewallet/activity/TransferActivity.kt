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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.EthMinerConfig
import com.ramble.ramblewallet.databinding.ActivityTransferBinding
import com.ramble.ramblewallet.eth.TransferEthUtils
import com.ramble.ramblewallet.network.getEthMinerConfigUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.applyIo


class TransferActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTransferBinding
    private var transferTitle: String = "ETH"
    private var transferAddress: String = "0x23flwrf2232wafrwerqwr2rlsf23r23"
    private var transferName: String = "小猫的钱包"
    private var transferBalance: String = "26.232323"
    private var transferUnit: String = "USDT"
    private var transferGwei: String = "161"
    private var transferGas: String = "21000"
    private var transferGweiDefaultConvert: String = "0.13"
    private var transferGweiFast: String = "16"
    private var transferGasFast: String = "2000"
    private var transferSpeedFast: String = "0.75"
    private var transferGweiSlow: String = "161"
    private var transferGasSlow: String = "21000"
    private var transferSpeedSlow: String = "0.90"
    private var isCustom = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)

        setOnClickListener()

    }

    private fun initData() {
        mApiService.getEthMinerConfig(
            EthMinerConfig.Req(transferTitle).toApiRequest(getEthMinerConfigUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->ETH:${data.currencyType}") }
                } else {
                    println("-=-=-=->ETH:${it.message()}")
                }
            }, {
                println("-=-=-=->ETH:${it.printStackTrace()}")
            }
        )

        binding.tvTransferTitle.text = transferTitle + " " + getString(R.string.transfer)
        binding.tvWalletAddress.text = transferAddress
        binding.tvWalletName.text = transferName
        binding.tvQuantityBalance.text =
            getString(R.string.transfer_balance) + " " + transferBalance + " " + transferUnit
        binding.tvMinerFeeValue.text =
            "${transferGwei.toDouble() * transferGas.toDouble()} $transferUnit"
        binding.tvMinerFeeValueConvert.text = "≈USD$$transferGweiDefaultConvert"
        binding.tvTips.text = "$transferGwei GWEI * GAS ($transferGas)"

    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivTransferScan.setOnClickListener(this)
        binding.ivAddressBook.setOnClickListener(this)
        binding.clMinerFee.setOnClickListener(this)
        binding.tvSelectAll.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun showDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_miner_fee)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params = window.attributes //设置属性
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            params.dimAmount = 0.5f   //dialog背景层
            window.attributes = params

            window.findViewById<TextView>(R.id.tv_transfer_gwei_fast).text =
                "($transferGweiFast GWEI)"
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast).text =
                "$transferGasFast $transferUnit"
            window.findViewById<TextView>(R.id.tv_transfer_speed_fast).text =
                "<${transferSpeedFast}min"
            window.findViewById<TextView>(R.id.tv_transfer_gwei_slow).text =
                "($transferGweiSlow GWEI)"
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow).text =
                "$transferGasSlow $transferUnit"
            window.findViewById<TextView>(R.id.tv_transfer_speed_slow).text =
                "<${transferSpeedSlow}min"

            onClickTransferFast(window)
            onClickTransferSlow(window)
            onClickTransferCustom(window)

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (isCustom) {
                    transferGwei =
                        window.findViewById<EditText>(R.id.miner_fee_gas_price).text.toString()
                    transferGas =
                        window.findViewById<EditText>(R.id.miner_fee_limit_title).text.toString()
                }
                binding.tvMinerFeeValue.text =
                    "${transferGwei.toDouble() * transferGas.toDouble()} $transferUnit"
                binding.tvMinerFeeValueConvert.text = "≈USD$$transferGweiDefaultConvert"
                binding.tvTips.text = "$transferGwei GWEI * GAS ($transferGas)"
                dialog.dismiss()
            }
        }
    }

    private fun onClickTransferCustom(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_custom).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.VISIBLE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)

            window.findViewById<TextView>(R.id.tv_transfer_fast)
                .setTextColor(resources.getColor(R.color.color_000000))
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_speed_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))

            window.findViewById<TextView>(R.id.tv_transfer_slow)
                .setTextColor(resources.getColor(R.color.color_000000))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_speed_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            isCustom = true
        }
    }

    private fun onClickTransferSlow(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_slow).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.GONE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_green_bottom_btn)

            window.findViewById<TextView>(R.id.tv_transfer_fast)
                .setTextColor(resources.getColor(R.color.color_000000))
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_speed_fast)
                .setTextColor(resources.getColor(R.color.color_9598AA))

            window.findViewById<TextView>(R.id.tv_transfer_slow)
                .setTextColor(resources.getColor(R.color.color_3BB7A5))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_3BB7A5))
            window.findViewById<TextView>(R.id.tv_transfer_speed_slow)
                .setTextColor(resources.getColor(R.color.color_FFFFFF))

            transferGwei = transferGweiSlow
            transferGas = transferGasSlow
            isCustom = false
        }
    }

    private fun onClickTransferFast(window: Window) {
        window.findViewById<LinearLayout>(R.id.ll_transfer_fast).setOnClickListener {
            window.findViewById<LinearLayout>(R.id.ll_gwei_gas_input).visibility = View.GONE
            window.findViewById<LinearLayout>(R.id.ll_transfer_fast)
                .setBackgroundResource(R.drawable.shape_fix_green_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_slow)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)
            window.findViewById<LinearLayout>(R.id.ll_transfer_custom)
                .setBackgroundResource(R.drawable.shape_fix_gray_line_btn)

            window.findViewById<RelativeLayout>(R.id.rl_transfer_fast_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_green_bottom_btn)
            window.findViewById<RelativeLayout>(R.id.rl_transfer_slow_bottom)
                .setBackgroundResource(R.drawable.shape_half_radius_gray_bottom_btn)

            window.findViewById<TextView>(R.id.tv_transfer_fast)
                .setTextColor(resources.getColor(R.color.color_3BB7A5))
            window.findViewById<TextView>(R.id.tv_transfer_gas_fast)
                .setTextColor(resources.getColor(R.color.color_3BB7A5))
            window.findViewById<TextView>(R.id.tv_transfer_speed_fast)
                .setTextColor(resources.getColor(R.color.color_FFFFFF))

            window.findViewById<TextView>(R.id.tv_transfer_slow)
                .setTextColor(resources.getColor(R.color.color_000000))
            window.findViewById<TextView>(R.id.tv_transfer_gas_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))
            window.findViewById<TextView>(R.id.tv_transfer_speed_slow)
                .setTextColor(resources.getColor(R.color.color_9598AA))

            transferGwei = transferGweiFast
            transferGas = transferGasFast
            isCustom = false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_transfer_scan -> {
                startActivity(Intent(this, ScanActivity::class.java))
            }
            R.id.iv_address_book -> {
                startActivity(Intent(this, AddressBookActivity::class.java))
            }
            R.id.cl_miner_fee -> {
                showDialog()
            }
            R.id.tv_select_all -> {
                binding.edtInputQuantity.setText(transferBalance)
            }
            R.id.btn_confirm -> {
                TransferEthUtils.transferTest()
                //doTransaction()
            }
        }
    }


}