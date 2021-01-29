/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.model;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author joerg JTable is missing VisibleRowCount and therefore
 * PreferredScrollableViewportSize is not working as expected. See
 * https://stackoverflow.com/questions/9821425/how-to-set-jscrollpane-to-show-only-a-sepecific-amount-of-rows
 * Using org.jdesktop.swingx.JXTable would be good alternative. For now using
 * workaround suggested here: https://bugs.openjdk.java.net/browse/JDK-4901352
 */
public class AveTable extends JTable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_VISIBLE_ROW_COUNT = 1;
    private int visibleRowCount;
    private int viewportHeightMargin;

    public AveTable(final TableModel tableModel) {
        this(tableModel, Math.max(DEFAULT_VISIBLE_ROW_COUNT, tableModel.getRowCount()));
    }

    public AveTable(final TableModel tableModel, int visibleRowCount) {
        this(tableModel, visibleRowCount, 0);
    }

    public AveTable(final TableModel tableModel, int visibleRowCount, int columnHeaderPadding) {
        this(tableModel, visibleRowCount, columnHeaderPadding, 0);
    }

    public AveTable(final TableModel tableModel, int visibleRowCount, int columnHeaderPadding, int viewportHeightMargin) {
        super(tableModel);
        this.visibleRowCount = visibleRowCount >= 0 ? visibleRowCount : tableModel.getRowCount();
        this.viewportHeightMargin = viewportHeightMargin;

        // JTable uses some default values for PreferredScrollableViewportSize. Do not use these.
        super.setPreferredScrollableViewportSize(null);

        // Table will not fill ScrollPane's ViewPort width. See table.getScrollableTracksViewportWidth().
        super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Table will fill ScrollPane's ViewPort height.
        super.setFillsViewportHeight(false);

        // Table colums can not be reordered.
        super.getTableHeader().setReorderingAllowed(false);

        // Configure renderer and editor for ComboBoxes.
        super.setDefaultRenderer(AveUpdatableSelection.class, AveChoiceElementCellRenderer.getInstance());
        super.setDefaultEditor(AveUpdatableSelection.class, AveChoiceElementCellEditor.getInstance());

        // Configure renderer for ColumnHeaders.
        super.getTableHeader().setDefaultRenderer(new MinWidthHeaderRenderer(this, columnHeaderPadding));
    }

    /**
     * Sets the preferred number of rows in the table that can be displayed
     * without a scrollbar, as determined by the nearest <code>JViewport</code>
     * ancestor, if any. The value of this property only affects the return
     * value of the <code>JTable</code>'s
     * <code>getPreferredScrollableViewportSize</code>.
     * <p>
     * The default value of this property is 8.
     * <p>
     * @param visibleRowCount an integer specifying the preferred number of
     * visible rows
     */
    public void setVisibleRowCount(int visibleRowCount) {
        int oldValue = this.visibleRowCount;
        this.visibleRowCount = Math.max(0, visibleRowCount);
        firePropertyChange("visibleRowCount", oldValue, visibleRowCount);
    }

    /**
     * Returns the preferred number of visible rows.
     *
     * @return an integer indicating the preferred number of rows to display
     * without using a scroll bar
     * @see #setVisibleRowCount
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public int getViewportHeightMargin() {
        return viewportHeightMargin;
    }

    public void setViewportHeightMargin(int viewportHeightMargin) {
        this.viewportHeightMargin = viewportHeightMargin;
    }

    /**
     * Computes the size of the viewport needed to display
     * <code>visibleRowCount</code> number of rows and accomodate all columns
     * rows.
     *
     * @return a dimension containing the size of the viewport needed to display
     * <code>visibleRowCount</code> rows and all columns
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {

        // Just in case someone has set PreferredScrollableViewportSize manually, use it.
        if (super.getPreferredScrollableViewportSize() != null) {
            return super.getPreferredScrollableViewportSize();
        }

        final Dimension preferredSize = super.getPreferredSize();
        final Insets insets = getInsets();
        int insetsAndMargin = insets.top + insets.bottom + this.viewportHeightMargin;
        preferredSize.height = this.getVisibleRowCount() * super.getRowHeight() + insetsAndMargin;
//        preferredSize.width = super.getColumnModel().getTotalColumnWidth();
        return preferredSize;
    }

    private static class MinWidthHeaderRenderer implements TableCellRenderer {

        final DefaultTableCellRenderer renderer;
        int columnPadding;

        public MinWidthHeaderRenderer(final JTable table, int columnPadding) {
            this.renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
            
            // Have the header label be in the center of column.
            renderer.setHorizontalAlignment(JLabel.CENTER);
            
            this.columnPadding = columnPadding;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            final Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            final DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
            final TableColumn tableColumn = colModel.getColumn(column);

            int minColumnWidth = component.getPreferredSize().width + (2 * this.columnPadding);
            if (tableColumn.getWidth() < minColumnWidth) {
                tableColumn.setMinWidth(minColumnWidth);
            }

            return component;
        }
    }
}
