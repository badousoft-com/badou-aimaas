package com.badou.project.exception;

/**
 * 代表数据不合法
 */
public class DataValidException extends Exception {

    public DataValidException(String message) {
        super(message);
    }
}
