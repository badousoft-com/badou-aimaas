package com.badou.project.common.exception;

/**
 * @ClassName ParamErrorException
 * @Description 参数异常
 * @date 2022/9/23 10:09
 * @Version 1.0
 */

public class ParamErrorException extends RuntimeException{

    public ParamErrorException(String message) {
        super(message);
    }

}
