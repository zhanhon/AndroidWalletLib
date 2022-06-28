package com.ramble.ramblewallet.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.Wallet;
import com.ramble.ramblewallet.constant.ConstantsKt;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 身份钱包
 */
public class IdWalletManageAdapter extends BaseQuickAdapter<Wallet, BaseViewHolder> {

    public IdWalletManageAdapter() {
        super(R.layout.activity_wallet_manage_item);
        addChildClickViewIds(R.id.iv_copy_address);
        addChildClickViewIds(R.id.iv_wallet_more);
        addChildClickViewIds(R.id.cl_delete);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Wallet walletManageBean) {
        baseViewHolder.getView(R.id.cl_delete).setVisibility(View.GONE);
        if (walletManageBean.isChoose()) {
            baseViewHolder.getView(R.id.tv_is_choose).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.getView(R.id.tv_is_choose).setVisibility(View.GONE);
        }
        baseViewHolder.setText(R.id.tv_wallet_name, walletManageBean.getWalletName());
        baseViewHolder.setText(R.id.tv_currency_address, addressHandle(walletManageBean.getAddress()));

        switch (walletManageBean.getWalletType()) {
            case ConstantsKt.WALLET_TYPE_ETH:
                baseViewHolder.setText(R.id.tv_currency_name, "ETH");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_eth);
                break;
            case ConstantsKt.WALLET_TYPE_TRX:
                baseViewHolder.setText(R.id.tv_currency_name, "TRX");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_trx);
                break;
            case ConstantsKt.WALLET_TYPE_BTC:
                baseViewHolder.setText(R.id.tv_currency_name, "BTC");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_btc);
                break;
            case ConstantsKt.WALLET_TYPE_SOL:
                baseViewHolder.setText(R.id.tv_currency_name, "SOL");
                baseViewHolder.setBackgroundResource(R.id.cy_wallet_manage_item, R.drawable.shape_wallet_manage_sol);
                break;
            case ConstantsKt.WALLET_TYPE_DOGE:
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
