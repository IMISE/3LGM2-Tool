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
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.Repraesentationsform;

/**
 * @author Thomas
 */
public final class ObjReprVerbindung extends Doppelkante {

//	public static final Class[] stcl = {Objekttyp.class};
	public static final Class<? extends ModelElement> stcl = Objekttyp.class;
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};
	public static final Class<? extends ModelElement> etcl = Repraesentationsform.class;
//	public static final Class[] etcl = {Datensatztyp.class, Dokumententyp.class, Nachrichtentyp.class};
	
/*	private static Object[][] stcl = {{Objekttyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
	private static Object[][] etcl = {{Datensatztyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}, 
									{Dokumententyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
									{Nachrichtentyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
*/
	/**
	 * 
	 */
	public ObjReprVerbindung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public ObjReprVerbindung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
	}

	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public ObjReprVerbindung(
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
		return ModelConstants.INTER_DOMAIN_LOGICAL_LAYER; 
	}

}
