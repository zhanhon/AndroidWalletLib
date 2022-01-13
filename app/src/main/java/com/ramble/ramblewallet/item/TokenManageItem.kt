package com.ramble.ramblewallet.item

import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.databinding.TokenManageItemBinding
import com.ramble.ramblewallet.wight.CheckableSimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2022/1/13 15:24
 * 作者　: potato
 * 描述　:
 */
class TokenManageItem(val data: StoreInfo) : CheckableSimpleRecyclerItem(), View.OnClickListener {
    var isEditable = false

    override fun getLayout(): Int = R.layout.token_manage_item

    override fun bind(holder: ViewHolder) {
        val binding: TokenManageItemBinding = holder.binding()
        binding.tvTokenName.text = data.name
        binding.ivDelete.isVisible = isEditable

        binding.ivDelete.isChecked = isChecked
        binding.ivDelete.setOnClickListener(this)
        when (data.name) {
            "TFT" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_tft)
            "WBTC" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_wbtc)
            "DAI" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_dai)
            "USDC" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_usdc)
            "USDT" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_usdt)
            "LINK" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_link)
            "YFI" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_yfi)
            "UNI" -> binding.ivTokenIcon.setImageResource(R.drawable.vector_uni)
            else -> binding.ivTokenIcon.setImageResource(R.drawable.vector_dai)
        }
        if (!isEditable){
            if (data.isMyToken == 1) {
               binding.ivTokenStatus .setImageResource(R.drawable.vector_token_reduce)
            } else {
                binding.ivTokenStatus .setImageResource(R.drawable.vector_token_add)
            }

        }
        holder.attachOnClickListener(R.id.iv_token_status)
    }

    override fun onClick(v: View?) {
        isChecked = (v!! as CheckBox).isChecked
    }

    override fun bindPayloads(holder: ViewHolder, payloads: MutableList<Any>) {
        val binding: TokenManageItemBinding = holder.binding()
        binding.ivDelete.isVisible = isEditable
    }
}