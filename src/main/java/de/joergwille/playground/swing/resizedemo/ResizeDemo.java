package de.joergwille.playground.swing.resizedemo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ResizeDemo extends JFrame {

    public ResizeDemo() {

        initUI();
    }

    private void initUI() {

        var root = new JPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();

        var resPanLabel = new JLabel("Resizable Panel");
        ResizeDemo.updateGbc(gbc, 0, 0);
        root.add(resPanLabel, gbc);

//        final var panel = new JPanel();
//        panel.setBackground(Color.DARK_GRAY);
        final var resPan = new Resizable(new ScalablePanel());
//        resPan.setBounds(50, 50, 200, 150);
        ResizeDemo.updateGbc(gbc, 1, 0);
        root.add(resPan, gbc);

        var scaPanLabel = new JLabel("BorderLayout Panel");
        ResizeDemo.updateGbc(gbc, 0, 1);
        root.add(scaPanLabel, gbc);

        var scaPan = new ScalablePanel();
        ResizeDemo.updateGbc(gbc, 1, 1);
        root.add(scaPan, gbc);

        var sp = new JScrollPane(root);
        add(sp);

        root.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {

                requestFocus();
                resPan.repaint();
            }
        });

        setSize(550, 400);
        setTitle("ResizeDemo - click center with left mouse button to scale up and with right mouse button to scale down -");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new ResizeDemo();
            ex.setVisible(true);
        });
    }

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    private static void updateGbc(final GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.WEST;
        gbc.fill = (x == 0) ? GridBagConstraints.NONE : GridBagConstraints.NONE;

        gbc.insets = (x == 0) ? WEST_INSETS : WEST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
    }

}
