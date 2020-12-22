/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.model;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author joerg
 */
public class AveTableModel extends AbstractTableModel {

    private final Vector<AveTableRowEntry> entries;
    private final String[] columnNames;

    public AveTableModel(String[] columnNames) {
        super();
        this.entries = new Vector<>();
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
        return getValueAt(0, column).getClass();
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

}
