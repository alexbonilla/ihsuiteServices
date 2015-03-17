/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.processes;

import com.iveloper.ihsuite.services.entities.Company;
import com.iveloper.ihsuite.services.entities.CompanySettings;
import com.iveloper.ihsuite.services.entities.Document;

import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.entities.ProcessLog;
import com.iveloper.ihsuite.services.jpa.CompanyJpaController;
import com.iveloper.ihsuite.services.jpa.CompanySettingsJpaController;

import com.iveloper.ihsuite.services.jpa.LotJpaController;
import com.iveloper.ihsuite.services.jpa.ProcessLogJpaController;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import com.iveloper.ihsuite.services.ws.Operations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
@Stateless
public class ProcessesTimer {

    private final Logger log = Logger
            .getLogger(ProcessesTimer.class.getName());

    @Schedule(minute = "*/1", hour = "*")
    public void runEveryMinute() {
        log.log(Level.INFO, "running every minute .. now it''s: {0}", new Date().toString());
        fullProcess();
    }

    @Schedule(minute = "*/5", hour = "*")
    public void runEveryFiveMinutes() {
        log.log(Level.INFO, "running every 5 minutes .. now it''s: {0}", new Date().toString());
    }

    private void fullProcess() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihAccountsPU");
            Context c = new InitialContext();
            UserTransaction utx = null;
            CompanyJpaController companyController = new CompanyJpaController(utx, emf);
            Collection activeCompanies = companyController.findActiveCompanies(); //devuelve solo activas
            Iterator<Company> itrCompany = activeCompanies.iterator();
            while (itrCompany.hasNext()) {
                String id = itrCompany.next().getId();
                EntityManagerFactory emfEachEntity = Persistence.createEntityManagerFactory("ihsuite" + id + "PU");
                LotJpaController lotController = new LotJpaController(utx, emfEachEntity);
                Collection lotsToProcess = lotController.findCandidatesForProcessing();
                Iterator<Lot> itrLots = lotsToProcess.iterator();
                while (itrLots.hasNext()) {
                    Lot currentLot = itrLots.next();
                    currentLot.setInProcess(Boolean.TRUE);
                    lotController.edit(currentLot); //marco que esta siendo procesado para que no sea tomado por otro proceso

                    processLogEntry(id, currentLot, Level.INFO.toString(), "fullProcess", "Iniciando procesamiento de lote " + currentLot.getId());
                    Collection documents = currentLot.getDocumentCollection();
                    Iterator<Document> itrDocuments = documents.iterator();
                    ArrayList<Thread> documentThreads = new ArrayList();
                    while (itrDocuments.hasNext()) {
                        CompanySettingsJpaController companySettingsController = new CompanySettingsJpaController(utx, emfEachEntity);
                        CompanySettings companySettings = companySettingsController.findCompanySettings(id);
                        Document currentDocument = itrDocuments.next();
                        processLogEntry(id, currentLot, Level.INFO.toString(), "fullProcess", "Iniciando procesamiento de documento " + currentDocument.getDocNum());
                        DocumentProcessor docProcessor = new DocumentProcessor();
                        docProcessor.setDocument(currentDocument);
                        docProcessor.setUserEntityId(id);
                        docProcessor.setTimeToWaitForAuthorization(companySettings.getTimeWaitForAuthorization());
                        docProcessor.setAmbiente(companySettings.getEnvironment().toString());
                        Thread threadForDocument = new Thread(docProcessor);
                        threadForDocument.start();
                        documentThreads.add(threadForDocument);
                        processLogEntry(id, currentLot, Level.INFO.toString(), "fullProcess", "Procesamiento de documento en marcha " + currentDocument.getDocNum());
                    }
                    currentLot.setProcessed(Boolean.TRUE);
                    currentLot.setInProcess(Boolean.FALSE);
                    processLogEntry(id, currentLot, Level.INFO.toString(), "fullProcess", "Procesamiento de lote en marcha " + currentLot.getId());

                        LotProcessor lotProcessor = new LotProcessor();
                        lotProcessor.setDocumentThreads(documentThreads);
                        lotProcessor.setLot(currentLot);
                        lotProcessor.setLotController(lotController);
                        Thread threadForLot = new Thread(lotProcessor);
                        threadForLot.start();  //En este hilo se invoca el metodo de LotProcessor waitToSetLotAsProcessed                                      
                }
            }
            log.log(Level.INFO, "finishing entities processing .. now it''s: {0}", new Date().toString());

        } catch (NamingException ex) {
            Logger.getLogger(ProcessesTimer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ProcessesTimer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ProcessesTimer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ProcessesTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processLogEntry(String userEntityId, Lot lot, String type, String reference, String message) {
        Logger.getLogger(Operations.class.getName()).log(Level.parse(type), "{0} {1}", new Object[]{reference, message});
        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
            Context c = new InitialContext();
            UserTransaction utx = null;
            ProcessLogJpaController processLogJpaController = new ProcessLogJpaController(utx, emf);
            ProcessLog processLog = new ProcessLog();
            processLog.setId(UUID.randomUUID().toString());
            processLog.setDateEntered(new Date());
            processLog.setLotId(lot);
            processLog.setType(type);
            processLog.setReference("Processes Timer " + reference);
            processLog.setMessage(message);

            processLogJpaController.create(processLog);
        } catch (NamingException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
