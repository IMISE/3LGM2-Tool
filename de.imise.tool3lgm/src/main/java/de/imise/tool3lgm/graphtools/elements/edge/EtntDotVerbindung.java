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
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;

/**
 * @author Thomas
 */
public final class EtntDotVerbindung extends Doppelkante {

//    public static final Class[] stcl = {EreignisDokumentenTyp.class};
    public static final Class<? extends ModelElement> stcl = EreignisDokumentenTyp.class;
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.ONE};
	public static final Class<? extends ModelElement> etcl = Dokumententyp.class;
//	public static final Class[] etcl = {Dokumententyp.class};
	
//	private static Object[][] stcl = {{EreignisDokumentenTyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
//	private static Object[][] etcl = {{Dokumententyp.class, ModelConstants.ZERO, ModelConstants.ONE}}; 

	/**
	 * 
	 */
	public EtntDotVerbindung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public EtntDotVerbindung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
	}

	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public EtntDotVerbindung(
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
