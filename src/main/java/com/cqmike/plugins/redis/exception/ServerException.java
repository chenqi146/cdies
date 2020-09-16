package com.cqmike.plugins.redis.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author chen qi
 * @date 2020-08-31 18:10
 **/

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerException extends RuntimeException {

    private String msg;

    private int code;

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message) {
        super(message);
    }
}


