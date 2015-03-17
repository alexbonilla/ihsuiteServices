/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.processes;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author alexbonilla
 */
public class DocumentProcessorUtils {

    /**
     *
     * @param aut
     * @return
     */
    public static ByteArrayOutputStream convertAutorizacionToByteArrayOutputStream(Autorizacion aut) {
        try {
            com.iveloper.comprobantes.autorizacion.Autorizacion autorizacion = new com.iveloper.comprobantes.autorizacion.Autorizacion();
            String comprobante = aut.getComprobante();
            comprobante = comprobante.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
            autorizacion.setComprobante((new StringBuilder()).append("<![CDATA[").append(comprobante).append("]]>").toString());
            autorizacion.setEstado(aut.getEstado());
            autorizacion.setFechaAutorizacion(aut.getFechaAutorizacion());
            autorizacion.setMensajes(aut.getMensajes());
            autorizacion.setNumeroAutorizacion(aut.getNumeroAutorizacion());
            JAXBContext context = JAXBContext.newInstance(new Class[]{com.iveloper.comprobantes.autorizacion.Autorizacion.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(bos, "UTF-8");
            marshaller.marshal(autorizacion, out);
            out.close();

            return bos;
        } catch (JAXBException | IOException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static com.iveloper.comprobantes.autorizacion.Autorizacion convertByteArrayToAutorizacion(byte[] authorizationContent) {
        com.iveloper.comprobantes.autorizacion.Autorizacion autorizacion = null;
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new ByteArrayInputStream(authorizationContent)));

            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{com.iveloper.comprobantes.autorizacion.Autorizacion.class});
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            autorizacion = (com.iveloper.comprobantes.autorizacion.Autorizacion) unmarshaller.unmarshal(doc);
            String comprobante = autorizacion.getComprobante();
            comprobante = comprobante.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
            autorizacion.setComprobante((new StringBuilder()).append("<![CDATA[").append(comprobante).append("]]>").toString());

        } catch (ParserConfigurationException | SAXException | IOException | JAXBException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autorizacion;
    }

}
