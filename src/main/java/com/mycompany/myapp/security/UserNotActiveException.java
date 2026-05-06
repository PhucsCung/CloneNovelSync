package com.mycompany.myapp.security;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
public class UserNotActiveException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserNotActiveException(String message) {
        super(message);
    }

    public UserNotActiveException(String message, Throwable t) {
        super(message, t);
    }
}
