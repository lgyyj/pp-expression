(ns scs.core
  (:gen-class)
  (:require [clojure.string :as str]
            [sci.core :as sci]
            [scs.parser :as p])
  (:import (java.util.function Function)
           (scs.syntax SyntaxCompileException SyntaxException ExecuteException)))



(def ^:dynamic get_field_flag)
(def ^:dynamic get_conn_flag)
(def ^:dynamic get_table_flag)
(def ctx (atom (sci/init {})))


(def for-expr "(->> (for[%s %s] %s)(into []))")
(def if-expr "(if %s %s %s) ")
(def field-expr "(get_field_flag %s)")
(def conn-expr "(get_conn_flag %s)")
(def table-expr "(get_table_flag %s)")

(defn- token-name
  "docstring"
  [token]
  (str/join (next token)))

(declare parser-body)


(defn- parser-var-name
  "docstring"
  [scope token]
  (let [[flag _] token
        token-name (token-name token)]
    (case flag
      :TOKEN (if (or (= token-name "nil") (empty? scope) (not (not-any? #(= % token-name) scope)))
               token-name
               (throw (SyntaxCompileException. (str "token-{" token-name "} undefine!"))))
      :FIELD_FLAG (format field-expr (str \" token-name \"))
      :TABLE_FLAG (format table-expr (str \" token-name \"))
      :CON_FLAG (format conn-expr (str \" token-name \"))
      :STRING token-name)))

(defn for-flag
  "docstring"
  [scope token-tree]
  (let [[token _ body _ col] token-tree
        var-name (parser-var-name nil token)
        scope1 (conj scope var-name)]
    (format for-expr var-name (parser-var-name [] col) (parser-body scope1 body))))

(defn- func-parser
  "docstring"
  [scope [token args]]
  (let [fn-var [(str "(" (token-name token))]
        fns (reduce (fn [m i] (conj m (if (nil? i)
                                        ""
                                        (parser-body scope i)))) fn-var (next args))]
    (str/join " " (conj fns ")"))))

(defn- if-flag
  "docstring"
  [scope token-tree]
  (let [[token _ true-expr _ false_expr] token-tree]
    (format if-expr (parser-body scope token)
            (parser-body scope true-expr)
            (parser-body scope false_expr))))

(defn- parser-body [scope parserTree]
  (if-not (empty? parserTree)
    (let [flag (first parserTree)]
      (case flag
        :FOR_FLAG (for-flag scope (next parserTree))
        :FUN (func-parser scope (next parserTree))
        :IF_FLAG (if-flag scope (next parserTree))
        (parser-var-name scope parserTree)))))

(defn- parser-tree
  "docstring"
  [scope parseTree]
  (try
    (let [[_ tree] parseTree]
      (parser-body scope tree))
    (catch Exception e
      (throw (SyntaxCompileException. (.getMessage e))))))

(defn eval-script [input ^Function field-spy ^Function con-spy ^Function table-spy]
  (binding [get_field_flag (fn [x]
                             (if field-spy
                               (.apply field-spy x)
                               nil))
            get_conn_flag (fn [x]
                            (if con-spy
                              (.apply con-spy x)
                              nil))
            get_table_flag (fn [x]
                             (if table-spy
                               (.apply table-spy x)
                               nil))]
    (try
      (let [parser (p/parser input)]
        (if (not (vector? parser))
          (throw (SyntaxException. (str parser)))
          (let [compile-str (parser-tree [] parser)]
            (prn compile-str)
            (try
              (sci/eval-string* (merge @ctx {
                                             :bindings {'get_field_flag get_field_flag
                                                        'get_conn_flag  get_conn_flag
                                                        'get_table_flag get_table_flag}
                                             }) compile-str)
              (catch Exception e
                (throw (ExecuteException. (.getMessage e))))))))
      (catch Exception e
        (throw e)))))



(defn register [customer-ns]
  (require (symbol customer-ns))
  (let [all (ns-publics (symbol customer-ns))
        out (reduce (fn [m x]
                      (let [func-name (first x)
                            ns-name (val x)]
                        (assoc-in m [:namespaces 'clojure.core func-name] ns-name)))
                    @(:env @ctx) all)]
    (reset-vals! (:env @ctx) out)
    @ctx))

(defn register-by-source [ns source]
  (let [s (symbol ns)]
    (remove-ns s)
    (load-string source)
    (register ns)))

(defn init
  "docstring"
  []
  (register "scs.lib"))

