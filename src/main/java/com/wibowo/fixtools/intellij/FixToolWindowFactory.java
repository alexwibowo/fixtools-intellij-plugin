package com.wibowo.fixtools.intellij;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.wibowo.fixtools.intellij.actions.ClearAction;
import com.wibowo.fixtools.intellij.actions.ParseMessageAction;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import com.wibowo.fixtools.intellij.model.Dictionary;
import com.wibowo.fixtools.intellij.ui.AppSettings;
import com.wibowo.fixtools.intellij.ui.FixParserContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Set;

public class FixToolWindowFactory implements ToolWindowFactory, DumbAware {



    @Override
    public void createToolWindowContent(@NotNull final Project project,
                                        @NotNull final ToolWindow toolWindow) {
        final JPanel contentPane = new FixParserContainer().getContentPane();
        final SimpleToolWindowPanel panel = createWindowPanel();
        panel.add(contentPane);

        final ContentManager manager = toolWindow.getContentManager();
        final ContentFactory factory = ContentFactory.getInstance();
        manager.addContent(factory.createContent(panel, "Parser", false));
    }

    private static @NotNull SimpleToolWindowPanel createWindowPanel() {
        final ComboBoxAction chooseDictionaryAction = new ComboBoxAction() {
            private String selectedDictionaryAlias = null;
            @Override
            protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
                final Set<String> dictionaryAliases = AppSettings.getInstance().getState().getDictionaryAliases();
                DefaultActionGroup group = new DefaultActionGroup();
                // Add some actions to the combo box

                for (String dictionaryAlias : dictionaryAliases) {
                    group.add(new DictionaryOption(dictionaryAlias));
                }

                return group;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                super.update(e);
                final Presentation presentation = e.getPresentation();
                if (selectedDictionaryAlias == null) {
                    final Set<String> dictionaryAliases = AppSettings.getInstance().getState().getDictionaryAliases();
                    if (dictionaryAliases.isEmpty()) {
                        presentation.setText("No dictionary available");
                        presentation.setEnabled(false);
                        presentation.setDescription("You need to register dictionary through Intellij settings (Tools -> FIX Tools Settings");
                    } else {
                        presentation.setEnabled(true);
                        presentation.setText("Choose Dictionary");
                        this.setPopupTitle("Choose Dictionary");
                    }
                } else {
                    presentation.setText(selectedDictionaryAlias);
                    final Dictionary selectedDictionary = AppSettings.getInstance().getState().getDictionaryByAlias(selectedDictionaryAlias);
                    final File file = new File(selectedDictionary.path);
                    if (!file.exists()) {
                        // TODO: handle
                    }
                    ApplicationModel.getInstance().setDataDictionary(file);
                }
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }

            private class DictionaryOption extends com.intellij.openapi.actionSystem.AnAction {
                private final String name;

                public DictionaryOption(final String name) {
                    super(name);
                    this.name = name;
                }

                @Override
                public @NotNull ActionUpdateThread getActionUpdateThread() {
                    return ActionUpdateThread.EDT;
                }


                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    selectedDictionaryAlias = name;
                }
            }
        };


        final ParseMessageAction parseMessageAction = new ParseMessageAction(ApplicationModel.getInstance());
        final ClearAction clearAction = new ClearAction(ApplicationModel.getInstance());

        final SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
        final ActionGroup actions = new ActionGroup() {
            @Override
            public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
                return new AnAction[]{chooseDictionaryAction, new Separator(), parseMessageAction, clearAction};
            }
        };

        final ActionToolbarImpl actionToolbar = new ActionToolbarImpl(ActionPlaces.TOOLBAR, actions, true);
        actionToolbar.setTargetComponent(panel);

        panel.setToolbar(actionToolbar);
        return panel;
    }




}
