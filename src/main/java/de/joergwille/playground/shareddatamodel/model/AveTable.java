/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.model;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author joerg 
 * JTable is missing VisibleRowCount and therefore PreferredScrollableViewportSize is not working as expected.
 * See https://stackoverflow.com/questions/9821425/how-to-set-jscrollpane-to-show-only-a-sepecific-amount-of-rows
 * Using org.jdesktop.swingx.JXTable would be good alternative.
 * For now using workaround suggested here: https://bugs.openjdk.java.net/browse/JDK-4901352
 */
public class AveTable extends JTable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_VISIBLE_ROW_COUNT = 1;
    private int visibleRowCount; 
    private int viewportHeightMargin; 

    public AveTable(final TableModel tableModel) {
        this(tableModel, Math.max(DEFAULT_VISIBLE_ROW_COUNT, tableModel.getRowCount()));
    }
    
    public AveTable(final TableModel tableModel, int visibleRowCount) {
        this(tableModel, visibleRowCount, 0);
    }

    public AveTable(final TableModel tableModel, int visibleRowCount, int viewportHeightMargin) {
        super(tableModel);
        this.visibleRowCount = visibleRowCount >= 0 ? visibleRowCount : tableModel.getRowCount();
        this.viewportHeightMargin = viewportHeightMargin;
        super.setPreferredScrollableViewportSize(null);
        
        // table will not fill ScrollPane's ViewPort width. See table.getScrollableTracksViewportWidth()
        super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // table will fill ScrollPane's ViewPort height.
        super.setFillsViewportHeight(false);
    }

    
    /**
     * Sets the preferred number of rows in the table that can be displayed
     * without a scrollbar, as determined by the nearest <code>JViewport</code>
     * ancestor, if any. The value of this property only affects the return
     * value of the <code>JTable</code>'s
     * <code>getPreferredScrollableViewportSize</code>.
     * <p>
     * The default value of this property is 8.
     * <p>
     * @param visibleRowCount an integer specifying the preferred number of
     * visible rows
     */
    public void setVisibleRowCount(int visibleRowCount) {
        int oldValue = this.visibleRowCount;
        this.visibleRowCount = Math.max(0, visibleRowCount);
        firePropertyChange("visibleRowCount", oldValue, visibleRowCount);
    }

    /**
     * Returns the preferred number of visible rows.
     *
     * @return an integer indicating the preferred number of rows to display
     * without using a scroll bar
     * @see #setVisibleRowCount
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public int getViewportHeightMargin() {
        return viewportHeightMargin;
    }

    public void setViewportHeightMargin(int viewportHeightMargin) {
        this.viewportHeightMargin = viewportHeightMargin;
    }
    
    /**
     * Computes the size of the viewport needed to display
     * <code>visibleRowCount</code> number of rows and accomodate all columns
     * rows.
     *
     * @return a dimension containing the size of the viewport needed to display
     * <code>visibleRowCount</code> rows and all columns
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        
        // just in case someone has set it manually
        if (super.getPreferredScrollableViewportSize() != null) {
            return super.getPreferredScrollableViewportSize();
        }

        Dimension preferredSize = super.getPreferredSize();
        final Insets insets = getInsets();
        int insetsAndMargin = insets.top + insets.bottom + this.viewportHeightMargin;
        preferredSize.height = this.getVisibleRowCount() * super.getRowHeight() + insetsAndMargin;
        return preferredSize;
    }
}
