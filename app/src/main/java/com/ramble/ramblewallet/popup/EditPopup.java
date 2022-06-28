package com.ramble.ramblewallet.popup;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;
import com.ramble.ramblewallet.R;

public class EditPopup extends CenterPopupView {
    private TextView popup_title;
    private String popupTitle;
    private EditText popup_content;
    private String popupContent;
    private String popupContentHit;
    private TextView popup_cancel;
    private String popupCancel;
    private TextView popup_ok;
    private String popupOk;
    private ImageView imagePlaintext;
    private boolean isVisible;
    private OnClickConfirmListener onClickListener;
    public EditPopup(@NonNull Context context, String title, String content, String contentHit,
                     String cancelTxt, String okTxt,OnClickConfirmListener onClickListener) {
        super(context);
        this.popupTitle=title;
        this.popupContent=content;
        this.popupContentHit=contentHit;
        this.popupCancel=cancelTxt;
        this.popupOk=okTxt;
        this.onClickListener = onClickListener;
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.wallet_popup_edit_layout;
    }
    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    @Override
    protected void onCreate() {
        super.onCreate();
        imagePlaintext = this.findViewById(R.id.image_plaintext);
        popup_title=this.findViewById(R.id.popup_title);
        popup_title.setText(popupTitle);
        popup_content=this.findViewById(R.id.popup_content);
        popup_content.setText(popupContent);
        popup_content.setHint(popupContentHit);
        popup_cancel=this.findViewById(R.id.popup_cancel);
        popup_cancel.setText(popupCancel);

        popup_ok=this.findViewById(R.id.popup_ok);
        popup_ok.setText(popupOk);
        setIsPasswordView(false);
        popup_cancel.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();

        }});
         popup_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (onClickListener != null){
                        onClickListener.onClickConfirm(view,popup_content.getText().toString().trim());
                    }
                }
        });

         imagePlaintext.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View view) {
                 isVisible = !isVisible;
                 setPasswordIsVisible(isVisible);
             }
         });
    }

    /**
     * 设置密码是否可见
     */
    private void setPasswordIsVisible(boolean isVisible){
        if (!isVisible) {
            imagePlaintext.setImageResource(R.mipmap.wallet_ic_mm_nor);
            popup_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else {
            popup_content.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imagePlaintext.setImageResource(R.mipmap.wallet_ic_mm_sel);
        }

    }

    /**
     * 设置密码视图
     */
    public void setIsPasswordView(boolean isPasswordView){
        if (isPasswordView) {
            imagePlaintext.setVisibility(VISIBLE);
            popup_content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else {
            imagePlaintext.setVisibility(GONE);
            popup_content.setInputType(InputType.TYPE_CLASS_TEXT);
        }

    }

    public interface OnClickConfirmListener{
        void onClickConfirm(View view,String text);
    }

}
