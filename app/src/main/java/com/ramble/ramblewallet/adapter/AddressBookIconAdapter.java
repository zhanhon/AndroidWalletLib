package com.ramble.ramblewallet.adapter;

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
public class AddressBookIconAdapter extends BaseQuickAdapter<MyDataBean, BaseViewHolder> {
    public AddressBookIconAdapter(List<MyDataBean> datas) {
        super(R.layout.activity_main_currency_icon_item, datas);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MyDataBean myDataBean) {
//        baseViewHolder.setText(R.id.tv_main_currency_name, myDataBean.getIndex() + "");
//        baseViewHolder.setText(R.id.tv_wallet_name, myDataBean.getName());
//        baseViewHolder.setText(R.id.tv_wallet_address, myDataBean.getAge() + "");
    }
}
