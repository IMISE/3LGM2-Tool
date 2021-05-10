package de.imise.util.swing.component;

/**
 * All components which should not take more than the full prants pan withd
 * shoul implement the commented functions and zis interface. If they do they
 * will resize correct without horizontal scrolling.
 *
 * @author AXS (06.05.2021)
 */
public interface MinWidthComponent {

    /**
     * @return
     */
    public default int getMinWidth() {
        return 1;
    }

    // Components which implement this interface should implemnt the following functions
    //__________________________________________________________________________________

    //    @Override
    //    public Dimension getMaximumSize() {
    //        Dimension size = super.getMaximumSize();
    //        size.width = getMinWidth();
    //        return size;
    //    }
    //
    //    @Override
    //    public Dimension getPreferredSize() {
    //        Dimension size = super.getPreferredSize();
    //        size.width = getMinWidth();
    //        return size;
    //    }
    //
    //    @Override
    //    public Dimension getPreferredScrollableViewportSize() {
    //        Dimension size = super.getPreferredScrollableViewportSize();
    //        size.width = getMinWidth();
    //        return size;
    //    }

}
