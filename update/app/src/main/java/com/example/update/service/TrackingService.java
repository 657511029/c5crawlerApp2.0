package com.example.update.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.update.R;
import com.example.update.api.TrackingApi;
import com.example.update.entity.NotificationOfTracking;
import com.example.update.entity.UserInfo;
import com.example.update.receiver.BlockJewelryReceiver;
import com.example.update.receiver.NotificationClickReceiver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackingService extends Service {
    private  String [] illegalList;

    private String user;

    private String userName;

    private String password;

    private double  percentage1_c5;

    private double  percentage2_c5;

    private double  percentage3_c5;

    private double  percentage4_c5;

    private double  percentage1_ig;

    private double  percentage2_ig;

    private double  percentage3_ig;

    private double  percentage4_ig;

    private String token;

    private int roundNumber;

    private int exceptionNumber;

    private int warningExceptionNumber = 10;
    private List<String> jewelryIDList;
    private static final String TestApp="TestApp";

    private Context context;



    private NotificationManager notificationManager;

    private String highChannelId;

    private String defaultChannelId;

    private boolean isDestory = false;


    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        startForeground(10000,initForeground(defaultChannelId));
        change();
    }

    private void initData(){
        illegalList = new String[]{"纪念品", "伽马多普勒", "★", "StatTrak"};
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        user = sharedPreferences.getString("user","");
        percentage1_c5 = 1.00;
        percentage2_c5 = 1.00;
        percentage3_c5 = 1.00;
        percentage4_c5 = 1.00;
        percentage1_ig = 1.00;
        percentage2_ig = 1.00;
        percentage3_ig = 1.00;
        percentage4_ig = 1.00;
        userName = "";
        password = "";
        token = "";
        roundNumber = 0;
        exceptionNumber = 0;
        jewelryIDList = new ArrayList<>();
        context = getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        highChannelId = "重要通知渠道";
        defaultChannelId = "默认通知渠道";
    }

    private Notification initForeground(String channelId){
        Notification notification = new NotificationCompat.Builder(context,channelId)
                .setContentTitle("capoo导购")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("程序运行中···\n当前用户为: " + user))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_capoo)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.welcome1))   //设置大图标
                .build();
        return notification;
    }

    private void change(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    jewelryIDList.clear();
                    if(connectRedis() instanceof NotificationOfTracking){
                        continue;
                    }
                    roundNumber++;
                    if(initializeUUAccount(userName,password) instanceof NotificationOfTracking){
                        continue;
                    }

                    start();
                }
            }
        }).start();
    }
    private Object connectRedis() {
        Object object = TrackingApi.TrackingConnectRedis(user);
        if(object instanceof NotificationOfTracking){
            NotificationOfTracking notificationOfTracking = (NotificationOfTracking)object;
            createNotification(notificationOfTracking.getMessage(),
                    notificationOfTracking.getPoint(),
                    notificationOfTracking.getTitle(),
                    notificationOfTracking.getName(),
                    notificationOfTracking.getPrice());
        }
        else {
            Map<String,Object> map = (Map<String,Object>)object;
            UserInfo userInfo = (UserInfo) map.get("userInfo");
            List<String> jewelryIDList = (List<String>) map.get("jewelryIDList");
            userName = userInfo.getUuAccount();
            password = userInfo.getUuPassword();
            percentage1_c5 = 1 + Double.parseDouble(userInfo.getScale1_c5());
            percentage2_c5 = 1 + Double.parseDouble(userInfo.getScale2_c5());
            percentage3_c5 = 1 + Double.parseDouble(userInfo.getScale3_c5());
            percentage4_c5 = 1 + Double.parseDouble(userInfo.getScale4_c5());
            percentage1_ig = 1 + Double.parseDouble(userInfo.getScale1_ig());
            percentage2_ig = 1 + Double.parseDouble(userInfo.getScale2_ig());
            percentage3_ig = 1 + Double.parseDouble(userInfo.getScale3_ig());
            percentage4_ig = 1 + Double.parseDouble(userInfo.getScale4_ig());
            this.jewelryIDList = jewelryIDList;
        }
        return object;
    }
    private Object initializeUUAccount(String userName,String password){

        Object object = TrackingApi.initializeUUAccount(userName,password);
        if(object instanceof String){
            String token = (String) object;
            this.token = token;
        }
        else {
            NotificationOfTracking notificationOfTracking = (NotificationOfTracking)object;
            createNotification(notificationOfTracking.getMessage(),
                    notificationOfTracking.getPoint(),
                    notificationOfTracking.getTitle(),
                    notificationOfTracking.getName(),
                    notificationOfTracking.getPrice());
        }
        return object;

    }

    private void start(){
        exceptionNumber = 0;
        try{
            for(int i = 0;i < jewelryIDList.size();i++){
                int point = i + 1;
                String jewelryID = jewelryIDList.get(i);
                Map<String,Object> map = TrackingApi.getC5Price(
                        jewelryID,
                        point,
                        point,
                        percentage1_c5,
                        percentage2_c5,
                        percentage3_c5,
                        percentage4_c5,
                        percentage1_ig,
                        percentage2_ig,
                        percentage3_ig,
                        percentage4_ig,
                        token,
                        roundNumber,
                        exceptionNumber,
                        warningExceptionNumber);
                exceptionNumber = (int) map.get("exceptionNumber");
                Object object = map.get("NotificationOfTracking");
                if(object != null){
                    if(object instanceof NotificationOfTracking){
                        NotificationOfTracking notificationOfTracking = (NotificationOfTracking)object;
                        createNotification(notificationOfTracking.getMessage(),
                                notificationOfTracking.getPoint(),
                                notificationOfTracking.getTitle(),
                                notificationOfTracking.getName(),
                                notificationOfTracking.getPrice());
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void createNotification(String message,int point,String title,String name,double price){
        String channelId;
        if(title.equals("c5饰品捡漏") || title.equals("igxe饰品捡漏") || point == 10002){
            channelId = highChannelId;
        }
        else {
            channelId = defaultChannelId;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                .setContentTitle(title + ":  " + roundNumber)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_capoo)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.welcome1))   //设置大图标
        if(title.equals("c5饰品捡漏") || title.equals("igxe饰品捡漏")){
            Intent intent = new Intent(this, NotificationClickReceiver.class);
            intent.putExtra("text",name);
            PendingIntent pi;

            Intent intent2 = new Intent(this, BlockJewelryReceiver.class);
            intent2.putExtra("jewelryID",jewelryIDList.get(point%10000 - 1));
            intent2.putExtra("point",point);
            intent2.putExtra("user",user);
            PendingIntent pi2;
//
//            Intent intent3 = new Intent(this,PriceNotificationReceiver.class);
//            intent3.putExtra("jewelryName",name);
//            intent3.putExtra("price",price);
//            intent3.putExtra("user",user);
//            PendingIntent pi3;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pi = PendingIntent.getBroadcast(TrackingService.this, point, intent, PendingIntent.FLAG_IMMUTABLE);
                pi2 = PendingIntent.getBroadcast(TrackingService.this,point,intent2,PendingIntent.FLAG_IMMUTABLE);
//                pi3 = PendingIntent.getBroadcast(TrackingService.this,point,intent3,PendingIntent.FLAG_IMMUTABLE);
            } else {
                pi = PendingIntent.getBroadcast(TrackingService.this,point, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                pi2 = PendingIntent.getBroadcast(TrackingService.this,point,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
//                pi3 = PendingIntent.getBroadcast(TrackingService.this,point,intent3,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
            builder.setContentIntent(pi);
            builder.addAction(R.mipmap.ic_capoo, "复制饰品名称", pi);
            builder.addAction(R.mipmap.ic_capoo, "拉黑饰品", pi2);
//            builder.addAction(R.mipmap.ic_capoo, "获取详情", pi3);
        }
        Notification notification = builder.build();
        notificationManager.notify(point, notification);

    }
    @Override
    public void onDestroy() {
        initData();
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        //获取Editor对象的引用
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //将获取过来的值放入文件
        editor.putString("tracking", "false");
        editor.commit();
        super.onDestroy();
        createNotification("服务已结束",0,"服务提示","服务已结束",0.00);
        notificationManager.cancelAll();
        // 移除悬浮窗 View
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}