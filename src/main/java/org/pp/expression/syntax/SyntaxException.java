package org.pp.expression.syntax;

/**
 * @author lgy
 */
public class SyntaxException extends Exception {
    public SyntaxException(String msg) {
        super("syntax parser error:" + msg);
    }
}
