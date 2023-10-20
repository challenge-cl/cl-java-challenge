package com.castlabs.isobmff;

import java.util.ArrayList;
import java.util.List;

public class BoxNode {
    private final Box box;
    private final BoxNode parent;
    private final List<BoxNode> childs;

    public BoxNode(Box box, BoxNode parent) {
        this.box = box;
        this.parent = parent;
        this.childs = new ArrayList<>();
    }

    public Box getBox() {
        return box;
    }

    public BoxNode getParent() {
        return parent;
    }

    public List<BoxNode> getChilds() {
        return childs;
    }

    public void addChild(BoxNode child) {
        this.childs.add(child);
    }
}
