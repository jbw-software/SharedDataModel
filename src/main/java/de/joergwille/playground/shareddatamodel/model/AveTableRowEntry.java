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
        switch (columnType) {
            case "string":
                return String.class;
            case "boolean":
                return Boolean.class;
            case "choice":
                return AveUpdatableSelection.class;
        }
        throw new UnsupportedOperationException("Given 'columnType' " + columnType +
                " is not supported. The 'columnType' must be a string which can be mapped to a class.");
    }
    
    private static Class<?>[] columnTypesToClasses(String[] columnTypes) {
        Class<?>[] classes = new Class<?>[columnTypes.length];
        for (int i = 0; i < classes.length; i++) {
            int toIndex = columnTypes[i].indexOf("(") > 0 ? columnTypes[i].indexOf("(") : columnTypes[i].length();
            classes[i] = columnTypeToClass(columnTypes[i].substring(0, toIndex).toLowerCase());
        }
        return classes;
    }
    
    public AveTableRowEntry(String[] columnTypes, AveSharedDataModel<String>[] choiceModels) {
      this(columnTypes, choiceModels, null);
    }
    
    public AveTableRowEntry(String[] columnTypes, AveSharedDataModel<String>[] choiceModels, String[] defaultValues) {
        Class<?>[] classes = columnTypesToClasses(columnTypes);
        
        if (defaultValues != null && defaultValues.length > 0 && 
                columnTypes.length > 0 && columnTypes.length != defaultValues.length) {
            throw new IllegalArgumentException("The array arguments 'columnTypes' (" + columnTypes.length + ") "
                    + "and 'defaultValues' (" + defaultValues.length + ") must be of the same lenght.");
        }
        
        // check if given arguments are consistent, e.g. the number of choiceModels must match the number of
        // classes of type AveUpdatableSelection in columnTypes.
        int numberOfAveUpdatableSelectionClasses = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                numberOfAveUpdatableSelectionClasses++;
            }            
        }
        if (numberOfAveUpdatableSelectionClasses > 0 && (choiceModels == null || numberOfAveUpdatableSelectionClasses != choiceModels.length)) {
            throw new IllegalArgumentException("The number of choice elements specified in 'columnTypes' (" + numberOfAveUpdatableSelectionClasses + ") "
                    + "does not match the length of the given argument 'choiceModels' (" + ((choiceModels == null) ? "null" : choiceModels.length) + ").");
        }
        
        rowData = new Object[classes.length];
        int c = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c] = new AveUpdatableSelection<>(choiceModels[m++], defaultValues != null ? defaultValues[c] : null, allowEmptySelection,
                        matchSelectionByString);
            } else if (clazz.equals(String.class)) {
                rowData[c] = (defaultValues != null && defaultValues[c] != null) ? defaultValues[c] : "";
            } else if (clazz.equals(Boolean.class)) {
                rowData[c] = (defaultValues != null && defaultValues[c] != null) ? "true".equalsIgnoreCase(defaultValues[c].trim()) : false;
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
            c++;
        }
    }
    
    /**
     *
     * @param columnTypes An array of <code>String</code> to define the sequence in
     * which the <code>stringValues</code> and <code>comboBoxValues</code> are stored
     * in the row.
     * @param stringValues   An array of <code>String</code> for String columns.
     * @param booleanValues  An array of <code>Boolean</code> for CheckBox columns.
     * @param comboBoxValues An array of <code>AveUpdatableSelection</code> for
     * JComboBox columns.
     */
    public AveTableRowEntry(String[] columnTypes, String[] stringValues, Boolean[] booleanValues, AveUpdatableSelection<?>[] comboBoxValues) {
        this(columnTypesToClasses(columnTypes), stringValues, booleanValues, comboBoxValues);
    }
    
    /**
     *
     * @param classes An array of <code>Class</code> to define the sequence in
     * which the <code>stringValues</code> and <code>comboBoxValues</code> are stored
     * in the row.
     * @param stringValues   An array of <code>String</code> for String columns.
     * @param booleanValues  An array of <code>Boolean</code> for CheckBox columns.
     * @param comboBoxValues An array of <code>AveUpdatableSelection</code> for
     * JComboBox columns.
     */
    public AveTableRowEntry(Class<?>[] classes, String[] stringValues, Boolean[] booleanValues, AveUpdatableSelection<?>[] comboBoxValues) {
        if (classes.length != stringValues.length + comboBoxValues.length + booleanValues.length) {
            throw new IndexOutOfBoundsException("The length of the 'classes' array, which specifies the sequence of " +
                    "the other given arguments, does not match the length of these arguments.");
        }
        rowData = new Object[classes.length];
        int c = 0, s = 0, b = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                rowData[c] = comboBoxValues[m++];
            } else if (clazz.equals(String.class)) {
                rowData[c] = stringValues[s++];
            } else if (clazz.equals(Boolean.class)) {
                rowData[c] = booleanValues[b++];
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
        } else if (clazz.equals(Boolean.class)) {
            final boolean aBooleanValue;
            if (aValue instanceof Boolean) {
                aBooleanValue = (Boolean) aValue;
            } else if (aValue instanceof String) {
                aBooleanValue = "true".equalsIgnoreCase(((String) aValue).trim());
            } else {
                aBooleanValue = false;
            }
            rowData[column] = aBooleanValue;
        } else {
            rowData[column] = aValue;
        }
    }

    public Object[] getRowData() {
        return rowData;
    }

}
