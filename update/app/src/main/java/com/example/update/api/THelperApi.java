package com.example.update.api;

import com.example.update.entity.OrdersItem;
import com.example.update.entity.OrdersTimeItem;
import com.example.update.entity.Rank_jewelry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class THelperApi {
    public static long getDataTime(int year,int month,int day,String TimingOrder){
        int hour = 16;
        if(TimingOrder.equals("夏令时")){
            hour = 15;
        }
        LocalDateTime localDateTime = LocalDateTime.of(year,month,day,hour,0,0);
        long seconds = localDateTime.toEpochSecond(ZoneOffset.UTC);
        return seconds;
    }

    public static String getToken(String phone,String password){
        String token = "";
        String httpUrl = "https://www.c5game.com/napi/trade/user/v3/login";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate,br");
            connection.setRequestProperty("Accept", "application/json, text/plain, */*");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
            connection.setRequestProperty("Host", "www.c5game.com");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.setRequestProperty("Referer", "https://www.c5game.com/");
            connection.setRequestProperty("Origin", "https://www.c5game.com");
            connection.setRequestProperty("Device", "1");
            connection.setRequestProperty("Platform", "2");
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中

            Map subscribeMessage = new HashMap<String, Object>();
            subscribeMessage.put("loginType", 1);
            subscribeMessage.put("password", password);
            subscribeMessage.put("rememberMe", 1);
            subscribeMessage.put("username", phone);
            subscribeMessage.put("verifyCode", "");
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
                if(!obj.getBoolean("success")){
                    return token;
                }
                JSONObject items = obj.getJSONObject("data");
                String refreshToken = items.getString("refreshToken");
                return refreshToken;
            }
            return token;

        } catch (Exception e) {
            e.printStackTrace();
            return token;
        }
    }
    public static Map<String,OrdersItem> getSellList(long start, long end, int number, String token){
        Map<String,OrdersItem> map = new HashMap<>();
        String httpUrl = "https://www.c5game.com/napi/trade/steamtrade/sts/order/v3/seller-list?page=1&limit=" + number +"&appId=730&type=&status=3";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate,br");
            connection.setRequestProperty("Accept", "application/json, text/plain, */*");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
            connection.setRequestProperty("Host", "www.c5game.com");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.setRequestProperty("Referer", "https://www.c5game.com/user-center/sell?actag=2");
            connection.setRequestProperty("Access_token", token);
            connection.setRequestProperty("Device", "1");
            connection.setRequestProperty("Platform", "2");
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
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
                if(!obj.getBoolean("success")){
                    return map;
                }
                JSONObject items = obj.getJSONObject("data");
                JSONArray list = items.getJSONArray("list");
                for(int i = 0;i < list.length();i++){
                    JSONObject jsonObject = list.getJSONObject(i);
                    long timestamp = jsonObject.getLong("orderCreateTime") * 1000;
                    if(timestamp > end || timestamp < start){
                        continue;
                    }
                    String statusName = jsonObject.getString("statusName");
                    if(!statusName.equals("出售成功")){
                        continue;
                    }
                    String jewelryName = (jsonObject.getJSONArray("orderAssetList")).getJSONObject(0).getString("name");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //将时间转化为类似 2020-02-13 16:01:30 格式的字符串
                    String date = sdf.format(new Date(timestamp));

                    double price = jsonObject.getDouble("actualAmount");
                    OrdersTimeItem ordersTimeItem = new OrdersTimeItem();
                    ordersTimeItem.setDate(date);
                    ordersTimeItem.setPrice(price);
                    if(!map.containsKey(jewelryName)){
                        OrdersItem ordersItem = new OrdersItem();
                        ordersItem.add(ordersTimeItem);
                        map.put(jewelryName,ordersItem);
                    }
                    else {
                        OrdersItem ordersItem = map.get(jewelryName);
                        ordersItem.add(ordersTimeItem);
                    }
                }
                return map;
            }
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
    }
}
