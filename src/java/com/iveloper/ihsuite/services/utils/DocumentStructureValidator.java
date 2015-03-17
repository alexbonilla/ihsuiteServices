/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.utils;

//import java.io.IOException;


import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

//import java.io.InputStream;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.validation.*;
//import org.xml.sax.SAXException;


/**
 *
 * @author Rolando
 */
public class DocumentStructureValidator {

    private StreamSource xsdFile;
    private StreamSource xmlFile;

    public DocumentStructureValidator(InputStream strXsdFile, InputStream strXmlFile) {
        this.xsdFile = new StreamSource(strXsdFile);
        this.xmlFile = new StreamSource(strXmlFile);
    }

    public String validate() {
        validateFile(xsdFile, "xsdFile");
        validateFile(xmlFile, "xmlFile");
        String message = null;
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        try {
            schema = schemaFactory.newSchema(xsdFile);
        } catch (SAXException e) {
            throw new IllegalStateException("Existe un error en la sintaxis del esquema", e);
        }
        Validator validator = schema.newValidator();
        try {
            validator.validate(xmlFile);
        } catch (SAXException | IOException e) {
            return e.getMessage();
        }
        return message;
    }

    protected void validateFile(StreamSource file, String name)
            throws IllegalStateException {
        if (null == file) {
            throw new IllegalStateException((new StringBuilder()).append(name).append(" es nulo o esta vacio").toString());
        } else {
            return;
        }
    }
}
