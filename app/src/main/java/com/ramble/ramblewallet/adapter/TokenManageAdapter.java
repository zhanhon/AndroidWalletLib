package com.ramble.ramblewallet.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.MyDataBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class TokenManageAdapter extends BaseQuickAdapter<MyDataBean, BaseViewHolder> {
    private final boolean isNeedDelete;

    public TokenManageAdapter(List<MyDataBean> datas, boolean isNeedDelete) {
        super(R.layout.activity_token_manage_item, datas);
        this.isNeedDelete = isNeedDelete;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MyDataBean myDataBean) {
        switch (myDataBean.getName()) {
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
        if (isNeedDelete) {
            baseViewHolder.itemView.findViewById(R.id.cl_icon).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.itemView.findViewById(R.id.cl_icon).setVisibility(View.GONE);
        }
        baseViewHolder.setText(R.id.tv_token_name, myDataBean.getName());
    }
}
