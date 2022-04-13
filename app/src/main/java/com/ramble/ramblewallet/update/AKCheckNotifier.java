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
import org.lzh.framework.updatepluginlib.base.CheckNotifier;
import org.lzh.framework.updatepluginlib.util.SafeDialogHandle;

public class AKCheckNotifier extends CheckNotifier {
    DialogInterface.OnClickListener onClickListener;

    public AKCheckNotifier(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public Dialog create(Activity activity) {
        String appSize = "";
        if (update instanceof UpdateImp) {
            appSize = ((UpdateImp) update).getAppSize();
        }
        View inflate = activity.getLayoutInflater().inflate(R.layout.dialog_ios_updata, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(inflate);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tv_content = inflate.findViewById(R.id.txt_message);
        String updateContent = "版本号: " + update.getVersionName() + "\n" + (TextUtils.isEmpty(appSize) ? "" : "大\u3000小: " + appSize) + "\n\n"
                + update.getUpdateContent();
        tv_content.setText(updateContent);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button btn_no = inflate.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(v -> {
            //MMKVManager.INSTANCE.setVersionIgnore(update.getVersionName());
            //不能直接用忽略，调用取消，掉忽略第三方会保存在自己的SP里面做忽略
            sendUserCancel();
            SafeDialogHandle.safeDismissDialog(dialog);
        });
        Button cancel = inflate.findViewById(R.id.cancel);
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
            btn_no.setText("取消");
        }
        return dialog;
    }
}
