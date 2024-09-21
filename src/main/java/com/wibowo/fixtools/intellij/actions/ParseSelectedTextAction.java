package com.wibowo.fixtools.intellij.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import com.wibowo.fixtools.intellij.ui.ChooseDictionaryDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public final class ParseSelectedTextAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        Document document = editor.getDocument();

        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        final String selectedText = document.getText(new TextRange(start, end));
        ApplicationModel.getInstance().setFixMessage(selectedText);

        SwingUtilities.invokeLater(() -> {
            final ChooseDictionaryDialog chooseDictionaryDialog = new ChooseDictionaryDialog();
            chooseDictionaryDialog.pack();
            chooseDictionaryDialog.setLocationRelativeTo((Component) event.getInputEvent().getSource());
            chooseDictionaryDialog.setVisible(true);
        });

    }
}
