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
import com.example.update.receiver.NotificationClickReceiver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class TrackingService extends Service {
    private  String [] illegalList;

    private String user;

    private String userName;

    private String password;
    private String token;
    private  double lowPrice;
    private  double highPrice;

    private double profit;

    private double  percentage1;

    private double  percentage2;

    private double  percentage3;

    private int roundNumber;

    private int exceptionNumber;

    private int warningExceptionNumber;
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
        lowPrice = 10.00;
        highPrice = 1000.00;

        profit = 1.005;
        roundNumber = 0;
        exceptionNumber = 0;
        warningExceptionNumber = 5;
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
                    connectRedis();
                    roundNumber++;
                    initializeUUAccount(userName,password);
                    start();
                }
            }
        }).start();
    }
    private void connectRedis() {
        jewelryIDList.clear();
//        InputStream is = null;
//        BufferedReader br = null;
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    createNotification("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
                    return;
                }
                if (!jedis.exists(user)) {
                    createNotification("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
                    return;
                }
                if (!jedis.exists(jedis.hget(user,"jewelryMap"))) {
                    createNotification("Redis服务运行失败\n饰品列表出错，请联系管理员", 10002, "Redis服务", "Redis服务",0.00);
                    return;
                }
                userName = jedis.hget(user, "username");
                password = jedis.hget(user, "password");
                percentage1 = 1 + Double.parseDouble(jedis.hget(user,"0-50"));
                percentage2 = 1 + Double.parseDouble(jedis.hget(user,"50-100"));
                percentage3 = 1 + Double.parseDouble(jedis.hget(user,"100-"));
                jewelryIDList = new ArrayList<>(jedis.hkeys(jedis.hget(user,"jewelryMap")));
//                jewelryIDList = new ArrayList<>(jedis.smembers(jedis.hget(user,"jewelryIDSet")));

//                createNotification("Redis服务运行成功\n当前用户为: " + user, 10001, "Redis服务", "Redis服务");
            } else {
                createNotification("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
            }
        } catch (Exception e) {
            createNotification("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
        }
    }

    private void start(){
        exceptionNumber = 0;
        try{
            for(int i = 0;i < jewelryIDList.size();i++){
                int point = i + 1;
                String jewelryID = jewelryIDList.get(i);
                getC5Price(jewelryID,point);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initializeUUAccount(String userName,String password){

        String httpUrl = "https://api.youpin898.com/api/user/Auth/PwdSignIn";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Referer", "https://www.youpin898.com/");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            /* 4. 处理输入输出 */
            // 写入参数到请求中
            Map subscribeMessage = new HashMap<String, Object>();
            subscribeMessage.put("UserName", userName);
            subscribeMessage.put("UserPwd", password);
            subscribeMessage.put("Code", "");
            subscribeMessage.put("SessionId", "");
            JSONObject subscribeMessageJson = new JSONObject(subscribeMessage);
            String params = subscribeMessageJson.toString();
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();
            if(connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                is.close();
                reader.close();
                result = sbf.toString();
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.getJSONObject("Data");
                token = data.getString("Token");
//                createNotification("初始化UU账户成功",0,"UU账户初始化","初始化UU账户成功");
            }
            else {
                createNotification("响应码错误: " + connection.getResponseCode(),10002,"UU账户初始化","响应码错误: " + connection.getResponseCode(),0.00);
            }

        } catch (Exception e) {
            e.printStackTrace();
            createNotification("初始化UU账户失败",10002,"UU账户初始化","初始化UU账户失败",0.00);
        }
    }
    private void getC5Price(String jewelryID,int point){
        String httpUrlStart = "https://www.c5game.com/napi/trade/steamtrade/sga/sell/v3/list?itemId=";
        String httpUrlEnd = "&delivery=&page=1&limit=10";
        String httpArg = jewelryID;
        requestOfGetC5Price(httpUrlStart,httpArg,httpUrlEnd,point);
    }

    private void requestOfGetC5Price(String httpUrlStart, String httpArg, String httpUrlEnd,int point) {

        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = httpUrlStart + httpArg + httpUrlEnd;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Cookie", "NC5_deviceId=169050548948190205; NC5_version_id=new_web_grey; _bl_uid=dOlRek6nlI9vh1baRqs8h8sk14mL; NC5_uid=1000189316; aliyungf_tc=a609d9540c8fa6321d5d7d286c9c200a03f0462c8e28eb7d284cdbc7bb35efa5; alicfw=1032882838%7C2016287211%7C1328233530%7C1328232805; alicfw_gfver=v1.200309.1; NC5_crossAccessToken=undefined; noticeList=%5B%5D; hideNotice=0; Hm_lvt_86084b1bece3626cd94deede7ecf31a8=1694142048,1694396720,1694482744,1694573477; Hm_lpvt_86084b1bece3626cd94deede7ecf31a8=1694576615");
            connection.connect();
            if(connection.getResponseCode() == 200){
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                is.close();
                reader.close();
                result = sbf.toString();
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.getJSONObject("data");
                JSONArray list = data.getJSONArray("list");
                if(list.length() != 0){
                    JSONObject item = list.getJSONObject(0);
                    String name = item.getString("itemName");
                    double price = Double.parseDouble(item.getString("cnyPrice"));
                    String statTrak = "StatTrak";
                    String souvenir = "纪念品";
                    String misicBox = "花脸";
                    String out = "★";
                    String out1 = "伽玛多普勒";
                    String out2 = "龍王";
                    if(name.contains(statTrak) || name.contains(souvenir) || name.contains(misicBox) || name.contains(out) || name.contains(out1) || name.contains(out2)){
                        return;
                    }
                    if(price < lowPrice || price > highPrice){
                        return;
                    }

                    requestOfGetUUJewelryList(name,price,point);
                }
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= 10){
                    createNotification("响应码错误: " + connection.getResponseCode() + "  " + httpArg,10002,"获取c5饰品价格","响应码错误: " + connection.getResponseCode() + "  " + httpArg,0.00);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= 10){
                createNotification("c5价格爬取失败",10002,"获取c5饰品价格","c5价格爬取失败",0.00);
            }
        }
    }



    private void requestOfGetUUJewelryList(String name,double price,int point){

        String httpUrl = "https://api.youpin898.com/api/homepage/search/match";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            /* 4. 处理输入输出 */
            // 写入参数到请求中
            Map subscribeMessage = new HashMap<String, Object>();
            subscribeMessage.put("keyWords", name);
            subscribeMessage.put("listType", "10");

            JSONObject subscribeMessageJson = new JSONObject(subscribeMessage);
            String params = subscribeMessageJson.toString();
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();
            if(connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                is.close();
                reader.close();
                result = sbf.toString();
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.getJSONObject("Data");
                JSONArray items = data.getJSONArray("dataList");
                for(int i = 0;i < items.length();i++){
                    JSONObject item = items.getJSONObject(i);
                    String jewelryID = item.getString("templateId");
                    String jewelryName = item.getString("commodityName");
                    if(!jewelryName.equals(name)){
                        continue;
                    }
                    compareUUBuyPrice(jewelryName, price, jewelryID, point);
                }
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= warningExceptionNumber){
                    createNotification("响应码错误: " + connection.getResponseCode() + "  " + name,10002,"获取UU饰品id","响应码错误: " + connection.getResponseCode() + "  " + name,0.00);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= warningExceptionNumber){
                createNotification("uu饰品id爬取失败",10002,"获取UU饰品id","uu饰品id爬取失败",0.00);

            }
        }

    }

    private void compareUUBuyPrice(String jewelryName,double price,String jewelryID,int point){

        String httpUrl = "https://api.youpin898.com/api/youpin/commodity/purchase/find";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            /* 4. 处理输入输出 */
            // 写入参数到请求中
            Map subscribeMessage = new HashMap<String, Object>();
            subscribeMessage.put("pageIndex", 1);
            subscribeMessage.put("pageSize", 50);
            subscribeMessage.put("templateId", jewelryID);

            JSONObject subscribeMessageJson = new JSONObject(subscribeMessage);
            String params = subscribeMessageJson.toString();
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();
            if(connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                is.close();
                reader.close();
                result = sbf.toString();
                JSONObject obj = new JSONObject(result);
                double uuBuyPrice = 1.00;
                JSONObject data = obj.getJSONObject("data");
                JSONArray priceList = data.getJSONArray("response");
                if(priceList.length() != 0){
                    uuBuyPrice = priceList.getJSONObject(0).getDouble("unitPrice")/100.00;
                }
                if (price <= uuBuyPrice){
                    if(price < 50 && price >=0 && uuBuyPrice/price >= percentage1){
                        createNotification(jewelryName + ": " + price + "  " + uuBuyPrice,point,"c5饰品捡漏",jewelryName,price);
                    }
                    else if(price < 100 && price >= 50 && uuBuyPrice/price >= percentage2) {
                        createNotification(jewelryName + ": " + price + "  " + uuBuyPrice, point, "c5饰品捡漏", jewelryName,price);
                    }else if(price >= 100 && uuBuyPrice/price >= percentage3){
                        createNotification(jewelryName + ": " + price + "  " + uuBuyPrice, point, "c5饰品捡漏", jewelryName,price);
                    }
                }
                compareIgxePrice(jewelryName, uuBuyPrice, point);
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= warningExceptionNumber){
                    createNotification("响应码错误: " + connection.getResponseCode() + "  " + jewelryName,10002,"获取UU饰品价格","响应码错误: " + connection.getResponseCode() + "  " + jewelryName,0.00);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= warningExceptionNumber) {
                createNotification("uu价格爬取失败", 10002, "获取UU饰品价格", "uu价格爬取失败",0.00);
            }
        }


    }

    private void compareIgxePrice(String jewelryName,double uuBuyPrice,int point) throws UnsupportedEncodingException {


        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "https://www.igxe.cn/market/csgo?keyword=" + URLEncoder.encode(jewelryName, "UTF-8");

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Cookie", "NC5_deviceId=169050548948190205; NC5_version_id=new_web_grey; _bl_uid=dOlRek6nlI9vh1baRqs8h8sk14mL; NC5_uid=1000189316; aliyungf_tc=a609d9540c8fa6321d5d7d286c9c200a03f0462c8e28eb7d284cdbc7bb35efa5; alicfw=1032882838%7C2016287211%7C1328233530%7C1328232805; alicfw_gfver=v1.200309.1; NC5_crossAccessToken=undefined; noticeList=%5B%5D; hideNotice=0; Hm_lvt_86084b1bece3626cd94deede7ecf31a8=1694142048,1694396720,1694482744,1694573477; Hm_lpvt_86084b1bece3626cd94deede7ecf31a8=1694576615");
            connection.connect();
            if(connection.getResponseCode() == 200){
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                is.close();
                reader.close();
                result = sbf.toString();
                Document doc = Jsoup.parse(result);
                Elements data_list = doc.getElementsByClass("list list");
                if(data_list.size() != 1){
                    return;
                }
                Element data = data_list.get(0);
                Elements a_list = data.getElementsByTag("a");
                List<Element> a_elementList = new ArrayList<>();

                for(int i = 0;i < a_list.size();i++){
                    Element a = a_list.get(i);
                    String name = a.getElementsByClass("name").get(0).text();
                    if(name.equals(jewelryName)){
                        a_elementList.add(a);
                    }
                }
                if(a_elementList.size() != 1){
                    return;
                }
                String priceStr = a_elementList.get(0).getElementsByClass("price").get(0).text();
                double igxePrice = Double.parseDouble(priceStr.substring(1));
                if(igxePrice != 0.00 && uuBuyPrice >= igxePrice){
                    if(igxePrice < 50 && igxePrice >=0 && uuBuyPrice/igxePrice >= percentage1){
                        createNotification(jewelryName + ": " + igxePrice + "  " + uuBuyPrice,point,"igxe饰品捡漏",jewelryName,igxePrice);
                    }
                    else if(igxePrice < 100 && igxePrice >= 50 && uuBuyPrice/igxePrice >= percentage2) {
                        createNotification(jewelryName + ": " + igxePrice + "  " + uuBuyPrice, point, "igxe饰品捡漏", jewelryName,igxePrice);
                    }else if(igxePrice >= 100 && uuBuyPrice/igxePrice >= percentage3){
                        createNotification(jewelryName + ": " + igxePrice + "  " + uuBuyPrice, point, "igxe饰品捡漏", jewelryName,igxePrice);
                    }
                }
                exceptionNumber = 0;
            }
            else {
                exceptionNumber++;
                if (exceptionNumber >= warningExceptionNumber) {
                    createNotification("响应码错误: " + connection.getResponseCode() + "  " + jewelryName, 10002, "获取igxe饰品价格", "响应码错误: " + connection.getResponseCode() + "  " + jewelryName,0.00);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if (exceptionNumber >= warningExceptionNumber) {
                createNotification("igxe价格爬取失败", 10002, "获取igxe饰品价格", "igxe价格爬取失败",0.00);
            }
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