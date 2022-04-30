package org.pp.expression.invoker;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.pp.api.expression.Expression;

import java.util.function.Function;

/**
 * @author lgy
 */
public final class Invoker implements Expression {
    private static final IFn require = Clojure.var("clojure.core", "require");
    private final static Invoker invoker = new Invoker();
    private final static String NS = "org.pp.expression.core";

    private final IFn evalFn;
    private final IFn register;


    private Invoker() {
        require.invoke(Clojure.read(NS));
        IFn init = Clojure.var(NS, "init");
        evalFn = Clojure.var(NS, "eval-script");
        register = Clojure.var(NS, "register-by-source");
        init.invoke();
    }


    @Override
    public Object eval(String script, Function fieldProvider,
                       Function conProvider, Function tableProvider) {
        return invoker.evalFn.invoke(script, fieldProvider, conProvider, tableProvider);
    }

    public void register(String ns, String source) {
        invoker.register.invoke(ns, source);
    }

    public static void main(String[] args) {

        Invoker.invoker.register("foo", "(ns foo) (defn val1[] :foo) (defn v3[] 1)");
        System.out.println(Invoker.invoker.eval("", null, null, null));
    }


}
