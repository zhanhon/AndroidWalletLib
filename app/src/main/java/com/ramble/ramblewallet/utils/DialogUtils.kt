package com.ramble.ramblewallet.utils

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.AddressBookActivity
import com.ramble.ramblewallet.activity.ScanActivity
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.BottomNoticeDialog2Binding
import com.ramble.ramblewallet.databinding.BottomNoticeDialogBinding
import com.ramble.ramblewallet.databinding.TopNoticeDialogBinding
import com.ramble.ramblewallet.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start
import com.ramble.ramblewallet.tron.WalletTRXUtils.isTrxValidAddress


/**
 * 时间　: 2022/1/5 15:52
 * 作者　: potato
 * 描述　:编辑，复制，删除底部弹窗
 */
fun showBottomDialog(
    activity: Activity,
    tvName: String,
    tvType: Int,
    copeListener: View.OnClickListener? = null,
    editListener: View.OnClickListener? = null,
    delListener: View.OnClickListener? = null
): Dialog {
    val binding: BottomNoticeDialogBinding =
        LayoutInflater.from(activity).dataBinding(
            R.layout.bottom_notice_dialog
        )

    return AlertDialog.Builder(activity).create().apply {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
        show()
        setContentView(binding.root)
        binding.tvTitle.text = tvName
        binding.tvType.text = when (tvType) {
            1 -> {
                "ETH"
            }
            2 -> {
                "BTC"
            }
            else -> {
                "TRX"
            }
        }
        binding.tvCopy.setOnClickListener {
            dismiss()
            copeListener?.onClick(it)
        }
        binding.tvEdit.setOnClickListener {
            editListener?.onClick(it)
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            delListener?.onClick(it)
            dismiss()
        }
    }
}

/**
 * 时间　: 2022/1/5 15:52
 * 作者　: potato
 * 描述　:编辑，创造底部弹窗
 */
