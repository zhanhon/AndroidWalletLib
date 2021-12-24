package com.ramble.ramblewallet.item

import android.graphics.Color
import androidx.core.view.isVisible
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.QueryTransferRecord
import com.ramble.ramblewallet.databinding.ItemTransferAccountsBinding
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2021/12/20 11:49
 * 作者　: potato
 * 描述　:
 */
class TransferItem (val data: QueryTransferRecord.Record) : SimpleRecyclerItem() {
    override fun getLayout(): Int = R.layout.item_transfer_accounts

    override fun bind(holder: ViewHolder) {
        var binding: ItemTransferAccountsBinding = holder.binding()

        binding.tvAddress.text = data.thirdGameName
        binding.tvTime.text = data.createTime
        when (data.blockStatus){
            1-> {
                binding.tvMoney.setText(R.string.transaction_ing)
                binding.tvMoney.setTextColor( Color.parseColor("#222222"))
                binding.tvMoneyType.isVisible=false
            }
            2-> {
                when (data.actionStatus){
                    1->{//转入
                        binding.tvMoney.setTextColor( Color.parseColor("#03DAC5"))
                        binding.tvMoney.text="+"+data.validBetAmount
                    }
                    2->{//转出
                        binding.tvMoney.setTextColor( Color.parseColor("#df2939"))
                        binding.tvMoney.text="-"+data.validBetAmount.toString()
                    }
                }
                binding.tvMoneyType
                binding.tvMoneyType.text=data.userReceivedWashBetAmount.toString()
                binding.tvMoneyType.isVisible=false
            }
            3-> {
                binding.tvMoney.setText(R.string.transaction_failed)
                binding.tvMoney.setTextColor( Color.parseColor("#222222"))
                binding.tvMoneyType.text=data.userReceivedWashBetAmount.toString()
                binding.tvMoneyType.isVisible=false
            }
        }
        holder.attachOnClickListener(R.id.item_transfer)
    }
}