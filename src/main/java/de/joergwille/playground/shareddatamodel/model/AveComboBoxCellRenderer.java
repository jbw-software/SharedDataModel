package de.joergwille.playground.shareddatamodel.model;

import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author joerg
 */
public class AveComboBoxCellRenderer extends AveChoiceElement implements TableCellRenderer {

    private static final class InstanceHolder {

        static final AveComboBoxCellRenderer INSTANCE = new AveComboBoxCellRenderer();
    }

    private AveComboBoxCellRenderer() {}

    public static AveComboBoxCellRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this;
        }
        AveUpdatableSelection updatableSelection = (AveUpdatableSelection) value;
        this.setModel(new DefaultComboBoxModel(updatableSelection.toArray()));
        this.setSelectedItem(updatableSelection.getSelectedItem());

        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }

}
