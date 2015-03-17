/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.ws;

/**
 *
 * @author Alex
 */
public class Proxy {

    /**
     *
     */
    public Proxy() {
    }

    /**
     *
     * @param url
     * @param puerto
     * @param usuario
     * @param clave
     * @param wsProduccion
     * @param wsPruebas
     */
    public Proxy(String url, Integer puerto, String usuario, String clave, String wsProduccion, String wsPruebas) {
        this.url = url;
        this.puerto = puerto;
        this.usuario = usuario;
        this.clave = clave;
        this.wsProduccion = wsProduccion;
        this.wsPruebas = wsPruebas;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public Integer getPuerto() {
        return puerto;
    }

    /**
     *
     * @param puerto
     */
    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    /**
     *
     * @return
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     *
     * @param usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     *
     * @return
     */
    public String getClave() {
        return clave;
    }

    /**
     *
     * @param clave
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     *
     * @return
     */
    public String getWsProduccion() {
        return wsProduccion;
    }

    /**
     *
     * @param wsProduccion
     */
    public void setWsProduccion(String wsProduccion) {
        this.wsProduccion = wsProduccion;
    }

    /**
     *
     * @return
     */
    public String getWsPruebas() {
        return wsPruebas;
    }

    /**
     *
     * @param wsPruebas
     */
    public void setWsPruebas(String wsPruebas) {
        this.wsPruebas = wsPruebas;
    }
    private String url;
    private Integer puerto;
    private String usuario;
    private String clave;
    private String wsProduccion;
    private String wsPruebas;

}
