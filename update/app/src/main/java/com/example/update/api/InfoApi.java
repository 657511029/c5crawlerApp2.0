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
            jedis.select(255);
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
            jedis.select(255);
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
                if(jedis.exists(jedis.hget(user,"scale"))){
                    jedis.del(jedis.hget(user,"scale"));
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
            jedis.select(255);
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
                if(jedis.hexists(user,"scale")){
                    if(jedis.hexists(jedis.hget(user,"scale"),"0-50")){
                        userInfo.setScale1(jedis.hget(jedis.hget(user,"scale"),"0-50"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"50-100")){
                        userInfo.setScale2(jedis.hget(jedis.hget(user,"scale"),"50-100"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"100-500")){
                        userInfo.setScale3(jedis.hget(jedis.hget(user,"scale"),"100-500"));
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"500-")){
                        userInfo.setScale4(jedis.hget(jedis.hget(user,"scale"),"500-"));
                    }
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
            jedis.select(255);
            //调用ping()方法查看 Redis 服务是否运行
            if (jedis.ping().equals("PONG")) {
                if (!jedis.sismember("user", user)) {
                    return false;
                }
                if (!jedis.exists(user)) {
                    return false;
                }
                if(!jedis.hexists(user,"scale")){
                    return false;
                }
                if(!jedis.exists(jedis.hget(user,"scale"))){
                    return false;
                }
                if(jedis.hexists(user,"scale")){
                    if(jedis.hexists(jedis.hget(user,"scale"),"0-50")){
                        jedis.hset(jedis.hget(user,"scale"),"0-50",userInfo.getScale1());
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"50-100")){
                        jedis.hset(jedis.hget(user,"scale"),"50-100",userInfo.getScale2());
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"100-500")){
                        jedis.hset(jedis.hget(user,"scale"),"100-500",userInfo.getScale3());
                    }
                    if(jedis.hexists(jedis.hget(user,"scale"),"500-")){
                        jedis.hset(jedis.hget(user,"scale"),"500-",userInfo.getScale4());
                    }
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
