/*
 * Created on 09.02.2004 To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;

/**
 * @author AXS
 */
public class Kommunikationsprozess extends Knoten {

    protected Prozess prozess;

    /**
     * 
     */
    public Kommunikationsprozess() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.LOGICAL_LAYER;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        return super.createPropertyDialog();
    }

    /**
     * @return
     */
    public Prozess getProzess() {
        return prozess;
    }

    /**
     * @param prozess
     */
    public void setProzess(final Prozess prozess) {
        this.prozess = prozess;
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return true;
    }

}
