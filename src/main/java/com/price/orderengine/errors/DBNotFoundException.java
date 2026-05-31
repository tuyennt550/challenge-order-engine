package com.price.orderengine.errors;

public class DBNotFoundException extends BusinessException {
    public DBNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
