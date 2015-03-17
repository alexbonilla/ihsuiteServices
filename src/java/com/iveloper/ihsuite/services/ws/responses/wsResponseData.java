/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.ws.responses;

import com.iveloper.ihsuite.services.utils.SriStatus;
import com.iveloper.ihsuite.services.utils.SriTipoComprobante;
import com.iveloper.ihsuite.services.utils.SriTypeEmission;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Alex
 */
public class wsResponseData extends wsResponseBase {

    private String DocumentAutorizacion;
    private Date DocumentAutorizacionDate;
    private String DocumentClave;
    private List<wsResponseDocumentEvent> DocumentEvents;
    private String DocumentId;
    private DocFacInstance DocumentInstance;
    private boolean DocumentNotified;
    private String DocumentReference;
    private SriStatus DocumentStatus;
    private SriTipoComprobante DocumentType;
    private SriTypeEmission DocumentTypeEmision;

    /**
     *
     * @return
     */
    public String getDocumentAutorizacion() {
        return DocumentAutorizacion;
    }

    /**
     *
     * @param DocumentAutorizacion
     */
    public void setDocumentAutorizacion(String DocumentAutorizacion) {
        this.DocumentAutorizacion = DocumentAutorizacion;
    }

    /**
     *
     * @return
     */
    public Date getDocumentAutorizacionDate() {
        return DocumentAutorizacionDate;
    }

    /**
     *
     * @param DocumentAutorizacionDate
     */
    public void setDocumentAutorizacionDate(Date DocumentAutorizacionDate) {
        this.DocumentAutorizacionDate = DocumentAutorizacionDate;
    }

    /**
     *
     * @return
     */
    public String getDocumentClave() {
        return DocumentClave;
    }

    /**
     *
     * @param DocumentClave
     */
    public void setDocumentClave(String DocumentClave) {
        this.DocumentClave = DocumentClave;
    }

    /**
     *
     * @return
     */
    public List<wsResponseDocumentEvent> getDocumentEvents() {
        return DocumentEvents;
    }

    /**
     *
     * @param DocumentEvents
     */
    public void setDocumentEvents(List<wsResponseDocumentEvent> DocumentEvents) {
        this.DocumentEvents = DocumentEvents;
    }

    /**
     *
     * @return
     */
    public String getDocumentId() {
        return DocumentId;
    }

    /**
     *
     * @param DocumentId
     */
    public void setDocumentId(String DocumentId) {
        this.DocumentId = DocumentId;
    }

    /**
     *
     * @return
     */
    public DocFacInstance getDocumentInstance() {
        return DocumentInstance;
    }

    /**
     *
     * @param DocumentInstance
     */
    public void setDocumentInstance(DocFacInstance DocumentInstance) {
        this.DocumentInstance = DocumentInstance;
    }

    /**
     *
     * @return
     */
    public boolean isDocumentNotified() {
        return DocumentNotified;
    }

    /**
     *
     * @param DocumentNotified
     */
    public void setDocumentNotified(boolean DocumentNotified) {
        this.DocumentNotified = DocumentNotified;
    }

    /**
     *
     * @return
     */
    public String getDocumentReference() {
        return DocumentReference;
    }

    /**
     *
     * @param DocumentReference
     */
    public void setDocumentReference(String DocumentReference) {
        this.DocumentReference = DocumentReference;
    }

    /**
     *
     * @return
     */
    public SriStatus getDocumentStatus() {
        return DocumentStatus;
    }

    /**
     *
     * @param DocumentStatus
     */
    public void setDocumentStatus(SriStatus DocumentStatus) {
        this.DocumentStatus = DocumentStatus;
    }

    /**
     *
     * @return
     */
    public SriTipoComprobante getDocumentType() {
        return DocumentType;
    }

    /**
     *
     * @param DocumentType
     */
    public void setDocumentType(SriTipoComprobante DocumentType) {
        this.DocumentType = DocumentType;
    }

    /**
     *
     * @return
     */
    public SriTypeEmission getDocumentTypeEmision() {
        return DocumentTypeEmision;
    }

    /**
     *
     * @param DocumentTypeEmision
     */
    public void setDocumentTypeEmision(SriTypeEmission DocumentTypeEmision) {
        this.DocumentTypeEmision = DocumentTypeEmision;
    }

    
}
