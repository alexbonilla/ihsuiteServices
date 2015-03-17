/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.ws;

import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantes;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesService;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 *
 * @author Alex
 */
public class AutorizacionComprobantesWs {

    private final AutorizacionComprobantesService service;

    /**
     *
     * @param wsdlLocation
     * @throws MalformedURLException
     */
    public AutorizacionComprobantesWs(String wsdlLocation) throws MalformedURLException {

        service = new AutorizacionComprobantesService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesService"));

    }

    /**
     *
     * @param claveDeAcceso
     * @return
     */
    public RespuestaComprobante llamadaWSAutorizacionInd(String claveDeAcceso) {

        AutorizacionComprobantes port = service.getAutorizacionComprobantesPort();
        java.util.Map<String, Object> requestContext = ((javax.xml.ws.BindingProvider) port).getRequestContext();
        requestContext.put("set-jaxb-validation-event-handler", "false");
        RespuestaComprobante response = port.autorizacionComprobante(claveDeAcceso);

        return response;
    }
}
