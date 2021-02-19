package de.joergwille.playground.shareddatamodel.swing.model;

/**
 * AveTableRowEntry The data model to store the data for one row in a
 * {@link JTable}. It stores the data for <code>String</code> and
 * <code>JComboBox</code> columns.
 *
 * @author willejoerg
 */
public class AveTableRowEntry {

    private final Object[] rowData;
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

    /**
     * Creates a <code>AveTableRowEntry</code> object.
     *
     * @param columnTypes    An array of <code>String</code> to define the sequence and type of each column in a row. 
     *                       Each entry should be either <i>string</i>, <i>boolean</i> or <code>choice</code>.
     */
    public AveTableRowEntry(final String[] columnTypes) {
        this(columnTypes, null, null);
    }

    /**
     * Creates a <code>AveTableRowEntry</code> object.
     *
     * @param columnTypes    An array of <code>String</code> to define the sequence and type of each column in a row. 
     *                       Each entry should be either <i>string</i>, <i>boolean</i> or <i>choice</i>.
     * @param choiceModels   An array of <code>AveUpdatableSelection</code> for specifying
     *                       <code>AveChoiceElement</code> (JComboBox) columns. The sequence and number of <i>choiceModels</i>
     *                       must match the occurrences of <i>choice</i> entries in <i>columnTypes</i>.
     */
    public AveTableRowEntry(final String[] columnTypes, final AveSharedDataModel<String>[] choiceModels) {
        this(columnTypes, choiceModels, null);
    }

