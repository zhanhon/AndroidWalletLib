package com.ramble.ramblewallet.item

import android.view.View
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.MyAddressBean
import com.ramble.ramblewallet.databinding.ItemAddressBookBinding
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2022/1/5 14:41
 * 作者　: potato
 * 描述　:
 */
class AddressBookItem(val data: MyAddressBean) : SimpleRecyclerItem() {

    override fun getLayout(): Int = R.layout.item_address_book

    override fun bind(holder: ViewHolder) {
        var binding: ItemAddressBookBinding = holder.binding()

        if (data.isNeedDelete) {
            binding.ivMenu.visibility = View.GONE
            binding.ivReduce.visibility = View.VISIBLE

        } else {
            binding.ivMenu.visibility = View.VISIBLE
            binding.ivReduce.visibility = View.GONE
        }
        when (data.type) {
            1 -> {
                binding.clIcon.setImageResource(R.drawable.ic_eth_unselect)
                binding.tvMainCurrencyName.text = "ETH"
            }
            2 -> {
                binding.clIcon.setImageResource(R.drawable.ic_btc_unselcetor)
                binding.tvMainCurrencyName.text = "BTC"
            }
            3 -> {
                binding.clIcon.setImageResource(R.drawable.ic_trx_selcetor)
                binding.tvMainCurrencyName.text = "TRX"
            }
            4 -> {
                binding.clIcon.setImageResource(R.drawable.ic_trx_selcetor)
                binding.tvMainCurrencyName.text = "SOLA"
            }
        }
        binding.tvWalletAddress.text = addressHandle(data.address)
        binding.tvWalletName.text = data.userName
        holder.attachOnClickListener(R.id.iv_menu)
        holder.attachOnClickListener(R.id.iv_reduce)
        holder.attachOnClickListener(R.id.item_address)
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