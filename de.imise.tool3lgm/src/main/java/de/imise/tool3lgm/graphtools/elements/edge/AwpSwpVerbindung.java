/*
 * Created on 16.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;

/**
 * @author Thomas
 */
public final class AwpSwpVerbindung extends Doppelkante {


//	public static final Class[] stcl = {Anwendungsprogramm.class};
	public static final Class<? extends ModelElement> stcl = Anwendungsprogramm.class;
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.ONE};
	public static final Class<? extends ModelElement> etcl = Softwareprodukt.class;
//	public static final Class[] etcl = {Softwareprodukt.class};

//	private static Object[][] stcl = {{Anwendungsprogramm.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
//	private static Object[][] etcl = {{Softwareprodukt.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

	/**
	 * 
	 */
	public AwpSwpVerbindung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public AwpSwpVerbindung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
	}

	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public AwpSwpVerbindung(
		ModelElement knot1,
		ModelElement knot2,
		boolean registerInKnots) {
		super(knot1, knot2, registerInKnots);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.LOGICAL_LAYER; 
	}

}
