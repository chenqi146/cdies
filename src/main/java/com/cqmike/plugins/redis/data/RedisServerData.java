package com.cqmike.plugins.redis.data;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  服务器节点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisServerData {

    private String name;
    private String auth;
    private String host;
    private String port;
    private ServerStatus status;

    public String getServerKey() {
        return host + StrUtil.COLON + port;
    }

    private List<RedisServerData> redisServerDataList;

    public RedisServerData(String name) {
        this.name = name;
    }

    public RedisServerData(String name, String auth, String host, String port) {
        this.name = name;
        this.auth = auth;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum ServerStatus {
        ONLINE, OFFLINE;
    }
}
