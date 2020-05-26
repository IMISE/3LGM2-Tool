package de.imise.tool3lgm.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.Szenario;

/**
 * @author AXS (19.05.2020)
 */
public interface ViewContainer extends GraphDocumentOwner {

    /**
     * @return the name of this view.
     */
    public String getName();

    /**
     * @return the name of this view.
     */
    public String getFullName();

    /**
     * @return
     */
    public Szenario getSzenario();

    /**
     *
     */
    public Component getRealViewComponent();

    /**
     * @return
     */
    public JScrollPane getScrollPane();

}
