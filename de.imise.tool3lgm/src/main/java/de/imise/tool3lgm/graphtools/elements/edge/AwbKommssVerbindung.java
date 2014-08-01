/*
 * Created on 16.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Schnittstelle;

/**
 * @author Thomas
 */
public final class AwbKommssVerbindung extends Composition {

//	public static final Class[] stcl = {Anwendungsbaustein.class, RechAnwendungsbaustein.class, KonAnwendungsbaustein.class};
	public static final Class<? extends ModelElement> stcl = Anwendungsbaustein.class;
//	public static final int[] scard = {ModelConstants.ONE, ModelConstants.ONE};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};
	public static final Class<? extends ModelElement> etcl = Schnittstelle.class;
//	public static final Class[] etcl = {Bausteinschnittstelle.class, Benutzungsschnittstelle.class};
	
/*	
	private static Object[][] stcl = {{Anwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE},
									{KonAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE},
									{RechAnwendungsbaustein.class, ModelConstants.ONE, ModelConstants.ONE}}; 
	private static Object[][] etcl = {{Bausteinschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED},
									{Benutzungsschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
*/
	/**
	 * 
	 */
	public AwbKommssVerbindung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public AwbKommssVerbindung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
	}

	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public AwbKommssVerbindung(
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

//	public boolean isMasterSlave() { return true; }
	
}
