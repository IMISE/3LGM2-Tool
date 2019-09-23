package de.imise.tool3lgm.gui.menu;

import java.awt.Component;

import javax.swing.JPopupMenu;

import de.imise.util.Sys;

/**
 * @author AXS (23.09.2019)
 */
public class TemplateContextGenerator extends ContextGenerator {

    /**
     *
     */
    public TemplateContextGenerator() {
        init();
    }

    private void init() {

    }

    @Override
    public JPopupMenu getLayerContextMenu() {
        return null;
    }

    @Override
    public JPopupMenu getNodeContextMenu(final Component source) {
        Sys.err1(source);
        return null;
    }

}
