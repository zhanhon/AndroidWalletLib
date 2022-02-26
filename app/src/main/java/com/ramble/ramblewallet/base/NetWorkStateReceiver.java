package com.ramble.ramblewallet.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ramble.ramblewallet.utils.NetworkUtils;

/**
 * @创建人： Ricky
 * @创建时间： 2022/2/26
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtils.checkNetwork(context);
    }
}
