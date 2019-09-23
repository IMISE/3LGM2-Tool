package de.imise.tool3lgm.gui.menu;

import java.awt.Component;

import javax.swing.JPopupMenu;

/**
 * @author AXS (23.09.2019)
 */
public abstract class ContextGenerator {

    /**
     * COMMENTME
     */
    private boolean controlled = false;

    /**
     *
     */
    public ContextGenerator() {
        setControlled(false);
    }

    /**
     * @param b
     */
    public final void setControlled(final boolean b) {
        controlled = b;
    }

    /**
     * @return
     */
    public final boolean isControlled() {
        return controlled;
    }

    /**
     * @return
     */
    public abstract JPopupMenu getLayerContextMenu();

    /**
     * @param source
     * @return
     */
    public abstract JPopupMenu getNodeContextMenu(final Component source);

}
