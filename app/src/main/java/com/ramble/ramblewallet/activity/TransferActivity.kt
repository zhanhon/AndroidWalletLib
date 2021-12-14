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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityTransferBinding

class TransferActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTransferBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.clMinerFee.setOnClickListener(this)
    }

    private fun showDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_miner_fee)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params
//            val recyclerView: RecyclerView = window.findViewById(R.id.rv_list)
//            val currencyAdapter = CurrencyAdapter(currencyModels)
//            val linearLayoutManager = LinearLayoutManager(getContext())
//            linearLayoutManager.orientation = OrientationHelper.VERTICAL
//            recyclerView.layoutManager = linearLayoutManager
//            recyclerView.adapter = currencyAdapter
//            currencyAdapter.addChildClickViewIds(R.id.cb_erc20)
//            currencyAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
//                if (view.id == R.id.cb_erc20) {
//                    assestPr.createAddressUI(
//                        getActivity(),
//                        currencyModels.get(position).getAddressType()
//                    )
//                    dialog.dismiss()
//                }
//            })
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cl_miner_fee -> {
                showDialog()
            }
        }
    }
}