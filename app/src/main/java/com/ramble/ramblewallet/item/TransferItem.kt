package com.ramble.ramblewallet.item

import android.graphics.Color
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.databinding.ItemTransferAccountsBinding
import com.ramble.ramblewallet.utils.TimeUtils.dateToWeek
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2021/12/20 11:49
 * 作者　: potato
 * 描述　:
 */
class TransferItem(val data: QueryTransferRecord.Record) : SimpleRecyclerItem() {
    override fun getLayout(): Int = R.layout.item_transfer_accounts

    override fun bind(holder: ViewHolder) {
        var binding: ItemTransferAccountsBinding = holder.binding()

        binding.tvAddress.text = if (data.transferType==1){
            addressHandle(data.fromAddress)
        }else{
            addressHandle(data.toAddress)
        }
        binding.tvTime.text = dateToWeek(data.createTime) + "  " + data.createTime
        binding.tvMoneyType.isVisible = false
        when (data.addressType) {
            1 -> {
                binding.ivMoneyType.setImageResource(R.drawable.vector_eth_selecet)
            }
            2 -> {
                binding.ivMoneyType.setImageResource(R.drawable.vector_trx_selecter)
            }
            3 -> {
                binding.ivMoneyType.setImageResource(R.drawable.vector_bt_selecter)
            }
        }
        when (data.status) {
            1 -> {
                binding.tvMoney.setText(R.string.in_transaction)
                binding.tvMoney.setTextColor(Color.parseColor("#333333"))

            }
            2 -> {
                when (data.transferType) {
                    2 -> {//转入
                        binding.tvMoney.setTextColor(Color.parseColor("#009474"))
                        binding.tvMoney.text = "+" + data.amount
                    }
                    1 -> {//转出
                        binding.tvMoney.setTextColor(Color.parseColor("#e11334"))
                        binding.tvMoney.text = "-" + data.amount
                    }
                }

            }
            3 -> {
                binding.tvMoney.setText(R.string.transaction_failed)
                binding.tvMoney.setTextColor(Color.parseColor("#a1a1a1"))
            }
        }
        holder.attachOnClickListener(R.id.item_transfer)
    }

    private fun addressHandle(str: String): String? {
        if (str.isEmpty()) {
            return null
        }
        val subStr1 = str.substring(0, 10)
        val strLength = str.length
        val subStr2 = str.substring(strLength - 5, strLength)
        return "$subStr1...$subStr2"
    }
}