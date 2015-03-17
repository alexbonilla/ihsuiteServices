package com.iveloper.ihsuite.services.processes;

import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante.Autorizaciones;

import com.iveloper.comprobantes.ws.AutorizacionComprobantesWs;
import com.iveloper.comprobantes.ws.EnvioComprobantesWs;
import com.iveloper.comprobantes.utils.CertificadosSSL;
import com.iveloper.comprobantes.utils.WSUtil;
import com.iveloper.ihsuite.services.entities.Document;
import com.iveloper.ihsuite.services.jpa.DocumentJpaController;
import com.iveloper.ihsuite.services.jpa.exceptions.RollbackFailureException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Alex
 */
public class DocumentProcessor implements Runnable {

    private int timeToWaitForAuthorization = 3000;
    private String ambiente;
    private Document document;
    private String userEntityId;
    private String processMode = "Complete";

    public int getTimeToWaitForAuthorization() {
        return timeToWaitForAuthorization;
    }

    public void setTimeToWaitForAuthorization(int timeToWaitForAuthorization) {
        this.timeToWaitForAuthorization = timeToWaitForAuthorization;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getUserEntityId() {
        return userEntityId;
    }

    public void setUserEntityId(String userEntityId) {
        this.userEntityId = userEntityId;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getProcessMode() {
        return processMode;
    }

    public void setProcessMode(String processMode) {
        this.processMode = processMode;
    }

    @Override
    public void run() {
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Configuraciones de Proceso Completo de Documento");
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "ID de Documento: {0}", document.getId());
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "AccessKey de Documento: {0}", document.getAccessKey());
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "ID de Entidad: {0}", userEntityId);
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Tiempo de espera por autorizacion: {0}", timeToWaitForAuthorization);
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Ambiente: {0}", ambiente);
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Modo de proceso: {0}", processMode);

        if (processMode.equals("Complete")) {
            completeProcess();
        } else {
            verifyAuthorization();
        }

    }

    private void completeProcess() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
            DocumentJpaController documentController = new DocumentJpaController(utx, emf);

            if (document.getDocStatus().equals("NoProcesada")) {
                RespuestaSolicitud resultadoRecepcion = enviarComprobante(document, ambiente);
                Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Estado de documento: {0}.", resultadoRecepcion.getEstado());
                Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Detalle: {0}.", EnvioComprobantesWs.obtenerMensajeRespuesta(resultadoRecepcion));

                document.setDocStatus(resultadoRecepcion.getEstado());
                document.setStatusChanged(new Date());

                documentController.edit(document);
            } else if (document.getDocStatus().equals("RECIBIDA")){
                Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "El documento {0}  ya estaba recibido.", document.getDocNum());
            }

            String claveacceso = document.getAccessKey();
            if (document.getDocStatus().equals("RECIBIDA")) {
                Thread.sleep(timeToWaitForAuthorization);
                Autorizacion autComprobanteIndividual = autorizarComprobanteIndividual(claveacceso, ambiente);
                byte[] documento = DocumentProcessorUtils.convertAutorizacionToByteArrayOutputStream(autComprobanteIndividual).toByteArray();
                if (autComprobanteIndividual.getEstado().equals("AUTORIZADO")) {
                    String docAutorizacion = autComprobanteIndividual.getNumeroAutorizacion();
                    Date docAutorizacionDate = autComprobanteIndividual.getFechaAutorizacion().toGregorianCalendar().getTime();
                    document.setDocument(documento);
                    document.setDocStatus(autComprobanteIndividual.getEstado());
                    document.setDocAutorizacion(docAutorizacion);
                    document.setDocAutorizacionDate(docAutorizacionDate);

                    documentController.edit(document);

                } else {
                    document.setDocument(documento);
                    document.setDocStatus(autComprobanteIndividual.getEstado());

                    documentController.edit(document);
                }

                verifyAuthorizationLog(autComprobanteIndividual);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void verifyAuthorization() {
        try {

            String claveacceso = document.getAccessKey();

            Autorizacion autComprobanteIndividual = autorizarComprobanteIndividual(claveacceso, ambiente);
            byte[] documento = DocumentProcessorUtils.convertAutorizacionToByteArrayOutputStream(autComprobanteIndividual).toByteArray();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ihsuite" + userEntityId + "PU");
            Context c = new InitialContext();
            UserTransaction utx = (UserTransaction) c.lookup("java:comp/UserTransaction");
            DocumentJpaController documentController = new DocumentJpaController(utx, emf);
            if (autComprobanteIndividual.getEstado().equals("AUTORIZADO")) {
                String docAutorizacion = autComprobanteIndividual.getNumeroAutorizacion();
                Date docAutorizacionDate = autComprobanteIndividual.getFechaAutorizacion().toGregorianCalendar().getTime();
                document.setDocument(documento);
                document.setDocStatus(autComprobanteIndividual.getEstado());
                document.setDocAutorizacion(docAutorizacion);
                document.setDocAutorizacionDate(docAutorizacionDate);

                documentController.edit(document);

            } else {
                document.setDocument(documento);
                document.setDocStatus(autComprobanteIndividual.getEstado());

                documentController.edit(document);
            }

            verifyAuthorizationLog(autComprobanteIndividual);
        } catch (SQLException | JAXBException | IOException | SAXException | ParserConfigurationException | ClassNotFoundException | NamingException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DocumentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verifyAuthorizationLog(Autorizacion aut) {
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Estado de documento: {0}", aut.getEstado());

        StringBuilder msgAutorizacion = new StringBuilder();
        String estado = aut.getEstado().toUpperCase();
        if (estado.compareTo("AUTORIZADO") == 0) {
            msgAutorizacion.append(estado);
        } else {
            List<Mensaje> mensajes = aut.getMensajes().getMensaje();
            for (Mensaje mensaje : mensajes) {
                msgAutorizacion.append("NO AUTORIZADO").append(".").append(mensaje.getTipo()).append(".").append(mensaje.getIdentificador()).append(".").append(mensaje.getMensaje()).append(".").append(mensaje.getInformacionAdicional()).append("\n");
            }
        }
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Detalles: {0}", msgAutorizacion.toString());
    }

    public RespuestaSolicitud enviarComprobante(Document document, String ambiente) throws MalformedURLException, Exception {
        CertificadosSSL.instalarCertificados();
        EnvioComprobantesWs ec = new EnvioComprobantesWs(WSUtil.devuelveUrlWs(ambiente, "RecepcionComprobantes"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream(document.getDocument().length);
        baos.write(document.getDocument(), 0, document.getDocument().length);
        RespuestaSolicitud response = ec.enviarComprobante(baos);
        return response;
    }

    public Autorizacion autorizarComprobanteIndividual(String claveDeAcceso, String tipoAmbiente) throws JAXBException, IOException {
        CertificadosSSL.instalarCertificados();
        RespuestaComprobante respuesta = (new AutorizacionComprobantesWs(WSUtil.devuelveUrlWs(tipoAmbiente, "AutorizacionComprobantes"))).llamadaWSAutorizacionInd(claveDeAcceso);
        Logger.getLogger(DocumentProcessor.class.getName()).log(Level.INFO, "Numero de comprobantes en respuesta de autorizacion: {0}", respuesta.getNumeroComprobantes());
        Autorizaciones autorizaciones = respuesta.getAutorizaciones();
        ArrayList<Autorizacion> autorizacionesList = new ArrayList(autorizaciones.getAutorizacion());
        Autorizacion aut = null;
        for (Autorizacion autorizacion : autorizacionesList) {
            aut = autorizacion;
        }
        return aut;
    }
}
