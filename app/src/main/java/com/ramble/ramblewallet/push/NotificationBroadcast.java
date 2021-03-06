package com.ramble.ramblewallet.push;

import static com.ramble.ramblewallet.constant.ConstantsKt.ARG_PARAM1;
import static com.ramble.ramblewallet.constant.ConstantsKt.ARG_PARAM2;
import static com.ramble.ramblewallet.constant.ConstantsKt.ARG_PARAM3;
import static com.ramble.ramblewallet.constant.ConstantsKt.ARG_PARAM4;
import static com.ramble.ramblewallet.constant.ConstantsKt.ARG_PARAM5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ramble.ramblewallet.activity.MsgDetailsActivity;
import com.ramble.ramblewallet.activity.TransactionQueryActivity;
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
            UMessage msg = new UMessage(new JSONObject(message));

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
                    /**??????????????????????????????**/
                    switch (msg.extra.get("messageType")) {
                        case "201"://??????
                            data.setClass(context, MsgDetailsActivity.class);
                            data.putExtra(ARG_PARAM1, msg.title);
                            data.putExtra(ARG_PARAM2, msg.text);
                            data.putExtra(ARG_PARAM3, msg.extra.get("time"));
                            data.putExtra(ARG_PARAM4, 2);
                            data.putExtra(ARG_PARAM5, Integer.parseInt(msg.extra.get("id")));
                            break;
                        case "202"://??????
                            data.setClass(context, MsgDetailsActivity.class);
                            data.putExtra(ARG_PARAM1, msg.title);
                            data.putExtra(ARG_PARAM2, msg.text);
                            data.putExtra(ARG_PARAM3, msg.extra.get("time"));
                            data.putExtra(ARG_PARAM4, 22);
                            data.putExtra(ARG_PARAM5, Integer.parseInt(msg.extra.get("id")));
                            break;
                        case "1"://????????????
                        case "2"://????????????
                        case "3"://????????????
                        case "4"://????????????
                            data.setClass(context, TransactionQueryActivity.class);
                            break;
                        default:
                            data.setClass(context, WelcomeActivity.class);
                            break;
                    }
                    data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//??????Intent??????Flag???Intent.FLAG_ACTIVITY_NEW_TASK?????????????????????Activity???
                    context.startActivity(data);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
