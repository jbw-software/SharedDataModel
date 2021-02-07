package de.joergwille.playground.swing.resizedemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ScalablePanel extends JPanel {

    private final MouseAdapter scalePanelOnMousePressedAdapter;
    private final JPanel cPanel;
    
    public ScalablePanel() {
        super(new BorderLayout());
        this.scalePanelOnMousePressedAdapter = new ScalePanelOnMousePressedAdapter(this);
        
        this.cPanel = new JPanel();
        this.initUI();
        this.addListener();
    }

    private void initUI() {
        final JPanel psPanel = new MouseEventsTranparentPanel();
        psPanel.setPreferredSize(new Dimension(1, 30));
        psPanel.setBackground(Color.RED);
        this.add(psPanel, BorderLayout.PAGE_START);
        final JPanel lsPanel = new MouseEventsTranparentPanel();
        lsPanel.setPreferredSize(new Dimension(50, 1));
        lsPanel.setBackground(Color.YELLOW);
        this.add(lsPanel, BorderLayout.LINE_START);
        cPanel.setPreferredSize(new Dimension(300, 60));
        cPanel.setBackground(Color.GREEN);
        this.add(cPanel, BorderLayout.CENTER);
        final JPanel lePanel = new MouseEventsTranparentPanel();
        lePanel.setPreferredSize(new Dimension(50, 1));
        lePanel.setBackground(Color.ORANGE);
        this.add(lePanel, BorderLayout.LINE_END);
        final JPanel pePanel = new MouseEventsTranparentPanel();
        pePanel.setPreferredSize(new Dimension(1, 30));
        pePanel.setBackground(Color.BLUE);
        this.add(pePanel, BorderLayout.PAGE_END);
    }

    private void addListener() {
        cPanel.addMouseListener(this.scalePanelOnMousePressedAdapter);
    }

    private void removeListener() {
        cPanel.removeMouseListener(this.scalePanelOnMousePressedAdapter);
    }

    @Override
    public void removeNotify() {
        this.removeListener();
    }

    private void resize() {
        
        var currentBounds = this.getBounds();
        var currentDimension = new Dimension(currentBounds.width, currentBounds.height);
        setPreferredSize(currentDimension);
        System.out.println("ScalablePanel currentBounds w=" + currentBounds.width + ", h= " + currentBounds.height);
        if (getParent() != null) {
            getParent().setPreferredSize(null); // reset the parent's prefferedSize to have the parent repaint the child first.
            getParent().revalidate();
        }
    }

    private static class ScalePanelOnMousePressedAdapter extends MouseAdapter {

        private static final float DEFAULT_FACTOR = 1.5f;
        private final ScalablePanel panel;
        private float factor;

        public ScalePanelOnMousePressedAdapter(final ScalablePanel panel) {
            this(panel, DEFAULT_FACTOR);
        }

        public ScalePanelOnMousePressedAdapter(final ScalablePanel panel, float factor) {
            this.panel = panel;
            this.factor = factor;
        }

        private void scale(boolean isUp) {
            float upDownFactor = isUp ? this.factor : (1 / this.factor);
            int w = Math.round(panel.getWidth() * upDownFactor);
            int h = Math.round(panel.getHeight() * upDownFactor);
            this.panel.setBounds(panel.getX(), panel.getY(), w, h);
        }

        @Override
        public void mousePressed(MouseEvent e) {

            scale(e.getButton() == MouseEvent.BUTTON1);
            panel.resize();
        }
    }
    
    private static class MouseEventsTranparentPanel extends JPanel {
    
    @Override
    protected void processMouseEvent(MouseEvent e) {
//        if (e.getID() != MouseEvent.MOUSE_PRESSED) {
            if (getParent() != null) {
                getParent().dispatchEvent(e);
            }
//        } else {
//            super.processMouseEvent(e);
//        }
    }        
    }
}
