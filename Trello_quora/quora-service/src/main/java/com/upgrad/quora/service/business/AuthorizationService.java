package com.upgrad.quora.service.business;

import com.upgrad.quora.service.components.AuthorizationHelperComponent;
import com.upgrad.quora.service.components.AuthorizationHelperComponent;
import com.upgrad.quora.service.constant.ErrorCodeConstants;
import com.upgrad.quora.service.constant.ErrorMessage;
import com.upgrad.quora.service.dao.UserAuthTokenDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorizationHelperComponent authorizationHelperComponents;

    public UserAuthTokenEntity getUserAuthTokenEntity(final String accessToken) {
        return userAuthTokenDao.getUserAuthTokenEntity(accessToken);
    }

    public UserEntity validateTokenandFetchUserByUuid(final String accessToken, final String uuid) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = getUserAuthTokenEntity(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(ErrorCodeConstants.UserHasNotSignedIn.getCode(),
                    ErrorMessage.UserHasNotSignedIn.getErrorMessage());
        }
        if (!authorizationHelperComponents.isValidUserAuthTokenEntity(userAuthTokenEntity)) {
            throw new AuthorizationFailedException(ErrorCodeConstants.UserHasSignedOut.getCode(),
                    ErrorMessage.UserHasSignedOut.getErrorMessage());
        }

        UserEntity userEntity = userDao.getUserByUUID(uuid);
        return userEntity;
    }
}

