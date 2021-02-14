package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
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
public class AveChoiceElementCellRenderer extends JComboBox<String> implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final class InstanceHolder {

        static final AveChoiceElementCellRenderer INSTANCE = new AveChoiceElementCellRenderer();
    }

    private AveChoiceElementCellRenderer() {
    }

    public static AveChoiceElementCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this;
        }
        AveUpdatableSelection updatableSelection = (AveUpdatableSelection) value;
        this.setModel(new DefaultComboBoxModel(updatableSelection.toArray()));
        this.setMaximumRowCount(Math.min(30, updatableSelection.sharedModel.size()));

        // get PrototypeDisplayValue from AveUpdatableSelection amd use it to update column width.
        this.setPrototypeDisplayValue((String)updatableSelection.getPrototypeDisplayValue());
        final DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        final TableColumn tableColumn = colModel.getColumn(column);
        int prefColumnWidth = this.getPreferredSize().width;
        if (tableColumn.getWidth() != prefColumnWidth) {
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(tableColumn.getPreferredWidth());
        }
        
        this.setForeground((isSelected && hasFocus) ? table.getSelectionForeground() : table.getForeground());
        this.setBackground((isSelected && hasFocus) ? table.getSelectionBackground() : table.getBackground());

        this.setSelectedItem(updatableSelection.getSelectedItem());

        return this;
    }

}
