package com.cqmike.plugins.redis.data;


import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.HashMap;
import java.util.Map;

public class RedisDataCenter {

    private RedisDataCenter() {

    }

    public static Map<String, GenericObjectPool<StatefulRedisConnection<String, String>>> REDIS_MAP = new HashMap<>(16);


}
