/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.processes;

import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.jpa.LotJpaController;
import com.iveloper.ihsuite.services.jpa.exceptions.NonexistentEntityException;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexbonilla
 */
public class LotProcessor implements Runnable {

    private Lot lot;
    private ArrayList<Thread> documentThreads;
    private LotJpaController lotController;

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public ArrayList<Thread> getDocumentThreads() {
        return documentThreads;
    }

    public void setDocumentThreads(ArrayList<Thread> documentThreads) {
        this.documentThreads = documentThreads;
    }

    public LotJpaController getLotController() {
        return lotController;
    }

    public void setLotController(LotJpaController lotController) {
        this.lotController = lotController;
    }

    @Override
    public void run() {
        Logger.getLogger(LotProcessor.class.getName()).log(Level.INFO, "Configuraciones de Proceso Completo de Documento");
        waitToSetLotAsProcessed();
    }

    private void waitToSetLotAsProcessed() {
        try {
            Iterator<Thread> itrDocumentThreads = documentThreads.iterator();
            
            try {
                while (itrDocumentThreads.hasNext()) {
                    itrDocumentThreads.next().join();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessesTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            lotController.edit(lot);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(LotProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LotProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LotProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
