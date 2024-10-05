package com.wibowo.fixtools.intellij.ui;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;

public final class FixNode extends DefaultMutableTreeNode {
    private final String tagName;

    private final Object rawTagValue;

    @Nullable
    private final Object tagValue;

    private FixNode(final String tagName,
                    final Object rawTagValue,
                    final @Nullable Object tagValue) {
        this.tagName = tagName;
        this.rawTagValue = rawTagValue;
        this.tagValue = tagValue;
    }

    public static FixNode createHeaderNode(final String tagName,
                                           final Object tagValue) {
        return new FixNode(tagName, tagValue, null);
    }

    /**
     * Create node that is the container of multi values.
     * E.g.:
     * - Body contains multiple tags
     * - NoLegs contains multiple legs
     */
    public static FixNode createBaseContainer(final String tagName) {
        return new FixNode(tagName, "", null);
    }

    public static FixNode from(final String tagName,
                               final Pair<Object, String> fieldValue) {
        return new FixNode(tagName, fieldValue.getLeft(), fieldValue.getRight());
    }

    public Object getRawTagValue() {
        return rawTagValue;
    }

    public String getTagValue() {
        return tagValue != null  ? tagValue.toString() : "";
    }

    public String getTagName() {
        return tagName;
    }
}
