package com.bike.common;

public interface ResultCode {

    Integer SUCCESS = 200;
    Integer ERROR = 500;
    Integer PARAM_ERROR = 400;
    Integer UNAUTHORIZED = 401;
    Integer FORBIDDEN = 403;
    Integer NOT_FOUND = 404;
}
