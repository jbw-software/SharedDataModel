package de.joergwille.playground.shareddatamodel.swing.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
public final class AveChoiceElementCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final class InstanceHolder {

        static final AveChoiceElementCellEditor INSTANCE = new AveChoiceElementCellEditor();
    }

    AveUpdatableSelection<String> updatableSelection;
    final JComboBox<String> comboBox;

    private AveChoiceElementCellEditor() {
        super();
        this.comboBox = new JComboBox<>();
        this.comboBox.setOpaque(true);
        this.comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        this.comboBox.addActionListener(this::comboBoxActionPerformed);
        this.comboBox.addPopupMenuListener(new PopupMenuCanceledListener(this));
    }

    /**
     * Returns a single instance of <code>AveChoiceElementCellEditor</code>.
     *
     * @return <code>AveChoiceElementCellEditor</code> singelton object.
     */
    public static AveChoiceElementCellEditor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void comboBoxActionPerformed(ActionEvent event) {
        JComboBox<?> aComboBox = (JComboBox) event.getSource();
        this.updatableSelection.setSelectedItem(aComboBox.getSelectedItem());
        super.stopCellEditing();
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

        this.updatableSelection = (AveUpdatableSelection) value;
        this.comboBox.setModel(new AveSharedComboBoxModel<>(this.updatableSelection.sharedModel));
        this.comboBox.setPrototypeDisplayValue(this.updatableSelection.getPrototypeDisplayValue());
        this.comboBox.setSelectedItem(this.updatableSelection.getSelectedItem());

        return this.comboBox;
    }

    private final class PopupMenuCanceledListener implements PopupMenuListener {

        final CellEditor cellEditor;

        public PopupMenuCanceledListener(final CellEditor cellEditor) {
            this.cellEditor = cellEditor;
        }

        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
            // Do nothing
        }

        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            // Do nothing
        }

        @Override
        public void popupMenuCanceled(final PopupMenuEvent e) {
            this.cellEditor.cancelCellEditing();
        }
    }
}