fun showBottomDialog2(
    activity: AddressBookActivity,
    tvName: String,
    address: String,
    type: Int,
    editListener: View.OnClickListener? = null
): Dialog {
    val binding: BottomNoticeDialog2Binding =
        LayoutInflater.from(activity).dataBinding(
            R.layout.bottom_notice_dialog2
        )

    return AlertDialog.Builder(activity).create().apply {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM)
        show()
        setContentView(binding.root)
        binding.editName.setText(tvName)
        binding.editAddress.setText(address)
        when (type) {
            1 -> {
                binding.tvTitle.text = activity.getString(R.string.edit_address_wallet)
                binding.tvUpdata.text = activity.getString(R.string.edit)
                binding.ivQr.visibility=View.GONE
            }
            2 -> {
                binding.tvTitle.text = activity.getString(R.string.new_address_wallet)
                binding.tvUpdata.text = activity.getString(R.string.confirm)
                binding.ivQr.visibility=View.VISIBLE
            }
        }

        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        binding.ivQr.setOnClickListener {
            activity.start(ScanActivity::class.java, Bundle().also {
                it.putInt(ARG_PARAM1, 1)
                it.putSerializable(
                    ARG_PARAM2, MainETHTokenBean(
                        "ETH",
                        "ETH",
                        null,
                        "",
                        "",
                        null,
                        false
                    )
                )
            })
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvUpdata.setOnClickListener {
            when (type) {
                1 -> {
                    if (binding.editName.text.isNullOrEmpty() || binding.editAddress.text.isNullOrEmpty()) {
                        Toast.makeText(
                            MyApp.sInstance,
                            MyApp.sInstance.getString(R.string.address_already_null),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
                else -> {
                    if (binding.editAddress.text.isNullOrEmpty()) {
                        Toast.makeText(
                            MyApp.sInstance,
                            MyApp.sInstance.getString(R.string.address_already_null),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
            }
            var name = when (type) {
                1 -> {
                    binding.editName.text.toString()
                }
                else -> {
                    if (SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, "")
                            .isNotEmpty()
                    ) {

                        if (binding.editName.text.toString().isNotEmpty()) {
                            binding.editName.text.toString()
                        } else {
                            var myData: ArrayList<MyAddressBean> =
                                Gson().fromJson(
                                    SharedPreferencesUtils.getString(
                                        MyApp.sInstance,
                                        ADDRESS_BOOK_INFO,
                                        ""
                                    ),
                                    object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                                )
                            var number = 0
                            when {
                                isBtcValidAddress(binding.editAddress.text.toString()) -> {
                                    number = 2
                                }
                                isEthValidAddress(binding.editAddress.text.toString()) -> {
                                    number = 1
                                }
                                isTrxValidAddress( binding.editAddress.text.toString()) -> {
                                    number = 3
                                }
                            }
                            var cout = 1
                            myData.forEach {
                                if (it.type == number) {
                                    cout++
                                }
                            }

                            when {
                                isBtcValidAddress(binding.editAddress.text.toString()) -> {
                                    "BTC" + String.format("%02d", cout)
                                }
                                isEthValidAddress(binding.editAddress.text.toString()) -> {
                                    "ETH" + String.format("%02d", cout)
                                }
                                isTrxValidAddress( binding.editAddress.text.toString()) -> {
                                    "TRX" + String.format("%02d", cout)
                                }
                                else->""
                            }

                        }

                    } else {
                        if (binding.editName.text.toString().isNotEmpty()) {
                            binding.editName.text.toString()
                        } else {
                            when {
                                isBtcValidAddress(binding.editAddress.text.toString()) -> {
                                    "BTC01"
                                }
                                isEthValidAddress(binding.editAddress.text.toString()) -> {
                                    "ETH01"
                                }
                                isTrxValidAddress( binding.editAddress.text.toString()) -> {
                                    "TRX01"
                                }
                                else-> "ETH01"
                            }
                        }

                    }
                }
            }
            if (SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, "")
                    .isNotEmpty()
            ) {
                var myData: ArrayList<MyAddressBean> =
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, ""),
                        object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                    )
                when (type) {
                    1 -> {
                        myData.forEach {

                            if (it.userName != tvName) {
                                if (it.userName == name || it.address == binding.editAddress.text.toString()) {
                                    Toast.makeText(
                                        MyApp.sInstance,
                                        MyApp.sInstance.getString(R.string.address_already_exists),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@setOnClickListener
                                }
                            }

                        }
                    }
                    else -> {
                        myData.forEach {
                            if (it.address == binding.editAddress.text.toString() || it.userName == name) {
                                Toast.makeText(
                                    MyApp.sInstance,
                                    MyApp.sInstance.getString(R.string.address_already_exists),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                        }
                    }
                }

            }
            editListener?.onClick(it)
            var data = MyAddressBean()
            data.address = binding.editAddress.text.toString()
            data.userName = name
            data.type = when {
                isBtcValidAddress(binding.editAddress.text.toString()) -> {
                    2
                }
                isEthValidAddress(binding.editAddress.text.toString()) -> {
                    1
                }
                isTrxValidAddress( binding.editAddress.text.toString()) -> {
                    3
                }
                else->4
            }
            if ( isBtcValidAddress( data.address)) {
                if (data.address.length < 26) {
                    Toast.makeText(
                        MyApp.sInstance,
                        MyApp.sInstance.getString(R.string.address_already_err),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else if ( isEthValidAddress(data.address)) {
                if (data.address.length < 42) {
                    Toast.makeText(
                        MyApp.sInstance,
                        MyApp.sInstance.getString(R.string.address_already_err),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else if (isTrxValidAddress(data.address)) {
                if (data.address.length < 34) {
                    Toast.makeText(
                        MyApp.sInstance,
                        MyApp.sInstance.getString(R.string.address_already_err),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else {
                Toast.makeText(
                    MyApp.sInstance,
                    MyApp.sInstance.getString(R.string.address_already_err),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (data.type == 4||data.type == 0) {
                Toast.makeText(
                    MyApp.sInstance,
                    MyApp.sInstance.getString(R.string.address_already_err),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            var myData2: ArrayList<MyAddressBean> = arrayListOf()
            if (SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, "")
                    .isNotEmpty()
            ) {
                myData2 =
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, ""),
                        object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                    )
            }
            myData2.add(data)

            when (type) {
                1 -> RxBus.emitEvent(Pie.EVENT_ADDRESS_BOOK_UPDATA, data)
                2 -> {
                    SharedPreferencesUtils.saveString(
                        activity,
                        ADDRESS_BOOK_INFO,
                        Gson().toJson(myData2)
                    )
                    RxBus.emitEvent(Pie.EVENT_ADDRESS_BOOK_ADD, data)
                }
            }

            dismiss()
        }
        activity.setOnResultsListener(object : AddressBookActivity.OnResultsListener {
            override fun onResultsClick(result: String) {
                binding.editAddress.setText(result)
            }

        })

    }
}

/**
 * 时间　: 2022/2/3 9:52
 * 作者　: potato
 * 描述　:历史记录上部弹窗
 */
fun showTopTranDialog(
    activity: Activity,
    textView: View
): Dialog {
    val binding: TopNoticeDialogBinding =
        LayoutInflater.from(activity).dataBinding(
            R.layout.top_notice_dialog
        )

    return AlertDialog.Builder(activity).create().apply {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp: WindowManager.LayoutParams? = window?.attributes
        val notificationBar: Int = Resources.getSystem().getDimensionPixelSize(
            Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        )
        val location = IntArray(2)
        textView.getLocationInWindow(location) //获取在当前窗体内的绝对坐标
        textView.getLocationOnScreen(location) //获取在整个屏幕内的绝对坐标
        wlp?.x = 0 //对 dialog 设置 x 轴坐标
        wlp?.y = location[1] + textView.height - notificationBar//对dialog设置y轴坐标
        wlp?.gravity = Gravity.TOP
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = wlp
        setCanceledOnTouchOutside(false)
        show()
        setContentView(binding.root)
        var wallet: Wallet = Gson().fromJson(
            SharedPreferencesUtils.getString(activity, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
        var currencyUnit = SharedPreferencesUtils.getString(activity, CURRENCY_TRAN, "")
        if (currencyUnit.isNotEmpty()) {
            when (currencyUnit) {
                "ETH" -> {
                    binding.ivEth.visibility = View.VISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                }
                "BTC" -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.VISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                }
                "TRX" -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.VISIBLE
                }
            }
        } else {
            when (wallet.walletType) {
                1 -> {
                    binding.ivEth.visibility = View.VISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                }
                2 -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.INVISIBLE
                    binding.ivTrx.visibility = View.VISIBLE
                }
                3 -> {
                    binding.ivEth.visibility = View.INVISIBLE
                    binding.ivBtc.visibility = View.VISIBLE
                    binding.ivTrx.visibility = View.INVISIBLE
                }
            }
        }


        binding.rlEth.setOnClickListener {
            SharedPreferencesUtils.saveString(activity, CURRENCY_TRAN, "ETH")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 1)
            dismiss()
        }
        binding.rlBtc.setOnClickListener {
            SharedPreferencesUtils.saveString(activity, CURRENCY_TRAN, "BTC")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 0)
            dismiss()
        }
        binding.rlTrx.setOnClickListener {
            SharedPreferencesUtils.saveString(activity, CURRENCY_TRAN, "TRX")
            RxBus.emitEvent(Pie.EVENT_TRAN_TYPE, 2)
            dismiss()
        }
    }
}
