package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.adapter.MainAdapter
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MyDataBean
import com.ramble.ramblewallet.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var myDataBeans: ArrayList<MyDataBean> = arrayListOf()
    private lateinit var mainAdapter: MainAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        myDataBeans = ArrayList()
        for (i in 0..100) {
            myDataBeans.add(
                MyDataBean(
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

    }

}