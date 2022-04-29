package org.pp.expression.invoker;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.function.Function;

/**
 * @author lgy
 */
public class Invoker {
    private static final IFn require=Clojure.var("clojure.core", "require");
    private final static Invoker invoker = new Invoker();

    private final IFn evalFn;
    private final IFn register;


    private Invoker() {
        require.invoke(Clojure.read("scs.core"));
        IFn init = Clojure.var("scs.core", "init");
        evalFn = Clojure.var("scs.core", "eval-script");
        register = Clojure.var("scs.core", "register-by-source");
        init.invoke();
    }

    public static Object eval(String script, Function<String, Object> fieldProvider,
                              Function<String, Object> conProvider, Function<String, Object> tableProvider) {
        return invoker.evalFn.invoke(script, fieldProvider, conProvider, tableProvider);
    }

    public static void register(String ns, String source) {
        invoker.register.invoke(ns, source);
    }

    public static void main(String[] args) {
       // register("foo","(ns foo) (defn val1[] :foo) (defn v3[] 1)");
        System.out.println(eval("",null,null,null));
    }
}
