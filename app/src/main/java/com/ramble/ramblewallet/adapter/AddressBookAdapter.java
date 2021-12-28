package com.ramble.ramblewallet.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.MyAddressBean;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @创建人： Ricky
 * @创建时间： 2021/12/5
 */
public class AddressBookAdapter extends BaseQuickAdapter<MyAddressBean, BaseViewHolder> {
    private final boolean isNeedDelete;

    public AddressBookAdapter(List<MyAddressBean> datas, boolean isNeedDelete) {
        super(R.layout.activity_main_currency_item, datas);
        this.isNeedDelete = isNeedDelete;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MyAddressBean myDataBean) {
        if (isNeedDelete) {
            baseViewHolder.itemView.findViewById(R.id.iv_menu).setVisibility(View.GONE);
            baseViewHolder.itemView.findViewById(R.id.iv_reduce).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.itemView.findViewById(R.id.iv_menu).setVisibility(View.VISIBLE);
            baseViewHolder.itemView.findViewById(R.id.iv_reduce).setVisibility(View.GONE);
        }
        if (myDataBean.getType() == 1) {
            baseViewHolder.setImageResource(R.id.cl_icon, R.drawable.vector_eth_selecet);
        } else if (myDataBean.getType() == 2) {
            baseViewHolder.setImageResource(R.id.cl_icon, R.drawable.vector_bt_selecter);
        } else if (myDataBean.getType() == 3) {
            baseViewHolder.setImageResource(R.id.cl_icon, R.drawable.vector_trx_selecter);
        }
        if (myDataBean.getType() == 1) {
            baseViewHolder.setText(R.id.tv_main_currency_name, "ETH");
        } else if (myDataBean.getType() == 2) {
            baseViewHolder.setText(R.id.tv_main_currency_name, "BTC");
        } else if (myDataBean.getType() == 3) {
            baseViewHolder.setText(R.id.tv_main_currency_name, "TRX");
        }
        baseViewHolder.setText(R.id.tv_wallet_name, myDataBean.getUserName());
        baseViewHolder.setText(R.id.tv_wallet_address, myDataBean.getAddress());
//        baseViewHolder.itemView.addOnLayoutChangeListener(R.id.iv_menu);
    }

}
