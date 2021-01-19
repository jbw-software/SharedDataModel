package de.joergwille.playground.shareddatamodel.model;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * AveChoiceElementCellRenderer A {@link TableCellRenderer} for
 * {@link AveUpdatableSelection} data coloumns. {@link AveUpdatableSelection} is
 * used to store the selected item of a <code>JComboBox</code>.
 * <code>AveChoiceElementCellRenderer</code> uses a singleton pattern since a
 * single instance can be reused for multiple <code>JComboBox</code> instances.
 *
 * @author willejoerg
 * @param <E>
 */
public class AveChoiceElementCellRenderer extends JComboBox<String> implements TableCellRenderer {

    private static final class InstanceHolder {

        static final AveChoiceElementCellRenderer INSTANCE = new AveChoiceElementCellRenderer();
    }

    private AveChoiceElementCellRenderer() {
    }

    public static AveChoiceElementCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this;
        }
        AveUpdatableSelection updatableSelection = (AveUpdatableSelection) value;
        this.setModel(new DefaultComboBoxModel(updatableSelection.toArray()));
        this.setSelectedItem(updatableSelection.getSelectedItem());

        this.setForeground((isSelected && hasFocus) ? table.getSelectionForeground() : table.getForeground());
        this.setBackground((isSelected && hasFocus) ? table.getSelectionBackground() : table.getBackground());

        return this;
    }

}
