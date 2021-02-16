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
public class AveChoiceElementCellRenderer extends JComboBox<String> implements TableCellRenderer {

    private static final class InstanceHolder {

        static final AveChoiceElementCellRenderer INSTANCE = new AveChoiceElementCellRenderer();
    }

    private AveChoiceElementCellRenderer() {
        super();
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
        super.setFont(table.getFont());
        AveUpdatableSelection updatableSelection = (AveUpdatableSelection) value;
        super.setModel(new DefaultComboBoxModel(updatableSelection.toArray()));
        super.setMaximumRowCount(Math.min(30, updatableSelection.sharedModel.size()));

        // get PrototypeDisplayValue from AveUpdatableSelection amd use it to update column width.
        this.setPrototypeDisplayValue((String) updatableSelection.getPrototypeDisplayValue());
        
        // automatically resize column width
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final TableColumn resizingColumn = table.getTableHeader().getResizingColumn();

        int prefColumnWidth = super.getPreferredSize().width;
        if (resizingColumn != null && tableColumn.equals(resizingColumn)
                && tableColumn.getWidth() >= tableColumn.getMinWidth()) {
            prefColumnWidth = tableColumn.getWidth();
            tableColumn.setIdentifier("ColumnIsManuallyResized");
        }
        if (tableColumn.getPreferredWidth() != prefColumnWidth
                && !"ColumnIsManuallyResized".equals(tableColumn.getIdentifier())) {
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(tableColumn.getPreferredWidth());
        }
        
        this.setForeground((isSelected) ? table.getSelectionForeground() : table.getForeground());
        this.setBackground((isSelected) ? table.getSelectionBackground() : table.getBackground());

        this.setSelectedItem(updatableSelection.getSelectedItem());

        return this;
    }

}
