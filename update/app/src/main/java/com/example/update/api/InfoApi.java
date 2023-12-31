package com.example.update.api;

import com.example.update.BuildConfig;
import com.example.update.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class InfoApi {
    private static final String REDIS_IP = BuildConfig.REDIS_IP;

    private static final String REDIS_PASSWORD = BuildConfig.REDIS_PASSWORD;

    private static final int REDIS_SELECT = BuildConfig.REDIS_SELECT;
    public static boolean login(String user){
        Jedis jedis = new Jedis(REDIS_IP, 6379);
        try {
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth(REDIS_PASSWORD);
            jedis.select(REDIS_SELECT);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return false;
                }
                if (!jedis.exists(user)) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        finally {
            if (jedis != null) {
                //这里使用的close不代表关闭连接，指的是归还资源
                jedis.close();
            }
        }
    }
    public static boolean deleteUser(String user){
        Jedis jedis = new Jedis(REDIS_IP, 6379);
        try {
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth(REDIS_PASSWORD);
            jedis.select(REDIS_SELECT);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return false;
                }
                if (!jedis.exists(user)) {
                    return false;
                }
                jedis.srem("user", user);
                jedis.del(user);
                if(jedis.exists(jedis.hget(user,"jewelryIDList"))){
                    jedis.del(jedis.hget(user,"jewelryIDList"));
                }
                if(jedis.exists(jedis.hget(user,"blockJewelryIDList"))){
                    jedis.del(jedis.hget(user,"blockJewelryIDList"));
                }
                if(jedis.exists(jedis.hget(user,"scale_c5"))){
                    jedis.del(jedis.hget(user,"scale_c5"));
                }
                if(jedis.exists(jedis.hget(user,"scale_ig"))){
                    jedis.del(jedis.hget(user,"scale_ig"));
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        finally {
            if (jedis != null) {
                //这里使用的close不代表关闭连接，指的是归还资源
                jedis.close();
            }
        }
    }

    public static UserInfo getUserInfo(String user){
        UserInfo userInfo = new UserInfo();
        Jedis jedis = new Jedis(REDIS_IP, 6379);
        try {
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth(REDIS_PASSWORD);
            jedis.select(REDIS_SELECT);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return null;
                }
                if (!jedis.exists(user)) {
                    return null;
                }
                userInfo.setUserName(user);
                if(jedis.hexists(user,"username")){
                    userInfo.setUserName(jedis.hget(user,"username"));
                }
                if(jedis.hexists(user,"uuAccount")){
                    userInfo.setUuAccount(jedis.hget(user,"uuAccount"));
                }
                if(jedis.hexists(user,"scale_c5")){
                    if(jedis.hexists(jedis.hget(user,"scale_c5"),"0-50")){
                        userInfo.setScale1_c5(jedis.hget(jedis.hget(user,"scale_c5"),"0-50"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_c5"),"50-100")){
                        userInfo.setScale2_c5(jedis.hget(jedis.hget(user,"scale_c5"),"50-100"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_c5"),"100-500")){
                        userInfo.setScale3_c5(jedis.hget(jedis.hget(user,"scale_c5"),"100-500"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_c5"),"500-")){
                        userInfo.setScale4_c5(jedis.hget(jedis.hget(user,"scale_c5"),"500-"));
                    }
                }
                if(jedis.hexists(user,"scale_ig")){
                    if(jedis.hexists(jedis.hget(user,"scale_ig"),"0-50")){
                        userInfo.setScale1_ig(jedis.hget(jedis.hget(user,"scale_ig"),"0-50"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_ig"),"50-100")){
                        userInfo.setScale2_ig(jedis.hget(jedis.hget(user,"scale_ig"),"50-100"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_ig"),"100-500")){
                        userInfo.setScale3_ig(jedis.hget(jedis.hget(user,"scale_ig"),"100-500"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale_ig"),"500-")){
                        userInfo.setScale4_ig(jedis.hget(jedis.hget(user,"scale_ig"),"500-"));
                    }
                }
                return userInfo;
            } else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
        finally {
            if (jedis != null) {
                //这里使用的close不代表关闭连接，指的是归还资源
                jedis.close();
            }
        }
    }

    public static boolean submitInfo(String user,UserInfo userInfo,String flag){
        Jedis jedis = new Jedis(REDIS_IP, 6379);
        try {
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth(REDIS_PASSWORD);
            jedis.select(REDIS_SELECT);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return false;
                }
                if (!jedis.exists(user)) {
                    return false;
                }
                if(!jedis.hexists(user,"scale_c5")){
                    return false;
                }
                if(!jedis.exists(jedis.hget(user,"scale_c5"))){
                    return false;
                }
                if(!jedis.hexists(user,"scale_ig")){
                    return false;
                }
                if(!jedis.exists(jedis.hget(user,"scale_ig"))){
                    return false;
                }
                if(flag.equals("c5")){
                    if(jedis.hexists(user,"scale_c5")){
                        if(jedis.hexists(jedis.hget(user,"scale_c5"),"0-50")){
                            jedis.hset(jedis.hget(user,"scale_c5"),"0-50",userInfo.getScale1_c5());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_c5"),"50-100")){
                            jedis.hset(jedis.hget(user,"scale_c5"),"50-100",userInfo.getScale2_c5());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_c5"),"100-500")){
                            jedis.hset(jedis.hget(user,"scale_c5"),"100-500",userInfo.getScale3_c5());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_c5"),"500-")){
                            jedis.hset(jedis.hget(user,"scale_c5"),"500-",userInfo.getScale4_c5());
                        }
                    }
                }
                else if(flag.equals("ig")){
                    if(jedis.hexists(user,"scale_ig")){
                        if(jedis.hexists(jedis.hget(user,"scale_ig"),"0-50")){
                            jedis.hset(jedis.hget(user,"scale_ig"),"0-50",userInfo.getScale1_ig());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_ig"),"50-100")){
                            jedis.hset(jedis.hget(user,"scale_ig"),"50-100",userInfo.getScale2_ig());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_ig"),"100-500")){
                            jedis.hset(jedis.hget(user,"scale_ig"),"100-500",userInfo.getScale3_ig());
                        }
                        if(jedis.hexists(jedis.hget(user,"scale_ig"),"500-")){
                            jedis.hset(jedis.hget(user,"scale_ig"),"500-",userInfo.getScale4_ig());
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        finally {
            if (jedis != null) {
                //这里使用的close不代表关闭连接，指的是归还资源
                jedis.close();
            }
        }
    }
}
