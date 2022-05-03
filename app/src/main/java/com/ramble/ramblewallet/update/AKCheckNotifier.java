package com.ramble.ramblewallet.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.utils.Pie;
import com.ramble.ramblewallet.utils.RxBus;

import org.lzh.framework.updatepluginlib.base.CheckNotifier;
import org.lzh.framework.updatepluginlib.util.SafeDialogHandle;

public class AKCheckNotifier extends CheckNotifier {
    DialogInterface.OnClickListener onClickListener;
    String title;
    Boolean first;

    public AKCheckNotifier(DialogInterface.OnClickListener onClickListener,String title,Boolean first) {
        this.onClickListener = onClickListener;
        this.title=title;
        this.first=first;
    }

    @Override
    public Dialog create(Activity activity) {
        View inflate = activity.getLayoutInflater().inflate(R.layout.dialog_ios_updata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(inflate);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tv_title = inflate.findViewById(R.id.txt_title);
        tv_title.setText(title);
        TextView tv_content = inflate.findViewById(R.id.txt_message);
        String updateContent =update.getUpdateContent();
        tv_content.setText(updateContent);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button btn_no = inflate.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v -> {
            //MMKVManager.INSTANCE.setVersionIgnore(update.getVersionName());
            //不能直接用忽略，调用取消，掉忽略第三方会保存在自己的SP里面做忽略
            sendUserCancel();
            SafeDialogHandle.safeDismissDialog(dialog);
            if(first){
                RxBus.INSTANCE.emitEvent(Pie.EVENT_PUSH_JUMP, "1");
            }
        });
        TextView cancel = inflate.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(v -> {
            sendUserCancel();
            SafeDialogHandle.safeDismissDialog(dialog);
        });
        Button btn_yes = inflate.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(v -> {
            sendDownloadRequest();
            SafeDialogHandle.safeDismissDialog(dialog);
        });
        if (update.isIgnore()) {
            btn_no.setVisibility(View.GONE);
            btn_yes.setText(activity.getString(R.string.force_update));
        }
        return dialog;
    }
}
