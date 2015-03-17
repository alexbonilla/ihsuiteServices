/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.jpa;

import com.iveloper.ihsuite.services.entities.Document;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.PreexistingEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class DocumentJpaController implements Serializable {

    public DocumentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Document document) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Lot lotId = document.getLotId();
            if (lotId != null) {
                lotId = em.getReference(lotId.getClass(), lotId.getId());
                document.setLotId(lotId);
            }
            em.persist(document);
            if (lotId != null) {
                lotId.getDocumentCollection().add(document);
                lotId = em.merge(lotId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDocument(document.getId()) != null) {
                throw new PreexistingEntityException("Document " + document + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Document document) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin();
            Document persistentDocument = em.find(Document.class, document.getId());
            Lot lotIdOld = persistentDocument.getLotId();
            Lot lotIdNew = document.getLotId();
            if (lotIdNew != null) {
                lotIdNew = em.getReference(lotIdNew.getClass(), lotIdNew.getId());
                document.setLotId(lotIdNew);
            }
            document = em.merge(document);
            if (lotIdOld != null && !lotIdOld.equals(lotIdNew)) {
                lotIdOld.getDocumentCollection().remove(document);
                lotIdOld = em.merge(lotIdOld);
            }
            if (lotIdNew != null && !lotIdNew.equals(lotIdOld)) {
                lotIdNew.getDocumentCollection().add(document);
                lotIdNew = em.merge(lotIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = document.getId();
                if (findDocument(id) == null) {
                    throw new NonexistentEntityException("The document with id " + id + " no longer exists.");
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
            Document document;
            try {
                document = em.getReference(Document.class, id);
                document.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The document with id " + id + " no longer exists.", enfe);
            }
            Lot lotId = document.getLotId();
            if (lotId != null) {
                lotId.getDocumentCollection().remove(document);
                lotId = em.merge(lotId);
            }
            em.remove(document);
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

    public List<Document> findDocumentEntities() {
        return findDocumentEntities(true, -1, -1);
    }

    public List<Document> findDocumentEntities(int maxResults, int firstResult) {
        return findDocumentEntities(false, maxResults, firstResult);
    }

    private List<Document> findDocumentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Document.class));
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

    public Document findDocument(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Document.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Document> rt = cq.from(Document.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
