package org.pp.expression.syntax;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Parser {
    private final static Map<String, Object> cache = new ConcurrentHashMap<>();

    private static final char CON_FLAG = '!';
    private static final char STRING_FLAG = '\"';
    private static final char FIELD_FLAG = '$';
    private static final char TABLE_FLAG = '#';
    private static final char FOR_FLAG = '~';
    private static final char IF_FLAG = '?';
    private static final char FUN_FLAG = '(';
    private static final char FUN_EOF = ')';
    private static final char FLAG_OPEN = '{';
    private static final char FLAG_CLOSE = '}';
    private static final List<Character> SPACES = Arrays.asList('\n', '\t', ' ', '\t');

    public static void parser(String input) {
        Stack<Node> tree = new Stack<>();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == FUN_FLAG) {
                parserFun(i, chars, tree);
            } else if (c == CON_FLAG) {
                parserCon(i, chars, tree);
            }

        }
    }

    private static void parserCon(int i, char[] chars, Stack<Node> tree) {
        Node node = new Node();
        node.setFlag(CON_FLAG);
        boolean space = false;
        StringBuilder builder = new StringBuilder();
        do {
            i++;
            char c = chars[i];
            if (c == FLAG_OPEN) {
                node.setOpen(FLAG_OPEN);
            } else if (c == FLAG_CLOSE) {
                node.setClose(true);
            }
            if (c == '\n' || c == '\t' || c == ' ') {
                space = true;
            } else {
                builder.append(c);
            }
        } while (!space);
        node.setSource(builder.toString());
        tree.push(node);

    }

    private static boolean isSpace(char c) {
        return SPACES.stream().anyMatch(character -> c == character);
    }

    private static void parserFun(int i, char[] chars, Stack<Node> tree) {
        Node node = new Node();
        node.setFlag(FUN_FLAG);
        boolean space = false;
        StringBuilder builder = new StringBuilder();
        do {
            i++;
            char c = chars[i];
            if (isSpace(c)) {
                space = true;
            } else {
                builder.append(c);
            }
        } while (!space);
        node.setSource(builder.toString());
        tree.push(node);

    }

}
