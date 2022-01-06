package com.ramble.ramblewallet.utils

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.activity.AddressBookActivity
import com.ramble.ramblewallet.activity.ScanActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.ARG_PARAM3
import com.ramble.ramblewallet.constant.ARG_PARAM4
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
    tvName:String,
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
        binding.tvTitle.text=tvName
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
    tvName:String,
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
        binding.tvTitle.text=tvName
        binding.ivQr.setOnClickListener {
            activity.start(ScanActivity::class.java, Bundle().also {
                it.putInt(ARG_PARAM1, 1)
            })
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvUpdata.setOnClickListener {
            editListener?.onClick(it)
            dismiss()
        }
        activity.setOnResultsListener(object :AddressBookActivity.OnResultsListener{
            override fun onResultsClick(result: String) {
                binding.editAddress.setText(result)
            }

        })
    }
}
