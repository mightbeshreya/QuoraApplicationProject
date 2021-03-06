package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    //**userSignup**//
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        //calling the business logic
        // The Http Status code 201 , the response code HttpStatus.CREATED and the status message USER SUCCESSFULLY REGISTERED
        //defined here are as per the requirement provided in the user.json
        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }
    //**userSignin**//
    //This method defines the user can login to application after the successfull registration
    //This endpoint requests for the User credentials to be passed in the authorization field of header as part of Basic authentication.
    //username:password of the String is encoded to Base64 format in the authorization header
    //For example, a username of ‘Anu’ and a password of ‘12345’ becomes the string ‘Anu:12345’
    // and then this string is encoded to Base64 format to ‘QXJjaGFuYUE6MTIzNDU=’
    //Since this is basic authentication the format of authorization header is Basic QXJjaGFuYUE6MTIzNDU=
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decoded = Base64.getDecoder().decode(authorization.split(" ")[0]);
        String decodedText = new String(decoded);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthEntity = userBusinessService.signin(decodedArray[0], decodedArray[1]);
        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthEntity.getUser().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", userAuthEntity.getAccessToken());
        // The Http Status code 200 , the response code HttpStatus.OK and the status message SIGNED IN SUCCESSFULLY
        //returned here are as per the requirement provided in the problem statement
        //This method returns SigninResponse object, access token and Http Status

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }
    //**userSignout**//
    //This endpoint requests for the access token in the authorisation header as a part of Bearer authentication
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken)
            throws SignOutRestrictedException {
        //The input can be of any form "Bearer <accesstoken>" or "<accesstoken>" in the authorization header
        UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
        try {
            String[] bearerAccessToken = accessToken.split("Bearer");
            userAuthToken = userBusinessService.signOut(bearerAccessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            userAuthToken = userBusinessService.signOut(accessToken);
        }
        UserEntity user = userAuthToken.getUser();
        SignoutResponse authorizedSignoutResponse = new SignoutResponse().id(user.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(authorizedSignoutResponse, HttpStatus.OK);
    }
}
