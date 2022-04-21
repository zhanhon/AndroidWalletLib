package com.ramble.ramblewallet.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.Wallet;

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
            if (walletManageBean.isClickDelete()) {
                baseViewHolder.setImageResource(R.id.iv_delete, R.drawable.vector_have_deleted);
            } else {
                baseViewHolder.setImageResource(R.id.iv_delete, R.drawable.vector_not_deleted);
            }
        } else {
            baseViewHolder.itemView.findViewById(R.id.cl_delete).setVisibility(View.GONE);
        }
        if (walletManageBean.isChoose()) {
            baseViewHolder.itemView.findViewById(R.id.tv_is_choose).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.itemView.findViewById(R.id.tv_is_choose).setVisibility(View.GONE);
        }
        baseViewHolder.setText(R.id.tv_wallet_name, walletManageBean.getWalletName());
        baseViewHolder.setText(R.id.tv_currency_address, addressHandle(walletManageBean.getAddress()));

        switch (walletManageBean.getWalletType()) {
            case 1:
                baseViewHolder.setText(R.id.tv_currency_name, "ETH");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_eth);
                break;
            case 2:
                baseViewHolder.setText(R.id.tv_currency_name, "TRX");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_trx);
                break;
            case 3:
                baseViewHolder.setText(R.id.tv_currency_name, "BTC");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_btc);
                break;
            case 4:
                baseViewHolder.setText(R.id.tv_currency_name, "SOL");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_sol);
                break;
            case 5:
                baseViewHolder.setText(R.id.tv_currency_name, "DOGE");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_doge);
                break;
            default:
                break;
        }
    }

    private String addressHandle(String str) {
        if (str.isEmpty()) {
            return null;
        }
        String subStr1 = str.substring(0, 10);
        int strLength = str.length();
        String subStr2 = str.substring(strLength - 6, strLength);
        return subStr1 + "..." + subStr2;
    }

}
