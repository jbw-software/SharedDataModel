package de.joergwille.playground.shareddatamodel.model;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * AveTableModel
 * A table model which uses {@link AveTableRowEntry}.
 *
 * @author willejoerg
 */
public class AveTableModel extends AbstractTableModel {

    private final Vector<AveTableRowEntry> entries;
    private final String[] columnNames;

    private static Vector<AveTableRowEntry> newVector(int size) {
        Vector<AveTableRowEntry> v = new Vector<>(size);
        v.setSize(size);
        return v;
    }

    public AveTableModel(String[] columnNames) {
        this(columnNames, 0);
    }

    public AveTableModel(String[] columnNames, int rowCount) {
        super();
        this.entries = newVector(rowCount);
        this.columnNames = columnNames;
    }

    /**
     * Adds a row to the end of the model.
     * Notification of the row being added will be generated.
     *
     * @param tableEntry The non-null {@code AveTableRowEntry} object to be added.
     */
    @SuppressWarnings("unchecked")
    public void addRow(AveTableRowEntry tableEntry) {
        if (columnNames.length != tableEntry.rowData.length) {
            throw new IllegalArgumentException("The number of colums (" + tableEntry.rowData.length + ") in given 'TableRowEntry' "
                    + "does not match with the number of columns (" + columnNames.length + ") in this 'TableModel'.");
        }
        int currentIndex = entries.size();
        this.entries.add(tableEntry);
        fireTableRowsInserted(currentIndex, currentIndex);
    }
    
    /**
     * Removes the row at <code>row</code> from the model. Notification
     * of the row being removed will be sent to all the listeners.
     *
     * @param row the row index of the row to be removed
     * @exception ArrayIndexOutOfBoundsException if the row was invalid
     */
    public void removeRow(int row) {
        this.entries.removeElementAt(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getRowCount() {
        return this.entries.size();
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        Class<?> clazz = getValueAt(0, column).getClass();
        if (clazz == null) {
            clazz = super.getColumnClass(column);
        }
        return clazz;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AveTableRowEntry rowData = this.entries.elementAt(rowIndex);
        return rowData.getRowDataForColumn(columnIndex);
    }
    
    public String getStringValueAt(int rowIndex, int columnIndex) {
        final Object valueAt = this.getValueAt(rowIndex, columnIndex);
        String stringValueAt = null;
        if (valueAt instanceof String) {
            stringValueAt = (String) valueAt;
        } else if (valueAt instanceof Boolean) {
            stringValueAt = ((Boolean) valueAt) ? "true" : "false";
        } else if (valueAt instanceof AveUpdatableSelection) {
            stringValueAt = (String) ((AveUpdatableSelection) valueAt).getSelectedItem();
        }
        return stringValueAt;
    }

    /**
     * Sets the object value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>. <code>aValue</code> is the new value.
     * This method will generate a <code>tableChanged</code> notification.
     *
     * @param aValue      the new value; this can be null
     * @param rowIndex    the row whose value is to be changed
     * @param columnIndex the column whose value is to be changed
     * @exception ArrayIndexOutOfBoundsException if an invalid row or column was given
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        AveTableRowEntry rowData = this.entries.elementAt(rowIndex);
        rowData.setRowDataForColumn(aValue, columnIndex);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

}
