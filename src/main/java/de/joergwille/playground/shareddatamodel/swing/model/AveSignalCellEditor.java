/**
 * Provides an editor for each row of the single column of the signal table.
 * <p>
 * Each row consists of a separate instance of {@code AveSignalEntryPanel}
 * and must be rendered accordingly.</p>
 * <p>
 * Each row is addressed using a hash map from row index to the corresponding signal entry.</p>
 * <p>
 * As additional method the 'removeRow' method is added, because upon deletion of a row,
 * the corresponding editors/renders have to be shifted.</p>
 */
package de.joergwille.playground.shareddatamodel.swing.model;

import de.joergwille.playground.shareddatamodel.swing.AveSignalEntryPanel;
import de.joergwille.playground.shareddatamodel.swing.AveTablePanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author willejoerg
 */
@SuppressWarnings("serial")
public class AveSignalCellEditor extends AbstractCellEditor implements TableCellEditor {

    private AveSignalEntryPanelWithinTable aveSignalEntryPanel;

    private static final class InstanceHolder {

        static final AveSignalCellEditor INSTANCE = new AveSignalCellEditor();
    }

    private AveSignalCellEditor() {
        super();
    }

    /**
     * Returns a single instance of <code>AveSignalCellEditor</code>.
     *
     * @return <code>AveSignalCellEditor</code> singelton object.
     */
    public static AveSignalCellEditor getInstance() {
        return AveSignalCellEditor.InstanceHolder.INSTANCE;
    }

    @Override
    public Object getCellEditorValue() {
        final String currentEditorValue = this.aveSignalEntryPanel.getSignal();
        this.aveSignalEntryPanel.setContents("");
        return currentEditorValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (this.aveSignalEntryPanel == null) {
            this.aveSignalEntryPanel = new AveSignalEntryPanelWithinTable(table);
        }
        this.aveSignalEntryPanel.setSelectedRow(row);
        this.aveSignalEntryPanel.setContents((String) value);
        return this.aveSignalEntryPanel;
    }

    private final class AveSignalEntryPanelWithinTable extends AveSignalEntryPanel implements FocusListener {

        private final JTable table;
        private int selectedRow;
        
        /**
         * Creates a new instance of {@code AveSignalEntryPanel}
         * which provides a text field and a chooser button
         * which opens a signal tree browser, if triggered.
         */
        public AveSignalEntryPanelWithinTable(final JTable table) {
            super(table);
            this.table = table;
        }

        @Override
        protected void addListener() {
            super.addListener();
            super.chooserButton.addFocusListener(this);
            super.signalTextField.addFocusListener(this);
        }

        @Override
        protected void removeListener() {
            super.removeListener();
            super.chooserButton.removeFocusListener(this);
            super.signalTextField.removeFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            this.table.setRowSelectionInterval(this.selectedRow, this.selectedRow);
            super.setForeground(this.table.getSelectionForeground());
            super.setBackground(this.table.getSelectionBackground());
        }

        @Override
        public void focusLost(FocusEvent e) {
            final Component component = e.getOppositeComponent();
            // Do not clear focus if a descending component of the current AveTablePanel is clicked, e.g. Add or Delete button.
            // But clear focus if click is from within the same table.
            if (component != null && !component.equals(this.table)) {
                final Container container = SwingUtilities.getAncestorOfClass(AveTablePanel.class, component);

                if (container != null && SwingUtilities.isDescendingFrom(component, container)) {
                    return;
                }
            }

            this.table.clearSelection();
            super.setForeground(this.table.getForeground());
            super.setBackground(this.table.getBackground());
        }

        public void setSelectedRow(int selectedRow) {
            this.selectedRow = selectedRow;
        }
    }
}
