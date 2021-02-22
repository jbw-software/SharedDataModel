package de.joergwille.playground.shareddatamodel.swing;

import de.joergwille.playground.shareddatamodel.swing.model.AveSharedDataModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveTable;
import de.joergwille.playground.shareddatamodel.swing.model.AveTableModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveTableRowEntry;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

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
    private final JButton addRowButton;
    private final JButton removeRowButton;
    private final JScrollPane scrollPane;
    private final MouseInputListener buttonsPanelResizeListener;
    private final ChangeListener viewportChangeListener;
    private final FocusListener focusListener;

    public boolean verticalScrollBarIsShowing;
    private boolean autoCreateNewRowAfterLastEdit;

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
        // Use absolute positioning or GridLayout for LAST_COLUMN_FILL_WIDTH layout.
        super(LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(layoutMode) ? new GridLayout(1, 0) : null);

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

        this.autoCreateNewRowAfterLastEdit = false;

        // Create empty table (rows are added dynamically).
        this.tableModel = new AveTableModel(columnHeaders);
        // Table initialisation and configuration
        this.table
                = new AveTable(this.tableModel, minHeightInRows, DEFAULT_COLUMN_HEADER_PADDING, DEFAULT_VIEWPORT_HEIGHT_MARGIN);
        table.setRowHeight(DEFAULT_ROW_HEIGHT);
        table.setAutoResizeMode(LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode));
        this.focusListener = new ClearSelectionFocusAdapter(table);

        // Automatically create initNbrOfRows but at least minNbrOfRows rows.
        for (int idx = 0; idx < initNbrOfRows; idx++) {
            this.addRow();
        }

        // A Panel for table, add- and remove-button.
        this.tablePanel = new JPanel(new BorderLayout());

        // A layout Panel for add- and remove-button.
        this.buttonsPanel = new JPanel(new BorderLayout());
        this.addRowButton = new JButton("Add");
        this.removeRowButton = new JButton("Delete");

        this.buttonsPanelResizeListener = new ButtonsPanelMouseInputAdapter(this);

        // A ScrollPane for the table with horizontal scrollbars.
        this.scrollPane = new ScrollWhenScrollBarShowingPane(table, this);
        this.viewportChangeListener = this::viewportChanged;
        this.verticalScrollBarIsShowing = this.scrollPane.getVerticalScrollBar().isShowing();

        this.initUI();
        this.addListener();
    }

    private void addListener() {
        this.scrollPane.getViewport().addChangeListener(this.viewportChangeListener);
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.buttonsPanel.addMouseListener(this.buttonsPanelResizeListener);
            this.buttonsPanel.addMouseMotionListener(this.buttonsPanelResizeListener);
        }
        this.table.addFocusListener(this.focusListener);
    }

    private void removeListener() {
        this.scrollPane.getViewport().removeChangeListener(this.viewportChangeListener);
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.buttonsPanel.removeMouseListener(this.buttonsPanelResizeListener);
            this.buttonsPanel.removeMouseMotionListener(this.buttonsPanelResizeListener);
        }
        this.table.removeFocusListener(this.focusListener);
    }

    private void initUI() {
        // TablePanel configuration.
        this.tablePanel.setOpaque(true);

        // ScrollPane configuration.
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.tablePanel.add(this.scrollPane, BorderLayout.CENTER);

        // Add- and remove button initialisation and configuration.
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.addRowButton.addActionListener(a -> {
                SwingUtilities.invokeLater(() -> {
                    this.addRow();
                });
            });
            this.removeRowButton.addActionListener(a -> {
                SwingUtilities.invokeLater(() -> {
                    this.removeSelectedRows();
                });
            });
            this.buttonsPanel.add(this.addRowButton, BorderLayout.LINE_START);
            // Add 20px spacing between buttons.
            this.buttonsPanel.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
            this.buttonsPanel.add(this.removeRowButton, BorderLayout.LINE_END);
            // A quick dirty fix to pixel align buttonspanel with table. Add 1px inset from right.
            this.buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
            this.tablePanel.add(buttonsPanel, BorderLayout.PAGE_END);

            // Set the table's minimum width if tablePanel with buttonsPanel is wider than the current table width.
            if (this.table.getColumnModel().getTotalColumnWidth() < this.tablePanel.getPreferredSize().width) {
                this.table.setMinimumWidth(this.tablePanel.getPreferredSize().width);
            }
        } else {
            this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }

        super.add(tablePanel);

        // Save ScrollPane minimum height.
        this.updateScrollPaneMinimumHeight();
    }

    // Table columns width change dynamically and therefore the ViewPort changes.
    // If layoutMode is not LAST_COLUMN_FILL_WIDTH we reflect these changes to update ScrollPane size
    // and have TablePanel to layout itself.
    private void viewportChanged(ChangeEvent event) {
        if (!LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode)) {
            final Dimension scrollPaneViewPortSize = this.scrollPane.getViewport().getViewSize();
            int scrollPaneWidth = scrollPaneViewPortSize.width + this.scrollPane.getInsets().left + this.scrollPane.getInsets().right;

            if (this.scrollPane.getVerticalScrollBarPolicy() != JScrollPane.VERTICAL_SCROLLBAR_NEVER) {
                // Add an extra width if vertical ScrollBar is showing to render ScrollBar on the right after last column.
                int barWidth = this.scrollPane.getVerticalScrollBar().isShowing() ? this.scrollPane.getVerticalScrollBar().getWidth() : 0;
                // If last column is of class String then use BestWidthStringCellRenderer to compensate ScrollBar width
                // else simply resize scrollPaneWidth, which for some scrollPane height values occasionally renders scrollBar within last column. 
                if (barWidth > 0) {
                    if (String.class.equals(this.table.getColumnClass(this.table.getColumnCount() - 1))) {
                        this.table.setLastColumnExtraWidth(barWidth);
                    } else {
                        scrollPaneWidth += barWidth;
                    }
                }
            }

            final Dimension scrollPaneSize = this.scrollPane.getPreferredSize();
            scrollPaneSize.width = scrollPaneWidth;
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
            super.setPreferredSize(new Dimension(tablePanelSize.width, tablePanelSize.height));

            // Do absolute layout.
            final Rectangle preferredBounds = new Rectangle(insets.left, insets.top, w, h);
            final Rectangle tablePanelBounds = this.tablePanel.getBounds();

            if (!tablePanelBounds.equals(preferredBounds)) {
                this.tablePanel.setBounds(preferredBounds);
                super.revalidate();
            }
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
        final AveTableRowEntry newRow = new AveTableRowEntry(this.columnTypes, this.choiceModels, this.columnDefaults);
        this.tableModel.addRow(newRow);
    }

    /**
     * Removes selected rows.
     */
    private void removeSelectedRows() {
        if (this.tableModel.getRowCount() == 0 || table.getSelectedRowCount() == 0) {
            return;
        }
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
            prefSelection = this.tableModel.getRowCount() > prefSelection ? prefSelection
                    : this.tableModel.getRowCount() - 1;
            table.setRowSelectionInterval(prefSelection, prefSelection);
            table.scrollRectToVisible(new Rectangle(table.getCellRect(prefSelection, 0, true)));
        }
    }

    /**
     * Notifies this component that it no longer has a parent component. This
     * method is called by the toolkit internally and should not be called
     * directly by programs.
     *
     * @see #registerKeyboardAction
     */
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
        if (!LayoutMode.VECTOR.equals(this.layoutMode)) {
            this.addRowButton.setEnabled(false);
            this.removeRowButton.setEnabled(false);
        }
    }

    /**
     * Specifies if a new row is being added after the last row, when editing
     * last row finished.
     *
     * @param isAutoCreate if <i>true</i> a new row will be added after editing
     * last row finished.
     */
    public void setAutoCreateNewRowAfterLastEdit(final boolean isAutoCreate) {
        final AveTableRowEntry rowPrototype
                = new AveTableRowEntry(this.columnTypes, this.choiceModels, this.columnDefaults);
        this.table.setAutoCreateNewRowAfterLastEdit(isAutoCreate ? rowPrototype : null);
        this.autoCreateNewRowAfterLastEdit = isAutoCreate;
    }

    /**
     * Returns if a new row is being added after the last row, when editing last
     * row finished.
     *
     * @return <i>true</i> if a row will be added after editing last row
     * finished.
     */
    public boolean isAutoCreateNewRowAfterLastEdit() {
        return autoCreateNewRowAfterLastEdit;
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

    private static class ButtonsPanelMouseInputAdapter extends MouseInputAdapter {

        private final AveTablePanel aveTablePanel;

        private int startPointY;

        public ButtonsPanelMouseInputAdapter(final AveTablePanel aveTablePanel) {
            this.aveTablePanel = aveTablePanel;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.aveTablePanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            this.aveTablePanel.setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                startPointY = e.getPoint().y;
            }
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
                    aveTablePanel.getTablePanel().setPreferredSize(null);
                    aveTablePanel.resize();
                }
            }
        }
    }

    private static class ScrollWhenScrollBarShowingPane extends JScrollPane {

        private final JPanel rootPanel;

        public ScrollWhenScrollBarShowingPane(Component view, final JPanel rootPanel) {
            super(view);
            this.rootPanel = rootPanel;
        }

        @Override
        protected void processMouseWheelEvent(MouseWheelEvent evt) {
            final Component parent = SwingUtilities.getAncestorOfClass(Component.class, this.rootPanel);
            final JScrollBar vScrollBar = this.getVerticalScrollBar();

            if (parent != null && vScrollBar != null && !vScrollBar.isShowing()) {
                parent.dispatchEvent(evt);
            } else {
                super.processMouseWheelEvent(evt);
            }
        }
    }

    private static class ClearSelectionFocusAdapter extends FocusAdapter {

        final JTable table;

        public ClearSelectionFocusAdapter(JTable table) {
            this.table = table;
        }

        @Override
        public void focusLost(FocusEvent arg0) {
            final Component component = arg0.getOppositeComponent();

            // Do not clear focus if component's parent is fithin the same Table or
            // if Add or Delete button from the same TablePanel are clicked.
            if ((component != null && component.getParent() != null && component.getParent().equals(this.table))
                    || component instanceof JButton
                    && ("Delete".equals(((JButton) component).getText())
                    || "Add".equals(((JButton) component).getText()))) {
                return;
            }
            table.clearSelection();
        }
    }

    /**
     * The LayoutMode enumeration specifies possible values for the layout of
     * <code>AveTablePanel</code>. Following options are defined: COMPACT:
     * layout table as compact as possible with respect to table rendering
     * constraints and with row add- and delete buttons. LAST_COLUMN_FILL_WIDTH:
     * layout table where the last column automatically resizes to parent width
     * and with row add- and delete buttons. VECTOR: layout table as compact as
     * possible with respect to table rendering constraints but without add- and
     * delete buttons, without vertical scrollbar and with only 1 row.
     */
    public static enum LayoutMode {
        COMPACT,
        LAST_COLUMN_FILL_WIDTH,
        VECTOR
    }
}
