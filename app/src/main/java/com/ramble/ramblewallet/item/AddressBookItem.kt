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
                binding.clIcon.setImageResource(R.drawable.vector_eth_selecet)
                binding.tvMainCurrencyName.text = "ETH"
            }
            2 -> {
                binding.clIcon.setImageResource(R.drawable.vector_bt_selecter)
                binding.tvMainCurrencyName.text = "BTC"
            }
            3 -> {
                binding.clIcon.setImageResource(R.drawable.vector_trx_selecter)
                binding.tvMainCurrencyName.text = "TRX"
            }
        }
        binding.tvWalletAddress.text = data.address
        binding.tvWalletName.text = data.userName
        holder.attachOnClickListener(R.id.iv_menu)
        holder.attachOnClickListener(R.id.iv_reduce)
        holder.attachOnClickListener(R.id.item_address)
    }
}