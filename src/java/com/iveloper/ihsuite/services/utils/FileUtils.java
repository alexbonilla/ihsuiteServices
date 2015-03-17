/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Alex
 */
public class FileUtils {

    public static String selectXsd(String tipo) {
        String nombreXsd = null;
        if (tipo.equals(TipoComprobanteEnum.FACTURA.getCode())) {
            nombreXsd = TipoComprobanteEnum.FACTURA.getXsd();
        } else if (tipo.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCode())) {
            nombreXsd = TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getXsd();
        } else if (tipo.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getCode())) {
            nombreXsd = TipoComprobanteEnum.GUIA_DE_REMISION.getXsd();
        } else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getCode())) {
            nombreXsd = TipoComprobanteEnum.NOTA_DE_CREDITO.getXsd();
        } else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getCode())) {
            nombreXsd = TipoComprobanteEnum.NOTA_DE_DEBITO.getXsd();
        } else if (tipo.equals(TipoComprobanteEnum.LOTE.getCode())) {
            nombreXsd = TipoComprobanteEnum.LOTE.getXsd();
        }
        return nombreXsd;
    }
    
    /**
     *
     * @param xmlSource
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static Document stringToDom(String xmlSource)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    /**
     *
     * @param xmlString
     * @return
     * @throws TransformerConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static InputStream xmlStringToInputStream(String xmlString) throws TransformerConfigurationException, SAXException, TransformerException, ParserConfigurationException, IOException {
        Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Convirtiendo xmlString en InputStream.");
        Document xmlDoc = stringToDom(xmlString);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlDoc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        return is;
    }
    
    /**
     *
     * @param xmlString
     * @return
     * @throws TransformerConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static ByteArrayOutputStream xmlStringToByteArrayOutputStream(String xmlString) throws TransformerConfigurationException, SAXException, TransformerException, ParserConfigurationException, IOException {
        Logger.getLogger(FileUtils.class.getName()).log(Level.INFO, "Convirtiendo xmlString en ByteArrayOutputStream.");
        Document xmlDoc = stringToDom(xmlString);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(xmlDoc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);        
        return outputStream;
    }
}
