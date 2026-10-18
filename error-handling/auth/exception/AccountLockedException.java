package com.astronity.pos.auth.exception;

import com.astronity.pos.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/** Too many wrong passwords: locked_until is in the future. */
public class AccountLockedException extends BusinessException {

    public AccountLockedException() {
        super(HttpStatus.LOCKED, "ACCOUNT_LOCKED", "Account locked after too many wrong passwords, try again later");
    }
}
