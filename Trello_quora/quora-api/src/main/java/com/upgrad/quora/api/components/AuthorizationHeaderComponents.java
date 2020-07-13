package com.upgrad.quora.api.components;

import com.upgrad.quora.api.constants.TokenPrefixes;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class AuthorizationHeaderComponents {

    public String getBearerToken(final String authorizationHeader){
        final String encodedJwtToken = authorizationHeader.split(TokenPrefixes.BearerToken.getTokenPrefixes())[1];
        return encodedJwtToken;
    }

    public String getBasicToken(final String authorizationHeader) {
        final String encodedBasicToken = authorizationHeader.split(TokenPrefixes.BasicToken.getTokenPrefixes())[1];
        return encodedBasicToken;
    }

    public String[] getUserDetailsFromBearToken(final String basicToken) {
        final byte[] decodeTokenBytes = Base64.getDecoder().decode(basicToken);
        final String[] userDetails = new String(decodeTokenBytes).split(":");
        return userDetails;
    }
}
