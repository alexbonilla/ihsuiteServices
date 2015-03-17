package com.iveloper.comprobantes.security.signer;

/**
 *
 * @author Alex
 */
import com.iveloper.ihsuite.services.entities.Certificate;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.keystore.KSStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Clase base que deberían extender los diferentes ejemplos para realizar firmas
 * XML.
 * </p>
 *
 * @author Alex Bonilla
 * @version 2.0
 */
public abstract class GenericXMLSignature {

    /**
     * <p>
     * Ejecución del ejemplo. La ejecución consistirá en la firma de los datos
     * creados por el método abstracto <code>createDataToSign</code> mediante el
     * certificado declarado en la constante <code>PKCS12_FILE</code>. El
     * resultado del proceso de firma será almacenado en un fichero XML en el
     * directorio correspondiente a la constante <code>OUTPUT_DIRECTORY</code>
     * del usuario bajo el nombre devuelto por el método abstracto
     * <code>getSignFileName</code>
     * </p>
     *
     * @param cert Contiene la información del Certificado: El certificado
     * digital en sí, y su clave.
     * @return ByteArrayOutputStream que contiene el stream del archivo firmado.
     * @throws java.security.KeyStoreException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.cert.CertificateException
     * @throws java.io.IOException
     * @throws es.mityc.javasign.pkstore.CertStoreException
     */        
    protected ByteArrayOutputStream execute(Certificate certif) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, CertStoreException, Exception {

        // Obtencion del gestor de claves
        IPKStoreManager storeManager = getPKStoreManager(certif);
        if (storeManager == null) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "No hay almacen de claves.");
        }

        // Obtencion del certificado para firmar. Utilizaremos el primer
        // certificado del almacen.
        X509Certificate certificate = getFirstCertificate(storeManager);
        if (certificate == null) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "No hay certificado.");
        }

        // Obtención de la clave privada asociada al certificado
        PrivateKey privateKey;
        try {
            privateKey = storeManager.getPrivateKey(certificate);
        } catch (CertStoreException e) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "Clave incorrecta.");
            throw e;
        }

        // Obtención del provider encargado de las labores criptográficas
        Provider provider = storeManager.getProvider(certificate);

        /*
         * Creación del objeto que contiene tanto los datos a firmar como la
         * configuración del tipo de firma
         */
        DataToSign dataToSign = createDataToSign();

        // Firmamos el documento
        Document docSigned = null;
        try {
            /*
             * Creación del objeto encargado de realizar la firma
             */
            FirmaXML firma = createFirmaXML();
            Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
            docSigned = (Document) res[0];
        } catch (Exception ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "No existe el objeto firma.");
            throw ex;
        }

        // Guardamos la firma a un fichero en el home del usuario
        return saveDocumentToFile(docSigned);
    }

    /**
     * <p>
     * Crea el objeto DataToSign que contiene toda la información de la firma
     * que se desea realizar. Todas las implementaciones deberán proporcionar
     * una implementación de este método
     * </p>
     *
     * @return El objeto DataToSign que contiene toda la información de la firma
     * a realizar
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    protected abstract DataToSign createDataToSign() throws ParserConfigurationException, SAXException, IOException;

    /**
     * <p>
     * Crea el objeto <code>FirmaXML</code> con las configuraciones necesarias
     * que se encargará de realizar la firma del documento.
     * </p>
     * <p>
     * En el caso más simple no es necesaria ninguna configuración específica.
     * En otros casos podría ser necesario por lo que las implementaciones
     * concretas de las diferentes firmas deberían sobreescribir este método
     * (por ejemplo para añadir una autoridad de sello de tiempo en aquellas
     * firmas en las que sea necesario)
     * <p>
     *
     *
     * @return firmaXML Objeto <code>FirmaXML</code> configurado listo para
     * usarse
     */
    protected FirmaXML createFirmaXML() {
        return new FirmaXML();
    }

    /**
     * <p>
     * Escribe el documento a un fichero.
     * </p>
     *
     * @param document El documento a imprmir
     * @param pathfile El path del fichero donde se quiere escribir.
     */
    private ByteArrayOutputStream saveDocumentToFile(Document document) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        UtilidadTratarNodo.saveDocumentToOutputStream(document, bos, true);
        return bos;
    }

    /**
     * <p>
     * Escribe el documento a un fichero. Esta implementacion es insegura ya que
     * dependiendo del gestor de transformadas el contenido podría ser alterado,
     * con lo que el XML escrito no sería correcto desde el punto de vista de
     * validez de la firma.
     * </p>
     *
     * @param document El documento a imprmir
     * @param pathfile El path del fichero donde se quiere escribir.
     */
    @SuppressWarnings("unused")
    private void saveDocumentToFileUnsafeMode(Document document, String pathfile) throws TransformerException {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();

            serializer.transform(new DOMSource(document), new StreamResult(new File(pathfile)));
        } catch (TransformerException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "XML Erróneo.");
            throw ex;
        }
    }

    /**
     * <p>
     * Devuelve el <code>Document</code> correspondiente al
     * <code>resource</code> pasado como parámetro
     * </p>
     *
     * @param resource El recurso que se desea obtener, cargado en un Documento.
     * @return El <code>Document</code> asociado al <code>resource</code>
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    protected Document getDocument(InputStream resource) throws ParserConfigurationException, SAXException, IOException {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            doc = dbf.newDocumentBuilder().parse(resource);
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.SEVERE, "XML erróneo.");
            throw ex;
        }
        return doc;
    }

    /**
     * <p>
     * Devuelve el contenido del documento XML correspondiente al
     * <code>resource</code> pasado como parámetro
     * </p> como un <code>String</code>
     *
     * @param resource El recurso que se desea obtener como String.
     * @return El contenido del documento XML como un <code>String</code>
     * @throws javax.xml.transform.TransformerException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    protected String getDocumentAsString(InputStream resource) throws TransformerException, ParserConfigurationException, SAXException, IOException {
        Document doc = getDocument(resource);
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        StringWriter stringWriter = new StringWriter();
        try {
            serializer = tfactory.newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        } catch (TransformerException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "XML Erróneo.");
            throw ex;
        }

        return stringWriter.toString();
    }

    /**
     * <p>
     * Devuelve el gestor de claves que se va a utilizar
     * </p>
     *
     * @return El gestor de claves que se va a utilizar</p>
     */
    

    private IPKStoreManager getPKStoreManager(Certificate certif) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

        IPKStoreManager storeManager = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new ByteArrayInputStream(certif.getContent()), certif.getPass().toCharArray());
            storeManager = new KSStore(ks, new PassStoreKS(certif.getPass()));
            for (Enumeration e = ks.aliases(); e.hasMoreElements();) {
                String alias = (String) e.nextElement();
                //System.out.println("@:" + alias);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            Logger.getLogger(GenericXMLSignature.class.getName()).log(Level.WARNING, "Error de clave o no se encuentra archivo firma.");
            throw ex;
        }
        return storeManager;
    }
    
    /**
     * <p>
     * Recupera el primero de los certificados del almacén.
     * </p>
     *
     * @param storeManager Interfaz de acceso al almacén
     * @return Primer certificado disponible en el almacén
     */
    private X509Certificate getFirstCertificate(
            final IPKStoreManager storeManager) {
        List<X509Certificate> certs = null;
        try {
            certs = storeManager.getSignCertificates();
        } catch (CertStoreException ex) {
            System.out.println("E09CERTIFICADO NO DISPONIBLE");
            System.exit(-1);
        }
        if ((certs == null) || (certs.isEmpty())) {
            System.out.println("E10CERTIFICADO VACIO");
            System.exit(-1);
        }

        X509Certificate certificate = certs.get(0);
        return certificate;
    }
}