    /**
     * Creates a <code>AveTableRowEntry</code> object.
     *
     * @param columnTypes    An array of <code>String</code> to define the sequence and type of each column in a row. 
     *                       Each entry should be either <i>string</i>, <i>boolean</i> or <i>choice</i>.
     * @param choiceModels   An array of <code>AveUpdatableSelection</code> for specifying
     *                       <code>AveChoiceElement</code> (JComboBox) columns. The sequence and number of <i>choiceModels</i>
     *                       must match the occurrences of <i>choice</i> entries in <i>columnTypes</i>.
     * @param defaultValues  An array of <code>String</code> to define default values. The sequence and number must match
     *                       the entries in <i>columnTypes</i>. 
     */
    public AveTableRowEntry(final String[] columnTypes, final AveSharedDataModel<String>[] choiceModels, final String[] defaultValues) {
        Class<?>[] classes = columnTypesToClasses(columnTypes);

        if (defaultValues != null && defaultValues.length > 0 &&
                columnTypes.length > 0 && columnTypes.length != defaultValues.length) {
            throw new IllegalArgumentException("The array arguments 'columnTypes' (" + columnTypes.length + ") " +
                    "and 'defaultValues' (" + defaultValues.length + ") must be of the same lenght.");
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
            throw new IllegalArgumentException("The number of choice elements specified in 'columnTypes' (" + numberOfAveUpdatableSelectionClasses + ") " +
                    "does not match the length of the given argument 'choiceModels' (" + ((choiceModels == null) ?
                            "null" : choiceModels.length) + ").");
        }

        this.rowData = new Object[classes.length];
        int c = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                this.rowData[c] = new AveUpdatableSelection<>(choiceModels[m++], defaultValues != null ?
                        defaultValues[c] : null, allowEmptySelection,
                        matchSelectionByString);
            } else if (clazz.equals(String.class)) {
                this.rowData[c] = (defaultValues != null && defaultValues[c] != null) ? defaultValues[c] : "";
            } else if (clazz.equals(Boolean.class)) {
                this.rowData[c] = (defaultValues != null && defaultValues[c] != null) ?
                        "true".equalsIgnoreCase(defaultValues[c].trim()) : false;
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
            c++;
        }
    }

    /**
     * Creates a <code>AveTableRowEntry</code> object.
     *
     * @param columnTypes    An array of <code>String</code> to define the sequence and type of each column in a row. 
     *                       Each entry should be either <i>string</i>, <i>boolean</i> or <i>choice</i>.
     * @param stringValues   An array of <code>String</code> for String columns. The sequence and number of <i>stringValues</i>
     *                       must match the occurrences of <i>String</i> entries in <i>columnTypes</i>.
     * @param booleanValues  An array of <code>Boolean</code> for CheckBox columns. The sequence and number of <i>booleanValues</i>
     *                       must match the occurrences of <i>Boolean</i> entries in <i>columnTypes</i>.
     * @param choiceModels   An array of <code>AveUpdatableSelection</code> for specifying
     *                       <code>AveChoiceElement</code> (JComboBox) columns. The sequence and number of <i>choiceModels</i>
     *                       must match the occurrences of <i>choice</i> entries in <i>columnTypes</i>.
     */
    public AveTableRowEntry(String[] columnTypes, String[] stringValues, Boolean[] booleanValues, AveUpdatableSelection<?>[] choiceModels) {
        this(columnTypesToClasses(columnTypes), stringValues, booleanValues, choiceModels);
    }

    /**
     * Creates a <code>AveTableRowEntry</code> object.
     * 
     * @param classes        An array of <i>Classes</i> to define the sequence and type of each column in a row.
     *                       Each entry should be either <i>String.class</i>, <i>Boolean.class</i> or <i>AveUpdatableSelection.class</i>.
     * @param stringValues   An array of <code>String</code> for String columns. The sequence and number of <i>stringValues</i>
     *                       must match the occurrences of <i>String</i> entries in <i>columnTypes</i>.
     * @param booleanValues  An array of <code>Boolean</code> for CheckBox columns. The sequence and number of <i>booleanValues</i>
     *                       must match the occurrences of <i>Boolean</i> entries in <i>columnTypes</i>.
     * @param choiceModels   An array of <code>AveUpdatableSelection</code> for specifying
     *                       <code>AveChoiceElement</code> (JComboBox) columns. The sequence and number of <i>choiceModels</i>
     *                       must match the occurrences of <i>choice</i> entries in <i>columnTypes</i>.
     */
    public AveTableRowEntry(Class<?>[] classes, String[] stringValues, Boolean[] booleanValues, AveUpdatableSelection<?>[] choiceModels) {
        if (classes.length != stringValues.length + choiceModels.length + booleanValues.length) {
            throw new IndexOutOfBoundsException("The length of the 'classes' array, which specifies the sequence of " +
                    "the other given arguments, does not match the length of these arguments.");
        }
        this.rowData = new Object[classes.length];
        int c = 0, s = 0, b = 0, m = 0;
        for (Class<?> clazz : classes) {
            if (clazz.equals(AveUpdatableSelection.class)) {
                this.rowData[c] = choiceModels[m++];
            } else if (clazz.equals(String.class)) {
                this.rowData[c] = stringValues[s++];
            } else if (clazz.equals(Boolean.class)) {
                this.rowData[c] = booleanValues[b++];
            } else {
                throw new IllegalArgumentException("The types specified in the 'classes' array is invalid.");
            }
            c++;
        }
    }

    /**
     * Copy constructor to create a new row from a rowPrototype.
     *
     * @param rowPrototype
     */
    public AveTableRowEntry(final AveTableRowEntry rowPrototype) {
        final Object[] newRowData = new Object[rowPrototype.getRowData().length];
        // Make a deep copy;
        System.arraycopy(rowPrototype.getRowData(), 0, newRowData, 0, rowPrototype.getRowData().length);
        this.rowData = newRowData;
    }

    public Object getRowDataForColumn(int column) {
        return this.rowData[column];
    }

    public void setRowDataForColumn(Object aValue, int column) {
        Class<?> clazz = this.rowData[column].getClass();
        if (clazz.equals(AveUpdatableSelection.class)) {
            ((AveUpdatableSelection) this.rowData[column]).setSelectedItem(aValue);
        } else if (clazz.equals(Boolean.class)) {
            final boolean aBooleanValue;
            if (aValue instanceof Boolean) {
                aBooleanValue = (Boolean) aValue;
            } else if (aValue instanceof String) {
                aBooleanValue = "true".equalsIgnoreCase(((String) aValue).trim());
            } else {
                aBooleanValue = false;
            }
            this.rowData[column] = aBooleanValue;
        } else {
            this.rowData[column] = aValue;
        }
    }

    public Object[] getRowData() {
        return this.rowData;
    }
    
    /**
     * Notifies this object that it is no longer being used.
     * This method should be called from the <code>AveTable</code> removeNotify() when it removes this row.
     */
    public void removeNotify() {
        for (Object row : this.rowData) {
            if (row instanceof AveUpdatableSelection) {
                ((AveUpdatableSelection) row).removeNotify();
            }
        }
    }

}
