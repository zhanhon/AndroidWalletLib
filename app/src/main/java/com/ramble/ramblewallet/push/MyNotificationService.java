package com.ramble.ramblewallet.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.bean.Page;
import com.ramble.ramblewallet.utils.SharedPreferencesUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Random;

import static com.ramble.ramblewallet.constant.ConstantsKt.STATION_INFO;

public class MyNotificationService extends Service {
    private static final String TAG = UmengNotificationService.class.getName();
    public static UMessage oldMessage = null;
    private ArrayList<Page.Record> records = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String message = intent.getStringExtra("UmengMsg");
        try {
            UMessage msg = new UMessage(new JSONObject(message));
            /**根据主题返回不同页面**/
            if (msg.extra.get("message_type").equals("201")) {//消息 保存本地
                Page.Record a = new Page.Record();
                LocalDateTime tiem = LocalDateTime.now();
                a.setId(Integer.parseInt(msg.extra.get("id")));
                a.setTitle(msg.title);
                a.setContent(msg.text);
                a.setCreateTime(tiem.toString());
                Boolean isP = SharedPreferencesUtils.getString(
                        this,
                        STATION_INFO,
                        ""
                ).isEmpty();
                if (!isP) {
                    records = (ArrayList<Page.Record>) SharedPreferencesUtils.String2SceneList(
                            SharedPreferencesUtils.getString(
                                    this,
                                    STATION_INFO,
                                    ""
                            )
                    );

                } else {
                    records = new ArrayList<Page.Record>();
                }
                records.add(a);
                String addId = SharedPreferencesUtils.SceneList2String(records);
                SharedPreferencesUtils.saveString(this, STATION_INFO, addId);

            } else if (msg.extra.get("message_type").equals("202")) {//公告

            } else if (msg.extra.get("message_type").equals("1") || msg.extra.get("message_type").equals("2") ||
                    msg.extra.get("message_type").equals("3") || msg.extra.get("message_type").equals("4")) {//交易记录

            } else {//其它

            }
            if (oldMessage != null) {
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(oldMessage);
            }
            showNotification(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(UMessage msg) {
        int id = new Random(System.nanoTime()).nextInt();
        oldMessage = msg;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            manager.cancelAll();
        } catch (Exception ignored) {
        }
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            if (!UmengMessageHandler.isChannelSet) {
                UmengMessageHandler.isChannelSet = true;
                NotificationChannel chan = new NotificationChannel(UmengMessageHandler.PRIMARY_CHANNEL,
                        PushAgent.getInstance(this).getNotificationChannelName(),
                        NotificationManager.IMPORTANCE_DEFAULT);
                if (manager != null) {
                    manager.createNotificationChannel(chan);
                }
            }
            builder = new Notification.Builder(this, UmengMessageHandler.PRIMARY_CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }
        builder.setContentTitle(msg.title)
                .setContentText(msg.text)
                .setTicker(msg.ticker)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.umeng_socialize_copy)
                .setAutoCancel(true);
        Notification notification = builder.getNotification();
        PendingIntent clickPendingIntent = getClickPendingIntent(this, msg);
        PendingIntent dismissPendingIntent = getDismissPendingIntent(this, msg);
        notification.deleteIntent = dismissPendingIntent;
        notification.contentIntent = clickPendingIntent;
        manager.notify(id, notification);
        UTrack.getInstance(this).trackMsgShow(msg, notification);
    }

    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
        Intent clickIntent = new Intent();
        clickIntent.setClass(context, NotificationBroadcast.class);
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
                msg.getRaw().toString());
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_CLICK);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        return clickPendingIntent;
    }

    public PendingIntent getDismissPendingIntent(Context context, UMessage msg) {
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, NotificationBroadcast.class);
        deleteIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
                msg.getRaw().toString());
        deleteIntent.putExtra(
                NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_DISMISS);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis() + 1),
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return deletePendingIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
