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

    @SuppressWarnings("unchecked")
    public void addRow(AveTableRowEntry tableEntry) {
        int currentIndex = entries.size();
        this.entries.add(tableEntry);
        fireTableRowsInserted(currentIndex, currentIndex);
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
        } else if (valueAt instanceof AveUpdatableSelection) {
            stringValueAt = (String) ((AveUpdatableSelection) valueAt).getSelectedItem();
        }
        return stringValueAt;
    }

}
