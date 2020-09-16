package com.cqmike.plugins.redis.data;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * database 1 2 3 4 5 6 7 8
 *
 * @author chen qi
 * @date 2020-08-31 16:00
 **/
@Data
public class RedisDataBase {

    private String name;
    private long dbSize;

    private String serverKey;

    @Override
    public String toString() {
        return name + "  (" + dbSize + ")";
    }
}
