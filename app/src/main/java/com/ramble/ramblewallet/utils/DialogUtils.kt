package com.ramble.ramblewallet.utils

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.databinding.BottomNoticeDialogBinding
import com.ramble.ramblewallet.helper.dataBinding

/**
 * 时间　: 2022/1/5 15:52
 * 作者　: potato
 * 描述　:
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
