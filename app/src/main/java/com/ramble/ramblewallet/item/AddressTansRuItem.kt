package com.ramble.ramblewallet.item

import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.databinding.ItemAddressRuBinding
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2022/2/23 15:48
 * 作者　: potato
 * 描述　:
 */
class AddressTansRuItem(val data: QueryTransferRecord.InRecord) : SimpleRecyclerItem() {

    override fun getLayout(): Int = R.layout.item_address_ru

    override fun bind(holder: ViewHolder) {
        var binding: ItemAddressRuBinding = holder.binding()
        binding.tvToAddress.text = data.address
        holder.attachOnClickListener(R.id.add_copy)
    }
}