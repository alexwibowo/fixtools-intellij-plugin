package com.wibowo.fixtools.intellij.ui;

import javax.swing.tree.DefaultMutableTreeNode;

public final class FixNode extends DefaultMutableTreeNode {
    private final String tagName;
    private final Object rawTagValue;

    public FixNode(final String name,
                   final Object rawTagValue) {
        this.tagName = name;
        this.rawTagValue = rawTagValue;
    }

    public Object getRawTagValue() {
        return rawTagValue;
    }

    public String getTagName() {
        return tagName;
    }
}
