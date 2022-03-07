package com.ramble.ramblewallet.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.MainETHTokenBean;
import com.ramble.ramblewallet.utils.DecimalFormatUtil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

import static com.ramble.ramblewallet.constant.ConstantsKt.CNY;
import static com.ramble.ramblewallet.constant.ConstantsKt.HKD;
import static com.ramble.ramblewallet.constant.ConstantsKt.USD;
import static com.ramble.ramblewallet.utils.StringUtils.strAddComma;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MainAdapter extends BaseQuickAdapter<MainETHTokenBean, BaseViewHolder> {
    public MainAdapter(List<MainETHTokenBean> datas) {
        super(R.layout.activity_main_item, datas);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MainETHTokenBean mainETHTokenBean) {
        tokenIcon(baseViewHolder, mainETHTokenBean);
        baseViewHolder.setText(R.id.tv_token_name, mainETHTokenBean.getSymbol());
        baseViewHolder.setText(R.id.tv_token_balance, strAddComma(DecimalFormatUtil.format8.format(mainETHTokenBean.getBalance())));
        switch (mainETHTokenBean.getCurrencyUnit()) {
            case CNY:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "￥");
                break;
            case HKD:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "HK$");
                break;
            case USD:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "$");
                break;
            default:
                break;
        }
        baseViewHolder.setText(R.id.tv_unit_price, strAddComma(DecimalFormatUtil.format8.format(new BigDecimal(mainETHTokenBean.getUnitPrice()))));
    }

    private void tokenIcon(@NotNull BaseViewHolder baseViewHolder, MainETHTokenBean mainETHTokenBean) {
        switch (mainETHTokenBean.getSymbol()) {
            case "ETH":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_eth);
                break;
            case "TRX":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_trx);
                break;
            case "BTC":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_btc);
                break;
            case "WETH":
                if (mainETHTokenBean.getContractAddress().equals("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.ic_weth);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "WBTC":
                if (mainETHTokenBean.getContractAddress().equals("0x2260fac5e5542a773aa44fbcfedf7c193bc2c599")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_wbtc);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "DAI":
                if (mainETHTokenBean.getContractAddress().equals("0x6b175474e89094c44da98b954eedeac495271d0f")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_dai);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "USDC":
                if (mainETHTokenBean.getContractAddress().equals("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_usdc);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "USDT":
                if (mainETHTokenBean.getContractAddress().equals("0xdac17f958d2ee523a2206206994597c13d831ec7")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_usdt);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "LINK":
                if (mainETHTokenBean.getContractAddress().equals("0x514910771af9ca656af840dff83e8264ecf986ca")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_link);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "YFI":
                if (mainETHTokenBean.getContractAddress().equals("0x0bc529c00c6401aef6d220be8c6ea1667f6ad93e")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_yfi);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            case "UNI":
                if (mainETHTokenBean.getContractAddress().equals("0x1f9840a85d5af5bf1d1762f925bdaddc4201f984")){
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_uni);
                }else{
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            default:
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                break;
        }
    }
}
