package com.wibowo.fixtools.intellij.ui;

import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.wibowo.fixtools.intellij.model.MessageTree;
import com.wibowo.fixtools.intellij.model.ApplicationModel;

import javax.swing.tree.DefaultMutableTreeNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public final class TreeTableUpdater implements PropertyChangeListener {

    private final JBTreeTable treeTable;

    private final DefaultMutableTreeNode root;

    private final ColumnInfo[] columns;

    public TreeTableUpdater(final JBTreeTable treeTable,
                            final DefaultMutableTreeNode root,
                            final ColumnInfo[] columns) {
        this.treeTable = treeTable;
        this.root = root;
        this.columns = columns;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();

        if (propertyName.equals(ApplicationModel.PROPERTY_MESSAGE_TREE)) {
            final MessageTree messageTree = (MessageTree) evt.getNewValue();

            root.removeAllChildren();
            if (messageTree != null) {
                final Map<String, Object> headerValues = messageTree.getHeaderValues();
                for (final Map.Entry<String, Object> headerEntry : headerValues.entrySet()) {
                    final DefaultMutableTreeNode node = new FixNode(headerEntry.getKey(), headerEntry.getValue());
                    root.add(node);
                }
                final DefaultMutableTreeNode body = buildBody("Body", messageTree.getBodyValues());
                root.add(body);
            }
            final ListTreeTableModel listTreeTableModel = new ListTreeTableModel(root, columns) {
                @Override
                public boolean isCellEditable(Object node, int column) {
                    return false;
                }
            };
            treeTable.setModel(listTreeTableModel);
            treeTable.repaint();
        }
    }

    private DefaultMutableTreeNode buildBody(final String parentName,
                                             final Map<String, Object> collection) {
        final DefaultMutableTreeNode result = new FixNode(parentName, "");
        for (final Map.Entry<String, Object> entry : collection.entrySet()) {
            if (entry.getValue() instanceof String) {
                final DefaultMutableTreeNode node = new FixNode(entry.getKey(), entry.getValue());
                result.add(node);
            } else if (entry.getValue() instanceof java.util.List<?>) {
                final java.util.List listValue = (java.util.List) entry.getValue();
                for (int i = 0; i < listValue.size(); i++) {
                    Map<String, Object> o = (Map<String, Object>) listValue.get(i);
                    DefaultMutableTreeNode defaultMutableTreeNode = buildBody(entry.getKey() + "-" + i, o);
                    result.add(defaultMutableTreeNode);
                }
            }
        }
        return result;
    }
}
