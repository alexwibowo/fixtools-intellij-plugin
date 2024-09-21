package com.wibowo.fixtools.intellij.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTreeTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;

public final class FIXTableCellRenderer extends DefaultTableCellRenderer {

    private static final JBColor[] TREE_LEVEL_COLOURS = new JBColor[]{
            JBColor.BLACK,
            JBColor.BLUE,
            JBColor.ORANGE,
            JBColor.RED,
    };

    private final JBTreeTable treeTable;

    public FIXTableCellRenderer(final JBTreeTable treeTable) {
        this.treeTable = treeTable;
    }


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
}
