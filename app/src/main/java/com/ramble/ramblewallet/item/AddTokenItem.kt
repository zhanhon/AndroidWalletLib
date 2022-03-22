package com.ramble.ramblewallet.item


import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.StoreInfo
import com.ramble.ramblewallet.databinding.ItemAddTokenBinding
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2022/1/11 17:34
 * 作者　: potato
 * 描述　:
 */
class AddTokenItem(val data: StoreInfo) : SimpleRecyclerItem() {

    override fun getLayout(): Int = R.layout.item_add_token

    override fun bind(holder: ViewHolder) {
        var binding: ItemAddTokenBinding = holder.binding()
        when (data.symbol) {
            "WETH" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.ic_weth)
            }

            "WBTC" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_wbtc)
            }
            "DAI" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_dai)
            }
            "USDC" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_usdc)
            }
            "USDT" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_usdt)
            }
            "LINK" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_link)
            }
            "YFI" -> {
                binding.ivTokenIcon.setImageResource(R.drawable.vector_yfi)
            }
            "UNI" -> {
                if (data.contractAddress == "0x1f9840a85d5af5bf1d1762f925bdaddc4201f984") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_uni)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            else -> binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
        }
        when (data.isMyToken) {
            0 -> {
                binding.ivTokenStatus.setImageResource(R.drawable.vector_token_add)
                binding.ivTokenStatus.isVisible = true
            }
            1 -> {
                binding.ivTokenStatus.setImageResource(R.drawable.vector_token_reduce)
                binding.ivTokenStatus.isVisible = true
            }
            else -> binding.ivTokenStatus.isVisible = false
        }

        binding.tvTokenName.text = data.symbol
        holder.attachOnClickListener(R.id.add_view)
    }
}