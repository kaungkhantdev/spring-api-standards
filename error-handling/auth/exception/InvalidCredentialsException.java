package com.astronity.pos.auth.exception;

import com.astronity.pos.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Unknown username or wrong password. Both give the same message on purpose,
 * so the login screen never reveals which usernames exist.
 */
public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password");
    }
}
