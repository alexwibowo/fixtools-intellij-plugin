package com.wibowo.fixtools.intellij.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.wibowo.fixtools.intellij.model.Dictionary;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.Set;

public class AppSettingsConfigurable implements Configurable {
    private static final Logger LOGGER = Logger.getInstance(AppSettingsConfigurable.class);

    private ApplicationSettingsForm applicationSettingsForm;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public  String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.applicationSettingsForm = new ApplicationSettingsForm();
        return applicationSettingsForm.getContentPane();
    }

    @Override
    public boolean isModified() {
        final AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        final Set<Dictionary> dictionaries = applicationSettingsForm.getDictionaries();
        final Set<Dictionary> dictionariesFromState = state.dictionaries;
        final boolean isModified = !dictionaries.equals(dictionariesFromState);
        LOGGER.info(String.format("Checking if state is modified. Dictionary from form: [%s]. From state: [%s]. Has it been modified ? [%s]",
                dictionaries, dictionariesFromState, isModified));
        return isModified;
    }

    /**
     * This will be called when user click 'OK' or 'Apply' on the settings dialog.
     *
     * SettingsEditor -> ConfigurableEditor -> ConfigurableWrapper#apply() -> this method
     * So here we are updating the AppSettings.State with whatever is coming from the UI dialog.
     */
    @Override
    public void apply() throws ConfigurationException {
        final AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        final Set<Dictionary> dictionaries = applicationSettingsForm.getDictionaries();
        LOGGER.info(String.format("Updating state with dictionary from form: [%s]", dictionaries));
        state.updateDictionary(dictionaries);
    }


    @Override
    public void reset() {
        final AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        LOGGER.info(String.format("Updating form with dictionary from state [%s]", state.dictionaries));
        applicationSettingsForm.setDictionaries(state.dictionaries);
    }

    @Override
    public void disposeUIResources() {
        applicationSettingsForm = null;
    }
}
