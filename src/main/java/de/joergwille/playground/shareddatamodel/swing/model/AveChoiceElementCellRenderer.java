package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
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
public final class AveChoiceElementCellRenderer extends JComboBox<String> implements TableCellRenderer {

    private static final class InstanceHolder {

        static final AveChoiceElementCellRenderer INSTANCE = new AveChoiceElementCellRenderer();
    }

    private AveChoiceElementCellRenderer() {
        super();
        super.setMaximumRowCount(30);
        setOpaque(true);
    }

    /**
     * Returns a single instance of <code>AveChoiceElementCellRenderer</code>.
     *
     * @return <code>AveChoiceElementCellRenderer</code> singelton object.
     */
    public static AveChoiceElementCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        AveUpdatableSelection updatableSelection = (AveUpdatableSelection) value;
        super.setModel(new DefaultComboBoxModel(updatableSelection.toArray()));

        // get PrototypeDisplayValue from AveUpdatableSelection and use it to update column width.
        this.setPrototypeDisplayValue((String) updatableSelection.getPrototypeDisplayValue());

        // Automatically resize column width or use manually size.
        // If once manual resized then do not automatically layout
        // until updatableSelection was upated.
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final TableColumn resizingColumn = table.getTableHeader().getResizingColumn();
        int prefColumnWidth = Math.max(tableColumn.getMinWidth(), super.getPreferredSize().width);
        // Check if column is manually being resized.
        if (resizingColumn != null && tableColumn.equals(resizingColumn)) {
            tableColumn.setIdentifier("ColumnIsManuallyResized");
        }
        // Check if updatableSelection has been updated.
        if (updatableSelection.isSelectionUpdated()) {
            tableColumn.setIdentifier(tableColumn.getHeaderValue());
        }
        // Automatically resize column.
        if (!"ColumnIsManuallyResized".equals(tableColumn.getIdentifier())) {
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(tableColumn.getPreferredWidth());
        }

        this.setSelectedItem(updatableSelection.getSelectedItem());

        this.setForeground((isSelected && hasFocus) ? table.getSelectionForeground() : table.getForeground());
        this.setBackground((isSelected && hasFocus) ? table.getSelectionBackground() : table.getBackground());

        return this;
    }

}
