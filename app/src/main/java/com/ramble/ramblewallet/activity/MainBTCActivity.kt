package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.ActivityMainBtcBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.utils.Pie
import com.ramble.ramblewallet.utils.RxBus

class MainBTCActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBtcBinding
    private var myDataBeans: ArrayList<MainETHTokenBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_078DC2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_btc)

        binding.scroll01.post { binding.scroll01.fullScroll(ScrollView.FOCUS_UP) } //初始值
        findViewById<RelativeLayout>(R.id.toolbar).setBackgroundResource(0)
        binding.scroll01.setOnScrollChangeListener { view, x, y, oldx, oldy ->
            if (y > 64) {
                binding.txtTitle.visibility = View.INVISIBLE
                binding.linearBtns.visibility = View.INVISIBLE
                binding.relBtns.visibility = View.VISIBLE
                binding.card01.visibility = View.VISIBLE
                binding.toolbar.background = getDrawable(R.mipmap.ic_home_bg_eth_small)
            } else {
                binding.txtTitle.visibility = View.VISIBLE
                binding.linearBtns.visibility = View.VISIBLE
                binding.relBtns.visibility = View.INVISIBLE
                binding.card01.visibility = View.GONE
                binding.toolbar.setBackgroundResource(0)
            }
        }

//        myDataBeans.add(MyDataBean(1, "TFT", ""))
//        myDataBeans.add(MyDataBean(2, "WBTC", ""))
//        myDataBeans.add(MyDataBean(3, "DAI", ""))
//        myDataBeans.add(MyDataBean(4, "USDC", ""))
//        myDataBeans.add(MyDataBean(5, "USDT", ""))
//        myDataBeans.add(MyDataBean(6, "LINK", ""))
//        myDataBeans.add(MyDataBean(7, "YFI", ""))
//        myDataBeans.add(MyDataBean(8, "UNI", ""))
        mainAdapter = MainAdapter(myDataBeans)
        binding.rvCurrency.adapter = mainAdapter

        mainAdapter.setOnItemClickListener { adapter, view, position ->
            //showTransferGatheringDialog()
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

        binding.ivTokenManageClick01.setOnClickListener(this)

    }

//    private fun showTransferGatheringDialog() {
//        var dialog = AlertDialog.Builder(this).create()
//        dialog.show()
//        val window: Window? = dialog.window
//        if (window != null) {
//            window.setContentView(R.layout.dialog_transfer_gathering)
//            window.setGravity(Gravity.CENTER)
//            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            //设置属性
//            val params = window.attributes
//            params.width = WindowManager.LayoutParams.MATCH_PARENT
//            //弹出一个窗口，让背后的窗口变暗一点
//            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//            //dialog背景层
//            params.dimAmount = 0.5f
//            window.attributes = params
//
//            val btnTransfer = window.findViewById<Button>(R.id.btn_transfer)
//            val btnGathering = window.findViewById<Button>(R.id.btn_gathering)
//
//            btnTransfer.setOnClickListener { v1: View? ->
//                startActivity(Intent(this, TransferActivity::class.java))
//                dialog.dismiss()
//            }
//            btnGathering.setOnClickListener { v1: View? ->
//                startActivity(Intent(this, GatheringActivity::class.java))
//                dialog.dismiss()
//            }
//        }
//    }

    override fun onRxBus(event: RxBus.Event) {
        super.onRxBus(event)
        when (event.id()) {
            Pie.EVENT_ADDRESS_TRANS_SCAN -> {
                start(TransferActivity::class.java, Bundle().also {
                    it.putString(ARG_PARAM1, event.data())
                })
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnMenu -> {
                startActivity(Intent(this, WalletManageActivity::class.java))
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
                start(ScanActivity::class.java, Bundle().also {
                    it.putInt(ARG_PARAM1, 3)
                })
            }
        }
    }

}