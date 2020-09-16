package com.cqmike.plugins.redis.data;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class DataCenter {

    public static List<RedisServerData> REDIS_DATA_LIST;

    public static DefaultMutableTreeNode ROOT_NODE = new DefaultMutableTreeNode(new RedisServerData("root"));
    public static DefaultTreeModel ROOT_MODE = new DefaultTreeModel(ROOT_NODE, true);

    public static GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig = new GenericObjectPoolConfig<>();
    public static GenericObjectPool<StatefulRedisConnection<String, String>> pool;

    static {
        poolConfig.setMaxIdle(6);
        poolConfig.setMaxTotal(18);
    }

}
