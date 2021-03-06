package com.ramble.ramblewallet.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ramble.ramblewallet.R;

/**
 * @创建人： Ricky
 * @创建时间： 2022/3/18
 */
public class ToastUtils {

    /**
     * toast 自定义
     */
    public static Toast showToastFree(Context ctx, String str) {
        Toast toast = Toast.makeText(ctx.getApplicationContext(), str, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.toast_hor_view, null);
        TextView tv = toastView.findViewById(R.id.tv_toast_content);
        tv.setText(str);
        toast.setView(toastView);
        toast.show();
        return toast;
    }

    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastToastTime;
    private final static long TIME = 1500;

    public static boolean isShowToast() {
        long time = System.currentTimeMillis();
        if (time - lastToastTime < TIME) {
            return true;
        }
        lastToastTime = time;
        return false;
    }

}
