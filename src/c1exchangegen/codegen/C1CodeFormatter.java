/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.codegen;

/**
 *
 * @author psyriccio
 */
public class C1CodeFormatter {

    public static String[] TB_TOKENS = new String[]{};

    public static String[] NL_TOKENS = new String[]{};

    private String text;
    
    public C1CodeFormatter(String text) {
        this.text = text;
    }

    public void compress() {

        boolean wasCharactersInLine = false;
        boolean isWhiteSpace = false;
        
    }
    

    public String done() {
        return text;
    }
    
    
}
