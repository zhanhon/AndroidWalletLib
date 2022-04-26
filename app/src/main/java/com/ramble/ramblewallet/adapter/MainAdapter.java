package com.ramble.ramblewallet.adapter;

import static com.ramble.ramblewallet.constant.ConstantsKt.CNY;
import static com.ramble.ramblewallet.constant.ConstantsKt.HKD;
import static com.ramble.ramblewallet.constant.ConstantsKt.USD;
import static com.ramble.ramblewallet.utils.StringUtils.strAddComma;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.MainTokenBean;
import com.ramble.ramblewallet.utils.DecimalFormatUtil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class MainAdapter extends BaseQuickAdapter<MainTokenBean, BaseViewHolder> {
    public MainAdapter(List<MainTokenBean> datas) {
        super(R.layout.activity_main_item, datas);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MainTokenBean mainETHTokenBean) {
        tokenIcon(baseViewHolder, mainETHTokenBean);
        baseViewHolder.setText(R.id.tv_token_name, mainETHTokenBean.getSymbol());
        baseViewHolder.setText(R.id.tv_token_balance, strAddComma(DecimalFormatUtil.format(mainETHTokenBean.getBalance(), 8)));
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
        baseViewHolder.setText(R.id.tv_unit_price, strAddComma(DecimalFormatUtil.format(new BigDecimal(mainETHTokenBean.getUnitPrice()), 8)));
    }

    private void tokenIcon(@NotNull BaseViewHolder baseViewHolder, MainTokenBean mainETHTokenBean) {
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
            case "SOL":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_sol_selecter);
                break;
            case "WETH":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.ic_weth);
                break;
            case "WBTC":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_wbtc);
                break;
            case "DAI":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_dai);
                break;
            case "USDC":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_usdc);
                break;
            case "USDT":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_usdt);
                break;
            case "LINK":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_link);
                break;
            case "YFI":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_yfi);
                break;
            case "UNI":
                if (mainETHTokenBean.getContractAddress().equals("0x1f9840a85d5af5bf1d1762f925bdaddc4201f984")) {
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_uni);
                } else {
                    baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                }
                break;
            default:
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.mipmap.def_token_img);
                break;
        }
    }
}
