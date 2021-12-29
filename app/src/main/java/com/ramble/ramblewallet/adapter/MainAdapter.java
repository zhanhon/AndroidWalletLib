package com.ramble.ramblewallet.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.MainETHTokenBean;
import com.ramble.ramblewallet.utils.DecimalFormatUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ramble.ramblewallet.constant.ConstantsKt.HKD;
import static com.ramble.ramblewallet.constant.ConstantsKt.RMB;
import static com.ramble.ramblewallet.constant.ConstantsKt.USD;

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
        baseViewHolder.setText(R.id.tv_token_name, mainETHTokenBean.getName());
        baseViewHolder.setText(R.id.tv_token_balance, DecimalFormatUtil.format8.format(mainETHTokenBean.getBalance()));
        switch (mainETHTokenBean.getCurrencyUnit()) {
            case RMB:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "￥");
                break;
            case HKD:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "HK$");
                break;
            case USD:
                baseViewHolder.setText(R.id.tv_converted_token_unit, "$");
                break;
        }

        baseViewHolder.setText(R.id.tv_converted_token_balance, DecimalFormatUtil.format2.format(mainETHTokenBean.getBalance().multiply(mainETHTokenBean.getRate())));
        if (mainETHTokenBean.getChange().toString().contains("-")) {
            baseViewHolder.setTextColor(R.id.tv_increase_change, getContext().getResources().getColor(R.color.color_E11334));
        } else {
            baseViewHolder.setTextColor(R.id.tv_increase_change, getContext().getResources().getColor(R.color.color_009272));
        }
        baseViewHolder.setText(R.id.tv_increase_change, mainETHTokenBean.getChange() + "%");
    }

    private void tokenIcon(@NotNull BaseViewHolder baseViewHolder, MainETHTokenBean mainETHTokenBean) {
        switch (mainETHTokenBean.getName()) {
            case "ETH":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_eth);
                break;
            case "TRX":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_trx);
                break;
            case "BTC":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_btc);
                break;
            case "TFT":
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_tft);
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
                baseViewHolder.setImageResource(R.id.iv_token_icon, R.drawable.vector_uni);
                break;
        }
    }
}
