package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.service.dao.UserDao;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService ansBusinessService;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionBusinessService quesBusinessService;

    @Autowired
    QuestionDao questionDao;

    @RequestMapping(method = RequestMethod.POST, path = "question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId, final AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = new AnswerEntity();
        AnswerEntity createdAnswer;
        answerEntity.setAns(answerRequest.getAnswer());
        try {
            String[] bearerAccessToken = authorization.split("Bearer ");
            createdAnswer = ansBusinessService.createAnswer(answerEntity, bearerAccessToken[1], questionId);
        } catch (ArrayIndexOutOfBoundsException are) {
            createdAnswer = ansBusinessService.createAnswer(answerEntity, authorization, questionId);
        }

        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).
                status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId,
                                                                @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity updatedAnswer = new AnswerEntity();
        updatedAnswer.setAns(answerEditRequest.getContent());
        try {
            String[] accessToken = authorization.split("Bearer ");
            updatedAnswer = ansBusinessService.updateAnswer(updatedAnswer, answerId, accessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            updatedAnswer = ansBusinessService.updateAnswer(updatedAnswer, answerId, authorization);
        }
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
}




