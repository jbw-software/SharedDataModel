package de.joergwille.playground.shareddatamodel;

import de.joergwille.playground.shareddatamodel.swing.AveTablePanel;
import de.joergwille.playground.shareddatamodel.swing.model.AveChoiceElement;
import de.joergwille.playground.shareddatamodel.swing.model.AveSharedComboBoxModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveSharedDataModel;
import de.joergwille.playground.shareddatamodel.swing.model.AveTableRowEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author joerg.wille@gmail.com
 */
public class SharedDataModelUi extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String[] items = {"None1", "Spring", "Summer", "Fall", "Winter"};
    private static final String[] updatedItems = {"None2", "Frühling", "Sommer", "Winter", "Herbst", "EinGanzLangerStringMitSehrVielenBuchstaben"};

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

    private void createTable(final JTabbedPane tabPane) {
        String[] columnNames = {"Number", "Choice", "Text", "SharedChoice", "Checkbox"};
        final String[][] stringData = {
            {"0", "null"}, {"1", "eins"}, {"2", "zwei"}, {"3", "drei"}, {"4", "vier"}
        };

        // create a SharedDataModel with AutoSetPrototypeDisplayValue enabled.
        final AveSharedDataModel<String> choiceData = new AveSharedDataModel<>(new String[]{"None3", "A", "B", "C", "D"}, true);

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

//        for (int i = 0; i < numberOfRows; i++) {
//            String[] stringDataForRow = stringData[i];
//            // defaultValues is optional, but if set, then it must be same length as number of coloumns.
//            // if there are no defaultValues for ComboBoxes use 'null' for 'choice' coloumns.
//            String[] defaultValues = new String[]{stringDataForRow[0], choiceData.getElementAt(i), stringDataForRow[1], this.sharedDataModel.getElementAt(i), (i % 2 == 0) ? "true" : "false"};
//            tableData[i] = new AveTableRowEntry(columnTypes, choiceModels, defaultValues);
//        }
//      END OF TABEL DATA INITIALIZATION         

//      TABLE MODEL INITIALIZATION
//        final AveTableModel tableModel = new AveTableModel(columnNames);
//        for (AveTableRowEntry rowData : tableData) {
//            tableModel.addRow(rowData);
//        }

//      UI INITIALIZATION
        final JPanel rootJTables = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        final String[] tableNames = new String[]{"CompactTablePane", "FilledWidthTablePane", "VectorPane"};

        for (int i = 0; i < 1; i++) { // tableNames.length
            final JLabel label = new JLabel(tableNames[i]);
            SharedDataModelUi.updateGbc(gbc, 0, i);
            rootJTables.add(label, gbc);
  
            final AveTablePanel panel;
            switch (i) {
                case 0:
                    panel = new AveGenericTablePanel(columnNames, columnTypes, choiceModels, null);
                    break;
                case 1:
                    panel = new AveGenericTablePanel(columnNames, columnTypes, choiceModels, null);
                    break;
                case 2:
                    panel = new AveVectorPanel(columnNames, columnTypes, choiceModels, null);
                    break;
                default:
                    throw new IllegalArgumentException(); 
            }
            SharedDataModelUi.updateGbc(gbc, 1, i);
            panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
            rootJTables.add(panel, gbc);
            
            panel.setRowHeight(40);
        }

//        final JPanel rootJTables = new JPanel(null);
//        rootJTables.setLayout(new BoxLayout(rootJTables, BoxLayout.Y_AXIS));
//
//        final AveGenericTablePanel[] aveGenericTablePanel = new AveGenericTablePanel[1];
//        final JButton toggleCompactModeButton = new JButton("Create CompactMode");
//        rootJTables.add(toggleCompactModeButton);
//        toggleCompactModeButton.addActionListener(a -> {
//            LayoutMode layoutMode = DEFAULT_LAYOUT_MODE;
//            if (aveGenericTablePanel[0] != null) {
//                layoutMode = aveGenericTablePanel[0].getLayoutMode();
//                layoutMode = (layoutMode.ordinal() == 0) ? LayoutMode.LAST_COLUMN_FILL_WIDTH : LayoutMode.COMPACT;
//                rootJTables.remove(aveGenericTablePanel[0]);
//            }
//            aveGenericTablePanel[0] = new AveGenericTablePanel(layoutMode, tableModel, columnTypes, choiceModels);
//            rootJTables.add(aveGenericTablePanel[0]);
//
//            String toggledlayoutModeText = (layoutMode.ordinal() == 0) ? "Switch to LastColumnFillMode" : "Switch to CompactMode";
//            toggleCompactModeButton.setText(toggledlayoutModeText);
//            rootJTables.validate();
//        });

        final JScrollPane scrollPane = new JScrollPane(rootJTables);


        tabPane.add("Table", scrollPane);
    }

//    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
//    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    private static void updateGbc(final GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.WEST;
        gbc.fill = (x == 0) ? GridBagConstraints.NONE
                : GridBagConstraints.NONE;

//        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
    }
}
