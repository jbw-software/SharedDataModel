package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author joerg JTable is missing VisibleRowCount and therefore
 * PreferredScrollableViewportSize is not working as expected. See
 * https://stackoverflow.com/questions/9821425/how-to-set-jscrollpane-to-show-only-a-sepecific-amount-of-rows
 * Using org.jdesktop.swingx.JXTable would be good alternative. For now using
 * workaround suggested here: https://bugs.openjdk.java.net/browse/JDK-4901352
 */
@SuppressWarnings("serial")
public class AveTable extends JTable {

    /**
     * The default value for height of the tables viewport in rows.
     */
    public static final int DEFAULT_VISIBLE_ROW_COUNT = 1;
    private final MinWidthHeaderRenderer minWidthHeaderRenderer;

    private boolean autoResizeMode; // hides field in JTable
    private int visibleRowCount;
    private int viewportHeightMargin;
    private AveTableRowEntry rowPrototype;
    private int lastColumnExtraWidth;

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
        this.minWidthHeaderRenderer = new MinWidthHeaderRenderer(this, columnHeaderPadding);

        // Initially empty. It get initalized in setAutoCreateNewRowAfterLastEdit(). Table needs to know row data, when rows are created.
        this.rowPrototype = null;

        // Initially empty. Value gets set in setLastColumnExtraWidth() to add extra width for last column, e.g. to gain space for vertical ScrollBar.
        this.lastColumnExtraWidth = 0;

        // JTable uses some default values for PreferredScrollableViewportSize. Do not use these.
        super.setPreferredScrollableViewportSize(null);

        // Table will not fill ScrollPane's ViewPort width. See table.getScrollableTracksViewportWidth().
        this.setAutoResizeMode(false);

        // Table will fill ScrollPane's ViewPort height.
        super.setFillsViewportHeight(false);

        // Usually editing is not terminated if the focus is lost, change this!
        super.putClientProperty("terminateEditOnFocusLost", true);

        // Table colums can not be reordered.
        super.getTableHeader().setReorderingAllowed(false);

        // Configure custom renderer and editor for ComboBoxes...
        super.setDefaultRenderer(AveUpdatableSelection.class, AveChoiceElementCellRenderer.getInstance());
        super.setDefaultEditor(AveUpdatableSelection.class, AveChoiceElementCellEditor.getInstance());
        // ...and for String values.
        super.setDefaultRenderer(String.class, OptimizedWidthStringCellRenderer.getInstance());

        // Configure renderer for ColumnHeaders.
        super.getTableHeader().setDefaultRenderer(this.minWidthHeaderRenderer);
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

