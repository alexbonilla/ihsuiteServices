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
public class TipoAmbienteEnum {

    /**
     *
     * @return
     */
    public static TipoAmbienteEnum[] values() {
        return (TipoAmbienteEnum[]) $VALUES.clone();
    }

    private TipoAmbienteEnum(String s, int i, String code) {
        this.code = code;
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     *
     */
    public static final TipoAmbienteEnum PRODUCCION;

    /**
     *
     */
    public static final TipoAmbienteEnum PRUEBAS;
    private String code;
    private static final TipoAmbienteEnum $VALUES[];

    static {
        PRODUCCION = new TipoAmbienteEnum("PRODUCCION", 0, "2");
        PRUEBAS = new TipoAmbienteEnum("PRUEBAS", 1, "1");
        $VALUES = (new TipoAmbienteEnum[]{
            PRODUCCION, PRUEBAS
        });
    }

}
