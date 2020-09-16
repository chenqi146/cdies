package com.cqmike.plugins.redis.data;


import lombok.Data;

@Data
public class RedisKeyData {


    private DataDirType type;

    private String name;

    private long ttl;

    private DataType dataType;

    public enum DataDirType {
        DIR, DATA;
    }
    public enum DataType {
        BASE, HASH;
    }

    @Override
    public String toString() {
        return name;
    }
}


