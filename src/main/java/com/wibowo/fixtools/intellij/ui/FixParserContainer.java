package com.wibowo.fixtools.intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTreeTable;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.ColumnInfo;
import com.wibowo.fixtools.intellij.model.ApplicationModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Objects;

public class FixParserContainer {
    private JPanel contentPane;
    private JTextPane fixMessageTextArea;
    private JPanel fixTreeContainer;

    private static final JBColor[] TREE_LEVEL_COLOURS = new JBColor[]{
            JBColor.BLACK,
            JBColor.BLUE,
            JBColor.ORANGE,
            JBColor.RED,
    };

    public FixParserContainer() {
        final ColumnInfo<FixNode, String> name = new FixNodeColumnInfo<>("Tag", FixNode::getTagName);
        final ColumnInfo<FixNode, Object> rawValue = new FixNodeColumnInfo<>("Raw Value", FixNode::getRawTagValue);
        final ColumnInfo<FixNode, Object> convertedValue = new FixNodeColumnInfo<>("Value", fixNode -> fixNode.getTagValue());
        final ColumnInfo[] columns = {name, rawValue, convertedValue};

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final JBTreeTable treeTable = createTreeTable(root, columns);
        fixMessageTextArea.setEditorKit(new WrapEditorKit());

        // make sure when user enter a text, it is populated in the model
        fixMessageTextArea.getDocument().addDocumentListener(new FixMessageUpdater(ApplicationModel.getInstance()));

        // draw our tree when the message tree in model is updated
        ApplicationModel.getInstance().addPropertyChangeListener(new TreeTableUpdater(treeTable, root, columns));

        // For the case where user highlight a text and want to parse it
        ApplicationModel.getInstance().addPropertyChangeListener(evt -> {
            final String propertyName = evt.getPropertyName();
            if (propertyName.equals(ApplicationModel.PROPERTY_FIX_MESSAGE)) {
                SwingUtilities.invokeLater(() -> {
                    if (!StringUtil.isEmpty((String) evt.getNewValue())) {
                        if (!Objects.equals(fixMessageTextArea.getText(), evt.getNewValue())) {
                            fixMessageTextArea.setText(evt.getNewValue().toString());
                        }
                    } else {
                        fixMessageTextArea.setText(null);
                    }
                });

            }
        });
        fixTreeContainer.add(treeTable,
                new GridConstraints(0, 0, 1, 1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50),
                        null, 0, false));
    }

    private static @NotNull JBTreeTable createTreeTable(DefaultMutableTreeNode root, ColumnInfo[] columns) {
        final ListTreeTableModel model = createTableModel(root, columns);
        final JBTreeTable treeTable = new JBTreeTable(model);
        /* TODO: make this premium feature
        final FIXTableCellRenderer fixTableCellRenderer = new FIXTableCellRenderer(treeTable);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(final JTable table,
                                                           final Object value,
                                                           final boolean isSelected,
                                                           final boolean hasFocus,
                                                           final int row,
                                                           final int column) {
                final Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final TreePath pathForRow = treeTable.getTree().getPathForRow(row);
                if (pathForRow != null) {
                    setForeground(TREE_LEVEL_COLOURS[pathForRow.getPathCount() - 2]);
                }
                return tableCellRendererComponent;
            }
        };
*/
//        treeTable.setDefaultRenderer(Object.class, fixTableCellRenderer);
        treeTable.setColumnProportion(0.98f); // this is so that the tree column only occupies a little area
        return treeTable;
    }

    @NotNull
    private static ListTreeTableModel createTableModel(final DefaultMutableTreeNode root,
                                                       final ColumnInfo[] columns) {
        return new ListTreeTableModel(root, columns) {
            @Override
            public boolean isCellEditable(Object node, int column) {
                return false;
            }
        };
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    private static class FixMessageUpdater extends DocumentAdapter {
        private final ApplicationModel applicationModel;

        private FixMessageUpdater(ApplicationModel applicationModel) {
            this.applicationModel = applicationModel;
        }

        @Override
        protected void textChanged(@NotNull final DocumentEvent documentEvent) {
            try {
                final Document document = documentEvent.getDocument();
                applicationModel.setFixMessage(document.getText(0, document.getLength()));
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(50);
        splitPane1.setOrientation(0);
        contentPane.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 1, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        scrollPane1.setMaximumSize(new Dimension(80, 80));
        scrollPane1.setPreferredSize(new Dimension(80, 17));
        scrollPane1.setVerticalScrollBarPolicy(22);
        scrollPane1.putClientProperty("html.disable", Boolean.FALSE);
        splitPane1.setLeftComponent(scrollPane1);
        fixMessageTextArea = new JTextPane();
        fixMessageTextArea.setMaximumSize(new Dimension(50, 2147483647));
        fixMessageTextArea.setMinimumSize(new Dimension(50, 17));
        fixMessageTextArea.setPreferredSize(new Dimension(608, 17));
        fixMessageTextArea.setText("2937498273948723984729374982734972394729837498273498723948723984729387492374");
        scrollPane1.setViewportView(fixMessageTextArea);
        fixTreeContainer = new JPanel();
        fixTreeContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(fixTreeContainer);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
