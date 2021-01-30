package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.model.AveChoiceElement;
import de.joergwille.playground.shareddatamodel.model.AveSharedComboBoxModel;
import de.joergwille.playground.shareddatamodel.model.AveSharedDataModel;
import de.joergwille.playground.shareddatamodel.model.AveTable;
import de.joergwille.playground.shareddatamodel.model.AveTableModel;
import de.joergwille.playground.shareddatamodel.model.AveTableRowEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author joerg.wille@gmail.com
 */
public class SharedDataModelUi extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String[] items = {"None1", "Spring", "Summer", "Fall", "Winter"};
    private static final String[] updatedItems = {"None2", "Frühling", "Sommer", "Winter", "Herbst", "EinGanzLangerStringMitSehrVielenBuchstaben"};
    private static final int MIN_VISIBLE_ROW_COUNT = 3;
    private static final int DEFAULT_VIEWPORT_HEIGHT_MARGIN = 10;
    private static final int DEFAULT_COLUMN_HEADER_PADDING = 10;

    //The one & only sharedDataModel for both JComboBoxes
    final AveSharedDataModel<String> sharedDataModel;

    public SharedDataModelUi() {
        super("SharedDataModel Test");
        super.setSize(800, 400);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);

        // create a SharedDataModel with AutoSetPrototypeDisplayValue enabled.
        this.sharedDataModel = new AveSharedDataModel<>(true);
        for (String item : items) {
            sharedDataModel.addElement(item);
        }

        createLayout(super.getContentPane());
        super.setVisible(true);
    }

    private void createLayout(Container container) {
        JTabbedPane tabPane = new JTabbedPane();
        createJCombos(tabPane);
        createTable(tabPane);
        container.add(tabPane);
    }

    @SuppressWarnings("unchecked")
    private void createJCombos(final JTabbedPane tabPane) {
        JPanel rootJCombos = new JPanel(new BorderLayout());
        tabPane.add("ComboBox", rootJCombos);

        JPanel combosPanel = new JPanel(new FlowLayout());
        rootJCombos.add(combosPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 15, 5));
        rootJCombos.add(buttonsPanel, BorderLayout.PAGE_END);

        final int NUM_JCOMBO_BOXES = 2;
        final String[] lastSelectedjComboItem = new String[NUM_JCOMBO_BOXES];

        for (int i = 0; i < NUM_JCOMBO_BOXES; i++) {
            final int index = i;
            AveSharedComboBoxModel<String> seperateSelectableSharedDataModel = new AveSharedComboBoxModel<>(sharedDataModel);
            seperateSelectableSharedDataModel.setAllowEmptySelection(false); // no null or empty selection, always fall back to select 1'st item.
            seperateSelectableSharedDataModel.setMatchSelectionByString(true); // on item updates, match the updatableSelection item by string instead of the index.
            seperateSelectableSharedDataModel.setAllowOneMutation(true); // retain selection state (keep updatableSelection index) if exactly one element mutates (e.g. if it is being "renamed")
            seperateSelectableSharedDataModel.setForceDeselectionOnIndexChange(true); // force a deselection of the item since the index has changed but the updatableSelection item remained constant

            AveChoiceElement jComboBox = new AveChoiceElement(seperateSelectableSharedDataModel);
            seperateSelectableSharedDataModel.setAssociatedComboBox(jComboBox); // associate a JComboBox for optimization of rendering with a DisplayPrototyp

            jComboBox.setSelectedIndex(0);
            lastSelectedjComboItem[index] = seperateSelectableSharedDataModel.getSelectedItem().toString();
            jComboBox.addItemListener(itemEvent -> {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    final Object selectedItem = itemEvent.getItem();
                    final String string = selectedItem != null ? selectedItem.toString() : "none";
                    System.out.printf("List %d selected %s (idx=%d)\n", index + 1, string, sharedDataModel.getIndexOf(selectedItem));
                    lastSelectedjComboItem[index] = selectedItem != null ? string : null;
                }
                if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                    final Object selectedItem = itemEvent.getItem();
                    final String string = selectedItem != null ? selectedItem.toString() : "none";
                    System.out.printf("List %d deselected %s (idx=%d)\n", index + 1, string, sharedDataModel.getIndexOf(selectedItem));
                }
            });
            combosPanel.add(jComboBox);
            JButton btnAddOrRename;
            if (index == 0) {
                btnAddOrRename = new JButton("Add to both");
                btnAddOrRename.addActionListener(a -> {
                    String add = JOptionPane.showInputDialog("Add an entry");
                    if (add != null && !add.isEmpty()) {
                        sharedDataModel.addElement(add);
                    }
                });
            } else {
                btnAddOrRename = new JButton("Rename in both");
                btnAddOrRename.addActionListener(a -> {
                    if (lastSelectedjComboItem[index] != null) {
                        String rename = JOptionPane.showInputDialog("Rename " + lastSelectedjComboItem[index]);
                        if (rename != null && !rename.isEmpty()) {
                            final List<String> sharedDataModelAsList = sharedDataModel.toList();
                            int itemIndex = sharedDataModel.getIndexOf(lastSelectedjComboItem[index]);
                            sharedDataModelAsList.set(itemIndex, rename);
                            sharedDataModel.update(sharedDataModelAsList);
                        }
                    }
                });
            }
            buttonsPanel.add(btnAddOrRename);

            JButton btnRemoveOrUpdate;
            if (index == 0) {
                btnRemoveOrUpdate = new JButton("Update all");
                btnRemoveOrUpdate.addActionListener(a -> {
                    sharedDataModel.update(Arrays.asList(updatedItems));
                });
            } else {
                btnRemoveOrUpdate = new JButton("Remove from both");
                btnRemoveOrUpdate.addActionListener(a -> {
                    if (lastSelectedjComboItem[index] != null) {
                        sharedDataModel.removeElement(lastSelectedjComboItem[index]);
                        lastSelectedjComboItem[index] = null;
                    }
                });
            }
            buttonsPanel.add(btnRemoveOrUpdate);
        }
    }

    private static void layoutTablePanel(final JPanel parentPanel, final JPanel childPanel, int topPosition, int leftPosition) {
        final Insets parentInsets = parentPanel.getInsets();
        final Dimension size = childPanel.getPreferredSize();
        childPanel.setBounds(parentInsets.left + leftPosition, parentInsets.top + topPosition, size.width, size.height);
        parentPanel.validate();
    }

    private void createTable(final JTabbedPane tabPane) {
        String[] columnNames = {"Number", "Choice", "Text", "SharedChoice", "Checkbox"};
        String[][] stringData = {
            {"0", "null"}, {"1", "eins"}, {"2", "zwei"}, {"3", "drei"}, {"4", "vier"}
        };

        // create a SharedDataModel with AutoSetPrototypeDisplayValue enabled.
        AveSharedDataModel<String> choiceData = new AveSharedDataModel<>(new String[]{"None3", "A", "B", "C", "D"}, true);

        int numberOfRows = stringData.length;
        final AveTableRowEntry[] tableData = new AveTableRowEntry[numberOfRows];

//        TWO POSSIBLE WAYS TO INSTNCIATE TABEL DATA:
//        1. USING CLASSES AND AVEUPDATABLESELECTION
//        final Class<?>[] sequence = new Class<?>[]{String.class, AveUpdatableSelection.class, String.class, AveUpdatableSelection.class, Boolean.class};
//        for (int i = 0; i < numberOfRows; i++) {
//            AveUpdatableSelection<String> comboBoxData1 = new AveUpdatableSelection<>(choiceData, null, false, true);
//            AveUpdatableSelection<String> comboBoxData2 = new AveUpdatableSelection<>(this.sharedDataModel, null, false, true);
//            tableData[i] = new AveTableRowEntry(sequence, stringData[i], new Boolean[]{(i % 2 == 0)}, new AveUpdatableSelection<?>[]{comboBoxData1, comboBoxData2});
//        }
//        2. USING COLUMNTYPES AND CHOICEMODELS
        final String[] columnTypes = new String[]{"string", "choice", "string", "choice", "boolean"};
        @SuppressWarnings({"unchecked", "rawtypes"})
        final AveSharedDataModel<String>[] choiceModels = new AveSharedDataModel[]{choiceData, this.sharedDataModel};

        for (int i = 0; i < numberOfRows; i++) {
            String[] stringDataForRow = stringData[i];
            // defaultValues is optional, but if set, then it must be same length as number of coloumns.
            // if there are no defaultValues for ComboBoxes use 'null' for 'choice' coloumns.
            String[] defaultValues = new String[]{stringDataForRow[0], choiceData.getElementAt(i), stringDataForRow[1], this.sharedDataModel.getElementAt(i), (i % 2 == 0) ? "true" : "false"};
            tableData[i] = new AveTableRowEntry(columnTypes, choiceModels, defaultValues);
        }
//      END OF TABEL DATA INITIALIZATION         

//      TABLE MODEL INITIALIZATION
        final AveTableModel tableModel = new AveTableModel(columnNames);
        for (AveTableRowEntry rowData : tableData) {
            tableModel.addRow(rowData);
        }

//      TABLE INITIALIZATION
        final AveTable table = new AveTable(tableModel, MIN_VISIBLE_ROW_COUNT, DEFAULT_COLUMN_HEADER_PADDING, DEFAULT_VIEWPORT_HEIGHT_MARGIN);
        table.setRowHeight(35);

//      UI INITIALIZATION
        final JPanel rootJTables = new JPanel();
        rootJTables.setLayout(null); // absolute positioning
        rootJTables.setBackground(Color.BLUE);

        final JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(true);
        tablePanel.setBackground(Color.GREEN);

        // SCROLLPANE INITIALIZATION
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.MAGENTA);
        scrollPane.getViewport().setBackground(Color.RED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // In JTable PreferredScrollableViewportSize is not implemented, therefore use PreferredSize
        // scrollPane.getViewport().setPreferredSize(table.getPreferredSize());
        // In AveTable PreferredScrollableViewportSize is implemented.
        scrollPane.getViewport().setPreferredSize(table.getPreferredScrollableViewportSize());

        // Table columns width change dynamically and therefore the ViewPort changes.
        // To reflect these changes TablePanel needs to be layouted.
        scrollPane.getViewport().addChangeListener((ChangeEvent e) -> {
            final Dimension d = table.getPreferredScrollableViewportSize();
            d.height = tablePanel.getHeight();
            if (scrollPane.getVerticalScrollBar().isVisible()) {
                d.width += scrollPane.getVerticalScrollBar().getWidth();
            }
            tablePanel.setPreferredSize(d);
            SharedDataModelUi.layoutTablePanel(rootJTables, tablePanel, 60, 50);
        });
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // BUTTON INITIALIZATION
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        final JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(a -> {
            tableModel.addRow(new AveTableRowEntry(columnTypes, choiceModels));
        });
        final JButton removeRowButton = new JButton("Remove Row");
        removeRowButton.addActionListener(a -> {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        });
        buttonsPanel.add(addRowButton, BorderLayout.LINE_START);
        buttonsPanel.add(removeRowButton, BorderLayout.LINE_END);
        tablePanel.add(buttonsPanel, BorderLayout.PAGE_END);

        // TablePanel can be resized vertically by clicking and dragging mouse on it.
        final Point startPoint = new Point();
        tablePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    startPoint.setLocation(e.getPoint());
                }
            }
        });
        tablePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                final Point currentPoint = event.getPoint();
                int dY = currentPoint.y - startPoint.y;
                startPoint.setLocation(currentPoint);

                final Dimension d = tablePanel.getPreferredSize();
                d.height += dY;
                
                // Only change preferred height if greater then minimum height.
                if (d.height > tablePanel.getMinimumSize().height) {
                    tablePanel.setPreferredSize(d);
                }
                SharedDataModelUi.layoutTablePanel(rootJTables, tablePanel, 60, 50);
            }
        });

        SharedDataModelUi.layoutTablePanel(rootJTables, tablePanel, 60, 50);
        // save TablePanel minimum size.
        tablePanel.setMinimumSize(tablePanel.getPreferredSize());
        
        rootJTables.add(tablePanel);
        tabPane.add("Table", rootJTables);
    }
}
