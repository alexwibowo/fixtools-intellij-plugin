package com.wibowo.fixtools.intellij.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import org.jetbrains.annotations.NotNull;

public final class ClearAction extends AnAction {

    private final ApplicationModel applicationModel;

    public ClearAction(final ApplicationModel applicationModel) {
        final Presentation templatePresentation = getTemplatePresentation();
        templatePresentation.setIcon(AllIcons.Actions.GC);
        this.applicationModel = applicationModel;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.applicationModel.clear();
    }
}
