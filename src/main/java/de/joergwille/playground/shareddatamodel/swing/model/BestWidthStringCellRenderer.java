package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * BestWidthStringCellRenderer A {@link TableCellRenderer} for
 * {@link AveUpdatableSelection} data columns. {@link AveUpdatableSelection} is
 * used to store the selected item of a <code>JComboBox</code>.
 * <code>AveChoiceElementCellRenderer</code> uses a singleton pattern since a
 * single instance can be reused for multiple <code>JComboBox</code> instances.
 *
 * @author willejoerg
 */
@SuppressWarnings("serial")
public class BestWidthStringCellRenderer extends DefaultTableCellRenderer {

    private static final class InstanceHolder {

        static final BestWidthStringCellRenderer INSTANCE = new BestWidthStringCellRenderer();
    }

    protected BestWidthStringCellRenderer() {
        super();
    }

    /**
     * Returns a single instance of <code>AveChoiceElementCellRenderer</code>.
     *
     * @return <code>AveChoiceElementCellRenderer</code> singelton object.
     */
    public static BestWidthStringCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!(table instanceof AveTable)) {
            return component;
        }

        final AveTable aveTable = (AveTable) table;
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if (!("ColumnIsManuallyResized".equals(tableColumn.getIdentifier()))) {
            // Check if manually being resized and if so remember this in column identifier.
            final TableColumn resizingColumn = table.getTableHeader().getResizingColumn();
            if (resizingColumn != null && tableColumn.equals(resizingColumn)) {
                tableColumn.setIdentifier("ColumnIsManuallyResized");
                return component;
            }
            // Automatically resize column.
            int prefColumnWidth = super.getPreferredSize().width + super.getInsets().left + super.getInsets().right;
            prefColumnWidth = aveTable.getStringColumnsBestWidth(column, prefColumnWidth);
 
            // Optinionally add extra with, e.g. to gain space for vertical ScrollBar to last column. 
            if (column == (table.getColumnModel().getColumnCount() - 1)) {
                prefColumnWidth += aveTable.getLastColumnExtraWidth();
            }

            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(tableColumn.getPreferredWidth());
        }
        return component;
    }

}
