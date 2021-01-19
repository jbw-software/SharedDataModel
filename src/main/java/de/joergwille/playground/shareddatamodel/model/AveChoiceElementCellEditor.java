package de.joergwille.playground.shareddatamodel.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * AveChoiceElementCellEditor A {@link TableCellEditor} for
 * {@link AveUpdatableSelection} data coloumns. {@link AveUpdatableSelection} is
 * used to store the selected item of a <code>JComboBox</code>.
 * <code>AveChoiceElementCellEditor</code> uses a singleton pattern since a
 * single instance can be reused for multiple <code>JComboBox</code> instances.
 *
 * @author willejoerg
 */
public class AveChoiceElementCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final class InstanceHolder {

        static final AveChoiceElementCellEditor INSTANCE = new AveChoiceElementCellEditor();
    }

    AveUpdatableSelection<String> updatableSelection;
    final JComboBox<String> comboBox;

    private AveChoiceElementCellEditor() {
        this.comboBox = new JComboBox<>();
    }

    public static AveChoiceElementCellEditor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Object getCellEditorValue() {
        return this.updatableSelection.getSelectedItem();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this.comboBox;
        }
        this.updatableSelection = (AveUpdatableSelection) value;
        this.comboBox.setModel(new AveSharedComboBoxModel<>(this.updatableSelection.sharedModel));
        this.comboBox.setSelectedItem(this.updatableSelection.getSelectedItem());
        this.comboBox.addActionListener(this);
        this.comboBox.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        this.comboBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this.comboBox;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JComboBox<?> aComboBox = (JComboBox) event.getSource();
        this.updatableSelection.setSelectedItem(aComboBox.getSelectedItem());
    }

}
