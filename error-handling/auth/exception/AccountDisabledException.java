package com.astronity.pos.auth.exception;

import com.astronity.pos.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/** The user's status is disabled. */
public class AccountDisabledException extends BusinessException {

    public AccountDisabledException() {
        super(HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED", "Account disabled");
    }
}
