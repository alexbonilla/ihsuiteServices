/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iveloper.ihsuite.services.ws.responses;

/**
 *
 * @author Alex
 */
public class wsResponse extends wsResponseBase{
    private String DocumentId;
    private String LotId;
    private String LotNumber;

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
    public String getLotId() {
        return LotId;
    }

    /**
     *
     * @param LotId
     */
    public void setLotId(String LotId) {
        this.LotId = LotId;
    }

    /**
     *
     * @return
     */
    public String getLotNumber() {
        return LotNumber;
    }

    /**
     *
     * @param LotNumber
     */
    public void setLotNumber(String LotNumber) {
        this.LotNumber = LotNumber;
    }
    
}
