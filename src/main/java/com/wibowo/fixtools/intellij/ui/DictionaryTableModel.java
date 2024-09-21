package com.wibowo.fixtools.intellij.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.wibowo.fixtools.intellij.model.Dictionary;

import javax.swing.table.DefaultTableModel;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public final class DictionaryTableModel extends DefaultTableModel {
    private static final Logger LOGGER = Logger.getInstance(DictionaryTableModel.class);

    public Set<Dictionary> getDictionaries() {
        int rowCount = getRowCount();
        TreeSet<Dictionary> result = new TreeSet<>();
        for (int i = 0; i < rowCount; i++) {
            String alias = (String) getValueAt(i, 0);
            String path = (String) getValueAt(i, 1);
            Dictionary dictionary = new Dictionary(alias, path);
            result.add(dictionary);
        }
        return result;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // only allow editing path.
        // FIXME: need to handle when user edit the dictionary into invalid path
        return column != 0;
    }

    public void setDictionaries(final Set<Dictionary> dictionaries) {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.removeRow(i);
        }
        for (Dictionary dictionary : dictionaries) {
            this.addRow(new Object[]{dictionary.alias, dictionary.path});
        }
    }

    public void addDictionary(final Dictionary newDictionary) {
        Optional<Dictionary> matchingDictionary = getDictionaries().stream().filter(new Predicate<Dictionary>() {
            @Override
            public boolean test(Dictionary dictionary) {
                return dictionary.alias.equals(newDictionary.alias);
            }
        }).findFirst();


        if (matchingDictionary.isPresent()) {
            final Dictionary existingDictionary = matchingDictionary.get();
            LOGGER.info(String.format("Updating existing dictionary [%s] at [%s] to [%s]", existingDictionary.alias, existingDictionary.path, newDictionary.path));
            existingDictionary.path = newDictionary.path;
        } else {
            LOGGER.info(String.format("Adding new dictionary [%s] at [%s]", newDictionary.alias, newDictionary.path));
            this.addRow(new Object[]{newDictionary.alias, newDictionary.path});
        }
    }

    public String getDictionaryAliasAt(int row) {
        return (String) this.getValueAt(row, 0);
    }

    public void removeDictionaryWithAlias(final String selectedDictionaryAlias) {
        for (int i = 0; i < this.getRowCount(); i++) {
            String alias = (String) this.getValueAt(i, 0);
            if (alias.equals(selectedDictionaryAlias)) {
                this.removeRow(i);
                break;
            }
        }
    }
}
