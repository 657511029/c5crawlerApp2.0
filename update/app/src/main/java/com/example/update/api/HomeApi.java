package com.example.update.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.example.update.entity.Jewelry;
import com.example.update.entity.Rank_jewelry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class HomeApi {
    public static List<Jewelry> getJewelryList(String keyword) throws UnsupportedEncodingException {
        List<Jewelry> jewelryList = new ArrayList<>();
        String httpUrlStart = "https://www.c5game.com/napi/trade/search/v2/items/730/search?limit=100&appId=730&page=1&sort=0&marketKeyword=";
        String httpArg = URLEncoder.encode(keyword, "UTF-8").replace("%20", "+").replace("%28", "(").replace("%29", ")");
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = httpUrlStart + httpArg + "&keyword=" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60");
            connection.setRequestProperty("Cookie", "NC5_deviceId=169050548948190205; NC5_version_id=new_web_grey; _bl_uid=dOlRek6nlI9vh1baRqs8h8sk14mL; NC5_uid=1000189316; aliyungf_tc=a609d9540c8fa6321d5d7d286c9c200a03f0462c8e28eb7d284cdbc7bb35efa5; alicfw=1032882838%7C2016287211%7C1328233530%7C1328232805; alicfw_gfver=v1.200309.1; NC5_crossAccessToken=undefined; noticeList=%5B%5D; hideNotice=0; Hm_lvt_86084b1bece3626cd94deede7ecf31a8=1694142048,1694396720,1694482744,1694573477; Hm_lpvt_86084b1bece3626cd94deede7ecf31a8=1694576615");
            connection.connect();
            if (connection.getResponseCode() == 200) {
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
                for(int i = 0;i < list.length();i++){
                    JSONObject item = list.getJSONObject(i);
                    String itemName = item.getString("itemName");
                    String itemId = item.getString("itemId");
                    String imageUrl = item.getString("imageUrl");
                    String shortName = item.getString("shortName");
                    JSONObject itemInfo = item.getJSONObject("itemInfo");
                    String exteriorColor = itemInfo.getString("exteriorColor");

                    String exteriorName = itemInfo.getString("exteriorName");

                    String qualityColor = itemInfo.getString("qualityColor");

                    String qualityName = itemInfo.getString("qualityName");
                    Jewelry jewelry = new Jewelry();
                    jewelry.setC5ID(itemId);
                    jewelry.setJewelryName(itemName);
                    jewelry.setImageUrl(imageUrl);
                    jewelry.setShortName(shortName);
                    jewelry.setExteriorColor(exteriorColor);
                    jewelry.setExteriorName(exteriorName);
                    jewelry.setQualityColor(qualityColor);
                    jewelry.setQualityName(qualityName);
                    jewelry.setBitmap(urlToBitmap(imageUrl));
                    jewelryList.add(jewelry);
                }
            }
            return jewelryList;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error",e.toString());
            return jewelryList;
        }
    }

    public static Bitmap urlToBitmap(String urlStr) throws IOException {
        if(TextUtils.isEmpty(urlStr)){
            return null;
        }
        URL url = new URL(urlStr);
        return BitmapFactory.decodeStream(url.openStream());
    }

    public static List<Rank_jewelry> getRankJewelryList(Context context,String platform,String type,String sort,String mode,String day) throws ScriptException, JSONException, IOException, NoSuchMethodException, NoSuchAlgorithmException {

        List<Rank_jewelry> rankJewelryList = new ArrayList<>();
        String httpUrl = "https://www.muxicat.com/api/ranking/item";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");
            connection.setRequestProperty("Referer", "https://www.muxicat.com/csgo/ranking");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String timestamp = String.valueOf(new Date().getTime());
            connection.setRequestProperty("Timestamp", timestamp);
            String sign = getSign(timestamp);
            connection.setRequestProperty("Sign", sign);
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
            Map subscribeMessage = new HashMap<String, Object>();
            subscribeMessage.put("platfrom", platform);
            subscribeMessage.put("type", type);
            subscribeMessage.put("sort", sort);
            subscribeMessage.put("mode", mode);
            subscribeMessage.put("day", day);

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
                if(obj.getInt("code") != 0){
                    return rankJewelryList;
                }
                JSONArray items = obj.getJSONArray("data");
                for(int i = 0;i < items.length();i++){
                    JSONObject item = items.getJSONObject(i);
                    JSONObject full = item.getJSONObject("full");
                    JSONObject info = item.getJSONObject("info");
                    Rank_jewelry rankJewelry = new Rank_jewelry();

                    rankJewelry.setJewelryName(info.getString("name"));

                    rankJewelry.setC5ID(info.getString("c5game_id"));

                    rankJewelry.setImageUrl(info.getString("icon_url"));
                    rankJewelry.setBitmap(urlToBitmap(info.getString("icon_url")));

                    rankJewelry.setColor(info.getString("color"));

                    rankJewelry.setPrice(full.getDouble("min_sell"));

                    rankJewelry.setBalance(full.getString("balance"));

                    rankJewelry.setBuy_number(full.getInt("buy_count"));

                    rankJewelry.setSell_number(full.getInt("sell_count"));

                    rankJewelry.setScale(full.getString("scale"));
                    rankJewelryList.add(rankJewelry);
                }
            }
            return rankJewelryList;

        } catch (Exception e) {
            e.printStackTrace();
            return rankJewelryList;
        }

    }

    public static String getSign(String timestamp) throws IOException, ScriptException, JSONException, NoSuchMethodException, NoSuchAlgorithmException {
        String str = timestamp;
        byte[] digest = null;
        MessageDigest md5 = MessageDigest.getInstance("md5");
        digest = md5.digest(str.getBytes("utf-8"));
        String md5Str = new BigInteger(1, digest).toString(16) + "sign@muxicat.com";
        digest = md5.digest(md5Str.getBytes("utf-8"));
        return new BigInteger(1, digest).toString(16);
    }


}


