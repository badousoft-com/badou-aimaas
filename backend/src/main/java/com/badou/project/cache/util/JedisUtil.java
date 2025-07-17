package com.badou.project.cache.util;

import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**@AUTHOR
 * @ClassName JedisUtil
 * @Description Redis的连接释放比较短 使用/完成后需要及时关闭连接并重新连接开始下一步的操作. 否则会遇到连接过期/重置等相关错误!
 * @date 2022/1/20 9:17
 * @Version 1.0
 */
@Component
@DependsOn("jedisConfig")
@Order
public class JedisUtil {

    private volatile Jedis jedis;

    private static final Random random = new Random();

    //604800秒=7天
    private static final Integer defaultTimeOut = 604800;

    public JedisUtil() {
        //初始化Redis
        initRedis();
    }

    public void initRedis() {
        JedisPool jedisPoolInstance = JedisPoolUtil.getJedisPoolInstance();
        jedis = jedisPoolInstance.getResource();
    }

    public Set<String> keys(String key) {
        //求 7~14天范围的随机时间
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();

        Set<String> values = null;

        try {
            jedis = pool.getResource();
            values = jedis.keys(key);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return values;
    }

    /**
     * 设置Redis key/value 采用默认的过期时间+随机时间
     * 注意:不可让key同时过期 让key过期时间分散一些
     * 默认过期时间为7天 +随机的时间
     * @param key
     * @param value
     * @return
     */
    public String setEx(String key,String value) {
        //求 7~14天范围的随机时间
        Integer seconds = random.nextInt(defaultTimeOut*2)%(defaultTimeOut*2-defaultTimeOut+1) + defaultTimeOut;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.setex(key,seconds,value);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public List<String> getListByMQ(String... keys){
        //如果获取超时 返回null
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        List<String> value = null;
        try {
            jedis = pool.getResource();
            //持续监听阻塞,0代表一直等待直到有数据为止
            value = jedis.brpop(1200, keys);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public Long setMsgToMQ(String keys,String... value){
        //如果获取超时 返回null
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        Long result = null;
        try {
            jedis = pool.getResource();
            result = jedis.lpush(keys,value);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return result;
    }

    public Long incr(String key) {
        Long value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public void close() {
        if(jedis != null){
            jedis.close();
        }else {
            System.out.println("关闭时 Redis为null");
        }
    }

    public String setEx(String key,String value,int seconds) {
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.setex(key,seconds,value);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * @ LM
     * @功能：通过Redis的key获取值，并释放连接资源
     * @参数：key，键值
     * @返回： 成功返回value，失败返回null
     */
    public String get(String key){
        Jedis jedis = null;
        String value = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }


    public String set(String key,String value) {
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.set(key,value);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * Redis数组添加数据
     * @param arrayName 数组名字
     * @param values 数组数据
     * @return
     */
    public Long lpush(String arrayName,String... values){
        Long value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.lpush(arrayName,values);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * Redis数组获取全部数据
     * @param arrayName 数组名字
     * @return
     */
    public List<String> lrangeAll(String arrayName){
        List value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value = jedis.lrange(arrayName,0,-1);
        } catch (Exception e) {
            e.printStackTrace();
            jedis.close();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public Long del(String key) {
        Long value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value =  jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public Long del(String... key) {
        Long value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value =  jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public Long sadd(String key,String... values){
        Long value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value =  jedis.sadd(key,values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }

    public Set<String> smembers(String key){
        Set value = null;
        Jedis jedis = null;
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        try {
            jedis = pool.getResource();
            value =  jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != jedis) {
                jedis.close();
            }
        }
        return value;
    }
}
