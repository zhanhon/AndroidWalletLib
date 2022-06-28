package com.ramble.ramblewallet.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ramble.ramblewallet.R;

import org.lzh.framework.updatepluginlib.base.DownloadCallback;
import org.lzh.framework.updatepluginlib.base.DownloadNotifier;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.util.ActivityManager;
import org.lzh.framework.updatepluginlib.util.SafeDialogHandle;

import java.io.File;

public class AkDownloadNotifier extends DownloadNotifier {
    @Override
    public DownloadCallback create(Update update, Activity activity) {

        View inflate = activity.getLayoutInflater().inflate(R.layout.dialog_update_progress, null);
        ProgressBar progress_horizontal = inflate.findViewById(R.id.progress_horizontal);
        TextView tv_progress = inflate.findViewById(R.id.tv_progress);
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityManager.get().topActivity())
                .setCancelable(false)
                .setView(inflate);
        AlertDialog dialog = builder.show();
        Button btn_cancel = inflate.findViewById(R.id.btn_cancel);
        if (update.isForced()) {
            btn_cancel.setVisibility(View.GONE);
        }
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            if (null != OkhttpDownloadWorker.call) {
                OkhttpDownloadWorker.call.cancel();
                OkhttpDownloadWorker.call = null;
            }
            dialog.dismiss();
        });
        return new DownloadCallback() {
            @Override
            public void onDownloadStart() {
            }

            @Override
            public void onDownloadComplete(File file) {
                SafeDialogHandle.safeDismissDialog(dialog);
            }

            @Override
            public void onDownloadProgress(long current, long total) {
                int percent = (int) (current * 1.0f / total * 100);
                progress_horizontal.setProgress(percent);
                tv_progress.setText(percent + "%");
            }

            @Override
            public void onDownloadError(Throwable t) {
                if (null != t && null != t.getMessage() && t.getMessage().contains("stream was reset: CANCEL")) {//取消下载
                    System.out.println("cancel dowload");
                } else {
                    SafeDialogHandle.safeDismissDialog(dialog);
                    createRestartDialog();
                }

            }
        };
    }

    private void createRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityManager.get().topActivity())
                .setCancelable(!update.isForced())
                .setTitle("下载apk失败")
                .setMessage("是否重新下载？")
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartDownload();
                    }
                });

        builder.show();
    }
}
