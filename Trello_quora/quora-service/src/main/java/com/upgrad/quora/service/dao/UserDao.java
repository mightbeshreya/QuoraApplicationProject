package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public boolean isUsernameExists(final String username) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", username).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public boolean isEmailExists(final String email) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }
}