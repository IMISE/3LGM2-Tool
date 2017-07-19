/*
 * Created on 20.01.2004 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements;

/**
 * @author thomas
 */
public class LayerKnoten extends Knoten {

    /**
     * 
     */
    public LayerKnoten() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.NO_LAYER;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }

}
