package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.model.AveSharedDataModel;
import de.joergwille.playground.shareddatamodel.model.AveTable;
import de.joergwille.playground.shareddatamodel.model.AveTableModel;
import de.joergwille.playground.shareddatamodel.model.AveTableRowEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author joerg
 */
@SuppressWarnings("serial")
public class AveGenericTablePanel extends JPanel {

    private static final int MIN_VISIBLE_ROW_COUNT = 3;
    private static final int DEFAULT_VIEWPORT_HEIGHT_MARGIN = 10;
    private static final int DEFAULT_COLUMN_HEADER_PADDING = 10;
    private static final int DEFAULT_TABLE_PANEL_TOP = 60;
    private static final int DEFAULT_TABLE_PANEL_LEFT = 50;

    private final String[] columnTypes;
    private final AveSharedDataModel<String>[] choiceModels;
    private final LayoutMode layoutMode;

    private final AveTable table;
    private final JPanel tablePanel;
    final JPanel buttonsPanel;
    private final JScrollPane scrollPane;
    private final ComponentAdapter rootPanelResizedAdapter;
    private final MouseAdapter buttonsPanelMousePressedAdapter;
    private final MouseMotionAdapter buttonsPanelMouseDraggedAdapter;
    private final ChangeListener viewportChangeListener;

    public AveGenericTablePanel(final LayoutMode layoutMode, final AveTableModel tableModel, final String[] columnTypes, final AveSharedDataModel<String>[] choiceModels) {
        // Use absolute positioning for "compact" layout.
        super(null);
        super.setBackground(Color.BLUE);
        
        this.columnTypes = columnTypes;
        this.choiceModels = choiceModels;
        
        // Set layout mode.
        this.layoutMode = layoutMode;

        // Table initialisation and configuration
        this.table = new AveTable(tableModel, MIN_VISIBLE_ROW_COUNT, DEFAULT_COLUMN_HEADER_PADDING, DEFAULT_VIEWPORT_HEIGHT_MARGIN);
        table.setRowHeight(35);
        table.setAutoResizeMode(LayoutMode.LAST_COLUMN_FILL_WIDTH.equals(this.layoutMode));

        // A Panel for table, add- and remove-button.
        this.tablePanel = new JPanel(new BorderLayout());

        // A layout Panel for add- and remove-button.
        this.buttonsPanel = new JPanel(new BorderLayout());
        this.buttonsPanelMousePressedAdapter = new ButtonsPanelMousePressedAdapter();
        this.buttonsPanelMouseDraggedAdapter = new ButtonsPanelMouseDraggedAdapter((ButtonsPanelMousePressedAdapter) this.buttonsPanelMousePressedAdapter, this.tablePanel, this);

        // A ScrollPane for the table with horizontal scrollbars.
        this.scrollPane = new JScrollPane(table);
        this.viewportChangeListener = this::viewportChanged;

        this.rootPanelResizedAdapter = new RootPanelResizedAdapter(this.layoutMode, this.table, this.tablePanel, this, this.scrollPane);

        this.initUI();
        this.addListener();
    }

    @Override
    public void removeNotify() {
        this.removeListener();
    }

    private void addListener() {
        this.addComponentListener(this.rootPanelResizedAdapter);
        this.buttonsPanel.addMouseListener(this.buttonsPanelMousePressedAdapter);
        this.buttonsPanel.addMouseMotionListener(this.buttonsPanelMouseDraggedAdapter);
        this.scrollPane.getViewport().addChangeListener(this.viewportChangeListener);
    }

    private void removeListener() {
        this.removeComponentListener(this.rootPanelResizedAdapter);
        this.buttonsPanel.removeMouseListener(this.buttonsPanelMousePressedAdapter);
        this.buttonsPanel.removeMouseMotionListener(this.buttonsPanelMouseDraggedAdapter);
        this.scrollPane.getViewport().removeChangeListener(this.viewportChangeListener);
    }

