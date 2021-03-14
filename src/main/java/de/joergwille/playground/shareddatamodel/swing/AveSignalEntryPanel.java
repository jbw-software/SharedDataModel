package de.joergwille.playground.shareddatamodel.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class AveSignalEntryPanel extends JPanel {

    private final JTable table;

    protected final JTextField signalTextField;
    protected final JButton chooserButton;
    private JLabel label;

    public AveSignalEntryPanel() {
        this(null);
    }

    /**
     * Creates a new instance of {@code AveSignalEntryPanel}
     * which provides a text field and a chooser button
     * which opens a signal tree browser, if triggered.
     *
     * @param table A reference to the table if this {@code AveSignalEntryPanel} is used within a table or null.
     */
    public AveSignalEntryPanel(final JTable table) {
        this.table = table;
        //public AveSignalEntryPanel() {
        this.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Create text field to store the file path.
        if (table == null) {
            signalTextField = new JTextField();
        } else // Do not render a border and make background transparen, if this AveSignalEntryPanel is used within a table.
        {
            signalTextField = new JTextField() {
                @Override
                public void setBorder(Border border) {
                    // No Border.
                }
            };
            signalTextField.setOpaque(false);
        }
        signalTextField.setColumns(25);
        signalTextField.setToolTipText("Insert signal explicitly in SPICE notation or open the signal browser!");
        this.add(signalTextField, gbc);

        // Button to open a signal browser.
        chooserButton = new JButton("...");
        final Dimension buttonDimension = new Dimension(20, 18);
        chooserButton.setPreferredSize(buttonDimension);
        chooserButton.setMinimumSize(buttonDimension);
        chooserButton.setMaximumSize(buttonDimension);
        chooserButton.setToolTipText("Open the signal browser");
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        this.add(chooserButton, gbc);
        super.setOpaque(true);
        this.addListener();
    }

    protected void addListener() {
        this.chooserButton.addActionListener(this::chooserButtonActionPerformed);
    }

    protected void removeListener() {
        this.chooserButton.removeActionListener(this::chooserButtonActionPerformed);
    }

    private void chooserButtonActionPerformed(ActionEvent e) {
        this.browseSignalsAndGetSelection();
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
     * Returns the contents of the text field.
     * Note that signal names can contain '$', e.g. from Simetrix, which need to be escaped.
     *
     * @return String contents of the text field, resulting as chosen from the chooser or custom entry.
     */
    public String getSignal() {
        return signalTextField.getText().replace("$", "\\$");
    }

    /**
     * Sets the columns of the text field.
     *
     * @param columns Number of columns.
     */
    public void setColumns(final int columns) {
        signalTextField.setColumns(columns);
    }

    /**
     * Enables or disables the components of this element, respective its subcomponents.
     *
     * @param enabled If <i>true</i> the components are enabled, otherwise disabled.
     */
    @Override
    public void setEnabled(final boolean enabled) {
        signalTextField.setEnabled(enabled);
        chooserButton.setEnabled(enabled);
    }

    public void setLabelAndListener(final JLabel label, final boolean optional, final String defaultValue) {
        if (optional) {
            this.label = label;
        }
        signalTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(final DocumentEvent evt) {
                doUpdate();
            }

            @Override
            public void removeUpdate(final DocumentEvent evt) {
                doUpdate();
            }

            @Override
            public void changedUpdate(final DocumentEvent evt) {
                doUpdate();
            }

            private void doUpdate() {
                if (optional) {
                    label.setEnabled(true);
                }
            }
        });
    }

    /**
     * Sets the contents for this {@code AveSignalEntryPanel} element,
     * thus the contents of the text field.
     * Note that an escaped '$' is replaced by the pure one.
     *
     * @param paramVal The parameter value to be set.
     */
    public void setContents(final String paramVal) {
        signalTextField.setText(paramVal != null ? paramVal.replace("\\$", "$") : "");
    }

    /**
     * Opens a signal browser to select signals and assemble signal expressions.
     *
     * @param mainPanel According main test panel, needed for reference to signal tree file.
     */
    private void browseSignalsAndGetSelection() {
        final Component component = this.table != null ? this.table : this;
        final Container mainPanelOrNull = null; // = SwingUtilities.getAncestorOfClass(TabbedMainPane.class, component);
        String sourcesDirectory = "", resultsDirectory = "";
        if (mainPanelOrNull != null) {
//            final TabbedMainPane mainPanel = (TabbedMainPane) mainPanelOrNull;
//            sourcesDirectory = mainPanel.getWorkplaceDirectory("sources");
//            resultsDirectory = mainPanel.getWorkplaceDirectory("results");
        }
        final File waveformDatabaseFile = new File(resultsDirectory + "/waveforms");
        final File signalTreeFile = new File(sourcesDirectory + "/signal_tree.xml");
        if (waveformDatabaseFile.canRead()) {
            final String aveExtractSignalTree = System.getenv("AVENUE_ROOT") +
                    "/AVEperl/AVEscripts/aveExtractSignalTree.pl";
            final String command = aveExtractSignalTree + " -o " + signalTreeFile.getPath() + " " +
                    waveformDatabaseFile.getPath();
//            final int exitValue = AveGenericUtil.execShellScript(command);
//            if (exitValue != 0) {
//                AveMsg.error(608, "Extraction of signal tree failed!");
//                return;
//            }
//        } else if (signalTreeFile.canRead()) {
//            AveDialogs.infoDialog(AveDialogs.getParentFrame(component), "Warning!",
//                    "Signal Tree exists but may be not up to date (e.g. due to\n" +
//                    "currently running simulations)! Handle with care!");
//        } else {
//            AveDialogs.infoDialog(AveDialogs.getParentFrame(component), "Warning!",
//                    "Neither waveform database nor previously extracted signal tree available (yet),\n" +
//                    "Signal browsing not possible!");
//            return;
        }

        final String signalContent = this.getSignal();
        // Create Signal Browser.
        final AveSignalBrowser signalBrowser = new AveSignalBrowser(signalContent, signalTreeFile);

        // Allow resizing of dialog.
        signalBrowser.addHierarchyListener((evt) -> {
            final Window window = SwingUtilities.getWindowAncestor(signalBrowser);
            if (window instanceof Dialog) {
                final Dialog dialog = (Dialog) window;
                if (!dialog.isResizable()) {
                    dialog.setResizable(true);
                }
            }
        });
        final int choice = 0;
//        final int choice = JOptionPane.showConfirmDialog(AveDialogs.getParentFrame(component), signalBrowser,
//                "Select Signal and Assemble Signal Expression", JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            final String chosenSignal = signalBrowser.getSignal();
            if (!signalContent.equals(chosenSignal)) {
                this.setContents(chosenSignal);
                if (this.table != null) {
                    this.table.getCellEditor().stopCellEditing();
                }
            }
        }
    }

    /**
     * Provides a signal field browser to be shown within an option panel.
     */

    private final class AveSignalBrowser extends JPanel {

        // For the tree panel of the signalField tree.
//        private final TreePanel signalTree;
        // For the text field to hold the selected signal field or to build the signal field expressions.
//        private AveTextPanel signalField;
        // For a pop-up to clear the signal field field.
//        private AvePopupMenu signalFieldPopup;
        private AveSignalBrowser(final String content, final File signalTreeFile) {
            this.setLayout(new GridBagLayout());
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.BOTH;

            // Set preferred size.
            this.setPreferredSize(new Dimension(350, 500));

//            signalTree = new AveTreePanel(signalTreeFile.getPath(), AveContextType.SIGNAL_TREE_CONTEXT);
//            this.add(signalTree.getComponent(), gbc);
            gbc.weighty = 0;
            gbc.gridy = 1;

//            signalField = new AveTextPanel(content, 1, 28, "Signal/Signal Expression");
            final JTextArea textField = new JTextArea(); //signalField.getTextArea();
            textField.setToolTipText(
                    "Insert mathematical operators to combine multiple selected signals to a signal expression");

            // Add pop-up to clear the field.
//            signalFieldPopup = new AvePopupMenu();
            final JMenuItem clearItem = new JMenuItem("Clear text field");
            clearItem.addActionListener((evt) -> {
//                signalField.clear();
            });
//            signalFieldPopup.addItem(clearItem);

            textField.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(final MouseEvent evt) {
                    if (evt.getButton() == MouseEvent.BUTTON3) {
//                        signalFieldPopup.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
            });
//            signalTree.setTextField(textField);
//            this.add(signalField, gbc);
        }

        /**
         * Returns the signal entry from the signal field.
         *
         * @return The Signal string.
         */
        public String getSignal() {
            return ""; //signalField.getText();
        }
    }

}
