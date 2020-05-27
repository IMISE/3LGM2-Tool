package de.imise.tool3lgm.gui.viewpane;

/**
 * @author AXS (26.05.2020)
 */
public interface ViewPaneFrameComponentListener {

    public void viewClosing(ViewPaneFrameComponent source);

    public void viewClosed(ViewPaneFrameComponent source);

    public void viewActivated(ViewPaneFrameComponent source);

    public void viewDeactivated(ViewPaneFrameComponent source);

}
