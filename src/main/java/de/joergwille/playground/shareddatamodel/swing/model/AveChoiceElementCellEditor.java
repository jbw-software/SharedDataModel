package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
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
@SuppressWarnings("serial")
public class AveChoiceElementCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final class InstanceHolder {

        static final AveChoiceElementCellEditor INSTANCE = new AveChoiceElementCellEditor();
    }

    AveUpdatableSelection<String> updatableSelection;
    final JComboBox<String> comboBox;

    private AveChoiceElementCellEditor() {
        super();
        this.comboBox = new JComboBox<>();
        this.comboBox.setRequestFocusEnabled(false);
        this.comboBox.setOpaque(true);
    }

    /**
     * Returns a single instance of <code>AveChoiceElementCellEditor</code>.
     *
     * @return <code>AveChoiceElementCellEditor</code> singelton object.
     */
    public static AveChoiceElementCellEditor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            MouseEvent e = (MouseEvent) anEvent;
            return e.getID() != MouseEvent.MOUSE_DRAGGED;
        }
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        if (comboBox.isEditable()) {
            // Commit edited value.
            comboBox.actionPerformed(new ActionEvent(
                    AveChoiceElementCellEditor.this, 0, ""));
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        return this.updatableSelection.getSelectedItem();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (!(value instanceof AveUpdatableSelection)) {
            return this.comboBox;
        }

        this.comboBox.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        this.comboBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        this.updatableSelection = (AveUpdatableSelection) value;
        this.comboBox.setModel(new AveSharedComboBoxModel<>(this.updatableSelection.sharedModel));
        this.comboBox.setPrototypeDisplayValue(this.updatableSelection.getPrototypeDisplayValue());
        this.comboBox.setSelectedItem(this.updatableSelection.getSelectedItem());
        this.comboBox.addActionListener(this);

        return this.comboBox;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JComboBox<?> aComboBox = (JComboBox) event.getSource();
        this.updatableSelection.setSelectedItem(aComboBox.getSelectedItem());
    }

}
