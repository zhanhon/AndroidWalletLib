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
public class MnemonicConfirmAdapter extends BaseQuickAdapter<MyDataBean, BaseViewHolder> {


    public MnemonicConfirmAdapter(List<MyDataBean> datas) {
        super(R.layout.activity_mnemonic_confirm_item, datas);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MyDataBean myDataBean) {
        baseViewHolder.setText(R.id.tv_contributing_words_confirm_name, myDataBean.getName());
        if (myDataBean.getIsWrong()) {
            baseViewHolder.findView(R.id.imv_contributing_words_confirm_position).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.findView(R.id.imv_contributing_words_confirm_position).setVisibility(View.GONE);
        }
    }
}
