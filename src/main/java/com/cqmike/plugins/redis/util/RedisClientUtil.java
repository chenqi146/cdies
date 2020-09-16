package com.cqmike.plugins.redis.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cqmike.plugins.redis.data.DataCenter;
import com.cqmike.plugins.redis.data.RedisDataBase;
import com.cqmike.plugins.redis.data.RedisDataCenter;
import com.intellij.openapi.ui.Messages;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author chen qi
 * @date 2020-08-31 16:49
 **/
@Slf4j
public class RedisClientUtil {

    private RedisClientUtil() {

    }

    public static String connectionRedis(DefaultMutableTreeNode treeNode, String host, String auth, String port) {
        final GenericObjectPool<StatefulRedisConnection<String, String>> pool = getStatefulRedisConnectionGenericObjectPool(host, auth, port);
        try (final StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            final RedisCommands<String, String> sync = connection.sync();
            String Keyspace = sync.info("Keyspace");
            String[] sp = StrUtil.split(Keyspace, "\n");
            List<String> list = CollUtil.newArrayList(Arrays.asList(sp));
            list = list.subList(1, list.size());
            final int size = list.size();

            for (int i = 0; i < 16; i++) {
                RedisDataBase dataBase = new RedisDataBase();
                dataBase.setServerKey(host + StrUtil.COLON + port);
                if (i >= size) {
                    dataBase.setName("db" + i);
                    dataBase.setDbSize(0);
                } else {
                    // db0:keys=26,expires=0,avg_ttl=0
                    final String str = list.get(i);
                    if (StrUtil.isEmpty(str)) {
                        continue;
                    }
                    // [db0:keys=26, expires=0, avg_ttl=0]
                    final String[] split = StrUtil.split(str, StrUtil.COMMA);
                    // [db0, keys=26]
                    final String[] split1 = StrUtil.split(split[0], StrUtil.COLON);
                    dataBase.setName(split1[0]);
                    // [keys, 26]
                    final String[] split2 = StrUtil.split(split1[1], "=");
                    dataBase.setDbSize(Long.parseLong(split2[1]));
                }
                DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(dataBase);
                DataCenter.ROOT_MODE.insertNodeInto(databaseNode, treeNode, treeNode.getChildCount());
            }
            RedisDataCenter.REDIS_MAP.put(host + StrUtil.COLON + port, pool);
            return null;
        } catch (Exception exception) {
            log.error("redis连接失败: ", exception);
            return exception.getMessage();
        }
    }

    @NotNull
    private static GenericObjectPool<StatefulRedisConnection<String, String>> getStatefulRedisConnectionGenericObjectPool(String host, String auth, String port) {
        final RedisURI build = RedisURI.builder()
                .withHost(host)
                .withDatabase(0)
                .withPort(Integer.parseInt(port))
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        if (StrUtil.isNotEmpty(auth) && !"(Optional)".equals(auth)) {
            build.setPassword(auth);
        }
        RedisClient redisClient = RedisClient.create(build);
        return ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, DataCenter.poolConfig);
    }



}
