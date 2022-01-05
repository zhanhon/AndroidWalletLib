package com.ramble.ramblewallet.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.eth.Wallet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class WalletManageAdapter extends BaseQuickAdapter<Wallet, BaseViewHolder> {
    private final boolean isNeedDelete;

    public WalletManageAdapter(List<Wallet> datas, boolean isNeedDelete) {
        super(R.layout.activity_wallet_manage_item, datas);
        this.isNeedDelete = isNeedDelete;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Wallet walletManageBean) {
        if (isNeedDelete) {
            baseViewHolder.itemView.findViewById(R.id.cl_delete).setVisibility(View.VISIBLE);
            if (walletManageBean.getClickDelete()) {
                baseViewHolder.setImageResource(R.id.iv_delete, R.drawable.vector_have_deleted);
            } else {
                baseViewHolder.setImageResource(R.id.iv_delete, R.drawable.vector_not_deleted);
            }
        } else {
            baseViewHolder.itemView.findViewById(R.id.cl_delete).setVisibility(View.GONE);
        }


        baseViewHolder.setText(R.id.tv_wallet_name, walletManageBean.getWalletName());

        switch (walletManageBean.getType()) {
            case 1:
                baseViewHolder.setText(R.id.tv_currency_name, "ETH");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_eth);
                break;
            case 2:
                baseViewHolder.setText(R.id.tv_currency_name, "BTC");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_btc);
                break;
            case 3:
                baseViewHolder.setText(R.id.tv_currency_name, "TRX");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_trx);
                break;
        }
    }
}
