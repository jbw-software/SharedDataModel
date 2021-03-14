package de.joergwille.playground.shareddatamodel.swing;

import de.joergwille.playground.shareddatamodel.swing.model.BestWidthStringCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * Provides a JPanel which holds a table for display, that means non-editable,
 * with some specified color renderer and the possibility for row filtering.
 *
 * @author Rolf Neubert, IFAG ETS DMF TTN
 */
// Base class
@SuppressWarnings("serial")
public class AveDisplayTablePanel extends AveTablePanel {

    private static final Matcher TWO_DIM_TABLE = Pattern.compile("(?s)^\\(\\s*\\[.*\\]\\s*\\)").matcher("");
    // Define pattern for save split of rows, since column might contain function with ',', e.g. parval(a,test0)
    private static final Pattern safeCommaMatch = Pattern.compile(
            "\\s*,\\s*  # Match a comma\n" +
            "(?!        # only if it's not followed by...\n" +
            " [^(]*     #   any number of characters except opening parens\n" +
            " \\)       #   followed by a closing parens\n" +
            ")          # End of negative lookahead\n",
            Pattern.COMMENTS);
    private final String[] tableHeader;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private Map<String, Color> tableCellColorMap;
    private int separatorColumn = 0;

    /**
     * Creates a new instance of aveDisplayTablePanel, all entries center aligned.
     *
     * @param header   Table header, columns separated with ':'.
     * @param contents String representing a one or two-dimension table in AVF format.
     */
    public AveDisplayTablePanel(final String header, final String contents) {
        this(header, contents, null);
    }

    /**
     * Creates a new instance of aveDisplayTablePanel with a color map for coloring the table cells,
     * all entries center aligned.
     *
     * @param header   Table header, columns separated with ':'.
     * @param contents String representing a one or two-dimension table in AVF format.
     * @param colorMap Color map for the tables cell renderer.
     */
    public AveDisplayTablePanel(final String header, final String contents, final Map<String, Color> colorMap) {
        this(header, contents, colorMap, JLabel.CENTER);
    }

    /**
     * Creates a new instance of aveDisplayTablePanel with a titled border,
     * all entries aligned according to cellAlignment.
     *
     * @param header        Table header, columns separated with ':'.
     * @param contents      String representing a one or two-dimension table in AVF format.
     * @param title         Creates a titled border around the table.
     * @param cellAlignment Alignment of cells.
     */
    public AveDisplayTablePanel(final String header, final String contents, final Map<String, Color> colorMap, final int cellAlignment) {
        this(header, contents, colorMap, cellAlignment, "");

    }

    /**
     * Creates a new instance of aveDisplayTablePanel with a titled border,
     * all entries aligned according to cellAlignment.
     *
     * @param header        Table header, columns separated with ':'.
     * @param contents      String representing a one or two-dimension table in AVF format.
     * @param title         Creates a titled border around the table.
     * @param cellAlignment Alignment of cells.
     */
    public AveDisplayTablePanel(final String header, final String contents, final String title, final int cellAlignment) {
        this(header, contents, null, cellAlignment, title);

    }

    /**
     * Creates a new instance of aveDisplayTablePanel with a titled border,
     * all entries aligned according to cellAlignment.
     *
     * @param header        Table header, columns separated with ':'.
     * @param contents      String representing a one or two-dimension table in AVF format.
     * @param title         Creates a titled border around the table.
     * @param cellAlignment Alignment of cells.
     */
    public AveDisplayTablePanel(final String header, final String contents, final Map<String, Color> colorMap, final int cellAlignment, final String title) {
        // columnHeaders, columnTypes, choiceModels, columnDefaults, layoutMode, initNbrOfRows, minNbrOfRows, minHeightInRows
        super(generateColumnHeaders(header), generateColumnTypes(generateColumnHeaders(header).length), null, null, LayoutMode.DISPLAY, 0, 0, 0);

        // Get array of table header components (split such that also empty strings may occur).
        this.tableHeader = generateColumnHeaders(header);

        // Assign color map (null if not available).
        tableCellColorMap = colorMap;

        super.table.setBackground(Color.red);

         // Allow to sort the rows just by clicking the header.
        super.table.setAutoCreateRowSorter(true);

//        if (table.getColumnCount() > 0) {
//            if (header.matches("^:+$")) {
//                table.setTableHeader(null);
//            } else {
//                table.getColumnModel().getColumn(0).setHeaderRenderer(new AveDisplayTableCellHeaderRenderer());
//            }
            table.setDefaultRenderer(table.getColumnClass(0), new AveDisplayTableCellRenderer(cellAlignment));
//        }

        // Layout is BorderLayout.
//        this.setLayout(new BorderLayout());

        // Give any resize space to table.
//        this.add(tableScroll, BorderLayout.CENTER);

        // Add a titled border, if a title is given.
//        if (title != null) {
//            this.setBorder(BorderFactory.createTitledBorder(title));
//        }
    }
    
