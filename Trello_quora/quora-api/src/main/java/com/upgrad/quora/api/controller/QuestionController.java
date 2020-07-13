package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* RestController Annotation - Controller + RequestBody annotations */
@RestController
/* Request Mapping is done */
@RequestMapping("/")
/* QuestionController is added and autowired with QuestionBusinessService service */
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;

    /* method - POST for createQuestion (Creates a new question)
        Path mapped to - /question/create
        consumes JSON, produces JSON
        Takes in QuestionRequest Entity (through consumes) & Request header variable 'authorization'
        with annotation RequestHeader
        throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
    *  */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        /* Sets all the required fields from QuestionRequest JSON */
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        /* Creating the question */
        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,
                authorization);

        /* Creating the response - setting ID = question uuid and status with a message */
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
                .status("QUESTION CREATED");
        /* Return QuestionResponse with HttpStatus.CREATED status */
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }


    /* method - GET for getAllQuestions (gets all questions from DB)
        Path mapped to - /question/all
        produces JSON
        Takes in Request header variable 'authorization'
        with annotation RequestHeader
        throws AuthorizationFailedException - when user is not signed in or when user is signed out (Authorization fails)
    *  */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions
    (@RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException {
        /* Getting the list of all questions from the Service and in turn DB */
        List<QuestionEntity> listOfQuestions = questionBusinessService.getAllQuestions(authorization);
        /* Initializing QuestionDetailsResponse as linkedlist and adding each QuestionDetailsResponse entity to it */
        final List<QuestionDetailsResponse> questionDetailsResponses = new LinkedList<>() ;

        /* Run a for loop to add each question retrieved from DB */
        for(QuestionEntity q: listOfQuestions) {
            /* Setting question details (UUID and content ) to questionDetailsResponse and
            Adding in List of questionDetailsResponses  */
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(q.getUuid());
            questionDetailsResponse.setContent(q.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        /* Returning list of  QuestionDetailsResponse */
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
    //**editQuestionContent**//
    //This method only allows the owner of the question to edit a question
    //To edit a question, this endpoint takes in the questionUuid, access token and the content to be updated from the editRequest.

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization, final QuestionEditRequest editRequest) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity;
        QuestionEntity editedQuestion;
        try {
            String[] userToken = authorization.split("Bearer ");
            questionEntity = questionBusinessService.getQuestion(questionUuid, userToken[1]);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity, userToken[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            questionEntity = questionBusinessService.getQuestion(questionUuid, authorization);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity, authorization);
        }

        //In normal cases, updating an entity doesn't change the Uuid, meaning questionUuid==updatedUuid.
        // However, we have implemented this feature in case the system later requires to keep track of the updates, for e.g. by adding a suffix after every update like Uuid-1,-2, etc.

        String updatedUuid = editedQuestion.getUuid();
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedUuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    //**deleteQuestion**//
    //The admin or the owner of the Question has a privilege of deleting the question
    //This endpoint requests for the questionUuid to be deleted and the questionowner or admin accesstoken in the authorization header
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionIdUuid, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {
        String uuid;
        try {
            String[] accessToken = authorization.split("Bearer");
            uuid = questionBusinessService.deleteQuestion(questionIdUuid, accessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            uuid = questionBusinessService.deleteQuestion(questionIdUuid, authorization);
        }

        QuestionDeleteResponse authorizedDeleteResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");
        //This method returns an object of QuestionDeleteResponse and HttpStatus
        return new ResponseEntity<QuestionDeleteResponse>(authorizedDeleteResponse, HttpStatus.OK);

    }

}
