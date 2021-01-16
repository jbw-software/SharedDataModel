package de.joergwille.playground.shareddatamodel.model;

/**
 * AveTableRowEntry The data model to store the data for one row in a
 * {@link JTable}. It stores the data for <code>String</code> and
 * <code>JComboBox</code> columns.
 *
 * @author willejoerg
 */
public class AveTableRowEntry {

    final Object[] rowData;

    /**
     *
     * @param classes An array of <code>Class</code> to define the sequence in
     * which the <code>values</code> and <code>comboBoxValues</code> are stored
     * in the row.
     * @param values An array of <code>String</code> for String columns.
     * @param comboBoxValues An array of <code>AveUpdatableSelection</code> for
     * JComboBox columns.
     */
    public AveTableRowEntry(Class<?>[] classes, String[] values, AveUpdatableSelection<?>[] comboBoxValues) {

        if (classes.length != values.length + comboBoxValues.length) {
            throw new IndexOutOfBoundsException("The length of the 'classes' array, which specifies the sequence of the other given arguments, does not match the length of these arguments.");
        }
        rowData = new Object[classes.length];
        int c = 0, s = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c++] = comboBoxValues[m++];
            } else if (clazz.equals(String.class)) {
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
