package de.joergwille.playground.swing.resizedemo;

import java.awt.BorderLayout;


@SuppressWarnings("serial")
public class ResizeableBorderLayout extends BorderLayout {

    private final boolean[] northSouthWestEast = new boolean[4];
    
    public ResizeableBorderLayout() {
        this(BorderLayout.CENTER);
    }
    
    public ResizeableBorderLayout(final String dragableRegion) {
        this(new String[] {dragableRegion});
    }
    
    public ResizeableBorderLayout(final String[] dragableRegions) {
        final int maxRegionsLength = Math.min(northSouthWestEast.length, dragableRegions.length);
        for (int i = 0; i < maxRegionsLength; i++) {
            switch (dragableRegions[i]) {
                case BorderLayout.NORTH :
                    this.northSouthWestEast[0] = true;
                    break;
                case BorderLayout.SOUTH :
                    this.northSouthWestEast[1] = true;
                    break;
                case BorderLayout.WEST :
                    this.northSouthWestEast[2] = true;
                    break;
                case BorderLayout.EAST :
                    this.northSouthWestEast[3] = true;
                    break;
                case BorderLayout.CENTER :
                    this.northSouthWestEast[0] = true;
                    this.northSouthWestEast[1] = true;
                    this.northSouthWestEast[2] = true;
                    this.northSouthWestEast[3] = true;
                    break;
            }
        }
    }
    
}
