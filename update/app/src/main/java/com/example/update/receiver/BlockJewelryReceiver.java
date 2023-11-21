package com.example.update.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.example.update.R;
import com.example.update.api.TrackingApi;
import com.example.update.entity.NotificationOfTracking;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class BlockJewelryReceiver extends BroadcastReceiver {

    private Context context;
    private String highChannelId;

    private String defaultChannelId;

    private String user;
    private NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String jewelryID = intent.getStringExtra("jewelryID");
        user = intent.getStringExtra("user");
        int point = intent.getIntExtra("point",-1);
        initData(context);
        blockThread(jewelryID);
        if (point != -1) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(point);
        }
    }

    private void initData(Context context){
        this.context = context;
        highChannelId = "重要通知渠道";
        defaultChannelId = "默认通知渠道";
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    private void blockThread(String jewelryID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object object = TrackingApi.blockJewelry(user,jewelryID);
                if(object instanceof NotificationOfTracking){
                    NotificationOfTracking notificationOfTracking = (NotificationOfTracking)object;
                    createNotification(notificationOfTracking.getMessage(),
                            notificationOfTracking.getPoint(),
                            notificationOfTracking.getTitle(),
                            notificationOfTracking.getName(),
                            notificationOfTracking.getPrice());
                }
            }
        }).start();
    }


    private void createNotification(String message,int point,String title,String name,double price){
        String channelId;
        if(title.equals("c5饰品捡漏") || title.equals("igxe饰品捡漏")){
            channelId = highChannelId;
        }
        else {
            channelId = defaultChannelId;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_capoo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.welcome1))   //设置大图标
        if(title.equals("c5饰品捡漏") || title.equals("igxe饰品捡漏")){
            builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        }
        Notification notification = builder.build();
        notificationManager.notify(point, notification);

    }
}
