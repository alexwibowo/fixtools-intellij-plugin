package com.wibowo.fixtools.intellij.ui;

import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import com.wibowo.fixtools.intellij.model.MessageTree;
import org.apache.commons.lang3.tuple.Pair;

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
                    root.add(FixNode.createHeaderNode(headerEntry.getKey(), headerEntry.getValue()));
                }
                root.add(buildBody("Body", messageTree.getBodyValues()));
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

    private FixNode buildBody(final String parentName,
                              final Map<String, Pair<Object, String>> tagKeyValue) {
        final FixNode result = FixNode.createBaseContainer(parentName);
        for (final Map.Entry<String, Pair<Object, String>> entry : tagKeyValue.entrySet()) {
            final Pair<Object, String> tagValue = entry.getValue();
            final Object tagRawValue = tagValue.getLeft();
            if (tagRawValue instanceof String) {
                // for simple key value (e.g. MessageType)
                result.add(FixNode.from(entry.getKey(), tagValue));
            } else if (tagRawValue instanceof java.util.List listValue) {
                // for collection, e.g. leg
                for (int i = 0; i < listValue.size(); i++) {
                    final Map<String, Pair<Object, String>> o = (Map<String, Pair<Object, String>>) listValue.get(i);
                    result.add(buildBody(entry.getKey() + "-" + i, o));
                }
            }
        }
        return result;
    }
}
