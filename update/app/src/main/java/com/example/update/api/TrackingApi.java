package com.example.update.api;

import android.text.TextUtils;
import android.util.Log;

import com.example.update.entity.Jewelry;
import com.example.update.entity.NotificationOfTracking;
import com.example.update.entity.UserInfo;

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
import java.util.Set;

import redis.clients.jedis.Jedis;

public class TrackingApi {

    public static Object TrackingConnectRedis(String user) {
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return new NotificationOfTracking("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(user)) {
                    return new NotificationOfTracking("Redis服务运行失败\n当前用户不存在", 10002, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"scale_c5"))) {
                    return new NotificationOfTracking("Redis服务运行失败\n追踪比例信息出错，请联系管理员", 10002, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"scale_ig"))) {
                    return new NotificationOfTracking("Redis服务运行失败\n追踪比例信息出错，请联系管理员", 10002, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"jewelryIDList"))) {
                    return new NotificationOfTracking("Redis服务运行失败\n饰品列表出错，请联系管理员", 10002, "Redis服务", "Redis服务",0.00);
                }

                UserInfo userInfo = new UserInfo();
                String userName = jedis.hget(user, "username");
                String uuAccount = jedis.hget(user,"uuAccount");
                String password = jedis.hget(user, "uuPassword");
                String percentage1_c5 = jedis.hget(jedis.hget(user, "scale_c5"),"0-50");
                String percentage2_c5 = jedis.hget(jedis.hget(user, "scale_c5"),"50-100");
                String percentage3_c5 = jedis.hget(jedis.hget(user, "scale_c5"),"100-500");
                String percentage4_c5 = jedis.hget(jedis.hget(user, "scale_c5"),"500-");
                String percentage1_ig = jedis.hget(jedis.hget(user, "scale_ig"),"0-50");
                String percentage2_ig = jedis.hget(jedis.hget(user, "scale_ig"),"50-100");
                String percentage3_ig = jedis.hget(jedis.hget(user, "scale_ig"),"100-500");
                String percentage4_ig = jedis.hget(jedis.hget(user, "scale_ig"),"500-");

                userInfo.setUserName(userName);
                userInfo.setUuAccount(uuAccount);
                userInfo.setUuPassword(password);
                userInfo.setScale1_c5(percentage1_c5);
                userInfo.setScale2_c5(percentage2_c5);
                userInfo.setScale3_c5(percentage3_c5);
                userInfo.setScale4_c5(percentage4_c5);
                userInfo.setScale1_ig(percentage1_ig);
                userInfo.setScale2_ig(percentage2_ig);
                userInfo.setScale3_ig(percentage3_ig);
                userInfo.setScale4_ig(percentage4_ig);
                Set<String> sort = jedis.zrange(jedis.hget(user,"jewelryIDList"),0,-1);
                List<String> jewelryIDList = new ArrayList<>(sort);
                jewelryIDList.remove(0);
                Map<String,Object> map = new HashMap();
                map.put("userInfo",userInfo);
                map.put("jewelryIDList",jewelryIDList);
                return map;
            } else {
                return new NotificationOfTracking("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
            }
        } catch (Exception e) {
            return new NotificationOfTracking("Redis服务运行失败", 10002, "Redis服务", "Redis服务",0.00);
        }
    }

    public static Object initializeUUAccount(String userName,String password){

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
                String token = data.getString("Token");
                return token;
            }
            else {
                return new NotificationOfTracking("响应码错误: " + connection.getResponseCode(),10002,"UU账户初始化","响应码错误: " + connection.getResponseCode(),0.00);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new NotificationOfTracking("初始化UU账户失败",10002,"UU账户初始化","初始化UU账户失败",0.00);
        }
    }

    public static Map<String,Object> getC5Price(String jewelryID,
                                                 int point,
                                                 double  percentage1_c5,
                                                 double  percentage2_c5,
                                                 double  percentage3_c5,
                                                 double  percentage4_c5,
                                                double  percentage1_ig,
                                                double  percentage2_ig,
                                                double  percentage3_ig,
                                                double  percentage4_ig,
                                                 String token,
                                                 int roundNumber,
                                                 int exceptionNumber,
                                                 int warningExceptionNumber){
        String httpUrlStart = "https://www.c5game.com/napi/trade/steamtrade/sga/sell/v3/list?itemId=";
        String httpUrlEnd = "&delivery=&page=1&limit=10";
        String httpArg = jewelryID;
        return requestOfGetC5Price(
                httpUrlStart,
                httpArg,
                httpUrlEnd,
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
    }

    private static Map<String,Object> requestOfGetC5Price(String httpUrlStart,
                                                          String httpArg,
                                                          String httpUrlEnd,
                                                          int point,
                                                          double  percentage1_c5,
                                                          double  percentage2_c5,
                                                          double  percentage3_c5,
                                                          double  percentage4_c5,
                                                          double  percentage1_ig,
                                                          double  percentage2_ig,
                                                          double  percentage3_ig,
                                                          double  percentage4_ig,
                                                          String token,
                                                          int roundNumber,
                                                          int exceptionNumber,
                                                          int warningExceptionNumber) {

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
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",null);
                        return map;
                    }
                    return requestOfGetUUJewelryList(
                            name,
                            price,
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
                }
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= 10){
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking",
                            new NotificationOfTracking("响应码错误: " + connection.getResponseCode() + "  " + httpArg,10002,"获取c5饰品价格","响应码错误: " + connection.getResponseCode() + "  " + httpArg,0.00)
                           );
                    return map;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= 10){
                Map<String,Object> map = new HashMap<>();
                map.put("exceptionNumber",exceptionNumber);
                map.put("NotificationOfTracking",
                        new NotificationOfTracking("c5价格爬取失败",10002,"获取c5饰品价格","c5价格爬取失败",0.00)
                );
                return map;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("exceptionNumber",exceptionNumber);
        map.put("NotificationOfTracking", null);
        return map;
    }



    private static Map<String,Object> requestOfGetUUJewelryList(String name,
                                                                double price,
                                                                int point,
                                                                double  percentage1_c5,
                                                                double  percentage2_c5,
                                                                double  percentage3_c5,
                                                                double  percentage4_c5,
                                                                double  percentage1_ig,
                                                                double  percentage2_ig,
                                                                double  percentage3_ig,
                                                                double  percentage4_ig,
                                                                String token,
                                                                int roundNumber,
                                                                int exceptionNumber,
                                                                int warningExceptionNumber){

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
                    return compareUUBuyPrice(
                            jewelryName,
                            price,
                            jewelryID,
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
                }
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= warningExceptionNumber){
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking",
                            new NotificationOfTracking("响应码错误: " + connection.getResponseCode() + "  " + name,10002,"获取UU饰品id","响应码错误: " + connection.getResponseCode() + "  " + name,0.00)
                    );
                    return map;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= warningExceptionNumber){
                Map<String,Object> map = new HashMap<>();
                map.put("exceptionNumber",exceptionNumber);
                map.put("NotificationOfTracking",
                        new NotificationOfTracking("uu饰品id爬取失败",10002,"获取UU饰品id","uu饰品id爬取失败",0.00)
                );
                return map;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("exceptionNumber",exceptionNumber);
        map.put("NotificationOfTracking", null);
        return map;
    }

    private static Map<String,Object> compareUUBuyPrice(String jewelryName,
                                                        double price,
                                                        String jewelryID,
                                                        int point,
                                                        double  percentage1_c5,
                                                        double  percentage2_c5,
                                                        double  percentage3_c5,
                                                        double  percentage4_c5,
                                                        double  percentage1_ig,
                                                        double  percentage2_ig,
                                                        double  percentage3_ig,
                                                        double  percentage4_ig,
                                                        String token,
                                                        int roundNumber,
                                                        int exceptionNumber,
                                                        int warningExceptionNumber){

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
                    if(price < 50 && price >=0 && uuBuyPrice/price >= percentage1_c5){
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + price + "  " + uuBuyPrice,point + 20000,"c5饰品捡漏",jewelryName,price)
                        );
                        return map;
                    }
                    else if(price < 100 && price >= 50 && uuBuyPrice/price >= percentage2_c5) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + price + "  " + uuBuyPrice, point + 20000, "c5饰品捡漏", jewelryName,price)
                        );
                        return map;
                    }else if(price < 500 && price >= 100 && uuBuyPrice/price >= percentage3_c5){
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + price + "  " + uuBuyPrice, point + 20000, "c5饰品捡漏", jewelryName,price)
                        );
                        return map;
                    }else if(price >= 500 && uuBuyPrice/price >= percentage4_c5){
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + price + "  " + uuBuyPrice, point + 20000, "c5饰品捡漏", jewelryName,price)
                        );
                        return map;
                    }
                }
                return compareIgxePrice(
                        jewelryName,
                        uuBuyPrice,
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
            }
            else {
                exceptionNumber++;
                if(exceptionNumber >= warningExceptionNumber){
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking",
                            new NotificationOfTracking("响应码错误: " + connection.getResponseCode() + "  " + jewelryName,10002,"获取UU饰品价格","响应码错误: " + connection.getResponseCode() + "  " + jewelryName,0.00)
                    );
                    return map;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if(exceptionNumber >= warningExceptionNumber) {
                Map<String,Object> map = new HashMap<>();
                map.put("exceptionNumber",exceptionNumber);
                map.put("NotificationOfTracking",
                        new NotificationOfTracking("uu价格爬取失败", 10002, "获取UU饰品价格", "uu价格爬取失败",0.00)
                );
                return map;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("exceptionNumber",exceptionNumber);
        map.put("NotificationOfTracking", null);
        return map;
    }

    private static Map<String,Object> compareIgxePrice(String jewelryName,
                                                       double uuBuyPrice,
                                                       int point,
                                                       double  percentage1_c5,
                                                       double  percentage2_c5,
                                                       double  percentage3_c5,
                                                       double  percentage4_c5,
                                                       double  percentage1_ig,
                                                       double  percentage2_ig,
                                                       double  percentage3_ig,
                                                       double  percentage4_ig,
                                                       String token,
                                                       int roundNumber,
                                                       int exceptionNumber,
                                                       int warningExceptionNumber) throws UnsupportedEncodingException {
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
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking", null);
                    return map;
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
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking", null);
                    return map;
                }
                String priceStr = a_elementList.get(0).getElementsByClass("price").get(0).text();
                double igxePrice = Double.parseDouble(priceStr.substring(1));
                if(igxePrice != 0.00 && uuBuyPrice >= igxePrice){
                    if(igxePrice < 50 && igxePrice >=0 && uuBuyPrice/igxePrice >= percentage1_ig){
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + igxePrice + "  " + uuBuyPrice,point + 30000,"igxe饰品捡漏",jewelryName,igxePrice)
                                );
                        return map;
                    }
                    else if(igxePrice < 100 && igxePrice >= 50 && uuBuyPrice/igxePrice >= percentage2_ig) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + igxePrice + "  " + uuBuyPrice, point + 30000, "igxe饰品捡漏", jewelryName,igxePrice)
                        );
                        return map;

                    }
                    else if(igxePrice < 500 && igxePrice >= 100 && uuBuyPrice/igxePrice >= percentage3_ig) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + igxePrice + "  " + uuBuyPrice, point + 30000, "igxe饰品捡漏", jewelryName,igxePrice)
                        );
                        return map;

                    }else if(igxePrice >= 500 && uuBuyPrice/igxePrice >= percentage4_ig){
                        Map<String,Object> map = new HashMap<>();
                        map.put("exceptionNumber",exceptionNumber);
                        map.put("NotificationOfTracking",
                                new NotificationOfTracking(jewelryName + ": " + igxePrice + "  " + uuBuyPrice, point +30000, "igxe饰品捡漏", jewelryName,igxePrice)
                        );
                        return map;

                    }
                }
                exceptionNumber = 0;
            }
            else {
                exceptionNumber++;
                if (exceptionNumber >= warningExceptionNumber) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("exceptionNumber",exceptionNumber);
                    map.put("NotificationOfTracking",
                            new NotificationOfTracking("响应码错误: " + connection.getResponseCode() + "  " + jewelryName, 10002, "获取igxe饰品价格", "响应码错误: " + connection.getResponseCode() + "  " + jewelryName,0.00)
                    );
                    return map;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            exceptionNumber++;
            if (exceptionNumber >= warningExceptionNumber) {
                Map<String,Object> map = new HashMap<>();
                map.put("exceptionNumber",exceptionNumber);
                map.put("NotificationOfTracking",
                        new NotificationOfTracking("igxe价格爬取失败", 10002, "获取igxe饰品价格", "igxe价格爬取失败",0.00)
                );
                return map;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("exceptionNumber",exceptionNumber);
        map.put("NotificationOfTracking", null);
        return map;
    }

    public static Map<String,Object> getJewelryListBySearch(String user,String searchStr,String offset,int count,int addCount){
        List<Jewelry> jewelryList = new ArrayList<>();
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return null;
                }
                if (!jedis.exists(user)) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"scale_c5"))) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"scale_ig"))) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"jewelryIDList"))) {
                    return null;
                }

