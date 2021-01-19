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
    // in AveUpdatableSelection use default values for Avenue.
    public boolean allowEmptySelection = false;
    public boolean matchSelectionByString = true;

    private static Class<?> columnTypeToClass(final String columnType) {
        switch(columnType) {
            case "string":
                return String.class;
            case "choice":
                return AveUpdatableSelection.class;
        }
        throw new UnsupportedOperationException("Given 'columnType' " + columnType +
                " is not supported. The 'columnType' must be a string of 6 characters which can be mapped to a class.");
    }
    
    private static Class<?>[] columnTypesToClasses(String[] coloumnTypes) {
        Class<?>[] classes = new Class<?>[coloumnTypes.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = columnTypeToClass(coloumnTypes[i].substring(0, 6).toLowerCase());
        }
        return classes;
    }
    
    public AveTableRowEntry(String[] coloumnTypes, AveSharedDataModel<String>[] choiceModels) {
      this(coloumnTypes, choiceModels, null);
    }
    
    public AveTableRowEntry(String[] coloumnTypes, AveSharedDataModel<String>[] choiceModels, String[] defaultValues) {
        Class<?>[] classes = columnTypesToClasses(coloumnTypes);
        
        if (defaultValues != null && defaultValues.length > 0 && 
                coloumnTypes.length > 0 && coloumnTypes.length != defaultValues.length) {
            throw new IllegalArgumentException("The array arguments 'coloumnTypes' and 'defaultValues' must have of " +
                    "the same lenght.");
        }
        
        // check if given arguments are consistent, e.g. the number of choiceModels must match the number of
        // classes of type AveUpdatableSelection in coloumnTypes.
        int numberOfAveUpdatableSelectionClasses = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                numberOfAveUpdatableSelectionClasses++;
            }            
        }
        if (numberOfAveUpdatableSelectionClasses != choiceModels.length) {
            throw new IllegalArgumentException("The number of choice elements in 'coloumnTypes' does not match the " +
                    "length of these argument 'choiceModels'.");
        }
        
        rowData = new Object[classes.length];
        int c = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c] = new AveUpdatableSelection<>(choiceModels[m++], defaultValues != null ? defaultValues[c] : null, allowEmptySelection,
                        matchSelectionByString);
            } else if (clazz.equals(String.class)) {
                rowData[c] = (defaultValues != null && defaultValues[c] != null) ? defaultValues[c] : "";
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
            c++;
        }
    }
    
    /**
     *
     * @param coloumnTypes An array of <code>String</code> to define the sequence in
     * which the <code>values</code> and <code>comboBoxValues</code> are stored
     * in the row.
     * @param values An array of <code>String</code> for String columns.
     * @param comboBoxValues An array of <code>AveUpdatableSelection</code> for
     * JComboBox columns.
     */
    public AveTableRowEntry(String[] coloumnTypes, String[] values, AveUpdatableSelection<?>[] comboBoxValues) {
        this(columnTypesToClasses(coloumnTypes), values, comboBoxValues);
    }
    
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
            throw new IndexOutOfBoundsException("The length of the 'classes' array, which specifies the sequence of " +
                    "the other given arguments, does not match the length of these arguments.");
        }
        rowData = new Object[classes.length];
        int c = 0, s = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c] = comboBoxValues[m++];
            } else if (clazz.equals(String.class)) {
                rowData[c] = values[s++];
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
            c++;
        }
    }

    public Object getRowDataForColumn(int column) {
        return rowData[column];
    }
    
    public void setRowDataForColumn(Object aValue, int column) {
        Class<?> clazz = rowData[column].getClass();
        if (clazz.equals(AveUpdatableSelection.class)) {
            ((AveUpdatableSelection) rowData[column]).setSelectedItem(aValue);
        } else {
            rowData[column] = aValue;
        }
    } 

    public Object[] getRowData() {
        return rowData;
    }

}
