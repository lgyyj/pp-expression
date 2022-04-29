package org.pp.expression.syntax;

/**
 * @author lgy
 */
public class ExecuteException extends Exception {
    public ExecuteException(String msg) {
        super("execute error:" + msg);
    }
}
