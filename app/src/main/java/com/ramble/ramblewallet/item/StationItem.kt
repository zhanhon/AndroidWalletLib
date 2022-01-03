package com.ramble.ramblewallet.item

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
//        binding.time2.text = data.createTime
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
        binding.badge.isVisible=data.isRead==0

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