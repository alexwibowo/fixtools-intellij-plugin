package com.wibowo.fixtools.intellij.ui;

import javax.swing.text.*;

// Custom ViewFactory to wrap text
class WrapColumnFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            switch (kind) {
                case AbstractDocument.ContentElementName -> {
                    return new WrapLabelView(elem);
                }
                case AbstractDocument.ParagraphElementName -> {
                    return new ParagraphView(elem);
                }
                case AbstractDocument.SectionElementName -> {
                    return new BoxView(elem, View.Y_AXIS);
                }
                case StyleConstants.ComponentElementName -> {
                    return new ComponentView(elem);
                }
                case StyleConstants.IconElementName -> {
                    return new IconView(elem);
                }
            }
        }

        return new LabelView(elem);
    }
}