//                Set<String> sort = jedis.zrange(jedis.hget(user,"jewelryIDList"),0,-1);
//
//                List<String> jewelryIDList = new ArrayList<>(sort);
//                jewelryIDList.remove(0);
//                Log.e("jewelryIDList",String.valueOf(jewelryIDList.size()));
//                for(int i = 0;i < jewelryIDList.size();i++){
//                    String jewelryName = jedis.hget("jewelryMap",jewelryIDList.get(i));
//                    if(!jewelryName.contains(searchStr)){
//                        continue;
//                    }
//                    Jewelry jewelry = new Jewelry();
//                    String imageUrl = jedis.hget("jewelryImageUrlMap",jewelryIDList.get(i));
//
//                    if(jewelryName == null){
//                        jewelry.setJewelryName(jewelryIDList.get(i));
//                    }else {
//                        jewelry.setJewelryName(jewelryName);
//                    }
//                    jewelry.setBitmap(HomeApi.urlToBitmap(imageUrl));
//                    jewelry.setC5ID(jewelryIDList.get(i));
//                    jewelryList.add(jewelry);
//                }
                Set<String> sort;
                if(offset == "-"){
                     sort = jedis.zrangeByScore(jedis.hget(user,"jewelryIDList"),"-inf","+inf");

                }
                else {
                     sort = jedis.zrangeByScore(jedis.hget(user,"jewelryIDList"),"(" + jedis.zscore(jedis.hget(user,"jewelryIDList"),offset),"+inf");

                }
                List<String> jewelryIDList = new ArrayList<>(sort);
                jewelryIDList.remove("饰品ID");
                Log.e("jewelryIDList",String.valueOf(jewelryIDList.size()));
                int point = 0;
                for(int i = 0;i < jewelryIDList.size();i++){
                    String jewelryName = jedis.hget("jewelryMap",jewelryIDList.get(i));
                    if(!jewelryName.contains(searchStr)){
                        continue;
                    }
                    point++;
                    if(jewelryList.size() >= count){
                        continue;
                    }
                    Jewelry jewelry = new Jewelry();
                    String imageUrl = jedis.hget("jewelryImageUrlMap",jewelryIDList.get(i));
                    if(jewelryName == null){
                        jewelry.setJewelryName(jewelryIDList.get(i));
                    }else {
                        jewelry.setJewelryName(jewelryName);
                    }
                    jewelry.setBitmap(HomeApi.urlToBitmap(imageUrl));
                    jewelry.setC5ID(jewelryIDList.get(i));
                    jewelryList.add(jewelry);
                }

                Map<String,Object> map = new HashMap<>();
                map.put("number",point + addCount);
                map.put("jewelryList",jewelryList);
                return map;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    public static List<Jewelry> getBlockJewelryListBySearch(String user,String searchStr){
        List<Jewelry> jewelryList = new ArrayList<>();
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return null;
                }
                if (!jedis.exists(user)) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"scale_c5"))) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"scale_ig"))) {
                    return null;
                }
                if (!jedis.exists(jedis.hget(user,"blockJewelryIDList"))) {
                    return null;
                }

                Set<String> sort = jedis.zrange(jedis.hget(user,"blockJewelryIDList"),0,-1);
                List<String> jewelryIDList = new ArrayList<>(sort);
                jewelryIDList.remove(0);
                Log.e("blockJewelryIDList",String.valueOf(jewelryIDList.size()));
                for(int i = 0;i < jewelryIDList.size();i++){

                    String jewelryName = jedis.hget("jewelryMap",jewelryIDList.get(i));

                    if(!jewelryName.contains(searchStr)){
                        continue;
                    }
                    Jewelry jewelry = new Jewelry();
                    String imageUrl = jedis.hget("jewelryImageUrlMap",jewelryIDList.get(i));
                    if(jewelryName == null){
                        jewelry.setJewelryName(jewelryIDList.get(i));
                    }else {
                        jewelry.setJewelryName(jewelryName);
                    }
                    jewelry.setBitmap(HomeApi.urlToBitmap(imageUrl));
                    jewelry.setC5ID(jewelryIDList.get(i));
                    jewelryList.add(jewelry);
                }
                return jewelryList;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Object blockJewelry(String user,String jewelryID){
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);

                }
                if (!jedis.exists(user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"jewelryIDList"))) {
                    return new NotificationOfTracking("饰品列表出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);


                }
                if (!jedis.exists(jedis.hget(user,"blockJewelryIDList"))) {
                    return new NotificationOfTracking("饰品黑名单出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);

                }
                if(jedis.zscore(jedis.hget(user,"jewelryIDList"),jewelryID) == null){
                    return new NotificationOfTracking("饰品不存在于饰品列表", 10001, "Redis服务", "Redis服务",0.00);
                }
                if(jedis.zscore(jedis.hget(user,"blockJewelryIDList"),jewelryID) != null){
                    return new NotificationOfTracking("饰品已存在于饰品黑名单", 10001, "Redis服务", "Redis服务",0.00);

                }
                jedis.zrem(jedis.hget(user,"jewelryIDList"),jewelryID);
                Set<String> sort = jedis.zrevrange(jedis.hget(user,"blockJewelryIDList"),0,-1);
                double score = jedis.zscore(jedis.hget(user,"blockJewelryIDList"),sort.iterator().next());
                jedis.zadd(jedis.hget(user,"blockJewelryIDList"),score + 1,jewelryID);
                return "success";
//                createNotification("Redis服务运行成功\n饰品已拉黑 " + user, 10001, "Redis服务", "Redis服务");
            } else {
                return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);

            }
        } catch (Exception e) {
            return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);
        }

    }
    public static Object deleteJewelry(String user,String jewelryID){
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);

                }
                if (!jedis.exists(user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"jewelryIDList"))) {
                    return new NotificationOfTracking("饰品列表出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);


                }
                if (!jedis.exists(jedis.hget(user,"blockJewelryIDList"))) {
                    return new NotificationOfTracking("饰品黑名单出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);

                }
                if(jedis.zscore(jedis.hget(user,"jewelryIDList"),jewelryID) == null){
                    return new NotificationOfTracking("饰品不存在于饰品列表", 10001, "Redis服务", "Redis服务",0.00);
                }
                jedis.zrem(jedis.hget(user,"jewelryIDList"),jewelryID);
                return "success";
//                createNotification("Redis服务运行成功\n饰品已拉黑 " + user, 10001, "Redis服务", "Redis服务");
            } else {
                return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);

            }
        } catch (Exception e) {
            return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);
        }

    }
    public static Object cancelBlockJewelry(String user,String jewelryID) {
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务", 0.00);

                }
                if (!jedis.exists(user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务", 0.00);
                }
                if (!jedis.exists(jedis.hget(user, "jewelryIDList"))) {
                    return new NotificationOfTracking("饰品列表出错，请联系管理员", 10001, "Redis服务", "Redis服务", 0.00);


                }
                if (!jedis.exists(jedis.hget(user, "blockJewelryIDList"))) {
                    return new NotificationOfTracking("饰品黑名单出错，请联系管理员", 10001, "Redis服务", "Redis服务", 0.00);

                }
                if (jedis.zscore(jedis.hget(user, "jewelryIDList"), jewelryID) != null) {
                    return new NotificationOfTracking("饰品已存在于饰品列表", 10001, "Redis服务", "Redis服务", 0.00);
                }
                if (jedis.zscore(jedis.hget(user, "blockJewelryIDList"), jewelryID) == null) {
                    return new NotificationOfTracking("饰品不存在于饰品黑名单", 10001, "Redis服务", "Redis服务", 0.00);

                }
                jedis.zrem(jedis.hget(user, "blockJewelryIDList"), jewelryID);
                Set<String> sort = jedis.zrevrange(jedis.hget(user, "jewelryIDList"), 0, -1);
                double score = jedis.zscore(jedis.hget(user, "jewelryIDList"), sort.iterator().next());
                jedis.zadd(jedis.hget(user, "jewelryIDList"), score + 1, jewelryID);
                return "success";
//                createNotification("Redis服务运行成功\n饰品已拉黑 " + user, 10001, "Redis服务", "Redis服务");
            } else {
                return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务", 0.00);

            }
        } catch (Exception e) {
            return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务", 0.00);
        }
    }
    public static Object addJewelry(String user,Jewelry jewelry){
        String jewelryID = jewelry.getC5ID();
        String jewelryName = jewelry.getJewelryName();
        String imageUrl = jewelry.getImageUrl();
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);

                }
                if (!jedis.exists(user)) {
                    return new NotificationOfTracking("当前用户不存在", 10001, "Redis服务", "Redis服务",0.00);
                }
                if (!jedis.exists(jedis.hget(user,"jewelryIDList"))) {
                    return new NotificationOfTracking("饰品列表出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);


                }
                if (!jedis.exists(jedis.hget(user,"blockJewelryIDList"))) {
                    return new NotificationOfTracking("饰品黑名单出错，请联系管理员", 10001, "Redis服务", "Redis服务",0.00);

                }
                if(jedis.zscore(jedis.hget(user,"jewelryIDList"),jewelryID) != null){
                    return new NotificationOfTracking("饰品已存在于饰品列表", 10001, "Redis服务", "Redis服务",0.00);
                }
                if(jedis.zscore(jedis.hget(user,"blockJewelryIDList"),jewelryID) != null){
                    return new NotificationOfTracking("饰品已存在于饰品黑名单", 10001, "Redis服务", "Redis服务",0.00);
                }
                Set<String> sort = jedis.zrevrange(jedis.hget(user,"jewelryIDList"),0,-1);
                double score = jedis.zscore(jedis.hget(user,"jewelryIDList"),sort.iterator().next());
                jedis.zadd(jedis.hget(user,"jewelryIDList"),score + 1,jewelryID);
                jedis.hsetnx("jewelryMap",jewelryID,jewelryName);
                jedis.hsetnx("jewelryImageUrlMap",jewelryID,imageUrl);
                return "success";
//                createNotification("Redis服务运行成功\n饰品已拉黑 " + user, 10001, "Redis服务", "Redis服务");
            } else {
                return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);

            }
        } catch (Exception e) {
            return new NotificationOfTracking("Redis服务运行失败", 10001, "Redis服务", "Redis服务",0.00);
        }

    }
}
