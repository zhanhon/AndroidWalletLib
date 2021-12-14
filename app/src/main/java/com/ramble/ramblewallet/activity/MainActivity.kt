package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), View.OnClickListener{

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
        binding.scroll01.setOnScrollChangeListener { view, x, y, oldx, oldy ->
            findViewById<TextView>(R.id.txtTitle).visibility =
                if (y > 64) View.INVISIBLE else View.VISIBLE
            findViewById<LinearLayout>(R.id.linearBtns).visibility =
                if (y > 64) View.INVISIBLE else View.VISIBLE
            findViewById<LinearLayout>(R.id.relBtns).visibility =
                if (y > 64) View.VISIBLE else View.INVISIBLE
            findViewById<RelativeLayout>(R.id.card01).visibility =
                if (y > 64) View.VISIBLE else View.GONE
        }

        for (i in 0..100) {
            myDataBeans.add(
                MyDataBean(
                    i + 1,
                    "ricky-$i",
                    "21-$i"
                )
            )
        }
        mainAdapter = MainAdapter(myDataBeans)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = OrientationHelper.VERTICAL
        binding.rvCurrency.layoutManager = linearLayoutManager
        binding.rvCurrency.adapter = mainAdapter

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
            R.id.iv_transfer_top, R.id.ll_transfer  -> {
                startActivity(Intent(this, TransferActivity::class.java))
            }
            R.id.iv_scan_top, R.id.ll_scan -> {
                startActivity(Intent(this, ScanActivity::class.java))
            }
        }
    }

}