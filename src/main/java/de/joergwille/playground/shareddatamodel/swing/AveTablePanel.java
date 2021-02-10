package de.joergwille.playground.shareddatamodel.swing;

import de.joergwille.playground.shareddatamodel.swing.model.AveSharedDataModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveTable;
import de.joergwille.playground.shareddatamodel.swing.model.AveTableModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveTableRowEntry;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Extends a JPanel and creates the view and layout as a base class for
 * AveGenericTablePanel and AveVectorPanel.
 *
 * @author willejoerg
 */
@SuppressWarnings("serial")
public abstract class AveTablePanel extends JPanel {

    private static final LayoutMode DEFAULT_LAYOUT_MODE = LayoutMode.COMPACT;
    private static final int DEFAULT_NBR_INITIALLY_CREATED_ROWS = 0;
    private static final int DEFAULT_NBR_MINIMUM_ROWS = 0;
    private static final int DEFAULT_ROW_HEIGHT = 20;

    private static final int DEFAULT_VIEWPORT_HEIGHT_MARGIN = 0;
    private static final int DEFAULT_COLUMN_HEADER_PADDING = 10;

    private final String[] columnTypes;
    private final AveSharedDataModel<String>[] choiceModels;
    private final String[] columnDefaults;
    private final LayoutMode layoutMode;
    private final int minNbrOfRows;

