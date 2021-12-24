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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_078DC2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.scroll01.post { binding.scroll01.fullScroll(ScrollView.FOCUS_UP) } //初始值
        findViewById<RelativeLayout>(R.id.toolbar).setBackgroundResource(0)
        binding.scroll01.setOnScrollChangeListener { view, x, y, oldx, oldy ->

            findViewById<TextView>(R.id.txtTitle).visibility =
                if (y > 64) View.INVISIBLE else View.VISIBLE
            findViewById<LinearLayout>(R.id.linearBtns).visibility =
                if (y > 64) View.INVISIBLE else View.VISIBLE
            findViewById<LinearLayout>(R.id.relBtns).visibility =
                if (y > 64) View.VISIBLE else View.INVISIBLE
            findViewById<LinearLayout>(R.id.card01).visibility =
                if (y > 64) View.VISIBLE else View.GONE
            if (y > 64) {
                findViewById<RelativeLayout>(R.id.toolbar).background =
                    getDrawable(R.mipmap.ic_home_bg_eth_small)
            } else {
                findViewById<RelativeLayout>(R.id.toolbar).setBackgroundResource(0)
            }


        }

        myDataBeans.add(MyDataBean(1, "TFT", ""))
        myDataBeans.add(MyDataBean(2, "WBTC", ""))
        myDataBeans.add(MyDataBean(3, "DAI", ""))
        myDataBeans.add(MyDataBean(4, "USDC", ""))
        myDataBeans.add(MyDataBean(5, "USDT", ""))
        myDataBeans.add(MyDataBean(6, "LINK", ""))
        myDataBeans.add(MyDataBean(7, "YFI", ""))
        myDataBeans.add(MyDataBean(8, "UNI", ""))
        mainAdapter = MainAdapter(myDataBeans)
        binding.rvCurrency.adapter = mainAdapter

        mainAdapter.setOnItemClickListener { adapter, view, position ->
            showTransferGatheringDialog()
        }

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnMenu.setOnClickListener(this)
        binding.ivNoticeTop.setOnClickListener(this)
        binding.ivMineTop.setOnClickListener(this)

        binding.ivGatheringTop.setOnClickListener(this)
        binding.llGathering.setOnClickListener(this)

        binding.ivTransferTop.setOnClickListener(this)
        binding.llTransfer.setOnClickListener(this)

        binding.ivScanTop.setOnClickListener(this)
        binding.llScan.setOnClickListener(this)

        binding.card00.setOnClickListener(this)
        binding.card01.setOnClickListener(this)
    }

    private fun showTransferGatheringDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_transfer_gathering)
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params

            val btnTransfer = window.findViewById<Button>(R.id.btn_transfer)
            val btnGathering = window.findViewById<Button>(R.id.btn_gathering)

            btnTransfer.setOnClickListener { v1: View? ->
                startActivity(Intent(this, TransferActivity::class.java))
                dialog.dismiss()
            }
            btnGathering.setOnClickListener { v1: View? ->
                startActivity(Intent(this, GatheringActivity::class.java))
                dialog.dismiss()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnMenu -> {
                startActivity(Intent(this, ManageWalletActivity::class.java))
            }
            R.id.iv_notice_top -> {
                startActivity(Intent(this, MessageCenterActivity::class.java))
            }
            R.id.iv_mine_top -> {
                startActivity(Intent(this, MineActivity::class.java))
            }
            R.id.iv_gathering_top, R.id.ll_gathering -> {
                startActivity(Intent(this, GatheringActivity::class.java))
            }
            R.id.iv_transfer_top, R.id.ll_transfer -> {
                startActivity(Intent(this, TransferActivity::class.java))
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java))
            }
            R.id.card00, R.id.card01 -> {
                startActivity(Intent(this, TokenActivity::class.java))
            }
        }
    }

}