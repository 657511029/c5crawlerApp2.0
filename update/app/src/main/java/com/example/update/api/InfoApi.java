package com.example.update.api;

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
}
