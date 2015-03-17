/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.comprobantes.utils;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Alex
 */
public class JaxbCharacterEscapeHandler implements CharacterEscapeHandler {

    /**
     *
     * @param buf
     * @param start
     * @param len
     * @param isAttValue
     * @param out
     * @throws IOException
     */
    public void escape(char[] buf, int start, int len, boolean isAttValue, Writer out) throws IOException {

        for (int i = start; i < start + len; i++) {
            char ch = buf[i];
            out.write(ch);
        }
    }
}
