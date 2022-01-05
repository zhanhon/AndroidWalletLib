package com.ramble.ramblewallet.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ramble.ramblewallet.activity.WelcomeActivity;
import com.umeng.message.UTrack;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

public class NotificationBroadcast extends BroadcastReceiver {
    public static final String EXTRA_KEY_ACTION = "ACTION";
    public static final String EXTRA_KEY_MSG = "MSG";
    public static final int ACTION_CLICK = 10;
    public static final int ACTION_DISMISS = 11;
    public static final int EXTRA_ACTION_NOT_EXIST = -1;
    private static final String TAG = NotificationBroadcast.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_KEY_MSG);
        int action = intent.getIntExtra(EXTRA_KEY_ACTION,
                EXTRA_ACTION_NOT_EXIST);
        try {
            UMessage msg = (UMessage) new UMessage(new JSONObject(message));

            switch (action) {
                case ACTION_DISMISS:
                    Log.i(TAG, "dismiss notification");
                    UTrack.getInstance(context).trackMsgDismissed(msg);
                    break;
                case ACTION_CLICK:
                    Log.i(TAG, "click notification");
                    MyNotificationService.oldMessage = null;
                    UTrack.getInstance(context).trackMsgClick(msg);
                    Intent data = new Intent(intent);
                    data.setClass(context, WelcomeActivity.class);
                    data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需为Intent添加Flag：Intent.FLAG_ACTIVITY_NEW_TASK，否则无法启动Activity。
                    context.startActivity(data);
                    break;
            }
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
