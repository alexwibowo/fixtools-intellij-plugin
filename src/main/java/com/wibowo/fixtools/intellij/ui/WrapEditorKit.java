package com.wibowo.fixtools.intellij.ui;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

// Custom EditorKit for wrapping text
class WrapEditorKit extends StyledEditorKit {
    ViewFactory defaultFactory = new WrapColumnFactory();

    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}
