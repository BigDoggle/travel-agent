package com.travel.agent.exception;

/**
 * 授权异常
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String message) {
        super(403, message);
    }
}
