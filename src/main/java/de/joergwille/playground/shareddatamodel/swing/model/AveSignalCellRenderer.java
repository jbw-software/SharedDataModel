/**
 * Provides a renderer for each row of the single column of the signal table.
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
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author willejoerg
 */
public final class AveSignalCellRenderer extends AveSignalEntryPanel implements TableCellRenderer {

    private static final class InstanceHolder {

        static final AveSignalCellRenderer INSTANCE = new AveSignalCellRenderer();
    }

    private AveSignalCellRenderer() {
        super();
    }

    /**
     * Returns a single instance of <code>AveSignalCellRenderer</code>.
     *
     * @return <code>AveSignalCellRenderer</code> singelton object.
     */
    public static AveSignalCellRenderer getInstance() {
        return AveSignalCellRenderer.InstanceHolder.INSTANCE;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.setContents((String) value);
        this.setForeground((super.signalTextField.isFocusOwner() || super.chooserButton.isFocusOwner()) ?
                table.getSelectionForeground() : table.getForeground());
        this.setBackground((super.signalTextField.isFocusOwner() || super.chooserButton.isFocusOwner()) ?
                table.getSelectionBackground() : table.getBackground());
        return this;
    }
}
