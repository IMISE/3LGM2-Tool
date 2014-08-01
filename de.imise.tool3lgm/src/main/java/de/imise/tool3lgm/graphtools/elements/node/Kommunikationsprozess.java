/*
 * Created on 09.02.2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
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

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.LOGICAL_LAYER; 
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		return super.createPropertyDialog(gdcoll);
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
	public void setProzess(Prozess prozess) {
		this.prozess = prozess;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public boolean hasSortedKanten() {
		return true;
	}

}
