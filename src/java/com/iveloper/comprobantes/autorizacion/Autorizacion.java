/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.autorizacion;

import ec.gob.sri.comprobantes.ws.aut.Autorizacion.Mensajes;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 *
 * @author Alex
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "autorizacion", propOrder = {"estado", "numeroAutorizacion", "fechaAutorizacion", "comprobante", "mensajes"})
@XmlRootElement(name = "autorizacion")
public class Autorizacion {

    /**
     *
     */
    protected String estado;

    /**
     *
     */
    protected String numeroAutorizacion;

    /**
     *
     */
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaAutorizacion;    

    /**
     *
     */
    protected String comprobante;

    /**
     *
     */
    protected Mensajes mensajes;

    /**
     *
     */
    public Autorizacion() {
        //compiled code
    }

    /**
     *
     * @return
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     *
     * @param comprobante
     */
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    /**
     *
     * @return
     */
    public String getEstado() {
        return estado;
    }

    /**
     *
     * @param estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     *
     * @return
     */
    public XMLGregorianCalendar getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    /**
     *
     * @param fechaAutorizacion
     */
    public void setFechaAutorizacion(XMLGregorianCalendar fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    /**
     *
     * @return
     */
    public Mensajes getMensajes() {
        return mensajes;
    }

    /**
     *
     * @param mensajes
     */
    public void setMensajes(Mensajes mensajes) {
        this.mensajes = mensajes;
    }

    /**
     *
     * @return
     */
    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    /**
     *
     * @param numeroAutorizacion
     */
    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }
}

