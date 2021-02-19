package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * AveChoiceElementCellRenderer A {@link TableCellRenderer} for
 * {@link AveUpdatableSelection} data coloumns. {@link AveUpdatableSelection} is
 * used to store the selected item of a <code>JComboBox</code>.
 * <code>AveChoiceElementCellRenderer</code> uses a singleton pattern since a
 * single instance can be reused for multiple <code>JComboBox</code> instances.
 *
 * @author willejoerg
 */
@SuppressWarnings("serial")
public class OptimizedWidthStringCellRenderer extends DefaultTableCellRenderer {

    private static final class InstanceHolder {

        static final OptimizedWidthStringCellRenderer INSTANCE = new OptimizedWidthStringCellRenderer();
    }

    private OptimizedWidthStringCellRenderer() {
        super();
    }

    /**
     * Returns a single instance of <code>AveChoiceElementCellRenderer</code>.
     *
     * @return <code>AveChoiceElementCellRenderer</code> singelton object.
     */
    public static OptimizedWidthStringCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Automatically resize column width or use manually size.
        // If once manual resized then do not automatically layout.
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final TableColumn resizingColumn = table.getTableHeader().getResizingColumn();
        
        if (JTable.AUTO_RESIZE_LAST_COLUMN != table.getAutoResizeMode() &&
                 column != (table.getColumnModel().getColumnCount() - 1)) {
            int prefColumnWidth = super.getPreferredSize().width;
            // Check if column is manually being resized but still wider than minWidth.
            if (resizingColumn != null && tableColumn.equals(resizingColumn) &&
                     tableColumn.getWidth() >= tableColumn.getMinWidth()) {
                prefColumnWidth = tableColumn.getWidth();
                tableColumn.setIdentifier("ColumnIsManuallyResized");
            }
            // Automitcally resize column.
            if (tableColumn.getPreferredWidth() != prefColumnWidth &&
                     !"ColumnIsManuallyResized".equals(tableColumn.getIdentifier())) {
                tableColumn.setPreferredWidth(prefColumnWidth);
                tableColumn.setWidth(tableColumn.getPreferredWidth());
            }
        }

        return component;
    }

}
