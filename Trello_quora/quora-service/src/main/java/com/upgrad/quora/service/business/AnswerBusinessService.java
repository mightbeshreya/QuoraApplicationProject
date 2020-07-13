package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    private QuestionBusinessService quesBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity ansEntity, final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        ansEntity.setUuid(UUID.randomUUID().toString());
        ansEntity.setDate(ZonedDateTime.now());
        ansEntity.setQuestion(questionEntity);
        ansEntity.setUser(userAuthEntity.getUser());
        return answerDao.createAnswer(ansEntity);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(final AnswerEntity ansEnt, final String ansId, final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        AnswerEntity updatedAnswer;
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }
        AnswerEntity answerEntity = answerDao.getAnsById(ansId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        answerEntity.setAns(ansEnt.getAns());
        String ansOwnnerUuid = answerEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if (ansOwnnerUuid.equals(signedInUserUuid)) {
            updatedAnswer = answerDao.updateAnswer(answerEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        return updatedAnswer;
    }


}