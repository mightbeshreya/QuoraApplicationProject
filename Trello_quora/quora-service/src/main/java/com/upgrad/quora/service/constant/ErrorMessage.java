package com.upgrad.quora.service.constant;

public enum ErrorMessage {
    UserNameAlreadyExist("Try any other Username, this Username has already been taken"),
    UserEmailIdAlreadyExist("This user has already been registered, try with any other emailId"),
    UserNameDoesnotExist("This username does not exist"),
    PasswordInvalidInput("Password failed"),
    AccessTokenInvalid("User is not Signed in"),
    UserHasNotSignedIn("User has not signed in"),
    UserHasSignedOut("User is signed out.Sign in first to get user details"),
    UserNotFoundWithUUID("User with entered uuid does not exist"),
    USERDELTEACTIONUNAUTHORIZED("Unauthorized Access, Entered user is not an admin"),
    DeleteUserNotFoundWithUUID("User with entered uuid to be deleted does not exist");

    private String errorMessage;

    ErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
