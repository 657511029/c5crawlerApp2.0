package com.example.update.api;

import com.example.update.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class InfoApi {

    public static boolean login(String user){
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
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
    }
    public static boolean deleteUser(String user){
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
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
                if(jedis.exists(jedis.hget(user,"jewelryMap"))){
                    jedis.del(jedis.hget(user,"jewelryMap"));
                }
                if(jedis.exists(jedis.hget(user,"blockJewelryMap"))){
                    jedis.del(jedis.hget(user,"blockJewelryMap"));
                }
                if(jedis.exists(jedis.hget(user,"csqaqJewelryMap"))){
                    jedis.del(jedis.hget(user,"csqaqJewelryMap"));
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public static UserInfo getUserInfo(String user){
        UserInfo userInfo = new UserInfo();
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
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
                    userInfo.setUuAccount(jedis.hget(user,"username"));
                }
                if(jedis.hexists(user,"0-50")){
                    userInfo.setScale1(jedis.hget(user,"0-50"));
                }
                if(jedis.hexists(user,"50-100")){
                    userInfo.setScale2(jedis.hget(user,"50-100"));
                }
                if(jedis.hexists(user,"100-")){
                    userInfo.setScale3(jedis.hget(user,"100-"));
                }
                return userInfo;
            } else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }

    public static boolean submitInfo(String user,UserInfo userInfo){
        try {
            Jedis jedis = new Jedis("r-uf6ji3jrv0oomrgi9upd.redis.rds.aliyuncs.com", 6379);
            //如果 Redis 服务设置了密码，需要添加下面这行代码
            jedis.auth("Lenshanshan521!");
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return false;
                }
                if (!jedis.exists(user)) {
                    return false;
                }

                if(jedis.hexists(user,"0-50")){
                    jedis.hset(user,"0-50",userInfo.getScale1());

                }
                if(jedis.hexists(user,"50-100")){
                    jedis.hset(user,"50-100",userInfo.getScale2());
                }
                if(jedis.hexists(user,"100-")){
                    jedis.hset(user,"100-",userInfo.getScale3());
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
}
