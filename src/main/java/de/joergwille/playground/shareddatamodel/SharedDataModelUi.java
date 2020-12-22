package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.model.AveComboBoxCellEditor;
import de.joergwille.playground.shareddatamodel.model.AveComboBoxCellRenderer;
import de.joergwille.playground.shareddatamodel.model.AveSharedComboBoxModel;
import de.joergwille.playground.shareddatamodel.model.AveSharedSelectionModel;
import de.joergwille.playground.shareddatamodel.model.AveTableModel;
import de.joergwille.playground.shareddatamodel.model.AveTableRowEntry;
import de.joergwille.playground.shareddatamodel.model.AveUpdatableSelection;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 *
 * @author joerg.wille@gmail.com
 */
public class SharedDataModelUi extends JFrame {

    private static final int NUM_LISTS = 3;
    private static final String[] items = {"None", "Spring", "Summer", "Fall", "Winter"};
    private static final String[] updatedItems = {"None", "Frühling", "Sommer", "Winter", "Herbst"};
    //The one & only sharedDataModel for both JComboBoxes
    final AveSharedSelectionModel sharedDataModel;

    public SharedDataModelUi() {
        super("SharedDataModel Test");
        super.setSize(800, 400);
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);

        this.sharedDataModel = new AveSharedSelectionModel();
        for (String item : items) {
            sharedDataModel.addElement(item);
        }

        createLayout(super.getContentPane());
        super.setVisible(true);
    }

    private void createLayout(Container container) {

        JTabbedPane tabPane = new JTabbedPane();
        container.add(tabPane);
        createJCombos(tabPane);
        creatTable(tabPane);
    }

    @SuppressWarnings("unchecked")
    private void createJCombos(JTabbedPane tab) {
        JPanel rootJCombos = new JPanel(new BorderLayout());
        tab.add("ComboBox", rootJCombos);

        JPanel comboPanel = new JPanel(new GridLayout(1, NUM_LISTS, 5, 5));
        rootJCombos.add(comboPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, NUM_LISTS, 15, 5));
        rootJCombos.add(buttonsPanel, BorderLayout.SOUTH);

        final int NUM_JCOMBO_BOXES = 2;
        final String[] lastSelectedjComboItem = new String[NUM_JCOMBO_BOXES];

        for (int i = 0; i < NUM_JCOMBO_BOXES; i++) {
            final int index = i;
            AveSharedComboBoxModel<String> seperateSelectableSharedDataModel = new AveSharedComboBoxModel<>(sharedDataModel);
            seperateSelectableSharedDataModel.setAllowEmptySelection(false); // no null or empty selection, always fall back to select 1'st item.
            seperateSelectableSharedDataModel.setMatchSelectionByString(true); // on item updates, match the updatableSelection item by string instead of the index.
            seperateSelectableSharedDataModel.setAllowOneMutation(true); // retain selection state (keep updatableSelection index) if exactly one element mutates (e.g. if it is being "renamed")
            seperateSelectableSharedDataModel.setForceDeselectionOnIndexChange(true); // force a deselection of the item since the index has changed but the updatableSelection item remained constant

            JComboBox<String> jComboBox = new JComboBox<>(seperateSelectableSharedDataModel);
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
            comboPanel.add(new JScrollPane(jComboBox));

            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 5));
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
                            final List sharedDataModelAsList = sharedDataModel.toList();
                            int itemIndex = sharedDataModel.getIndexOf(lastSelectedjComboItem[index]);
                            sharedDataModelAsList.set(itemIndex, rename);
                            sharedDataModel.update(sharedDataModelAsList);
                        }
                    }
                });
            }
            buttonPanel.add(btnAddOrRename);

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
            buttonPanel.add(btnRemoveOrUpdate);

            buttonsPanel.add(buttonPanel);
        }
    }

    private void creatTable(JTabbedPane tab) {
        JPanel rootJCombos = new JPanel(new BorderLayout());
        tab.add("Table", rootJCombos);

        JPanel comboPanel = new JPanel(new GridLayout(1, NUM_LISTS, 5, 5));
        rootJCombos.add(comboPanel, BorderLayout.NORTH);

        String[] columnNames = {"Nummer", "Choice", "Text", "SharedChoice"};
        String[][] stringData = {
            {"0", "null"}, {"1", "eins"}, {"2", "zwei"}, {"3", "drei"}, {"4", "vier"}
        };

        AveSharedSelectionModel choiceData = new AveSharedSelectionModel(new String[]{"None", "A", "B", "C", "D"});

        final AveTableRowEntry[] tableData = new AveTableRowEntry[stringData.length];
        final Class[] sequence = new Class[]{String.class, AveUpdatableSelection.class, String.class, AveUpdatableSelection.class};

        for (int i = 0; i < tableData.length; i++) {
            AveUpdatableSelection comboBoxData1 = new AveUpdatableSelection(choiceData, null, false, true);
            AveUpdatableSelection comboBoxData2 = new AveUpdatableSelection(this.sharedDataModel, null, false, true);
            tableData[i] = new AveTableRowEntry(sequence, stringData[i], new AveUpdatableSelection[]{comboBoxData1, comboBoxData2});
        }

        final AveTableModel tableModel = new AveTableModel(columnNames);
        for (AveTableRowEntry rowData : tableData) {
            tableModel.addRow(rowData);
        }

        final JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setDefaultRenderer(AveUpdatableSelection.class, AveComboBoxCellRenderer.getInstance());
        table.setDefaultEditor(AveUpdatableSelection.class, AveComboBoxCellEditor.getInstance());

        comboPanel.add(table);
    }
}
