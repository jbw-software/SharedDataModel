package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.swing.AveTablePanel;
import de.joergwille.playground.shareddatamodel.swing.model.AveSharedDataModel;

/**
 *
 * @author joerg
 */
@SuppressWarnings("serial")
public class AveGenericTablePanel extends AveTablePanel {

    private static final int DEFAULT_NBR_INIT_ROWS = 2;
    private static final int DEFAULT_NBR_MIN_ROWS = 0;

    /**
     * Creates a new instance of {@code AveGenericTablePanel}.
     * The table is initially empty but maybe initialized using setContents.
     * On default the table has a minimum height of 2 rows.
     *
     * @param columnHeaders    The columnHeaders of the table, the number of entries defines the column dimension.
     * @param columnTypes      A vector that specifies the sequence and the types for table columns.
     * @param choiceModels     A vector that has a reference to the model for columns with a comboBox.
     *                         The sequence of the vector must match the sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults   A vector of the same dimension as the columnHeaders, where the entries are
     *                         not <i>null</i> and then hold the column default value,
     *                         if column entry is optional.
     * @param resizeLastColumn Specifies which AveTablePanel.LayoutMode to be used. If <i>true</i> then
     * <i>LayoutMode.LAST_COLUMN_FILL_WIDTH</i> is used.
     */
    public AveGenericTablePanel(final String[] columnHeaders, final String[] columnTypes, final AveSharedDataModel<String>[] choiceModels,
            final String[] columnDefaults, final boolean resizeLastColumn) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, resizeLastColumn, DEFAULT_NBR_INIT_ROWS);
    }

    /**
     * Creates a new instance of {@code AveGenericTablePanel}.
     * The table is initially empty but maybe initialized using setContents.
     * On default the table has 0 minimum rows.
     *
     * @param columnHeaders    The columnHeaders of the table, the number of entries defines the column dimension.
     * @param columnTypes      A vector that specifies the sequence and the types for table columns.
     * @param choiceModels     A vector that has a reference to the model for columns with a comboBox.
     *                         The sequence of the vector must match the sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults   A vector of the same dimension as the columnHeaders, where the entries are
     *                         not <i>null</i> and then hold the column default value,
     *                         if column entry is optional.
     * @param resizeLastColumn Specifies which AveTablePanel.LayoutMode to be used. If <i>true</i> then
     * <i>LayoutMode.LAST_COLUMN_FILL_WIDTH</i> is used.
     * @param initRowNbr       Defines the initial number of rows.
     */
    public AveGenericTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, final boolean resizeLastColumn, final int initRowNbr) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, resizeLastColumn, initRowNbr, DEFAULT_NBR_MIN_ROWS);
    }

    /**
     * Creates a new instance of {@code AveGenericTablePanel}.
     * The table is initially empty but maybe initialized using setContents.
     *
     * @param columnHeaders    The columnHeaders of the table, the number of entries defines the column dimension.
     * @param columnTypes      A vector that specifies the sequence and the types for table columns.
     * @param choiceModels     A vector that has a reference to the model for columns with a comboBox.
     *                         The sequence of the vector must match the sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults   A vector of the same dimension as the columnHeaders, where the entries are
     *                         not <i>null</i> and then hold the column default value,
     *                         if column entry is optional.
     * @param resizeLastColumn Specifies which AveTablePanel.LayoutMode to be used. If <i>true</i> then
     * <i>LayoutMode.LAST_COLUMN_FILL_WIDTH</i> is used.
     * @param initRowNbr       Defines the initial number of rows.
     * @param minRowNbr        Defines the minimum allowed number of rows.
     */
    public AveGenericTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults,
            final boolean resizeLastColumn, final int initRowNbr, final int minRowNbr) {
        // columnHeaders, columnTypes, choiceModels, columnDefaults, initRowNbr, minNbrOfRows, layoutMode
        super(columnHeaders, columnTypes, choiceModels, columnDefaults, initRowNbr, minRowNbr, resizeLastColumn ?
                LayoutMode.LAST_COLUMN_FILL_WIDTH : LayoutMode.COMPACT);
    }

}
