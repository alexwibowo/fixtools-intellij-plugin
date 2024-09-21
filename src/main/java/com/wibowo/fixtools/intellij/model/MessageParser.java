package com.wibowo.fixtools.intellij.model;

import com.wibowo.fixtools.intellij.FIXToolsException;
import quickfix.DataDictionary;
import quickfix.DefaultMessageFactory;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageUtils;

public final class MessageParser {

    public Message parse(final DataDictionary dataDictionary,
                         final String message) {
        try {
            return MessageUtils.parse(new DefaultMessageFactory(), dataDictionary, message);
        } catch (InvalidMessage e) {
            throw new FIXToolsException(String.format("Unable to parse FIX message. It seems to be invalid. %s", e.getMessage()),
                    e);
        }
    }
}
