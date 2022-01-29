package com.ramble.ramblewallet.item

import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.databinding.ItemAddTokenBinding
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2022/1/12 15:15
 * 作者　: potato
 * 描述　:
 */
class UnAddTokenItem(val data: StoreInfo) : SimpleRecyclerItem() {

    override fun getLayout(): Int = R.layout.item_add_token

    override fun bind(holder: ViewHolder) {
        var binding: ItemAddTokenBinding = holder.binding()
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
        binding.ivTokenStatus.setImageResource(R.drawable.vector_token_reduce)
        binding.tvTokenName.text = data.name
        holder.attachOnClickListener(R.id.add_view)
//        holder.attachOnClickListener(R.id.iv_reduce)
    }
}