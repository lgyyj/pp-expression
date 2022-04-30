(ns org.pp.expression.parser
  (:gen-class)
  (:require [instaparse.core :as insta]))


(def ^{:static :private} grammar
  (insta/parser
    "EXPR =<whitespace>* FUN|IF_FLAG|FOR_FLAG <whitespace>*
     FUN  =<'('> (TOKEN|IF_FLAG|FOR_FLAG) ARGS  <')'>
     ARGS = (<whitespace>* | (<whitespace>+ (FUN|IF_FLAG|FOR_FLAG|STRING|TOKEN|VALUE_FLAGS)))*
     IF_FLAG =<whitespace>* <'?{'><whitespace>* DYM <whitespace>* ':' <whitespace>*(DYM|STRING)<whitespace>* ':' <whitespace>* (DYM|STRING)<whitespace>* <'}'><whitespace>*
     FOR_FLAG =<whitespace>* <'...{'><whitespace>* DYM  <whitespace>* ':' <whitespace>* (FUN|IF_FLAG|FOR_FLAG) <whitespace>* ':' <whitespace>* (DYM|STRING) <whitespace>* <'}'> <whitespace>*
     <DYM>= FUN | IF_FLAG | FOR_FLAG | VALUE_FLAGS |TOKEN
     <VALUE_FLAGS> =(FIELD_FLAG|TABLE_FLAG|CON_FLAG)
     FIELD_FLAG =<whitespace>*<'${'><whitespace>* (ANY | (ANY ('.' ANY)+)) <whitespace>*<'}'><whitespace>*
     TABLE_FLAG =<whitespace>*<'#{'><whitespace>* ANY+ <whitespace>*<'}'><whitespace>*
     CON_FLAG =<whitespace>*<'!{'><whitespace>* ANY+ <whitespace>*<'}'><whitespace>*
     STRING ='\"' ANY+ '\"'
     TOKEN =(WORD | SG)+
     <ANY> = (NUMBER | '.' | whitespace | LETTER | #'[\u0080-\uffff]')+
     <WORD> = (NUMBER | LETTER)+
     <NUMBER> =#'[0-9]'
     <LETTER> =#'[a-zA-Z]'
     <SG> = '_' | '?' |'-'| '+' |'*'| '/' |'='
     <whitespace> = #'\\s+'"))

(defn parser
  ""
  [input]
  (insta/parse grammar input))


