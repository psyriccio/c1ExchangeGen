/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import com.google.common.primitives.Ints;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author psyriccio
 */
public class JTextAreaWriter extends StringWriter {

    private final JTextArea txt;

    public JTextAreaWriter(JTextArea txt) {
        this.txt = txt;
    }
    
    @Override
    public StringWriter append(CharSequence csq) {
        txt.append(csq.toString());
        txt.setCaretPosition(txt.getText().length());
        return this;
    }

    @Override
    public StringWriter append(char c) {
        txt.append("" + c);
        txt.setCaretPosition(txt.getText().length());
        return this;
    }

    @Override
    public StringWriter append(CharSequence csq, int start, int end) {
        txt.append(csq.subSequence(start, end).toString());
        txt.setCaretPosition(txt.getText().length());
        return this;
    }

    @Override
    public void write(String str) {
        txt.append(str);
        txt.setCaretPosition(txt.getText().length());
    }

    @Override
    public void write(int c) {
        byte[] b = Ints.toByteArray(c);
        try {
            txt.append(new String(b, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JTextAreaWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        txt.setCaretPosition(txt.getText().length());
    }

    @Override
    public void write(String str, int off, int len) {
        txt.append(str.substring(off, off+len));
        txt.setCaretPosition(txt.getText().length());
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        txt.append(new String(cbuf, off, len));
        txt.setCaretPosition(txt.getText().length());
    }
    
    
    
}
