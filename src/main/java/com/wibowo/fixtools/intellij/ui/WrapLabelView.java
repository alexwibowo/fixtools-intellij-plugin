package com.wibowo.fixtools.intellij.ui;

import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;

// Custom View to wrap text
class WrapLabelView extends LabelView {
    public WrapLabelView(Element elem) {
        super(elem);
    }

    public float getMinimumSpan(int axis) {
        return switch (axis) {
            case View.X_AXIS -> 0;
            case View.Y_AXIS -> super.getMinimumSpan(axis);
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
           };
    }
}
