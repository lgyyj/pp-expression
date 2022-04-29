(comment (ns scs.parser1
           (:require [jasentaa.monad :as m]
                     [jasentaa.position :refer [strip-location]]
                     [jasentaa.parser :refer [parse-all]]
                     [jasentaa.parser.basic :refer :all]
                     [jasentaa.parser.combinators :refer :all]
                     [clojure.string :as str]
                     [scs.template :as tpl]))


         (def digit (from-re #"[0-9]"))
         (def letter (from-re #"[a-zA-Z]"))
         (def alpha-num (any-of letter digit))

         (declare fun)
         (declare for-flag)


         (def single-word
           (m/do*
             (w <- (token (plus alpha-num)))
             (m/return (strip-location w))))

         (def sg
           (any-of (symb "_")
                   (symb "+") (symb "-")
                   (symb "?") (symb "*")
                   (symb "/")))

         (def cb (m/do*
                   (c <- (any-of letter digit sg))
                   (m/return (strip-location c))))

         (def fun-id (m/do*
                       (s <- (any-of sg single-word (plus cb)))
                       (m/return (str/join s))))


         (def uni (plus (any-of digit
                                letter
                                (match " ")
                                (sat (fn [x] (and (>= (Character/compare x \u0080) 0) (>= (Character/compare \uffff x) 0)))))))

         (def quoted-string
           (m/do*
             (symb "\"")
             (t <- (token uni))
             (symb "\"")
             (m/return {:string (strip-location t)})))


         (def uni-token (m/do*
                          (t <- uni)
                          (m/return (strip-location t))))


         (def table-flag (m/do*
                           (symb "#{")
                           (expr <- single-word)
                           (symb "}")
                           (m/return {:table-flag expr})))


         (def con-flag (m/do*
                         (symb "!{")
                         (expr <- single-word)
                         (symb "}")
                         (m/return {:con-flag expr})))

         (def field-expr (m/do*
                           (t <- uni-token)
                           (symb ".")
                           (f <- (plus uni-token))
                           (m/return (str t "." (str/join "." f)))))

         (def field-flag (m/do*
                           (symb "${")
                           (expr <- field-expr)
                           (symb "}")
                           (m/return {:field-flag expr})))

         (def if-flag (m/do*
                        (symb "?{")
                        (expr <- (any-of field-flag single-word fun quoted-string))
                        (symb ":")
                        (t <- (any-of field-flag single-word fun quoted-string for-flag))
                        (symb ":")
                        (f <- (any-of field-flag single-word fun quoted-string for-flag))
                        (symb "}")
                        (m/return (tpl/if-template expr t f))))

         (def for-flag (m/do*
                         (symb "...{")
                         (expr <- single-word)
                         (symb ":")
                         (body <- fun)
                         (symb ":")
                         (col <- (any-of if-flag for-flag field-flag single-word fun quoted-string))
                         (symb "}")
                         (m/return (tpl/for-template expr body col))))


         (def fun (m/do*
                    (symb "(")
                    (expr <- fun-id)
                    (arg <- (optional
                              (separated-by
                                (any-of if-flag for-flag con-flag table-flag single-word quoted-string field-flag fun) space)))
                    (symb ")")
                    (m/return {:func expr :arg (into [] arg)})))

         (def grammar (m/do*
                        (lst <- (any-of fun if-flag for-flag))
                        (m/return lst)))

         (defn parse
           [src]
           (try
             (parse-all grammar src)
             (catch Exception e
               (ex-message e)))))
