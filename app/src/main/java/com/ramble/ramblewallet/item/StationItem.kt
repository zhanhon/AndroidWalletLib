package com.ramble.ramblewallet.item

import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.Page
import com.ramble.ramblewallet.databinding.ItemNoticeBinding
import com.ramble.ramblewallet.wight.CheckableSimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2021/12/16 9:45
 * 作者　: potato
 * 描述　:
 */
class StationItem(val data: Page.Record) : CheckableSimpleRecyclerItem(), View.OnClickListener {
    var isEditable = false

    override fun getLayout(): Int = R.layout.item_notice

    override fun bind(holder: ViewHolder) {
        val binding: ItemNoticeBinding = holder.binding()
        binding.title1.text = data.title
        binding.tvContent.text = data.content
        if (isEditable) {
            binding.ckDelete.visibility = View.VISIBLE
            binding.bells.visibility = View.INVISIBLE
        } else {
            binding.ckDelete.visibility = View.GONE
            binding.bells.visibility = View.VISIBLE
        }
        binding.ckDelete.isChecked = isChecked
        binding.ckDelete.setOnClickListener(this)
        binding.badge.isVisible = data.isRead == 0
        if (data.isRead == 0) {
            binding.bells.setBackgroundResource(R.drawable.ic_unread)
            binding.title1.setTextColor(Color.parseColor("#222222"))
            binding.tvContent.setTextColor(Color.parseColor("#666666"))
        } else {
            binding.bells.setBackgroundResource(R.drawable.ic_read)
            binding.title1.setTextColor(Color.parseColor("#8d8d8d"))
            binding.tvContent.setTextColor(Color.parseColor("#a1a1a1"))
        }
        holder.attachOnClickListener(R.id.item_msg_notic)
    }

    override fun onClick(v: View?) {
        isChecked = (v!! as CheckBox).isChecked
    }

    override fun bindPayloads(holder: ViewHolder, payloads: MutableList<Any>) {
        val binding: ItemNoticeBinding = holder.binding()
        if (isEditable) {
            binding.ckDelete.visibility = View.VISIBLE
            binding.bells.visibility = View.INVISIBLE
        } else {
            binding.ckDelete.visibility = View.GONE
            binding.bells.visibility = View.VISIBLE
        }
    }
}