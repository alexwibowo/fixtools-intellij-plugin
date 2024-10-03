package com.wibowo.fixtools.intellij.model;

import com.jgoodies.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public final class ApplicationModel {

    private static final ApplicationModel INSTANCE = new ApplicationModel();

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    @Nullable
    private DataDictionary dataDictionary;


    public static final String PROPERTY_FIX_MESSAGE = "fixMessage";
    @Nullable
    private String fixMessage;

    public static final String PROPERTY_MESSAGE_TREE = "messageTree";
    @Nullable
    private MessageTree messageTree;


    public static ApplicationModel getInstance() {
        return INSTANCE;
    }

    private ApplicationModel() {
    }

    public void setDataDictionary(final @NotNull DataDictionary file) {
        this.dataDictionary = file;
    }

    public ApplicationModel setFixMessage(final String fixMessage) {
        final String oldFixMessage = this.fixMessage;
        this.fixMessage = fixMessage;
        if (!Objects.equals(oldFixMessage, this.fixMessage)) {
            SwingUtilities.invokeLater(() -> {
                changes.firePropertyChange(PROPERTY_FIX_MESSAGE, oldFixMessage, this.fixMessage);
            });
        }
        return this;
    }

    public boolean isReadyForProcessing() {
        return dataDictionary != null && fixMessage != null && !Strings.isEmpty(fixMessage);
    }

    /**
     * Reset:
     * <ul>
     *     <li>{@link #dataDictionary}</li>
     *     <li>{@link #messageTree}</li>
     *     <li>{@link #fixMessage}</li>
     * </ul>
     */
    public void clear() {
        this.dataDictionary = null;

        final MessageTree oldMessageTree = this.messageTree;
        this.messageTree = null;
        changes.firePropertyChange(PROPERTY_MESSAGE_TREE, oldMessageTree, this.messageTree);

        setFixMessage(null);
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void processMessage() throws FieldNotFound {
        final MessageTree oldMessageTree = this.messageTree;
        final Message parsed = new MessageParser().parse(dataDictionary, prepareMessage(fixMessage));
        this.messageTree = new MessageTree(parsed, dataDictionary);
        changes.firePropertyChange(PROPERTY_MESSAGE_TREE, oldMessageTree, this.messageTree);
    }

    private static @NotNull String prepareMessage(final String fixMessage) {
        String preparedMessage = fixMessage.replaceAll("\\^A", "\001");
        preparedMessage = preparedMessage.replaceAll("\\|", "\001");
        return preparedMessage;
    }
}