        final Dimension currentPrefSize = super.getPreferredSize();
        currentPrefSize.height = this.getVisibleRowCount() * super.getRowHeight();
        return currentPrefSize;
    }

    /**
     * Returns the {@code TableModel} that provides the data displayed by this
     * {@code JTable}.
     *
     * @return the {@code AveTableModel} that provides the data displayed by
     * this {@code AveTable}
     */
    @Override
    public AveTableModel getModel() {
        return (AveTableModel) super.getModel();
    }

    /**
     * Causes this table to lay out its rows and columns.
     */
    @Override
    public void doLayout() {
        //  Viewport size changed. Change last column width.
        if (this.autoResizeMode && super.tableHeader.getResizingColumn() == null) {
            final TableColumnModel tcm = super.getColumnModel();
            final int delta = getParent().getWidth() - tcm.getTotalColumnWidth();
            final TableColumn last = tcm.getColumn(tcm.getColumnCount() - 1);
            last.setPreferredWidth(last.getPreferredWidth() + delta);
            last.setWidth(last.getPreferredWidth());
        } else if (this.lastColumnExtraWidth != 0 && super.tableHeader.getResizingColumn() == null) {
            final TableColumnModel tcm = getColumnModel();
            final TableColumn last = tcm.getColumn(tcm.getColumnCount() - 1);
            last.setPreferredWidth(last.getPreferredWidth() + this.lastColumnExtraWidth);
            last.setWidth(last.getPreferredWidth());
            this.lastColumnExtraWidth = 0;
        } else {
            super.doLayout();
        }
    }

    /**
     * Add a new row automatically after editing the last one.
     * http://stackoverflow.com/questions/16368343/jtable-resize-only-selected-column-when-container-size-changes
     * Invoked when editing is finished. The changes are saved and the editor is
     * discarded.
     * <p>
     * Application code will not use these methods explicitly, they are used
     * internally by JTable.
     *
     * @param e the event received
     * @see CellEditorListener
     */
    @Override
    public void editingStopped(final ChangeEvent e) {

        // Getting these values before calling super.editingStopped(e); because they get erased.
        final int row = getEditingRow();
        final int column = getEditingColumn();
        super.editingStopped(e); // Must call the super code to have a working edition.
        if (this.rowPrototype != null) {
            final AveTableRowEntry newRow = new AveTableRowEntry(this.rowPrototype);
            if (row == getRowCount() - 1 && column == getColumnCount() - 1) {
                this.getModel().addRow(newRow);
            }
        }
    }

    /**
     * Deselects all selected columns and rows.
     */
    @Override
    public void clearSelection() {
        super.clearSelection();

        // Reset all column identifiers.
        // Column identifiers have been used for in custom renderer to flag
        // if a column has manually been resized.
        for (int i = 0; i < super.getColumnModel().getColumnCount(); i++) {
            final TableColumn column = super.getColumnModel().getColumn(i);
            column.setIdentifier(column.getHeaderValue());
        }
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

    /**
     * Returns the number of pixels to be added below table's last row.
     *
     * @return number of pixels.
     */
    public int getViewportHeightMargin() {
        return viewportHeightMargin;
    }

    /**
     * Specifies an addition number of pixels to be added below table's last
     * row.
     *
     * @param viewportHeightMargin margin in pixel.
     */
    public void setViewportHeightMargin(int viewportHeightMargin) {
        this.viewportHeightMargin = viewportHeightMargin;
    }

    /**
     * @return if <i>true</i> table is being layouted by it parent, to use it's
     * full widht.
     */
    public boolean isAutoResizeMode() {
        return autoResizeMode;
    }

    /**
     * Specifies if the table should be layout by the parent, to use it's full
     * widht. Additional width will only be added to the last column.
     *
     * @param autoResizeMode if <i>true</i> table will use parents complete
     * width.
     */
    public void setAutoResizeMode(boolean autoResizeMode) {
        this.autoResizeMode = autoResizeMode;
        if (this.autoResizeMode) {
            super.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        } else {
            super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    /**
     * Specifies if a new row is being added after the last row, when editing
     * last row finished. The Table needs to know row data, when rows are
     * created. It uses the given rowPrototype to duplicate new rows.
     *
     * @param rowPrototype a prototype for the data of a row, which get copied
     * to create new rows.
     */
    public void setAutoCreateNewRowAfterLastEdit(final AveTableRowEntry rowPrototype) {
        this.rowPrototype = rowPrototype;
    }

    /**
     * Specifies the minimum width of the table.
     *
     * @param minimumWidth the number of pixels for specifying the total minimum
     * width of all columns.
     */
    public void setMinimumWidth(int minimumWidth) {
        this.minWidthHeaderRenderer.setTotalMinimumWidth(minimumWidth);
    }

    /**
     * Optionally add or reduce extra width for last column, e.g. to gain space
     * for vertical ScrollBar.
     *
     * @param lastColumnExtraWidth the width added or substracted to last column
     * width.
     */
    public void setLastColumnExtraWidth(int lastColumnExtraWidth) {
        this.lastColumnExtraWidth = lastColumnExtraWidth;
    }

    private static class MinWidthHeaderRenderer implements TableCellRenderer {

        private final JTable table;
        private final DefaultTableCellRenderer renderer;
        private final int columnPadding;
        private int totalMinimumWidth;

        public MinWidthHeaderRenderer(final JTable table, int columnPadding) {
            this.table = table;
            this.renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();

            // Have the header label be in the center of column.
            renderer.setHorizontalAlignment(JLabel.CENTER);

            this.columnPadding = columnPadding;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            final Component component
                    = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            final TableColumnModel columnModel = table.getColumnModel();
            final TableColumn tableColumn = columnModel.getColumn(column);

            // Set minimum width from component preferred size.
            int minColumnWidth = component.getPreferredSize().width + (2 * this.columnPadding);

            // Make sure that the width of all columns is at least as wide as the totalMinimumWidth,
            // which might have been set externally (e.g. because add- and remove buttons need more space).
            if (columnModel.getTotalColumnWidth() < this.totalMinimumWidth
                    && column == (columnModel.getColumnCount() - 1)) {
                minColumnWidth = tableColumn.getWidth() + this.totalMinimumWidth - columnModel.getTotalColumnWidth();
            }

            if (tableColumn.getWidth() < minColumnWidth) {
                tableColumn.setMinWidth(minColumnWidth);
            }

            return component;
        }

        public void setTotalMinimumWidth(int totalMinimumWidth) {
            this.totalMinimumWidth = totalMinimumWidth;
        }
    }
}
