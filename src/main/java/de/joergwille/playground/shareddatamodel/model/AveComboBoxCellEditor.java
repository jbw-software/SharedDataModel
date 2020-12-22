package de.joergwille.playground.shareddatamodel.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author joerg
 */
public class AveComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final class InstanceHolder {

        static final AveComboBoxCellEditor INSTANCE = new AveComboBoxCellEditor();
    }

    AveUpdatableSelection updatableSelection;
    final AveChoiceElement comboBox;

    private AveComboBoxCellEditor() {
        this.comboBox = new AveChoiceElement();
    }

    public static AveComboBoxCellEditor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Object getCellEditorValue() {
        return this.updatableSelection.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this.comboBox;
        }
        this.updatableSelection = (AveUpdatableSelection) value;
        this.comboBox.setModel(new AveSharedComboBoxModel(this.updatableSelection.sharedModel));
        this.comboBox.setSelectedItem(this.updatableSelection.getSelectedItem());
        this.comboBox.addActionListener(this);
        this.comboBox.setBackground(isSelected ? table.getSelectionBackground() : table.getSelectionForeground());
        return this.comboBox;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        AveChoiceElement aComboBox = (AveChoiceElement) event.getSource();
        this.updatableSelection.setSelectedItem(aComboBox.getSelectedItem());
    }

}
