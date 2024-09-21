package com.wibowo.fixtools.intellij.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public final class ErrorDialog extends DialogWrapper {

    private final String message;

    public ErrorDialog(final String message) {
        super(true); // use current window as parent
        this.message = message;
        setTitle("ERROR");
        init();
        this.setResizable(false);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        final JLabel label = new JLabel(message);
        label.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }


    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

}
