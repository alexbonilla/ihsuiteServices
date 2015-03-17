/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.ws;

import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantes;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

import com.iveloper.comprobantes.utils.ArchivoUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;


/**
 *
 * @author Alex
 */
public class EnvioComprobantesWs {

    private static RecepcionComprobantesService service;

    /**
     *
     * @param wsdlLocation
     * @throws MalformedURLException
     * @throws WebServiceException
     */
    public EnvioComprobantesWs(String wsdlLocation) throws MalformedURLException, WebServiceException {
        URL url = new URL(wsdlLocation);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
        service = new RecepcionComprobantesService(url, qname);
    }

    /**
     *
     * @param wsdlLocation
     * @return
     */
    public static final Object webService(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesService");
            URL url = new URL(wsdlLocation);
            service = new RecepcionComprobantesService(url, qname);
            return null;
        } catch (MalformedURLException | WebServiceException ex) {
            return ex;
        }
    }

    /**
     *
     * @param xmlbos
     * @return
     * @throws Exception
     */
    public RespuestaSolicitud enviarComprobante(ByteArrayOutputStream xmlbos) throws Exception {
        RespuestaSolicitud response = null;
        RecepcionComprobantes port = service.getRecepcionComprobantesPort();
        byte[] archivoBytes = xmlbos.toByteArray();
        if (archivoBytes != null) {
            response = port.validarComprobante(archivoBytes);
        }
        return response;
    }

    /**
     *
     * @param ruc
     * @param xml
     * @param tipoComprobante
     * @param versionXsd
     * @return
     */
    public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte xml[], String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantes port = service.getRecepcionComprobantesPort();
            response = port.validarComprobante(xml);
        } catch (Exception e) {
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
        return response;
    }

    /**
     *
     * @param ruc
     * @param xml
     * @param tipoComprobante
     * @param versionXsd
     * @return
     */
    public RespuestaSolicitud enviarComprobanteLotes(String ruc, File xml, String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantes port = service.getRecepcionComprobantesPort();
            response = port.validarComprobante(ArchivoUtils.convertirArchivoAByteArray(xml));
        } catch (IOException e) {
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
        return response;
    }

    /**
     *
     * @param respuesta
     * @return
     */
    public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta) {
        StringBuilder mensajeDesplegable = new StringBuilder();
        if (respuesta == null) {
            return "";
        }
        if (respuesta.getEstado().equals("DEVUELTA")) {
            RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();
            for (Iterator i1 = comprobantes.getComprobante().iterator(); i1.hasNext(); mensajeDesplegable.append("\n")) {
                Comprobante comp = (Comprobante) i1.next();
                mensajeDesplegable.append(comp.getClaveAcceso());
                mensajeDesplegable.append("\n");
                for (Iterator i2 = comp.getMensajes().getMensaje().iterator(); i2.hasNext(); mensajeDesplegable.append("\n")) {
                    Mensaje m = (Mensaje) i2.next();
                    mensajeDesplegable.append(m.getMensaje()).append(" :\n");
                    mensajeDesplegable.append(m.getInformacionAdicional() == null ? "" : m.getInformacionAdicional());
                }

            }
        } else {
            mensajeDesplegable.append(respuesta.getEstado());
        }
        return mensajeDesplegable.toString();
    }
}
