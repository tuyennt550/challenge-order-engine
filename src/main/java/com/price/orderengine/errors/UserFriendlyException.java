package com.price.orderengine.errors;

public class UserFriendlyException extends BusinessException {
    public UserFriendlyException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
