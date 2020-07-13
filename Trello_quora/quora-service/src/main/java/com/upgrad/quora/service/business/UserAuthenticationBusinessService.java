package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

//A UserAuthenticationServiceClass for validating the user credentials username and password provided by user during login
@Service

public class UserAuthenticationBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

    //** authenticateToken for signin**//
    //This method authenticates the user by validating the login details username and password against the information stored in
    //the database
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName((userName));
        //checks if the username exists in the database, if it is not found it will throw an AuthenticationFailedException
        //with the message as stated below
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "This username does not exist");
        }
        //Here the  encrypt method will encrypt the password with the appropriate salt and
        // compares the encrypted password against the encrypted password stored in the database.
        //If both the encrypted password matches and the username is found in the database then it generates the access-token
        // and returns an object of UserAuthTokenEntity else throws an AuthenticationFailedException
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            //The expiry time limit for an accesstoken is 8 hours from the time of user login
            final ZonedDateTime expiresAt = now.plusHours(8);
            String uuid = userEntity.getUuid();

            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(uuid, now, expiresAt));
            userAuthToken.setUuid(uuid);
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthToken);
            return userAuthToken;

        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }

    }

    //**signOut**//
    // This method authorizes the authenticated user for signout endpoint with the securitytoken(accesstoken)
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signOut(final String bearerAcccessToken) throws SignOutRestrictedException, NullPointerException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(bearerAcccessToken);
        //if the access token doesnt exist in the database it will throw an error with below message
        //else if the access token exists in the database the logout time will be updated and persisted in the database
        if (userAuthToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            final ZonedDateTime now = ZonedDateTime.now();
            userAuthToken.setLogoutAt(now);
            userDao.updateUserLogoutAt(userAuthToken);
            return userAuthToken;
        }
    }

}
