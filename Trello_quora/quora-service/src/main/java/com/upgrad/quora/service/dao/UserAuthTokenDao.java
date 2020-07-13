package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.text.Normalizer;

@Repository
public class UserAuthTokenDao {

    @PersistenceContext
    private EntityManager em;

    public UserAuthTokenEntity createAuthTokenEntity(UserAuthTokenEntity userAuthTokenEntity){

        em.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public UserAuthTokenEntity getUserAuthTokenEntity(String jwtToken){

        try{
            return em.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", jwtToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
}
