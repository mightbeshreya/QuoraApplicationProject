package com.upgrad.quora.service.constant;

public enum ErrorCodeConstants {
    UserNameAlreadyExist("SGR-001"),
    AccessTokenInvalid("SGR-001"),
    UserEmailAlreadyExist("SGR-002"),
    UserNameNotValidInput("ATH-001"),
    PasswordInvalidInput("ATH-002"),
    UserHasNotSignedIn("ATHR-001"),
    UserHasSignedOut("ATHR-002"),
    USERDELTEACTIONUNAUTHORIZED("ATHR-003"),
    UserNotFoundWithUUID("USR-001");

    private String code;

    ErrorCodeConstants(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}

