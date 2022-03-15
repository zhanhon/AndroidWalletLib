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
        binding.tvTokenName.text = data.symbol
        binding.ivDelete.isVisible = isEditable
        binding.ivDelete.isChecked = isChecked
        binding.ivDelete.setOnClickListener(this)
        if (isEditable) {
            binding.ivTokenStatus.setImageResource(R.drawable.vector_token_move)
            binding.ivTokenStatus.visibility = View.VISIBLE
        } else {
            when (data.isMyToken) {
                1 -> {
                    binding.ivTokenStatus.setImageResource(R.drawable.vector_token_reduce)
                    binding.ivTokenStatus.visibility = View.VISIBLE
                }
                0 -> {
                    binding.ivTokenStatus.setImageResource(R.drawable.vector_token_add)
                    binding.ivTokenStatus.visibility = View.VISIBLE
                }
                else -> {
                    binding.ivTokenStatus.visibility = View.INVISIBLE
                }
            }
        }

        when (data.symbol) {
            "WETH" -> {
                if (data.contractAddress == "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2") {
                    binding.ivTokenIcon.setImageResource(R.drawable.ic_weth)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }

            "WBTC" -> {
                if (data.contractAddress == "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_wbtc)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "DAI" -> {
                if (data.contractAddress == "0x6b175474e89094c44da98b954eedeac495271d0f") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_dai)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "USDC" -> {
                if (data.contractAddress == "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_usdc)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "USDT" -> {
                if (data.contractAddress == "0xdac17f958d2ee523a2206206994597c13d831ec7") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_usdt)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "LINK" -> {
                if (data.contractAddress == "0x514910771af9ca656af840dff83e8264ecf986ca") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_link)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "YFI" -> {
                if (data.contractAddress == "0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e") {
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_yfi)
                } else {
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
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
        if (isEditable) {
            if (data.isMyToken == 2) {
                binding.ivDelete.isVisible = false
                binding.vDelete.isVisible = true
            } else {
                binding.vDelete.isVisible = false
                binding.ivDelete.isVisible = isEditable
            }
        } else {
            binding.vDelete.isVisible = isEditable

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