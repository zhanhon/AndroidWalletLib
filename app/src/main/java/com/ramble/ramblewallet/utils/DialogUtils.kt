package com.ramble.ramblewallet.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.AddressBookActivity
import com.ramble.ramblewallet.activity.ScanActivity
import com.ramble.ramblewallet.activity.TransferActivity
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.blockchain.bitcoin.WalletBTCUtils.isBtcValidAddress
import com.ramble.ramblewallet.blockchain.ethereum.WalletETHUtils.isEthValidAddress
import com.ramble.ramblewallet.blockchain.tron.WalletTRXUtils.isTrxValidAddress
import com.ramble.ramblewallet.constant.ADDRESS_BOOK_INFO
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.BottomNoticeDialog2Binding
import com.ramble.ramblewallet.databinding.BottomNoticeDialogBinding
import com.ramble.ramblewallet.databinding.DialogTransItemBinding
import com.ramble.ramblewallet.helper.dataBinding
import com.ramble.ramblewallet.helper.start


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
        show()
        setContentView(binding.root)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        binding.tvTitle.text = tvName
        binding.tvType.text = when (tvType) {
            1 -> {
                "ETH"
            }
            2 -> {
                "BTC"
            }
            3 -> {
                "TRX"
            }
            else -> {
                "SOLANA"
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
        show()
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.BOTTOM)
        binding.editName.setText(tvName)
        binding.editAddress.setText(address)
        when (type) {
            1 -> {
                binding.tvTitle.text = activity.getString(R.string.edit_address_wallet)
                binding.tvUpdata.text = activity.getString(R.string.edit)
                binding.ivQr.visibility = View.GONE
            }
            2 -> {
                binding.tvTitle.text = activity.getString(R.string.new_address_wallet)
                binding.tvUpdata.text = activity.getString(R.string.confirm)
                binding.ivQr.visibility = View.VISIBLE
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
                        18,
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
                        ToastUtils.showToastFree(
                            activity,
                            activity.getString(R.string.address_already_null)
                        )
                        return@setOnClickListener
                    }
                }
                else -> {
                    if (binding.editAddress.text.isNullOrEmpty()) {
                        ToastUtils.showToastFree(
                            activity,
                            activity.getString(R.string.address_already_null)
                        )
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
                                isTrxValidAddress(binding.editAddress.text.toString()) -> {
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
                                isTrxValidAddress(binding.editAddress.text.toString()) -> {
                                    "TRX" + String.format("%02d", cout)
                                }
                                else -> ""
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
                                isTrxValidAddress(binding.editAddress.text.toString()) -> {
                                    "TRX01"
                                }
                                else -> "ETH01"
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
                            if (it.userName != tvName && (it.userName == name || it.address == binding.editAddress.text.toString())) {
                                ToastUtils.showToastFree(
                                    activity,
                                    activity.getString(R.string.address_already_exists)
                                )
                                return@setOnClickListener
                            }
                        }
                    }
                    else -> {
                        myData.forEach {
                            if (it.address == binding.editAddress.text.toString() || it.userName == name) {
                                ToastUtils.showToastFree(
                                    activity,
                                    activity.getString(R.string.address_already_exists)
                                )
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
                isTrxValidAddress(binding.editAddress.text.toString()) -> {
                    3
                }
                else -> 4
            }
            if (isBtcValidAddress(data.address)) {
                if (data.address.length < 26) {
                    ToastUtils.showToastFree(
                        activity,
                        activity.getString(R.string.address_already_err)
                    )
                    return@setOnClickListener
                }
            } else if (isEthValidAddress(data.address)) {
                if (data.address.length < 42) {
                    ToastUtils.showToastFree(
                        activity,
                        activity.getString(R.string.address_already_err)
                    )
                    return@setOnClickListener
                }
            } else if (isTrxValidAddress(data.address)) {
                if (data.address.length < 34) {
                    ToastUtils.showToastFree(
                        activity,
                        activity.getString(R.string.address_already_err)
                    )
                    return@setOnClickListener
                }
            } else {
                ToastUtils.showToastFree(activity, activity.getString(R.string.address_already_err))
                return@setOnClickListener
            }
            if (data.type == 0) {
                ToastUtils.showToastFree(activity, activity.getString(R.string.address_already_err))
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
 * 时间　: 2022/1/5 15:52
 * 作者　: potato
 * 描述　:扫描编辑，创造底部弹窗
 */
fun showBottomSan(
    activity: ScanActivity,
    address: String,
    walletType: Int,
    tokenBean: MainETHTokenBean,
    type: Int,
): Dialog {
    val binding: DialogTransItemBinding =
        LayoutInflater.from(activity).dataBinding(
            R.layout.dialog_trans_item
        )
    var myDataBeans: ArrayList<MyAddressBean> = arrayListOf()
    if (SharedPreferencesUtils.getString(activity, ADDRESS_BOOK_INFO, "").isNotEmpty()) {
        myDataBeans =
            Gson().fromJson(
                SharedPreferencesUtils.getString(activity, ADDRESS_BOOK_INFO, ""),
                object : TypeToken<java.util.ArrayList<MyAddressBean>>() {}.type
            )
    }
    return AlertDialog.Builder(activity).create().apply {
        show()
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.BOTTOM)
        binding.tvTransferTitle.text = when (walletType) {
            1 -> {//ETH
                "ETH"
            }
            2 -> {//TRX
                "TRX"
            }
            3 -> {//btc
                "BTC"
            }
            else -> "SOLANA"
        }
        var isSame = false
        var num = -1
        var name = ""
        if (myDataBeans.isNotEmpty()) {
            myDataBeans.forEachIndexed { index, myAddressBean ->
                if (myAddressBean.address == address) {
                    num = index
                    binding.editName.setText(myAddressBean.userName)
                    name = myAddressBean.userName
                    isSame = true
                }
            }
        }
        binding.tvAddress.text = address
        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        var isChecked = false
        binding.tvCheck.setOnClickListener {
            isChecked = (it as CheckBox).isChecked
        }
        binding.ok.setOnClickListener {
            if (isChecked) {
                if (isSame) {
                    if (binding.editName.text.toString().trim() != name) {
                        myDataBeans[num].userName = binding.editName.text.toString().trim()
                        SharedPreferencesUtils.saveString(
                            activity,
                            ADDRESS_BOOK_INFO,
                            Gson().toJson(myDataBeans)
                        )
                    }
                } else {
                    name = when (type) {
                        1 -> {
                            binding.editName.text.toString()
                        }
                        else -> {
                            if (SharedPreferencesUtils.getString(
                                    MyApp.sInstance,
                                    ADDRESS_BOOK_INFO,
                                    ""
                                )
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
                                        isBtcValidAddress(binding.tvAddress.text.toString()) -> {
                                            number = 2
                                        }
                                        isEthValidAddress(binding.tvAddress.text.toString()) -> {
                                            number = 1
                                        }
                                        isTrxValidAddress(binding.tvAddress.text.toString()) -> {
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
                                        isBtcValidAddress(binding.tvAddress.text.toString()) -> {
                                            "BTC" + String.format("%02d", cout)
                                        }
                                        isEthValidAddress(binding.tvAddress.text.toString()) -> {
                                            "ETH" + String.format("%02d", cout)
                                        }
                                        isTrxValidAddress(binding.tvAddress.text.toString()) -> {
                                            "TRX" + String.format("%02d", cout)
                                        }
                                        else -> ""
                                    }

                                }

                            } else {
                                if (binding.editName.text.toString().isNotEmpty()) {
                                    binding.editName.text.toString()
                                } else {
                                    when {
                                        isBtcValidAddress(binding.tvAddress.text.toString()) -> {
                                            "BTC01"
                                        }
                                        isEthValidAddress(binding.tvAddress.text.toString()) -> {
                                            "ETH01"
                                        }
                                        isTrxValidAddress(binding.tvAddress.text.toString()) -> {
                                            "TRX01"
                                        }
                                        else -> "ETH01"
                                    }
                                }

                            }
                        }
                    }
                    val data = MyAddressBean()
                    data.address = binding.tvAddress.text.toString()
                    data.userName = name
                    data.type = when {
                        isBtcValidAddress(binding.tvAddress.text.toString()) -> {
                            2
                        }
                        isEthValidAddress(binding.tvAddress.text.toString()) -> {
                            1
                        }
                        isTrxValidAddress(binding.tvAddress.text.toString()) -> {
                            3
                        }
                        else -> 4
                    }
                    if (isBtcValidAddress(data.address)) {
                        if (data.address.length < 26) {
                            ToastUtils.showToastFree(
                                activity,
                                activity.getString(R.string.address_already_err)
                            )
                            return@setOnClickListener
                        }
                    } else if (isEthValidAddress(data.address)) {
                        if (data.address.length < 42) {
                            ToastUtils.showToastFree(
                                activity,
                                activity.getString(R.string.address_already_err)
                            )
                            return@setOnClickListener
                        }
                    } else if (isTrxValidAddress(data.address)) {
                        if (data.address.length < 34) {
                            ToastUtils.showToastFree(
                                activity,
                                activity.getString(R.string.address_already_err)
                            )
                            return@setOnClickListener
                        }
                    } else {
                        ToastUtils.showToastFree(
                            activity,
                            activity.getString(R.string.address_already_err)
                        )
                        return@setOnClickListener
                    }
                    if (data.type == 0) {
                        ToastUtils.showToastFree(
                            activity,
                            activity.getString(R.string.address_already_err)
                        )
                        return@setOnClickListener
                    }
                    myDataBeans.add(data)
                    SharedPreferencesUtils.saveString(
                        activity,
                        ADDRESS_BOOK_INFO,
                        Gson().toJson(myDataBeans)
                    )
                }
            }
            when (type) {
                2 -> {
                    RxBus.emitEvent(Pie.EVENT_RESS_TRANS_SCAN, address)
                    activity.finish()
                }
                3 -> {
                    activity.start(TransferActivity::class.java, Bundle().also {
                        it.putString(ARG_PARAM1, address)
                        it.putSerializable(ARG_PARAM2, tokenBean)
                    })
                    activity.finish()
                }
            }
            dismiss()
        }
    }
}

