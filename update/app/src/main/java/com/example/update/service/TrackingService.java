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
import com.example.update.receiver.NotificationClickReceiver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackingService extends Service {
    private  String [] illegalList;

    private String user;

    private String userName;

    private String password;

    private double  percentage1;

    private double  percentage2;

    private double  percentage3;

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
        percentage1 = 1.00;
        percentage2 = 1.00;
        percentage3 = 1.00;
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
//        try {
//            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
//            //如果 Redis 服务设置了密码，需要添加下面这行代码
//            jedis.auth("Lenshanshan521!");
//            //调用ping()方法查看 Redis 服务是否运行
//            if (jedis.ping().equals("PONG")) {
//                if (!jedis.sismember("user", user)) {
//                    createNotification("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
//                    return;
//                }
//                if (!jedis.exists(user)) {
//                    createNotification("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
//                    return;
//                }
//                if (!jedis.exists(jedis.hget(user,"jewelryMap"))) {
//                    createNotification("Redis服务运行失败\n饰品列表出错，请联系管理员", 10002, "Redis服务", "Redis服务",0.00);
//                    return;
//                }
//                userName = jedis.hget(user, "username");
//                password = jedis.hget(user, "password");
//                percentage1 = 1 + Double.parseDouble(jedis.hget(user,"0-50"));
//                percentage2 = 1 + Double.parseDouble(jedis.hget(user,"50-100"));
//                percentage3 = 1 + Double.parseDouble(jedis.hget(user,"100-"));
//                jewelryIDList = new ArrayList<>(jedis.hkeys(jedis.hget(user,"jewelryMap")));
//
//            } else {
//                createNotification("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
//            }
//        } catch (Exception e) {
//            createNotification("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
//        }
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
            percentage1 = 1 + Double.parseDouble(userInfo.getScale1());
            percentage2 = 1 + Double.parseDouble(userInfo.getScale2());
            percentage3 = 1 + Double.parseDouble(userInfo.getScale3());
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
//        String httpUrl = "https://api.youpin898.com/api/user/Auth/PwdSignIn";
//        BufferedReader reader = null;
//        String result = null;
//        StringBuffer sbf = new StringBuffer();
//
//        try {
//            URL url = new URL(httpUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
//            connection.setRequestProperty("Referer", "https://www.youpin898.com/");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.connect();
//
//            /* 4. 处理输入输出 */
//            // 写入参数到请求中
//            Map subscribeMessage = new HashMap<String, Object>();
//            subscribeMessage.put("UserName", userName);
//            subscribeMessage.put("UserPwd", password);
//            subscribeMessage.put("Code", "");
//            subscribeMessage.put("SessionId", "");
//            JSONObject subscribeMessageJson = new JSONObject(subscribeMessage);
//            String params = subscribeMessageJson.toString();
//            OutputStream out = connection.getOutputStream();
//            out.write(params.getBytes());
//            out.flush();
//            out.close();
//            if(connection.getResponseCode() == 200) {
//                InputStream is = connection.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                String strRead = null;
//                while ((strRead = reader.readLine()) != null) {
//                    sbf.append(strRead);
//                    sbf.append("\r\n");
//                }
//                is.close();
//                reader.close();
//                result = sbf.toString();
//                JSONObject obj = new JSONObject(result);
//                JSONObject data = obj.getJSONObject("Data");
//                token = data.getString("Token");
////                createNotification("初始化UU账户成功",0,"UU账户初始化","初始化UU账户成功");
//            }
//            else {
//                createNotification("响应码错误: " + connection.getResponseCode(),10002,"UU账户初始化","响应码错误: " + connection.getResponseCode(),0.00);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            createNotification("初始化UU账户失败",10002,"UU账户初始化","初始化UU账户失败",0.00);
//        }
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
                        percentage1,
                        percentage2,
                        percentage3,
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

//            Intent intent2 = new Intent(this,BlockJewelryReceiver.class);
//            intent2.putExtra("jewelryID",jewelryIDList.get(point - 1));
//            intent2.putExtra("jewelryName",name);
//            intent2.putExtra("point",point);
//            intent2.putExtra("user",user);
//            PendingIntent pi2;
//
//            Intent intent3 = new Intent(this,PriceNotificationReceiver.class);
//            intent3.putExtra("jewelryName",name);
//            intent3.putExtra("price",price);
//            intent3.putExtra("user",user);
//            PendingIntent pi3;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pi = PendingIntent.getBroadcast(TrackingService.this, point, intent, PendingIntent.FLAG_IMMUTABLE);
//                pi2 = PendingIntent.getBroadcast(TrackingService.this,point,intent2,PendingIntent.FLAG_IMMUTABLE);
//                pi3 = PendingIntent.getBroadcast(TrackingService.this,point,intent3,PendingIntent.FLAG_IMMUTABLE);
            } else {
                pi = PendingIntent.getBroadcast(TrackingService.this, point, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                pi2 = PendingIntent.getBroadcast(TrackingService.this,point,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
//                pi3 = PendingIntent.getBroadcast(TrackingService.this,point,intent3,PendingIntent.FLAG_UPDATE_CURRENT);
            }
            builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
            builder.setContentIntent(pi);
            builder.addAction(R.mipmap.ic_capoo, "复制饰品名称", pi);
//            builder.addAction(R.mipmap.ic_capoo, "拉黑饰品", pi2);
//            builder.addAction(R.mipmap.ic_capoo, "获取详情", pi3);
        }
        Notification notification = builder.build();
        notificationManager.notify(point, notification);

    }
    private String padLeft(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length) {
            sb.append(" ");
        }

        return sb.toString();
    }

    @Override
    public void onDestroy() {
        initData();
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