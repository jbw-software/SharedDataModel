/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.model;

/**
 *
 * @author joerg
 */
public class AveTableRowEntry {

    final Object[] rowData;

    public AveTableRowEntry(Class[] classes, String[] values, AveUpdatableSelection[] comboBoxValues) {

        if (classes.length != values.length + comboBoxValues.length) {
            throw new IndexOutOfBoundsException("The length of the 'classes' array, which specifies the sequence of the other given arguments, does not match the length of these arguments.");
        }
        rowData = new Object[classes.length];
        int c = 0, s = 0, m = 0;
        for (Class clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c++] = comboBoxValues[m++];
            } else if (clazz.equals(String.class)){
                rowData[c++] = values[s++];
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
        }
    }

    public Object getRowDataForColumn(int column) {
        return rowData[column];
    }

    public Object[] getRowData() {
        return rowData;
    }

}
