package org.pp.expression.syntax;

/**
 * @author lgy
 */
public class SyntaxCompileException extends Exception{
    public SyntaxCompileException(String msg){
        super("syntax compile error:"+msg);
    }
}
