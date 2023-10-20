package com.castlabs.isobmff;

public class Box {

    private final String type;
    private final int length;

    public Box(String type, int length) {
        this.type = type;
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

}
