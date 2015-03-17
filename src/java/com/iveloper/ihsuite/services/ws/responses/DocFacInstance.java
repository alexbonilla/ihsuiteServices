/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.ws.responses;

import com.iveloper.ihsuite.services.utils.SriStatus;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Alex
 */
public class DocFacInstance {
//documentid, lotid, datecreated, document, 
//docapprefcode , doctypecode, docnum, reference,
//docref, notifyname, notifyemail, docstatus, 
//statuschanged, docautorizacion, docautorizaciondate    
    private UUID documentid;
    private UUID lotid;
    private Date datecreated;
    private byte[] document;
    private String docapprefcode;
    private String doctypecode;
    private String docnum;
    private String reference;
    private byte[] docref;
    private String notifyname;
    private String notifyemail;
    private SriStatus docstatus;
    private Date statuschanged;
    private String docautorizacion;
    private Date docautorizaciondate;

    /**
     *
     * @return
     */
    public UUID getDocumentid() {
        return documentid;
    }

    /**
     *
     * @param documentid
     */
    public void setDocumentid(UUID documentid) {
        this.documentid = documentid;
    }

    /**
     *
     * @return
     */
    public UUID getLotid() {
        return lotid;
    }

    /**
     *
     * @param lotid
     */
    public void setLotid(UUID lotid) {
        this.lotid = lotid;
    }

    /**
     *
     * @return
     */
    public Date getDatecreated() {
        return datecreated;
    }

    /**
     *
     * @param datecreated
     */
    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    /**
     *
     * @return
     */
    public byte[] getDocument() {
        return document;
    }

    /**
     *
     * @param document
     */
    public void setDocument(byte[] document) {
        this.document = document;
    }

    /**
     *
     * @return
     */
    public String getDocapprefcode() {
        return docapprefcode;
    }

    /**
     *
     * @param docapprefcode
     */
    public void setDocapprefcode(String docapprefcode) {
        this.docapprefcode = docapprefcode;
    }

    /**
     *
     * @return
     */
    public String getDoctypecode() {
        return doctypecode;
    }

    /**
     *
     * @param doctypecode
     */
    public void setDoctypecode(String doctypecode) {
        this.doctypecode = doctypecode;
    }

    /**
     *
     * @return
     */
    public String getDocnum() {
        return docnum;
    }

    /**
     *
     * @param docnum
     */
    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }

    /**
     *
     * @return
     */
    public String getReference() {
        return reference;
    }

    /**
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     *
     * @return
     */
    public byte[] getDocref() {
        return docref;
    }

    /**
     *
     * @param docref
     */
    public void setDocref(byte[] docref) {
        this.docref = docref;
    }

    /**
     *
     * @return
     */
    public String getNotifyname() {
        return notifyname;
    }

    /**
     *
     * @param notifyname
     */
    public void setNotifyname(String notifyname) {
        this.notifyname = notifyname;
    }

    /**
     *
     * @return
     */
    public String getNotifyemail() {
        return notifyemail;
    }

    /**
     *
     * @param notifyemail
     */
    public void setNotifyemail(String notifyemail) {
        this.notifyemail = notifyemail;
    }

    /**
     *
     * @return
     */
    public SriStatus getDocstatus() {
        return docstatus;
    }

    /**
     *
     * @param doctatus
     */
    public void setDocstatus(SriStatus doctatus) {
        this.docstatus = doctatus;
    }

    /**
     *
     * @return
     */
    public Date getStatuschanged() {
        return statuschanged;
    }

    /**
     *
     * @param statuschanged
     */
    public void setStatuschanged(Date statuschanged) {
        this.statuschanged = statuschanged;
    }

    /**
     *
     * @return
     */
    public String getDocautorizacion() {
        return docautorizacion;
    }

    /**
     *
     * @param docautorizacion
     */
    public void setDocautorizacion(String docautorizacion) {
        this.docautorizacion = docautorizacion;
    }

    /**
     *
     * @return
     */
    public Date getDocautorizaciondate() {
        return docautorizaciondate;
    }

    /**
     *
     * @param docautorizaciondate
     */
    public void setDocautorizaciondate(Date docautorizaciondate) {
        this.docautorizaciondate = docautorizaciondate;
    }
        
}
