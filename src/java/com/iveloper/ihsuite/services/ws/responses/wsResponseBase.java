/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iveloper.ihsuite.services.ws.responses;

import java.util.List;

/**
 *
 * @author Alex
 */
public class wsResponseBase {
    private List<String> MessageException;
    private boolean Processed;

    /**
     *
     * @return
     */
    public List<String> getMessageException() {
        return MessageException;
    }

    /**
     *
     * @param MessageException
     */
    public void setMessageException(List<String> MessageException) {
        this.MessageException = MessageException;
    }

    /**
     *
     * @return
     */
    public boolean isProcessed() {
        return Processed;
    }

    /**
     *
     * @param Processed
     */
    public void setProcessed(boolean Processed) {
        this.Processed = Processed;
    }

    
}
