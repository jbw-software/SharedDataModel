/*
 * AveVectorPanel.java
 *
 * Copyright (c) 2009 Infineon Technologies AG. All Rights Reserved.
 * Infineon Technologies AG proprietary and confidential information
 */
package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.swing.AveTablePanel;
import de.joergwille.playground.shareddatamodel.swing.AveTablePanel.LayoutMode;
import de.joergwille.playground.shareddatamodel.swing.model.AveSharedDataModel;

/**
 *
 * Extends a AveTablePanel to hold a vector, which contents can be strings, combo boxes and booleans.
 *
 * @author joerg
 */
@SuppressWarnings("serial")
public final class AveVectorPanel extends AveTablePanel {


    /**
     * Creates a new instance of {@code AveVectorPanel}.
     * A 1-line Table.
     *
     * @param columnHeaders    The columnHeaders of the table, the number of entries defines the column dimension.
     * @param columnTypes      A vector that specifies the sequence and the types for table columns.
     * @param choiceModels     A vector that has a reference to the model for columns with a comboBox.
     *                         The sequence of the vector must match the sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults   A vector of the same dimension as the columnHeaders, where the entries are
     *                         not <i>null</i> and then hold the column default value.
     */
    public AveVectorPanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults) {
        // A Vecor is 1-line Table with a 1.line fixed layout. 
        super(columnHeaders, columnTypes, choiceModels, columnDefaults, LayoutMode.VECTOR);
    }
    
}
