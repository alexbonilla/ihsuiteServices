/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.ws;

import com.iveloper.comprobantes.security.signer.Signer;
import com.iveloper.ihsuite.services.entities.Certificate;
import com.iveloper.ihsuite.services.entities.Document;
import com.iveloper.ihsuite.services.entities.Lot;
import com.iveloper.ihsuite.services.entities.ProcessLog;
import com.iveloper.ihsuite.services.jpa.CertificateJpaController;
import com.iveloper.ihsuite.services.jpa.DocumentJpaController;
import com.iveloper.ihsuite.services.jpa.LotJpaController;
import com.iveloper.ihsuite.services.jpa.ProcessLogJpaController;
import com.iveloper.ihsuite.services.jpa.ValidatorJpaController;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import com.iveloper.ihsuite.services.utils.FileUtils;
import com.iveloper.ihsuite.services.utils.LotType;
import com.iveloper.ihsuite.services.utils.SriStatus;
import com.iveloper.ihsuite.services.ws.responses.DocFacInstance;
import com.iveloper.ihsuite.services.ws.responses.wsResponse;
import com.iveloper.ihsuite.services.ws.responses.wsResponseData;
import com.iveloper.ihsuite.services.security.LoginBean;
import es.mityc.javasign.pkstore.CertStoreException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author alexbonilla
 */
@WebService(serviceName = "Operations")
public class Operations {

    private LoginBean loginBean;

    private boolean verifyCredentials(String user, String pass, String userEntityId) {
        if (loginBean == null) {
            loginBean = new LoginBean();
        }
        
        loginBean.setUname(user);
        loginBean.setPassword(pass);
        loginBean.setEntityid(userEntityId);
        return loginBean.verifyAccount();
    }

