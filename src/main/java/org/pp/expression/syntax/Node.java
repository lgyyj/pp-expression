package org.pp.expression.syntax;

import java.util.List;

public class Node {
    private char flag;
    private boolean end;
    private String source;
    private List<Node> next;
    private char open;
    private boolean close;

    public char getFlag() {
        return flag;
    }

    public String getSource() {
        return source;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public void setFlag(char flag) {
        this.flag = flag;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setNext(List<Node> next) {
        this.next = next;
    }

    public List<Node> getNext() {
        return next;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public char getOpen() {
        return open;
    }

    public void setOpen(char open) {
        this.open = open;
    }

    public boolean isClose() {
        return close;
    }
}
