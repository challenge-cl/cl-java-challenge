package com.castlabs.isobmff;

import java.util.ArrayList;
import java.util.List;

public class ISOBmff {
    private final List<BoxNode> boxNodes;

    public ISOBmff() {
        this.boxNodes = new ArrayList<>();
    }

    public List<BoxNode> getBoxNodes() {
        return boxNodes;
    }

    public void addBoxNode(BoxNode boxNode) {
        boxNodes.add(boxNode);
    }
}