    private final AveTable table;
    private final AveTableModel tableModel;
    private final JPanel tablePanel;
    private final JPanel buttonsPanel;
    private final JScrollPane scrollPane;
    private final ComponentAdapter rootPanelResizedAdapter;
//    private final ComponentAdapter parentResizedAdapter;
    private final MouseAdapter buttonsPanelMousePressedAdapter;
    private final MouseMotionAdapter buttonsPanelMouseDraggedAdapter;
    private final ChangeListener viewportChangeListener;

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is initially
     * empty but maybe initialized using setContents.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels) {
        this(columnHeaders, columnTypes, choiceModels, (String[]) null);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is initially
     * empty but maybe initialized using setContents.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param layoutMode Either <i>COMPACT</i>, <i>LAST_COLUMN_FILL_WIDTH</i> or
     * <i>VECOR</i>. Define layout constraints for table. Possible values
     * defined in {@code AveTablePanel.LayoutMode}.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final LayoutMode layoutMode) {
        this(columnHeaders, columnTypes, choiceModels, (String[]) null, layoutMode);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is initially
     * empty but maybe initialized using setContents.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, DEFAULT_LAYOUT_MODE);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is initially
     * empty but maybe initialized using setContents.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     * @param layoutMode Either <i>COMPACT</i>, <i>LAST_COLUMN_FILL_WIDTH</i> or
     * <i>VECOR</i>. Define layout constraints for table. Possible values
     * defined in {@code AveTablePanel.LayoutMode}.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, final LayoutMode layoutMode) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, layoutMode, DEFAULT_NBR_INITIALLY_CREATED_ROWS);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is is
     * initialized with <i>initNbrOfRows</i> number of rows.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     * @param layoutMode Either <i>COMPACT</i>, <i>LAST_COLUMN_FILL_WIDTH</i> or
     * <i>VECOR</i>. Define layout constraints for table. Possible values
     * defined in {@code AveTablePanel.LayoutMode}.
     * @param initNbrOfRows Defines initial number of rows created automatically
     * on initialization. Default value is DEFAULT_NBR_INITIALLY_CREATED_ROWS.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, final LayoutMode layoutMode,
            int initNbrOfRows) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, layoutMode, initNbrOfRows, DEFAULT_NBR_MINIMUM_ROWS);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is is
     * initialized with <i>initNbrOfRows</i> but at least with
     * <i>minNbrOfRows</i> number of rows.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     * @param layoutMode Either <i>COMPACT</i>, <i>LAST_COLUMN_FILL_WIDTH</i> or
     * <i>VECOR</i>. Define layout constraints for table. Possible values
     * defined in {@code AveTablePanel.LayoutMode}.
     * @param initNbrOfRows Defines initial number of rows created automatically
     * on initialization. Default value is DEFAULT_NBR_MINIMUM_ROWS.
     * @param minNbrOfRows Defines the minimum numbers of (undeletable) rows
     * automatically created.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, final LayoutMode layoutMode,
            int initNbrOfRows, int minNbrOfRows) {
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, layoutMode, initNbrOfRows, minNbrOfRows, AveTable.DEFAULT_VISIBLE_ROW_COUNT);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. This constructor is used
     * for backward compatibility. The parameter <i>initNbrOfRows</i> is used to
     * define the height of the table and not the number of rows created
     * automatically on initialization. The default layout is
     * <i>LayoutMode.COMPACT</i>. The table may be initially empty but has at
     * least <i>minNbrOfRows</i> rows. Rows can be added after initialization
     * using setContents.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     * @param initRowNbr Defines minimum height information for the layout of
     * the table specified in numbers of rows.
     * @param minNbrOfRows Defines the minimum numbers of (undeletable) rows
     * automatically created.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, int initRowNbr, int minNbrOfRows) {
        // For backward compatibility:
        // minHeightInRows = initRowNbr
        // initNbrOfRows = 0
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, DEFAULT_LAYOUT_MODE, 0, minNbrOfRows, initRowNbr);
    }

    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, int initRowNbr, int minNbrOfRows, final LayoutMode layoutMode) {
        // For backward compatibility:
        // minHeightInRows = initRowNbr
        // initNbrOfRows = 0
        this(columnHeaders, columnTypes, choiceModels, columnDefaults, layoutMode, 0, minNbrOfRows, initRowNbr);
    }

    /**
     * Creates a new instance of {@code AveTablePanel}. The table is is
     * initialized with <i>initNbrOfRows</i> but at least with
     * <i>minNbrOfRows</i> number of rows.
     *
     * @param columnHeaders The columnHeaders of the table, the number of
     * entries defines the column dimension.
     * @param columnTypes A vector that specifies the sequence and the types for
     * table columns.
     * @param choiceModels A vector that has a reference to the model for
     * columns with a comboBox. The sequence of the vector must match the
     * sequence of choice elements in vector <i>columnTypes</i>.
     * @param columnDefaults A vector of the same dimension as the
     * columnHeaders, where the entries are not <i>null</i> and then hold the
     * column default value, if column entry is optional.
     * @param layoutMode Either COMPACT or LAST_COLUMN_FILL_WIDTH. Define layout
     * constraints for table.
     * @param initNbrOfRows Defines initial number of rows created automatically
     * on initialization. Default value is 0.
     * @param minNbrOfRows Defines the minimum numbers of (undeletable) rows
     * automatically created.
     * @param minHeightInRows Defines minimum height information for the layout
     * of the table specified in numbers of rows.
     */
    public AveTablePanel(final String[] columnHeaders, final String[] columnTypes,
            final AveSharedDataModel<String>[] choiceModels, final String[] columnDefaults, final LayoutMode layoutMode,
            int initNbrOfRows, int minNbrOfRows, int minHeightInRows) {
        super(null);

        this.columnTypes = columnTypes;
        this.choiceModels = choiceModels;
        this.columnDefaults = columnDefaults;

        // Set layout mode.
        this.layoutMode = layoutMode;

        // Set minNbrOfRows needed in removeSelectedRows to preserve minimum number of rows.
        // For Vector it defaults to 1.
        this.minNbrOfRows = LayoutMode.VECTOR.equals(this.layoutMode) ? 1 : minNbrOfRows;

        // Calculate the number of rows to create. 
        initNbrOfRows = Math.max(this.minNbrOfRows, initNbrOfRows);

        // Create empty table (rows are added dynamically).
        this.tableModel = new AveTableModel(columnHeaders);
        // Table initialisation and configuration
        this.table
                = new AveTable(this.tableModel, minHeightInRows, DEFAULT_COLUMN_HEADER_PADDING, DEFAULT_VIEWPORT_HEIGHT_MARGIN);
        table.setRowHeight(DEFAULT_ROW_HEIGHT);
        table.setAutoResizeMode(LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode));

        // Automatically create initNbrOfRows but at least minNbrOfRows rows.
        for (int idx = 0; idx < initNbrOfRows; idx++) {
            addRow();
        }

        // A Panel for table, add- and remove-button.
        this.tablePanel = new JPanel(new BorderLayout());

        // A layout Panel for add- and remove-button.
        this.buttonsPanel = new JPanel(new BorderLayout());
        this.buttonsPanelMousePressedAdapter = new ButtonsPanelMousePressedAdapter();
        this.buttonsPanelMouseDraggedAdapter
                = new ButtonsPanelMouseDraggedAdapter((ButtonsPanelMousePressedAdapter) this.buttonsPanelMousePressedAdapter, this);

        // A ScrollPane for the table with horizontal scrollbars.
        this.scrollPane = new JScrollPane(table);
        this.viewportChangeListener = this::viewportChanged;

        this.rootPanelResizedAdapter = new RootPanelResizedAdapter(this.table);
        this.initUI();
        this.addListener();
    }

    private void addListener() {
        this.scrollPane.getViewport().addChangeListener(this.viewportChangeListener);
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.buttonsPanel.addMouseListener(this.buttonsPanelMousePressedAdapter);
            this.buttonsPanel.addMouseMotionListener(this.buttonsPanelMouseDraggedAdapter);
        }
        if (LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode)) {
            this.addComponentListener(this.rootPanelResizedAdapter);
        }
    }

    private void removeListener() {
        this.scrollPane.getViewport().removeChangeListener(this.viewportChangeListener);
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.buttonsPanel.removeMouseListener(this.buttonsPanelMousePressedAdapter);
            this.buttonsPanel.removeMouseMotionListener(this.buttonsPanelMouseDraggedAdapter);
        }
        if (LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode)) {
            this.removeComponentListener(this.rootPanelResizedAdapter);
        }
    }

    private void initUI() {
        // TablePanel configuration.
        this.tablePanel.setOpaque(true);

        // ScrollPane configuration.
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.tablePanel.add(this.scrollPane, BorderLayout.CENTER);

        // Add- and remove button initialisation and configuration.
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.buttonsPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            final JButton addRowButton = new JButton("Add");
            addRowButton.setCursor(Cursor.getDefaultCursor());
            addRowButton.addActionListener(a -> {
                SwingUtilities.invokeLater(() -> {
                    this.addRow();
                });
            });
            final JButton removeRowButton = new JButton("Delete");
            removeRowButton.setCursor(Cursor.getDefaultCursor());
            removeRowButton.addActionListener(a -> {
                SwingUtilities.invokeLater(() -> {
                    this.removeSelectedRows();
                });
            });
            this.buttonsPanel.add(addRowButton, BorderLayout.LINE_START);
            this.buttonsPanel.add(Box.createHorizontalStrut(20), BorderLayout.CENTER); // add 20px spacing between buttons
            this.buttonsPanel.add(removeRowButton, BorderLayout.LINE_END);
            this.tablePanel.add(buttonsPanel, BorderLayout.PAGE_END);

            // Set the table's minimum width if buttonsPanel is wider than the current table width.
            if (this.table.getColumnModel().getTotalColumnWidth() < this.buttonsPanel.getPreferredSize().width) {
                this.table.setMinimumWidth(this.buttonsPanel.getPreferredSize().width);
            }
        } else {
            this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }

        super.add(tablePanel);

        // Save ScrollPane minimum height.
        this.updateScrollPaneMinimumHeight();
    }

    // Table columns width change dynamically and therefore the ViewPort changes.
    // To reflect these changes TablePanel needs to be layouted.
    private void viewportChanged(ChangeEvent event) {
        final Dimension scrollPaneViewPortSize = this.scrollPane.getViewport().getViewSize();
        final Dimension scrollPaneSize = this.scrollPane.getPreferredSize();
        if (scrollPaneViewPortSize.width != scrollPaneSize.width + this.scrollPane.getVerticalScrollBar().getWidth()) {
            scrollPaneSize.width = scrollPaneViewPortSize.width;

            // Depending on layoutMode, vertical ScrollBars are either within last column or added to the right side.
            if (LayoutMode.COMPACT.equals(this.layoutMode) && this.scrollPane.getVerticalScrollBar().isVisible()) {
                scrollPaneSize.width += this.scrollPane.getVerticalScrollBar().getWidth();
            }

            this.scrollPane.setPreferredSize(scrollPaneSize);
            this.resize();
        }
    }

    private void resize() {
        if (super.getParent() != null) {
            final Dimension tablePanelSize = this.tablePanel.getPreferredSize();
            final Insets insets = super.getInsets();
            int w = tablePanelSize.width - insets.left - insets.right;
            int h = tablePanelSize.height - insets.top - insets.bottom;

            // Do absolute layout.
            this.tablePanel.setBounds(insets.left, insets.top, w, h);
            super.setPreferredSize(new Dimension(tablePanelSize.width, tablePanelSize.height));
            super.getParent().revalidate();
        }
    }

    private void updateScrollPaneMinimumHeight() {
        final Dimension scrollPaneSize = this.scrollPane.getPreferredSize();
        scrollPaneSize.height += this.table.getTableHeader().getPreferredSize().height;
        this.scrollPane.setMinimumSize(scrollPaneSize);
    }

    private JPanel getTablePanel() {
        return this.tablePanel;
    }

    private JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    /**
     * Adds a new row and initializes it with the zero-th value of the choice.
     */
    protected final void addRow() {
        final AveTableRowEntry emptyRow = new AveTableRowEntry(this.columnTypes, this.choiceModels, this.columnDefaults);
        this.tableModel.addRow(emptyRow);
    }

    /**
     * Removes selected rows.
     */
    private void removeSelectedRows() {
        final int[] selected = table.getSelectedRows();
        final int lastSelected = selected[selected.length - 1];
        for (int idx = selected.length - 1; idx >= 0; idx--) {
            if (this.tableModel.getRowCount() > minNbrOfRows) {
                this.tableModel.removeRow(selected[idx]);
            }
        }
        // Select next to the last deleted row and scroll to the new selection. 
        if (this.tableModel.getRowCount() > 0) {
            int prefSelection = lastSelected + 1 - selected.length;
            prefSelection = this.tableModel.getRowCount() > prefSelection ? prefSelection : this.tableModel.getRowCount() - 1;
            table.setRowSelectionInterval(prefSelection, prefSelection);
            table.scrollRectToVisible(new Rectangle(table.getCellRect(prefSelection, 0, true)));
        }
    }

    @Override
    public void removeNotify() {
        this.removeListener();
    }

    /**
     * Returns the table model of the AveTablePanel.
     *
     * @return The AveTableModel.
     */
    public AveTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Disables editing of all cells in the table.
     */
    public void disableEditing() {
        this.table.setEnabled(false);
    }

    /**
     * Returns the height of a table row, in pixels.
     *
     * @return the height in pixels of a table row
     * @see #setRowHeight
     */
    public int getRowHeight() {
        return this.table.getRowHeight();
    }

    /**
     * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
     * revalidates, and repaints. The height of the cells will be equal to the
     * row height minus the row margin.
     *
     * @param rowHeight new row height
     * @exception IllegalArgumentException if <code>rowHeight</code> is less
     * than 1
     * @see #getRowHeight
     * @beaninfo bound: true description: The height of the specified row.
     */
    public void setRowHeight(int rowHeight) {
        this.table.setRowHeight(rowHeight);
        this.scrollPane.getViewport().setPreferredSize(table.getPreferredScrollableViewportSize());

        // Update ScrollPane minimum height.
        this.updateScrollPaneMinimumHeight();
    }

    private static class RootPanelResizedAdapter extends ComponentAdapter {

        private final AveTable table;

        public RootPanelResizedAdapter(AveTable table) {
            this.table = table;
        }

        @Override
        public void componentResized(ComponentEvent e) {

            AveTablePanel aveTablePanel = (AveTablePanel) e.getComponent();
            this.table.setPreferredWidth(aveTablePanel.getWidth() - 1);
            aveTablePanel.revalidate();
            aveTablePanel.repaint();
        }
    }

    private static class ButtonsPanelMousePressedAdapter extends MouseAdapter {

        private int pointY;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                pointY = e.getPoint().y;
            }
        }

        public int getPointY() {
            return pointY;
        }
    }

    private static class ButtonsPanelMouseDraggedAdapter extends MouseMotionAdapter {

        private final int startPointY;
        private final AveTablePanel aveTablePanel;
        private final JPanel tablePanel;

        public ButtonsPanelMouseDraggedAdapter(final ButtonsPanelMousePressedAdapter mousePressedAdapter, final AveTablePanel aveTablePanel) {
            this.startPointY = mousePressedAdapter.getPointY();
            this.aveTablePanel = aveTablePanel;
            this.tablePanel = aveTablePanel.getTablePanel();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            int yCurrent = event.getPoint().y;
            int dY = yCurrent - startPointY;

            final JScrollPane scrollPane = aveTablePanel.getScrollPane();
            if (scrollPane != null) {
                final Dimension scrollPaneSize = scrollPane.getPreferredSize();
                scrollPaneSize.height += dY;
                if (scrollPaneSize.height > scrollPane.getMinimumSize().height) {
                    scrollPane.setPreferredSize(scrollPaneSize);
                    // Reset the parent's prefferedSize to have the parent layout itself.
                    tablePanel.setPreferredSize(null);
                    aveTablePanel.resize();
                }
            }
        }
    }

    public static enum LayoutMode {
        // Layout table as compact as possible with respect to table rendering constraints.
        COMPACT,
        // Layout table where the last column automatically resizes to parent width.
        LAST_COLUMN_FILL_WIDTH,
        // Same as COMPACT Layout but without add- and delete buttons, without vertical scrollbar and with minNbrOfRows set to 1.
        VECTOR
    }
}