    private void processLogEntry(String userEntityId, Lot lot, String type, String reference, String message) {
        Logger.getLogger(Operations.class.getName()).log(Level.parse(type), "{0} {1}", new Object[]{reference, message});
        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
            ProcessLogJpaController processLogJpaController = new ProcessLogJpaController(utx, emf);
            ProcessLog processLog = new ProcessLog();
            processLog.setId(UUID.randomUUID().toString());
            processLog.setDateEntered(new Date());
            processLog.setLotId(lot);
            processLog.setType(type);
            processLog.setReference("Operations WS " + reference);
            processLog.setMessage(message);

            processLogJpaController.create(processLog);
        } catch (NamingException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Crea un Lote para enviar documentos
     *
     * @param description Una descripción general del lote, para referencia o
     * control, por ejemplo: el mes, una fecha, la semana, el grupo de clientes
     * que se va a armar el lote.
     * @param typeLot El tipo de Lote.
     * @param user Usuario del Portal.
     * @param pass Password del usuario indicado.
     * @param userEntityId ID (uuid) de la empresa del usuario indicado.
     * @param notiEmailInternal Email para notificación interna de lo que sucede
     * con los documentos del lote, tambien conocido como email del Operador o
     * del usuario que esta procesando el documento o el lote.
     * @return Devuelve un objecto de tipo wsResponse, con 2 valores
     * principales: LotId: Id (uuid) interno del Lote y LotNumber: Número del
     * Lote creado.
     */
    @WebMethod(operationName = "CreateLot")
    public wsResponse CreateLot(@WebParam(name = "description") String description, @WebParam(name = "typeLot") LotType typeLot, @WebParam(name = "user") String user, @WebParam(name = "pass") String pass, @WebParam(name = "userEntityId") String userEntityId, @WebParam(name = "notiEmailInternal") String notiEmailInternal) {
        wsResponse res = new wsResponse();
        if (verifyCredentials(user, pass, userEntityId)) {
            try {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                LotJpaController lotController = new LotJpaController(utx, emf);

                Lot lot = new Lot();
                lot.setId(UUID.randomUUID().toString());
                lot.setLotNumber(UUID.randomUUID().toString());
                lot.setLotOpen(true);
                lot.setLotType(typeLot.toString());
                lot.setDateEntered(new Date());
                lot.setInProcess(Boolean.FALSE);
                lot.setProcessed(Boolean.FALSE);
                lotController.create(lot);

                if (lotController.findLot(lot.getId()) != null) {
                    res.setProcessed(true);
                    res.setLotId(lot.getId());
                    res.setLotNumber(lot.getLotNumber());
                    processLogEntry(userEntityId, lot, Level.INFO.toString(), "CreateLot", "Nuevo Lote " + lot.getId());

                } else {
                    res.setProcessed(false);
                    String[] messages = {"No se pudo crear Lote"};
                    res.setMessageException(Arrays.asList(messages));
                    processLogEntry(userEntityId, null, Level.WARNING.toString(), "CreateLot", "No se pudo crear Lote");
                }
            } catch (NamingException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RollbackFailureException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            res.setProcessed(false);
            String[] messages = {"Credenciales incorrectas"};
            res.setMessageException(Arrays.asList(messages));
            processLogEntry(userEntityId, null, Level.WARNING.toString(), "CreateLot", "Credenciales incorrectas");
        }
        return res;
    }

    /**
     * Permite enviar un string XML, con un adjunto PDF (en bytes)
     * opcionalmente, especificando el tipo de aplicacion que envia el
     * documento. docAppRefCode, este parámetro sirve cuando una empresa tiene
     * diferentes sistemas (ERP) para generar los documentos de venta, y asi
     * poder indentificar que software es el que generó dicho documento.
     * Incluso, se puede configurar parámetros específicos para un tipo de
     * documento y software (segun este campo).
     *
     * @param lotId Id (uuid) del lote, obtenido previamente por el metodo
     * CreateLot.
     * @param docAppRefCode Codigo o referencia del tipo de aplicación/software.
     * @param xmlString La cadena de texto XML.
     * @param docTypeCode El tipo de documento segun la codificación del SRI.
     * @param docNum El número del documento en formato ###-###-#########
     * ejemplo: 001-001-000000305.
     * @param reference Una referencia o glosa general del documento.
     * @param docRef El documento PDF a adjuntar en Bytes.
     * @param sendNotification True o False Determina si procesa o no la
     * notificación al receptor, si es False solo procesa hasta obtener la
     * autorización del documento.
     * @param user Usuario del Portal
     * @param clientContact Objeto que contiene los datos del contacto a quien
     * entregar el documento
     * @param pass Password del usuario indicado
     * @param userEntityId ID (uuid) de la empresa del usuario indicado
     * @return Devuelve un objecto de tipo wsResponse, con 1 valor principal:
     * DoumentId: Id (uuid) interno del documento.
     */
    @WebMethod(operationName = "AddFilesWithAppNoAccessKey")
    public wsResponse AddFilesWithAppNoAccessKey(@WebParam(name = "lotId") String lotId, @WebParam(name = "docAppRefCode") String docAppRefCode, @WebParam(name = "xmlString") String xmlString, @WebParam(name = "docTypeCode") String docTypeCode, @WebParam(name = "docNum") String docNum, @WebParam(name = "reference") String reference, @WebParam(name = "docRef") byte[] docRef, @WebParam(name = "sendNotification") boolean sendNotification, @WebParam(name = "clientContact") com.iveloper.ihsuite.services.utils.ClientContactObject clientContact, @WebParam(name = "user") String user, @WebParam(name = "pass") String pass, @WebParam(name = "userEntityId") String userEntityId) {
        wsResponse res = new wsResponse();
        if (verifyCredentials(user, pass, userEntityId)) {
            try {

                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                ValidatorJpaController validatorsController = new ValidatorJpaController(utx, emf);
                InputStream is = FileUtils.xmlStringToInputStream(xmlString);
                String validation = validatorsController.validateAgainstSchema(docTypeCode, is);

                LotJpaController lotController = new LotJpaController(utx, emf);
                Lot thisFileLot = lotController.findLot(lotId);

                if (validation == null) {
                    processLogEntry(userEntityId, thisFileLot, Level.INFO.toString(), "AddFilesWithApp", "El archivo tiene una construcción válida");

                    CertificateJpaController certificateController = new CertificateJpaController(utx, emf);
                    Certificate certificate = certificateController.findCertificate(userEntityId);
                    Signer signer = new Signer(FileUtils.xmlStringToInputStream(xmlString), certificate);
                    ByteArrayOutputStream bos = signer.sign();

                    processLogEntry(userEntityId, thisFileLot, Level.INFO.toString(), "AddFilesWithApp", "Se firmó el documento");

                    Document document = new Document();
                    document.setId(UUID.randomUUID().toString());
                    document.setLotId(thisFileLot);
                    document.setDocument(bos.toByteArray());
                    document.setDocNum(docNum);
                    document.setDocTypeCode(docTypeCode);
                    document.setDocAppRefCode(docAppRefCode);
                    document.setDocRef(docRef);
                    document.setReference(reference);
                    document.setDocStatus("NO PROCESADO");
                    document.setDateEntered(new Date());

                    if (clientContact != null) {
                        document.setNotifyEmail(clientContact.getEmail1());
                        document.setNotifyName(clientContact.getName());
                        document.setCustomerId(clientContact.getVatNumber());
                    }

                    DocumentJpaController documentController = new DocumentJpaController(utx, emf);
                    documentController.create(document);

                    if (documentController.findDocument(document.getId()) != null) {

                        processLogEntry(userEntityId, document.getLotId(), Level.INFO.toString(), "AddFilesWithApp", "Nuevo Documento " + document.getId());
                        res.setProcessed(true);
                        res.setDocumentId(document.getId());

                    }
                } else {
                    processLogEntry(userEntityId, null, Level.WARNING.toString(), "AddFilesWithApp", "El archivo está mal construido o vacío: " + validation);
                    res.setProcessed(false);
                    res.setMessageException(Arrays.asList("El archivo está mal construido o vacio: " + validation));
                }

            } catch (IOException | ClassNotFoundException | SQLException | SAXException | TransformerException | ParserConfigurationException | CertificateException | CertStoreException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                res.setProcessed(false);
                res.setMessageException(Arrays.asList(ex.toString()));
            } catch (Exception ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                res.setProcessed(false);
                res.setMessageException(Arrays.asList(ex.toString()));
            }
        } else {
            res.setProcessed(false);
            String[] messages = {"Credenciales incorrectas"};
            res.setMessageException(Arrays.asList(messages));
            processLogEntry(userEntityId, null, Level.WARNING.toString(), "AddFilesWithApp", "Credenciales incorrectas");
        }
        return res;
    }
    /**
     * Permite enviar un string XML, con un adjunto PDF (en bytes)
     * opcionalmente, especificando el tipo de aplicacion que envia el
     * documento. docAppRefCode, este parámetro sirve cuando una empresa tiene
     * diferentes sistemas (ERP) para generar los documentos de venta, y asi
     * poder indentificar que software es el que generó dicho documento.
     * Incluso, se puede configurar parámetros específicos para un tipo de
     * documento y software (segun este campo).
     *
     * @param lotId Id (uuid) del lote, obtenido previamente por el metodo
     * CreateLot.
     * @param docAppRefCode Codigo o referencia del tipo de aplicación/software.
     * @param xmlString La cadena de texto XML.
     * @param docTypeCode El tipo de documento segun la codificación del SRI.
     * @param docNum El número del documento en formato ###-###-#########
     * ejemplo: 001-001-000000305.
     * @param reference Una referencia o glosa general del documento.
     * @param docRef El documento PDF a adjuntar en Bytes.
     * @param sendNotification True o False Determina si procesa o no la
     * notificación al receptor, si es False solo procesa hasta obtener la
     * autorización del documento.
     * @param user Usuario del Portal
     * @param clientContact Objeto que contiene los datos del contacto a quien
     * entregar el documento
     * @param pass Password del usuario indicado
     * @param userEntityId ID (uuid) de la empresa del usuario indicado
     * @param accessKey ID (uuid) de la empresa del usuario indicado
     * @return Devuelve un objecto de tipo wsResponse, con 1 valor principal:
     * DoumentId: Id (uuid) interno del documento.
     */
    @WebMethod(operationName = "AddFilesWithApp")
    public wsResponse AddFilesWithApp(@WebParam(name = "lotId") String lotId, @WebParam(name = "docAppRefCode") String docAppRefCode, @WebParam(name = "xmlString") String xmlString, @WebParam(name = "docTypeCode") String docTypeCode, @WebParam(name = "docNum") String docNum, @WebParam(name = "reference") String reference, @WebParam(name = "docRef") byte[] docRef, @WebParam(name = "sendNotification") boolean sendNotification, @WebParam(name = "clientContact") com.iveloper.ihsuite.services.utils.ClientContactObject clientContact, @WebParam(name = "user") String user, @WebParam(name = "pass") String pass, @WebParam(name = "userEntityId") String userEntityId, @WebParam(name = "accessKey") String accessKey) {
        wsResponse res = new wsResponse();
        if (verifyCredentials(user, pass, userEntityId)) {
            try {

                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                ValidatorJpaController validatorsController = new ValidatorJpaController(utx, emf);
                InputStream is = FileUtils.xmlStringToInputStream(xmlString);
                String validation = validatorsController.validateAgainstSchema(docTypeCode, is);

                LotJpaController lotController = new LotJpaController(utx, emf);
                Lot thisFileLot = lotController.findLot(lotId);

                if (validation == null) {
                    processLogEntry(userEntityId, thisFileLot, Level.INFO.toString(), "AddFilesWithApp", "El archivo tiene una construcción válida");

                    CertificateJpaController certificateController = new CertificateJpaController(utx, emf);
                    Certificate certificate = certificateController.findCertificate(userEntityId);
                    Signer signer = new Signer(FileUtils.xmlStringToInputStream(xmlString), certificate);
                    ByteArrayOutputStream bos = signer.sign();

                    processLogEntry(userEntityId, thisFileLot, Level.INFO.toString(), "AddFilesWithApp", "Se firmó el documento");

                    Document document = new Document();
                    document.setId(UUID.randomUUID().toString());
                    document.setLotId(thisFileLot);
                    document.setDocument(bos.toByteArray());
                    document.setDocNum(docNum);
                    document.setDocTypeCode(docTypeCode);
                    document.setDocAppRefCode(docAppRefCode);
                    document.setDocRef(docRef);
                    document.setReference(reference);
                    document.setDocStatus("NO PROCESADO");
                    document.setDateEntered(new Date());
                    document.setAccessKey(accessKey);

                    if (clientContact != null) {
                        document.setNotifyEmail(clientContact.getEmail1());
                        document.setNotifyName(clientContact.getName());
                        document.setCustomerId(clientContact.getVatNumber());
                    }

                    DocumentJpaController documentController = new DocumentJpaController(utx, emf);
                    documentController.create(document);

                    if (documentController.findDocument(document.getId()) != null) {

                        processLogEntry(userEntityId, document.getLotId(), Level.INFO.toString(), "AddFilesWithApp", "Nuevo Documento " + document.getId());
                        res.setProcessed(true);
                        res.setDocumentId(document.getId());

                    }
                } else {
                    processLogEntry(userEntityId, null, Level.WARNING.toString(), "AddFilesWithApp", "El archivo está mal construido o vacío: " + validation);
                    res.setProcessed(false);
                    res.setMessageException(Arrays.asList("El archivo está mal construido o vacio: " + validation));
                }

            } catch (IOException | ClassNotFoundException | SQLException | SAXException | TransformerException | ParserConfigurationException | CertificateException | CertStoreException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                res.setProcessed(false);
                res.setMessageException(Arrays.asList(ex.toString()));
            } catch (Exception ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
                res.setProcessed(false);
                res.setMessageException(Arrays.asList(ex.toString()));
            }
        } else {
            res.setProcessed(false);
            String[] messages = {"Credenciales incorrectas"};
            res.setMessageException(Arrays.asList(messages));
            processLogEntry(userEntityId, null, Level.WARNING.toString(), "AddFilesWithApp", "Credenciales incorrectas");
        }
        return res;
    }

    /**
     * Obtiene los datos inherentes a los posibles estados e instancias del
     * documento.
     *
     * @param documentId Id (uuid) del documento en InvoiceHub, se obtuvo en
     * cualquiera de los metodos Add
     *
     * @param user Usuario del Portal
     * @param pass Password del usuario indicado
     * @param userEntityId ID (uuid) de la empresa del usuario indicado
     * @return Devuelve un objecto de tipo wsResponseData, con los datos
     * correspondientes si encuentra la clave especificada.
     */
    @WebMethod(operationName = "GetData")
    public wsResponseData GetData(@WebParam(name = "documentId") String documentId, @WebParam(name = "user") String user, @WebParam(name = "pass") String pass, @WebParam(name = "userEntityId") String userEntityId) {
        wsResponseData wres = new wsResponseData();
        wres.setDocumentId(documentId);
        wres.setProcessed(false);

        LoginBean loginbean = new LoginBean();
        loginbean.setUname(user);
        loginbean.setPassword(pass);
        loginbean.setEntityid(userEntityId);
        boolean verifiedAccount = loginbean.verifyAccount();
        if (verifiedAccount) {
            try {

                EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
                Context c = new InitialContext();
                UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
                DocumentJpaController documentController = new DocumentJpaController(utx, emf);
                Document document = documentController.findDocument(documentId);

                if (document != null) {
                    DocFacInstance documentIns = new DocFacInstance();
                    documentIns.setDocumentid(UUID.fromString(documentId));
                    documentIns.setLotid(UUID.fromString(document.getLotId().getId()));
                    documentIns.setDatecreated(document.getDateEntered());
                    documentIns.setDocument(document.getDocument());
                    documentIns.setDocapprefcode(document.getDocAppRefCode());
                    documentIns.setDoctypecode(document.getDocTypeCode());
                    documentIns.setDocnum(document.getDocNum());
                    documentIns.setReference(document.getReference());
                    documentIns.setDocref(document.getDocRef());
                    documentIns.setNotifyname(document.getNotifyName());
                    documentIns.setNotifyemail(document.getNotifyEmail());
                    documentIns.setDocstatus(SriStatus.retornaSriStatus(document.getDocStatus()));
                    documentIns.setStatuschanged(document.getStatusChanged());
                    documentIns.setDocautorizacion(document.getDocAutorizacion());
                    documentIns.setDocautorizaciondate(document.getDocAutorizacionDate());

                    wres.setProcessed(true);
                    wres.setDocumentStatus(SriStatus.retornaSriStatus(document.getDocStatus()));
                    wres.setDocumentInstance(documentIns);
                    processLogEntry(userEntityId, document.getLotId(), Level.INFO.toString(), "GetData", "Se obtuvo información de documento " + document.getId());
                    if (documentIns.getDocstatus() == SriStatus.Autorizada) {
                        wres.setDocumentAutorizacion(document.getDocAutorizacion());
                        wres.setDocumentAutorizacionDate(document.getDocAutorizacionDate());
                    }
                } else {
                    processLogEntry(userEntityId, null, Level.INFO.toString(), "GetData", "No se obtuvo información de documento " + documentId);
                }
            } catch (NamingException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            wres.setProcessed(false);
            wres.setMessageException(Arrays.asList("Wrong credentials"));
            processLogEntry(userEntityId, null, Level.WARNING.toString(), "GetData", "Credenciales incorrectas");
        }
        return wres;
    }
}
