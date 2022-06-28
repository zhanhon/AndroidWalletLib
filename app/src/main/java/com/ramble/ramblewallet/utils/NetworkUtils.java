package com.ramble.ramblewallet.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.ramble.ramblewallet.R;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/26
 */
public class NetworkUtils {

    private NetworkUtils() {
        throw new IllegalStateException("NetworkUtils");
    }

    /*** 判断网络情况   有网络返回true，没网络返回false*/
    public static boolean isNetworkAvalible(Context context) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            //无线网连接和数据网连接都断开
            return wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected();
            //无线网连接和数据网连接一个
//API大于23时使用下面的方式进行网络监听
        } else {
            // 获得网络状态管理器
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            } else {
                // 建立网络数组
                NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
                if (netInfo != null) {
                    for (int i = 0; i < netInfo.length; i++) {
                        // 判断获得的网络状态是否是处于连接状态
                        if (netInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    // 如果没有网络，则弹出网络设置对话框
    public static void checkNetwork(final Context context) {
        if (!NetworkUtils.isNetworkAvalible(context)) {
            netWorkDialog(context);
        }

    }

    public static void netWorkDialog(final Context context) {
        Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_network_anomaly);
            dialogCenterTheme(window);

            window.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
                checkNetwork(context);
                dialog.dismiss();
            });
            window.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
        }
    }

    public static void dialogCenterTheme(Window window) {
        //设置属性
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //dialog背景层
        params.dimAmount = 0.5f;
        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}


