package com.ramble.ramblewallet.item


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
                if (data.contractAddress=="0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"){
                    binding.ivTokenIcon.setImageResource(R.drawable.ic_weth)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }

            "WBTC" ->{
                if (data.contractAddress=="0x2260fac5e5542a773aa44fbcfedf7c193bc2c599"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_wbtc)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "DAI" -> {
                if (data.contractAddress=="0x6b175474e89094c44da98b954eedeac495271d0f"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_dai)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "USDC" -> {
                if (data.contractAddress=="0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_usdc)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "USDT" -> {
                if (data.contractAddress=="0xdac17f958d2ee523a2206206994597c13d831ec7"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_usdt)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "LINK" -> {
                if (data.contractAddress=="0x514910771af9ca656af840dff83e8264ecf986ca"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_link)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "YFI" -> {
                if (data.contractAddress=="0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_yfi)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            "UNI" -> {
                if (data.contractAddress=="0x1f9840a85d5af5bf1d1762f925bdaddc4201f984"){
                    binding.ivTokenIcon.setImageResource(R.drawable.vector_uni)
                }else{
                    binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
                }
            }
            else -> binding.ivTokenIcon.setImageResource(R.mipmap.def_token_img)
        }
        when (data.isMyToken) {
            0 -> binding.ivTokenStatus.setImageResource(R.drawable.vector_token_add)
            1 -> binding.ivTokenStatus.setImageResource(R.drawable.vector_token_reduce)
        }

        binding.tvTokenName.text = data.symbol
        holder.attachOnClickListener(R.id.add_view)
    }
}