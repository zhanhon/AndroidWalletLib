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
        binding.tvTokenAddress.text = addressHandle(data.contractAddress)
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

    private fun addressHandle(str: String): String? {
        if (str.isEmpty()) {
            return null
        }
        val subStr1 = str.substring(0, 10)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 6, strLength)
        return "$subStr1...$subStr2"
    }

}