    private void initUI() {
        // TablePanel configuration.
        tablePanel.setOpaque(true);
        tablePanel.setBackground(Color.GREEN);

        // ScrollPane configuration.
        scrollPane.setBackground(Color.MAGENTA);
        scrollPane.getViewport().setBackground(Color.RED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setPreferredSize(table.getPreferredScrollableViewportSize());

        // Add- and remove button initialisation and configuration.
        final JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(a -> {
            this.table.getModel().addRow(new AveTableRowEntry(columnTypes, choiceModels));
        });
        final JButton removeRowButton = new JButton("Remove Row");
        removeRowButton.addActionListener(a -> {
            this.table.getModel().removeRow(this.table.getModel().getRowCount() - 1);
        });

        // Setup UI
        buttonsPanel.add(addRowButton, BorderLayout.LINE_START);
        buttonsPanel.add(removeRowButton, BorderLayout.LINE_END);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonsPanel, BorderLayout.PAGE_END);
        this.add(tablePanel);
        
        // Layout UI with absolute positions
        AveGenericTablePanel.setBoundsInParent(tablePanel, this, DEFAULT_TABLE_PANEL_TOP, DEFAULT_TABLE_PANEL_LEFT);

        // Save TablePanel minimum size.
        tablePanel.setMinimumSize(tablePanel.getPreferredSize());
    }

    // Table columns width change dynamically and therefore the ViewPort changes.
    // To reflect these changes TablePanel needs to be layouted.
    private void viewportChanged(ChangeEvent event) {
        final Dimension d = this.table.getPreferredScrollableViewportSize();
        d.height = tablePanel.getHeight();

        // Depending on layoutMode, vertical ScrollBars are either within last column or added to the right side.
        if (LayoutMode.COMPACT.equals(getLayoutMode()) && this.scrollPane.getVerticalScrollBar().isVisible()) {
            d.width += scrollPane.getVerticalScrollBar().getWidth();
        }
        tablePanel.setPreferredSize(d);
        AveGenericTablePanel.setBoundsInParent(tablePanel, this, DEFAULT_TABLE_PANEL_TOP, DEFAULT_TABLE_PANEL_LEFT);
    }

    public LayoutMode getLayoutMode() {
        return this.layoutMode;
    }

    private static void setBoundsInParent(final JPanel childPanel, final JPanel parentPanel, int topPosition, int leftPosition) {
        final Insets parentInsets = parentPanel.getInsets();
        final Dimension size = childPanel.getPreferredSize();
        childPanel.setBounds(parentInsets.left + leftPosition, parentInsets.top + topPosition, size.width, size.height);
        parentPanel.validate();
    }

    private static class RootPanelResizedAdapter extends ComponentAdapter {

        private final LayoutMode layoutMode;
        private final AveTable table;
        private final JPanel tablePanel;
        private final JPanel rootPanel;
        private final JScrollPane scrollPane;

        public RootPanelResizedAdapter(final LayoutMode layoutMode, final AveTable table, final JPanel tablePanel, final JPanel rootPanel, final JScrollPane scrollPane) {
            this.layoutMode = layoutMode;
            this.table = table;
            this.tablePanel = tablePanel;
            this.rootPanel = rootPanel;
            this.scrollPane = scrollPane;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            int tablePreferredWidth = this.rootPanel.getWidth() - this.tablePanel.getLocation().x;
            // If layoutMode is "compact", vertical ScrollBars width can add extra width to the tablePreferredWidth.
            if (LayoutMode.COMPACT.equals(this.layoutMode) && this.scrollPane.getVerticalScrollBar().isVisible()) {
                tablePreferredWidth -= this.scrollPane.getVerticalScrollBar().getWidth();
            }
            this.table.setPreferredWidth(tablePreferredWidth);
//            AveGenericTablePanel.setBoundsInParent(tablePanel, this.rootPanel, DEFAULT_TABLE_PANEL_TOP, DEFAULT_TABLE_PANEL_LEFT);
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
        private final JPanel tablePanel;
        private final JPanel rootPanel;

        public ButtonsPanelMouseDraggedAdapter(final ButtonsPanelMousePressedAdapter mousePressedAdapter, final JPanel tablePanel, final JPanel rootPanel) {
            this.startPointY = mousePressedAdapter.getPointY();
            this.tablePanel = tablePanel;
            this.rootPanel = rootPanel;
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            int yCurrent = event.getPoint().y;
            int dY = yCurrent - startPointY;

            final Dimension d = tablePanel.getPreferredSize();
            d.height += dY;
            
            // Only change preferred height if greater then minimum height.
            if (d.height > tablePanel.getMinimumSize().height) {
                tablePanel.setPreferredSize(d);
            }
            AveGenericTablePanel.setBoundsInParent(tablePanel, rootPanel, DEFAULT_TABLE_PANEL_TOP, DEFAULT_TABLE_PANEL_LEFT);
        }
    }

    public static enum LayoutMode {
        COMPACT,
        LAST_COLUMN_FILL_WIDTH
    }
}
