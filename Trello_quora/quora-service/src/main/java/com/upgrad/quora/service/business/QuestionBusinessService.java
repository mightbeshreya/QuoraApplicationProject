package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/* Service annotation - It is used to mark the class as a service provider, is a specialization of @Component annotation */
@Service
public class QuestionBusinessService {
    /* Autowired QuestionDao class that performs DB related queries of questions required by this service class */
    @Autowired
    private QuestionDao questionDao;

    /* Autowired UserDao class that performs DB related queries of users required by this service class */
    @Autowired
    private UserDao userDao;

    /* Transactional Annotation with Propagation - same transaction will propagate from a transactional caller
     * createQuestion function returns QuestionEntity that has been created
     * Takes in QuestionEntity object , authorization
     * Authorization (when user is signed out or if user has not signed in at all is performed here
     * If no exception is thrown, question is created and user whose authorization is done is set to the question
     *
     *  */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    /*
     * getAllQuestions function returns List of QuestionEntity objects from DB
     * Takes in authorization (access token)
     * Authorization (when user is signed out or if user has not signed in at all is performed here
     * If no exception is thrown, all questions are retrieved from DB and returned
     *
     *  */
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        List<QuestionEntity> listOfQuestions  = questionDao.getAllQuestions();
        return listOfQuestions;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        String questionOwnerUuid = questionEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if (questionOwnerUuid.equals(signedInUserUuid)) {
            QuestionEntity updatedQuestion = questionDao.updateQuestion(questionEntity);
            return updatedQuestion;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }
    //This method retrieves the question in the database
    public QuestionEntity getQuestion(final String questionUuid, final String accessToken) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(final String questionIdUuid, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(accessToken);
        //If the accessToken of admin or QuestionOwner doesnt exist in the database throw following Exception
        //It means that if the user hasnt signedin, then the basic Authentication is not done and the accessToken is not generated
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //we have logoutAt attribute in the userAuth table, upon successfull signout of the application
        //the user logoutAt attribute will be updated. So if the logoutAt is not null then it means that user has signed out
        ZonedDateTime logoutTime = userAuthToken.getLogoutAt();
        if (logoutTime != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
        //If the questionUuid doesnt exist in the database throw following exception
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionIdUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        //The owneroftheQuestion or an admin will have privelege of deleting the Question
        //The user with the role non-admin and non-owner of the Question is trying to delete the Question
        //then following exception is thrown
        String role = userAuthToken.getUser().getRole();
        String questionOwnerUuid = questionEntity.getUser().getUuid();
        String signedInUserUuid = userAuthToken.getUser().getUuid();

        if (role.equals("admin") || questionOwnerUuid.equals(signedInUserUuid)) {
            questionDao.deleteQuestion(questionEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return questionIdUuid;
    }




}
