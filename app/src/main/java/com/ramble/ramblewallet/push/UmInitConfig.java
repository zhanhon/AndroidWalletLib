package com.ramble.ramblewallet.push;

import static android.os.Looper.getMainLooper;
import static com.ramble.ramblewallet.constant.ConstantsKt.DEVICE_TOKEN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.ramble.ramblewallet.R;
import com.ramble.ramblewallet.utils.SharedPreferencesUtils;
import com.ramble.ramblewallet.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;


public class UmInitConfig {

    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private static final String TAG = "UmInitConfig";
    private static Handler handler;

    private UmInitConfig() {
        throw new IllegalStateException(TAG);
    }

    /**
     * 预初始化
     */
    public static void preInit(Context context) {
        //解决厂商通知点击时乱码等问题
        PushAgent.setup(context, PushConstants.APP_KEY, PushConstants.MESSAGE_SECRET);
        UMConfigure.preInit(context, PushConstants.APP_KEY, PushConstants.CHANNEL);
    }

    public static void umInit(Context context) {

        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(context, PushConstants.APP_KEY, PushConstants.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE, PushConstants.MESSAGE_SECRET);


        //集成umeng-crash-vx.x.x.aar，则需要关闭原有统计SDK异常捕获功能
        MobclickAgent.setCatchUncaughtExceptions(false);
        //PushSDK初始化(如使用推送SDK，必须调用此方法)
        initUpush(context);

        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

    }

    /**
     * 主进程和子进程channel都需要进行初始化和注册
     */
    private static void initUpush(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);
        handler = new Handler(getMainLooper());

        //sdk开启通知声音
        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
                super.dealWithNotificationMessage(context, msg);
                Log.i(TAG, "deviceToken --> " + msg);
            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                Log.i(TAG, "deviceToken 11--> " + msg);
                handler.post(() -> {
                    UTrack.getInstance(context).trackMsgClick(msg);
                    ToastUtils.showToastFree(context, msg.custom);
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                Log.i(TAG, "deviceToken222 --> " + msg);
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder;
                        if (Build.VERSION.SDK_INT >= 26) {
                            if (!UmengMessageHandler.isChannelSet) {
                                UmengMessageHandler.isChannelSet = true;
                                NotificationChannel chan = new NotificationChannel(UmengMessageHandler.PRIMARY_CHANNEL,
                                        PushAgent.getInstance(context).getNotificationChannelName(),
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager manager = (NotificationManager) context.getSystemService(
                                        Context.NOTIFICATION_SERVICE);
                                if (manager != null) {
                                    manager.createNotificationChannel(chan);
                                }
                            }
                            builder = new Notification.Builder(context, UmengMessageHandler.PRIMARY_CHANNEL);
                        } else {
                            builder = new Notification.Builder(context);
                        }
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        pushAgent.setMessageHandler(messageHandler);

        /*
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                ToastUtils.showToastFree(context, msg.custom);
            }
        };
        //使用自定义的NotificationHandler
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.i(TAG, "device token: " + deviceToken);
                SharedPreferencesUtils.saveSecurityString(context, DEVICE_TOKEN, deviceToken);
                context.sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG, "register failed: " + s + " " + s1);
                SharedPreferencesUtils.saveSecurityString(context, DEVICE_TOKEN, "");
                context.sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

        //使用完全自定义处理
        pushAgent.setPushIntentServiceClass(UmengNotificationService.class);
    }


}
