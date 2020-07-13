package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/* Repository - DB */
@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /* QuestionEntity Object is persisted - Question is created  and QuestionEntity object is returned*/
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /* List of already posted questions are retrieved from DB
     * getResultList is used for retrieving lists
     *  */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //This method updates the question in the database
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        QuestionEntity updatedQ = entityManager.merge(questionEntity);
        return updatedQ;
    }

    //This method retrieves the question based on question uuid, if found returns question else null
    public QuestionEntity getQuestionByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("QuestionByUUID", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }
    //This method deletes the question record from database
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
    //This method retrieves all the questions posted by a user and returns null if the list is empty.
    public List<QuestionEntity> getAllQuestionsByUser(final String userUuid){
        try{
            return entityManager.createNamedQuery("AllQuestionsByUser", QuestionEntity.class).setParameter("uuid",userUuid).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}
