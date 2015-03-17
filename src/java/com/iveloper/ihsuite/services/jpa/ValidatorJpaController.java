/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.jpa;

import com.iveloper.ihsuite.services.entities.Validator;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import com.iveloper.ihsuite.services.utils.DocumentStructureValidator;
import com.iveloper.ihsuite.services.utils.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class ValidatorJpaController implements Serializable {

    public ValidatorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Validator validator) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(validator);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findValidator(validator.getName()) != null) {
                throw new PreexistingEntityException("Validator " + validator + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Validator validator) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            validator = em.merge(validator);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = validator.getName();
                if (findValidator(id) == null) {
                    throw new NonexistentEntityException("The validator with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Validator validator;
            try {
                validator = em.getReference(Validator.class, id);
                validator.getName();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The validator with id " + id + " no longer exists.", enfe);
            }
            em.remove(validator);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Validator> findValidatorEntities() {
        return findValidatorEntities(true, -1, -1);
    }

    public List<Validator> findValidatorEntities(int maxResults, int firstResult) {
        return findValidatorEntities(false, maxResults, firstResult);
    }

    private List<Validator> findValidatorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Validator.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Validator findValidator(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Validator.class, id);
        } finally {
            em.close();
        }
    }

    public int getValidatorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Validator> rt = cq.from(Validator.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
     public String validateAgainstSchema(String documentType, InputStream xmlis) {
        String validationResponse = null;
        String xsdName = FileUtils.selectXsd(documentType);        
        Validator validator = findValidator(xsdName);

        InputStream xsdis = new ByteArrayInputStream(validator.getContent());
        DocumentStructureValidator docValidator = new DocumentStructureValidator(xsdis, xmlis);
        if (xmlis != null) {
            validationResponse = docValidator.validate();
        }
        return validationResponse;
    }
    
}
