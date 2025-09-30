package com.badou.project.exception;

/**
 * @ClassName DataErrorException
 * @Description 数据异常的错误
 * @date 2022/12/8 15:41
 * @Version 1.0
 */

public class DataErrorException extends RuntimeException {

    public DataErrorException(String message) {
        super(message);
    }

}