    private static String[] generateColumnHeaders(final String header) {
        return header.split("\\s*:\\s*", -1);
    }
    
        private static String[] generateColumnTypes(final int dim) {
        final String[] columnTypes = new String[dim];

        for (int idx = 0; idx < dim; idx++) {
            columnTypes[idx] = "string";
        }
        return columnTypes;
    }

    /**
     * Creates and add a new row sorter to the table.
     */
    public void setRowSorter() {
//        tableSorter = new TableRowSorter<>((TableModel)super.getTableModel());
//        table.setRowSorter(tableSorter);
    }

    /**
     * Sets the color map for coloring the table cell background for specified string/color combinations.
     *
     * @param colorMap Mapping of Strings to background colors.
     */
    public void setCellColorMap(final Map<String, Color> colorMap) {
        tableCellColorMap = colorMap;
    }

    /**
     * Sets the separator column (default is 0).
     *
     * @param col Column after which a separator is displayed. To have no separator, set to -1
     */
    public void setSeparatorColumn(final int col) {
        separatorColumn = col;
    }

    /**
     * Applies a row filter for the rows given by the integer array filteredRows.
     *
     * @param filteredRows An array holding the indices of the rows to be filtered.
     */
    public void applyRowsFilter(final int[] filteredRows) {
        final RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {

            @Override
            public boolean include(final Entry<? extends DefaultTableModel, ? extends Object> entry) {
                final Set<Integer> setOfIndices = new HashSet<>();
                for (final int row : filteredRows) {
                    setOfIndices.add(row);
                }
                return setOfIndices.contains((Integer) entry.getIdentifier());
            }
        };
        // Add table sorted, if not yet existent.
        if (tableSorter == null) {
            setRowSorter();
        }
        tableSorter.setRowFilter(rf);
        // Remove single scroll pane and replace it with the filtered table.
        this.remove(0);
//        this.add(new AveTableScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }

    /**
     * Adds a new row and initializes it with the zero-th value of the choice.
     */
//    private void addRow() {
//        final int dimension = tableModel.getColumnCount();
//        final String[] newObj = new String[dimension];
//        tableModel.addRow(newObj);
//        for (int idx = 0; idx < dimension; idx++) {
//            final TableColumn col = table.getColumnModel().getColumn(idx);
//            table.setValueAt("__none", tableModel.getRowCount() - 1, idx);
//        }
//        // Make taller rows
//        table.setRowHeight(tableModel.getRowCount() - 1, 20);
//    }

    /**
     * Returns the table of the display table panel.
     *
     * @return The table of the display table panel.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Returns the table model of the display table panel.
     *
     * @return The table model of the display table panel.
     */
//    public TableModel getTableModel() {
//        return super.getTableModel();
//    }

    /**
     * Returns the table header of the display table panel.
     *
     * @return The table header of the display table panel.
     */
    public String[] getTableHeader() {
        return tableHeader;
    }

    /**
     * Returns a list of all entries of a column.
     *
     * @param index Index of column to be processed.
     * @return Table column entries.
     */
    public String[] getColumnEntries(final int index) {
        return null;
//        return AveTableData.tableColumnToArray(super.getTableModel(), index);
    }

    /**
     * Deselects all selected columns and rows
     */
    public void clearSelection() {
        table.clearSelection();
        // table still has focus, assign it to other component
        this.requestFocus();
    }

    /**
     * Fills a (n,m) generic table with contents of form (a11,a21,..,an1) for m=1 or
     * ([a11,..,a1m],[a21,..], ,[..,anm]) for m &gt; 1.
     * Currently only raw error check done.
     *
     * @param contents String containing the contents in AVF format.
     */
    private void setContents(final String contents) {

        if (TWO_DIM_TABLE.reset(contents).matches()) {
            // m > 0, two-dimensional array
            final String sub = contents.substring(contents.indexOf('[') + 1, contents.lastIndexOf(']'));
            final String[] rows = sub.trim().split("\\s*\\]\\s*\\,\\s*\\[\\s*");
            final int nbrOfRows = rows.length;
            // get column entries for first row
            String[] row = safeCommaMatch.split(rows[0], -1);
            final int nbrOfCols = row.length;
            // Initialize 2-dimensional array and assign first row
            final String[][] values = new String[nbrOfRows][nbrOfCols];
            values[0] = row;
            // Get other rows
            for (int i = 1; i < nbrOfRows; i++) {
                row = safeCommaMatch.split(rows[i], -1);
                if (row.length != nbrOfCols) {
//                    AveMsg.error(538, "Inconsistent number of columns in table definition -> not loaded!");
                    return;
                } else {
                    values[i] = row;
                }
            }

            // Store values in table.
            for (int idx = 0; idx < nbrOfRows; idx++) {
                if (idx >= super.getTableModel().getRowCount()) {
                    addRow();
                }
                for (int j = 0; j < nbrOfCols; j++) {
                    super.getTableModel().setValueAt(values[idx][j], idx, j);
                }
            }
        } else {
            // m = 1, simple vector
            final String[] values = contents.substring(1, contents.length() - 1).trim().split("\\s*\\,\\s*");
            for (int idx = 0; idx < values.length; idx++) {
                if (idx >= super.getTableModel().getRowCount()) {
                    addRow();
                }
                super.getTableModel().setValueAt(values[idx], idx, 0);
            }
        }
    }

    /**
     * Provides a renderer for the table cells, optionally with a color map.
     */
    private final class AveDisplayTableCellRenderer extends BestWidthStringCellRenderer {

        /**
         * Creates a new instance of {@code AveDisplayTableCellRenderer}.
         */
        private AveDisplayTableCellRenderer() {
            initDisplayTableCellRenderer(JLabel.CENTER);
        }

        /**
         * Creates a new instance of {@code AveDisplayTableCellRenderer}.
         *
         * @param horizontalAlignment Alignment parameter, one of JLabel.LEFT, JLabel.CENTER, ...
         */
        private AveDisplayTableCellRenderer(final int horizontalAlignment) {
            initDisplayTableCellRenderer(horizontalAlignment);
        }

        /**
         * Initializes the display table cell renderer.
         *
         * @param horizontalAlignment Alignment parameter, one of JLabel.LEFT, JLabel.CENTER, ...
         */
        private void initDisplayTableCellRenderer(final int horizontalAlignment) {
            this.setOpaque(true);
            this.setFont(table.getFont().deriveFont(Font.PLAIN));
            this.setHorizontalAlignment(horizontalAlignment);
        }

        /**
         * Implements the mandatory method getTableCellRendererComponent of a TableCellRenderer.
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            final String cellContent = (String) value;
            this.setText(" " + cellContent + " ");

            // Sets a separator between the specified column and the rest of the table.
            if (column == separatorColumn) {
                final Border separator = BorderFactory.createMatteBorder(0, 0, 0, 2, table.getGridColor());
                this.setBorder(separator);
            } else {
                this.setBorder(null);
            }
            if (isSelected) {
                // Set selected cells always with the same background color.
//                this.setBackground(AveCustom.getColor(AveCustom.MY_BACK_GROUND));
            } else {
                // If a color map is provided, check and apply it.
                if (tableCellColorMap != null && tableCellColorMap.containsKey(cellContent)) {
                    this.setBackground(tableCellColorMap.get(cellContent));
                } else {
                    this.setBackground(table.getBackground());
                }
            }
            return this;
        }
    }

    /**
     * Provides a renderer for the table header, in particular for creating a separator
     * between a specified column and the rest of the table.
     */
//    private final class AveDisplayTableCellHeaderRenderer extends JLabel implements TableCellRenderer {
//
//        /**
//         * Creates a new instance of {@code AveDisplayTableCellHeaderRenderer}.
//         */
//        private AveDisplayTableCellHeaderRenderer() {
//            this.setOpaque(true);
//            this.setFont(table.getFont().deriveFont(Font.PLAIN));
//            this.setHorizontalAlignment(JLabel.CENTER);
//            this.setBackground(table.getTableHeader().getBackground());
//        }
//
//        /**
//         * Implements the mandatory method getTableCellRendererComponent of a TableCellRenderer.
//         */
//        @Override
//        public Component getTableCellRendererComponent(final JTable table, final Object value,
//                final boolean isSelected, final boolean hasFocus, final int row, final int column) {
//            // Set contents of the cell.
//            this.setText((String) value);
//            // Sets a separator between the specified column and the rest of the table.
//            if (column == separatorColumn) {
//                final Border separator = BorderFactory.createMatteBorder(0, 0, 1, 3, table.getGridColor());
//                this.setBorder(separator);
//            } else {
//                this.setBorder(null);
//            }
//
//            return this;
//        }
//    }
}

