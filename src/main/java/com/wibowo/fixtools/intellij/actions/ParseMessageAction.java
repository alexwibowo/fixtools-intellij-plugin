package com.wibowo.fixtools.intellij.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import com.wibowo.fixtools.intellij.ui.ErrorDialog;
import org.jetbrains.annotations.NotNull;

public final class ParseMessageAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(ParseMessageAction.class);

    private final ApplicationModel applicationModel;


    public ParseMessageAction(final ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        final Presentation templatePresentation = getTemplatePresentation();
        templatePresentation.setIcon(AllIcons.Actions.Execute);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        event.getPresentation().setEnabled(ApplicationModel.getInstance().isReadyForProcessing());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            this.applicationModel.processMessage();
        } catch (final Exception ex) {
            ErrorDialog errorDialog = new ErrorDialog(ex.getMessage());
            errorDialog.show();
            LOGGER.error("Failed to process message", ex);
        }
    }
}
