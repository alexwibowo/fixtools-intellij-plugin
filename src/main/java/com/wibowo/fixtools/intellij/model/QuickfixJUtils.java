package com.wibowo.fixtools.intellij.model;

import com.wibowo.fixtools.intellij.FIXToolsException;
import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;

public final class QuickfixJUtils {

    private QuickfixJUtils() {
    }

    public static final Function<Dictionary, DataDictionary> FIX_DICTIONARY_CONVERTER = new Function<>() {
        @Override
        public DataDictionary apply(final Dictionary dictionary) {
            try (final FileInputStream in = new FileInputStream(dictionary.path)) {
                return new DataDictionary(in);
            } catch (final IOException | ConfigError e) {
                throw new FIXToolsException("Invalid data dictionary file provided", e);
            }
        }
    };
}
