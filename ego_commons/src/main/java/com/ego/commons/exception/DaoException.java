package com.ego.commons.exception;

/**
 * 数据库操作异常
 */
public class DaoException extends Exception {
    public DaoException(String message) {
        super(message);
    }
}
