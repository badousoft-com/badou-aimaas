package com.badou.project.cache.util;

import com.badou.tools.redis.JedisConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
	private static volatile JedisPool jedisPool = null;

	private JedisPoolUtil() {
	}

	//单例模式-DLC 双重检测锁
	public static JedisPool getJedisPoolInstance() {
		if (null == jedisPool) {
			synchronized (JedisPoolUtil.class) {
				if (null == jedisPool) {
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(200);
					poolConfig.setMaxIdle(JedisConfig.maxIdle);
					poolConfig.setMaxWaitMillis(JedisConfig.maxWait);
					poolConfig.setBlockWhenExhausted(true);
					poolConfig.setTestOnBorrow(true);  // ping  PONG
					jedisPool = new JedisPool(poolConfig, JedisConfig.host, JedisConfig.port,  JedisConfig.timeOut, JedisConfig.password);
				}
			}
		}
		return jedisPool;
	}

}
