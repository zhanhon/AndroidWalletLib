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
        if (isNeedDelete) {
            baseViewHolder.itemView.findViewById(R.id.cl_icon).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.itemView.findViewById(R.id.cl_icon).setVisibility(View.GONE);
        }
        baseViewHolder.setText(R.id.tv_token_name, myDataBean.getName());
    }
}
