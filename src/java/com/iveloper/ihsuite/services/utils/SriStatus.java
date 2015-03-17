/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.ihsuite.services.utils;

/**
 *
 * @author Alex
 */
//public class SriStatus {
//
//    public static SriStatus[] values() {
//        return (SriStatus[]) $VALUES.clone();
//    }
//
//    private SriStatus(String s, int i, String code, String xsd, String descripcion) {
////        super(s, i);
//        this.code = code;
//        this.xsd = xsd;
//        this.descripcion = descripcion;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getXsd() {
//        return xsd;
//    }
//
//    public String getDescripcion() {
//        return descripcion;
//    }
//
//    public static String retornaCodigo(String valor) {
//        String codigo = null;
//        if (valor.equals(Autorizada.getDescripcion())) {
//            codigo = Autorizada.getCode();
//        } else if (valor.equals(Cancelada.getDescripcion())) {
//            codigo = Cancelada.getCode();
//        } else if (valor.equals(Devuelta.getDescripcion())) {
//            codigo = Devuelta.getCode();
//        } else if (valor.equals(NoAutorizada.getDescripcion())) {
//            codigo = NoAutorizada.getCode();
//        } else if (valor.equals(NoProcesada.getDescripcion())) {
//            codigo = NoProcesada.getCode();
//        } else if (valor.equals(Recibida.getDescripcion())) {
//            codigo = Recibida.getCode();
//        }
//        return codigo;
//    }
//
//    public static SriStatus retornaSriStatus(String valor) {
//        SriStatus status = null;
//        if (valor.equals(Autorizada.getDescripcion())) {
//            status = Autorizada;
//        } else if (valor.equals(Cancelada.getDescripcion())) {
//            status = Cancelada;
//        } else if (valor.equals(Devuelta.getDescripcion())) {
//            status = Devuelta;
//        } else if (valor.equals(NoAutorizada.getDescripcion())) {
//            status = NoAutorizada;
//        } else if (valor.equals(NoProcesada.getDescripcion())) {
//            status = NoProcesada;
//        } else if (valor.equals(Recibida.getDescripcion())) {
//            status = Recibida;
//        }
//        return status;
//    }
//
//    public static final SriStatus Autorizada;
//    public static final SriStatus Cancelada;
//    public static final SriStatus Devuelta;
//    public static final SriStatus NoAutorizada;
//    public static final SriStatus NoProcesada;
//    public static final SriStatus Recibida;
//    private String code;
//    private String xsd;
//    private String descripcion;
//    private static final SriStatus $VALUES[];
//
//    static  {
//        Autorizada = new SriStatus("Autorizada", 0, "00", "lote.xsd", "AUTORIZADO");
//        Cancelada = new SriStatus("Cancelada", 1, "01", "factura.xsd", "CANCELADO");
//        Devuelta = new SriStatus("Devuelta", 2, "04", "notaCredito.xsd", "DEVUELTA");
//        NoAutorizada = new SriStatus("NoAutorizada", 3, "05", "notaDebito.xsd", "RECHAZADO");
//        NoProcesada = new SriStatus("NoProcesada", 4, "06", "guiaRemision.xsd", "NO PROCESADO");
//        Recibida = new SriStatus("Recibida", 5, "07", "comprobanteRetencion.xsd", "RECIBIDA");
//
//        $VALUES = (new SriStatus[]{
//            Autorizada, Cancelada, Devuelta, NoAutorizada, NoProcesada, Recibida
//        });
//    }
//
//}
public enum SriStatus {

    /**
     *
     */
    Autorizada {
                String descripcion = "AUTORIZADO";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            },

    /**
     *
     */
    Cancelada {
                String descripcion = "CANCELADO";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            },

    /**
     *
     */
    Devuelta {
                String descripcion = "DEVUELTA";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            },

    /**
     *
     */
    NoAutorizada {
                String descripcion = "RECHAZADO";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            },

    /**
     *
     */
    NoProcesada {
                String descripcion = "NO PROCESADO";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            },

    /**
     *
     */
    Recibida {
                String descripcion = "RECIBIDA";

                @Override
                public String getDescripcion() {
                    return descripcion;
                }

                @Override
                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            };

    /**
     *
     * @return
     */
    public abstract String getDescripcion();

    /**
     *
     * @param descripcion
     */
    public abstract void setDescripcion(String descripcion);

    /**
     *
     * @param valor
     * @return
     */
    public static SriStatus retornaSriStatus(String valor) {
        SriStatus status = null;
        if (valor.equals(Autorizada.getDescripcion())) {
            status = Autorizada;
        } else if (valor.equals(Cancelada.getDescripcion())) {
            status = Cancelada;
        } else if (valor.equals(Devuelta.getDescripcion())) {
            status = Devuelta;
        } else if (valor.equals(NoAutorizada.getDescripcion())) {
            status = NoAutorizada;
        } else if (valor.equals(NoProcesada.getDescripcion())) {
            status = NoProcesada;
        } else if (valor.equals(Recibida.getDescripcion())) {
            status = Recibida;
        }
        return status;
    }
}
