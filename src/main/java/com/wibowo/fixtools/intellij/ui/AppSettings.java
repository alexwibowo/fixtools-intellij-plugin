package com.wibowo.fixtools.intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.wibowo.fixtools.intellij.FIXToolsException;
import com.wibowo.fixtools.intellij.model.Dictionary;
import com.wibowo.fixtools.intellij.model.QuickfixJUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@State(
        name = "com.wibowo.fixtools.intellij.ui.AppSettings",
        storages = @Storage("FIXToolsSettings.xml")
)
public class AppSettings implements PersistentStateComponent<AppSettings.State> {

    private State myState = new State();

    public static AppSettings getInstance() {
        return ApplicationManager.getApplication()
                .getService(AppSettings.class);
    }

    @Override
    public @Nullable AppSettings.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public State getMyState() {
        return myState;
    }

    public void setMyState(State myState) {
        this.myState = myState;
    }

    public static class State {

        Set<Dictionary> dictionaries = new HashSet<>();

        public void setDictionaries(final Set<Dictionary> dictionaries) {
            // this will be called by serialization. Do not modify this
            this.dictionaries = dictionaries;
        }

        public void updateDictionary(final Set<Dictionary> dictionaries) {
            this.dictionaries.clear();
            this.dictionaries.addAll(dictionaries);
        }

        public Set<Dictionary> getDictionaries() {
            return dictionaries;
        }

        public Set<String> getDictionaryAliases() {
            final Set<String> dictionaryAliases = dictionaries.stream()
                    .map(dictionary -> dictionary.alias)
                    .collect(Collectors.toSet());
            dictionaryAliases.add("FIX40");
            dictionaryAliases.add("FIX41");
            dictionaryAliases.add("FIX42");
            dictionaryAliases.add("FIX43");
            dictionaryAliases.add("FIX44");
            dictionaryAliases.add("FIX50");
            return dictionaryAliases;
        }

        public Optional<DataDictionary> getDictionaryByAlias(final String alias) {
            switch(alias) {
                case "FIX40":
                case "FIX41":
                case "FIX42":
                case "FIX43":
                case "FIX44":
                case "FIX50":
                    try {
                        return Optional.of(new DataDictionary(DictionaryTableModel.class.getClassLoader().getResourceAsStream(alias + ".xml")));
                    } catch (final ConfigError e) {
                        throw new FIXToolsException("Unable to load dictionary", e);
                    }
                default:
                    return dictionaries.stream()
                            .filter(dictionary -> dictionary.alias.equals(alias))
                            .findFirst()
                            .map(QuickfixJUtils.FIX_DICTIONARY_CONVERTER);
            }


        }
    }

}
