package com.castlabs.isobmff;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BoxNode {
    private final Box box;
    private final List<BoxNode> childs;

    public BoxNode(Box box) {
        this.box = box;
        this.childs = new ArrayList<>();
    }

    public Box getBox() {
        return box;
    }

    public List<BoxNode> getChilds() {
        return childs;
    }

    public void addChild(BoxNode child) {
        this.childs.add(child);
    }
}
