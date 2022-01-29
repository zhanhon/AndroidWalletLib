package com.ramble.ramblewallet.utils

import android.app.Activity
import android.app.Dialog
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
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.constant.ADDRESS_BOOK_INFO
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.databinding.BottomNoticeDialog2Binding
import com.ramble.ramblewallet.databinding.BottomNoticeDialogBinding
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
        when (type) {
            1 -> {
                binding.tvTitle.text = "编辑地址"
                binding.tvUpdata.text = activity.getString(R.string.edit)
            }
            2 -> {
                binding.tvTitle.text = "新增地址"
                binding.tvUpdata.text = activity.getString(R.string.confirm)
            }
        }

        window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        binding.ivQr.setOnClickListener {
            activity.start(ScanActivity::class.java, Bundle().also {
                it.putInt(ARG_PARAM1, 1)
            })
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvUpdata.setOnClickListener {
            if (binding.editName.text.isNullOrEmpty() || binding.editAddress.text.isNullOrEmpty()) {
                Toast.makeText(
                    MyApp.sInstance,
                    MyApp.sInstance.getString(R.string.address_already_null),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, "")
                    .isNotEmpty()
            ) {
                var myData: ArrayList<MyAddressBean> =
                    Gson().fromJson(
                        SharedPreferencesUtils.getString(MyApp.sInstance, ADDRESS_BOOK_INFO, ""),
                        object : TypeToken<ArrayList<MyAddressBean>>() {}.type
                    )
                myData.forEach {
                    if (it.userName == binding.editName.text.toString() && it.address == binding.editAddress.text.toString()) {
                        Toast.makeText(
                            MyApp.sInstance,
                            MyApp.sInstance.getString(R.string.address_already_exists),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
            }
            editListener?.onClick(it)
            var data = MyAddressBean()
            data.address = binding.editAddress.text.toString()
            data.userName = binding.editName.text.toString()
            data.type = if (data.address.startsWith("1") || data.address.startsWith("3")) {
                1
            } else if (data.address.startsWith("0")) {
                2
            } else if (data.address.startsWith("T") || data.address.startsWith("t")) {
                3
            } else {
                4
            }
            if (data.address.startsWith("1") || data.address.startsWith("3")) {
                if (data.address.length < 26) {
                    Toast.makeText(
                        MyApp.sInstance,
                        MyApp.sInstance.getString(R.string.address_already_err),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else if (data.address.startsWith("0")) {
                if (data.address.length < 41) {
                    Toast.makeText(
                        MyApp.sInstance,
                        MyApp.sInstance.getString(R.string.address_already_err),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            } else if (data.address.startsWith("T") || data.address.startsWith("t")) {
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
            if (data.type == 4) {
                Toast.makeText(
                    MyApp.sInstance,
                    MyApp.sInstance.getString(R.string.address_already_err),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            when (type) {
                1 -> RxBus.emitEvent(Pie.EVENT_ADDRESS_BOOK_UPDATA, data)
                2 -> RxBus.emitEvent(Pie.EVENT_ADDRESS_BOOK_ADD, data)
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
