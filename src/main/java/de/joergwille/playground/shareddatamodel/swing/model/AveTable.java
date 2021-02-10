package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
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

        // JTable uses some default values for PreferredScrollableViewportSize. Do not use these.
        super.setPreferredScrollableViewportSize(null);

        // Table will not fill ScrollPane's ViewPort width. See table.getScrollableTracksViewportWidth().
        super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Table will fill ScrollPane's ViewPort height.
        super.setFillsViewportHeight(false);

        // Usually editing is not terminated if the focus is lost, change this!
        super.putClientProperty("terminateEditOnFocusLost", true);

        // Table colums can not be reordered.
        super.getTableHeader().setReorderingAllowed(false);

        // Configure renderer and editor for ComboBoxes.
        super.setDefaultRenderer(AveUpdatableSelection.class, AveChoiceElementCellRenderer.getInstance());
        super.setDefaultEditor(AveUpdatableSelection.class, AveChoiceElementCellEditor.getInstance());

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

    @Override
    public AveTableModel getModel() {
        return (AveTableModel) super.getModel();
    }

    @Override
    public void doLayout() {
        //  Viewport size changed. Change last column width.
        if (this.autoResizeMode && tableHeader.getResizingColumn() == null) {
            final TableColumnModel tcm = getColumnModel();
            final int delta = getParent().getWidth() - tcm.getTotalColumnWidth();
            final TableColumn last = tcm.getColumn(tcm.getColumnCount() - 1);
            last.setPreferredWidth(last.getPreferredWidth() + delta);
            last.setWidth(last.getPreferredWidth());
        } else {
            super.doLayout();
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

    public int getViewportHeightMargin() {
        return viewportHeightMargin;
    }

    public void setViewportHeightMargin(int viewportHeightMargin) {
        this.viewportHeightMargin = viewportHeightMargin;
    }

    public boolean isAutoResizeMode() {
        return autoResizeMode;
    }

    public void setAutoResizeMode(boolean autoResizeMode) {
        this.autoResizeMode = autoResizeMode;
        if (this.autoResizeMode) {
            super.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        } else {
            super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public void setMinimumWidth(int mnimumWidth) {
        this.minWidthHeaderRenderer.setTotalMinimumWidth(mnimumWidth);
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
            final Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            final TableColumnModel columnModel = table.getColumnModel();
            final TableColumn tableColumn = columnModel.getColumn(column);

            // Set minimum width on first rendering.
            int minColumnWidth = component.getPreferredSize().width + (2 * this.columnPadding);

            // Make sure that the width of all columns is at least as wide as the totalMinimumWidth,
            // which might have been set externally (e.g. because add- and remove buttons need more space).
            if (columnModel.getTotalColumnWidth() < this.totalMinimumWidth
                    && column == (columnModel.getColumnCount() - 1)) {
                minColumnWidth += this.totalMinimumWidth - columnModel.getTotalColumnWidth();
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
