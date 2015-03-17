/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.security.signer;

/**
 *
 * @author Alex
 */
import com.iveloper.ihsuite.services.entities.Certificate;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Alex Bonilla
 */
public class Signer extends GenericXMLSignature {

    private InputStream isToSign;

    private Certificate certificate;

    /**
     *
     * @param isToSign
     * @param certificate
     */
    public Signer(InputStream isToSign, Certificate certificate) {
        this.isToSign = isToSign;
        this.certificate = certificate;
    }

    @Override
    protected DataToSign createDataToSign() throws ParserConfigurationException, SAXException, IOException {
        DataToSign dataToSign = new DataToSign();

        dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
        dataToSign.setEsquema(XAdESSchemas.XAdES_132);
        dataToSign.setXMLEncoding("UTF-8");
        dataToSign.setEnveloped(true);
        dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "Documento de ejemplo", null, "text/xml", null));
        Document docToSign = getDocument(this.isToSign);
        dataToSign.setDocument(docToSign);

        return dataToSign;

    }

    /**
     *
     * @return @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws CertStoreException
     * @throws KeyStoreException
     * @throws Exception
     */
    public ByteArrayOutputStream sign() throws NoSuchAlgorithmException, CertificateException, IOException, CertStoreException, KeyStoreException, Exception {
        return execute(this.certificate);

    }
}
