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
public enum LotType {

    /**
     *
     */
    Unitario {

                @Override

                public String toString() {
                    return "Unitario";
                }

            },
    /**
     *
     */
    Multiple {

                @Override

                public String toString() {
                    return "Multiple";
                }

            };

}
