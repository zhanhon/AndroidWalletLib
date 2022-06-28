package com.ramble.ramblewallet.popup;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.ramble.ramblewallet.R;


public class CustomPopup extends CenterPopupView {
    private TextView popup_title;
    private String popupTitle;
    private TextView popup_content;
    private String popupContent;
    private TextView popup_cancel;
    private String popupCancel;
    private TextView popup_ok;
    private String popupOk;
    private View.OnClickListener confirmListener;
    private View.OnClickListener btnCancleListener;
    //11 删除账单记录

    public CustomPopup(@NonNull Context context, String title, String content, String cancelTxt, String okTxt,
                       View.OnClickListener confirmListener,View.OnClickListener btnCancleListener) {
        super(context);
        this.popupTitle=title;
        this.popupContent=content;
        this.popupCancel=cancelTxt;
        this.popupOk=okTxt;
        this.btnCancleListener = btnCancleListener;
        this.confirmListener = confirmListener;
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_custom_layout_wallet;
    }
    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    @Override
    protected void onCreate() {
        super.onCreate();
        popup_title=this.findViewById(R.id.popup_title);
        popup_title.setText(popupTitle);
        popup_content=this.findViewById(R.id.popup_content);
        popup_content.setText(popupContent);
        popup_cancel=this.findViewById(R.id.popup_cancel);
        popup_cancel.setText(popupCancel);

        popup_ok=this.findViewById(R.id.popup_ok);
        popup_ok.setText(popupOk);
        popup_cancel.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            if (btnCancleListener != null){
                btnCancleListener.onClick(view);
            }

        }});
         popup_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (confirmListener != null){
                        confirmListener.onClick(view);
                    }

                }
        });
    }


}
