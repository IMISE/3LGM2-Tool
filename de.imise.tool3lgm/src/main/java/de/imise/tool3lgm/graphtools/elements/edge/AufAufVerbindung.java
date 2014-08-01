/*
 * Created on 16.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;

/**
 * @author Thomas
 *
 */
public final class AufAufVerbindung extends PartOfBeziehung {

//    public static final Class[] stcl = {Aufgabe.class};  
    public static final Class<? extends ModelElement> stcl = Aufgabe.class;  
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};
	public static final Class<? extends ModelElement> etcl = Aufgabe.class;
//	public static final Class[] etcl = {Aufgabe.class};
	
//	private static Object[][] stcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
//	private static Object[][] etcl = {{Aufgabe.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 


	/**
	 * 
	 */
	public AufAufVerbindung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public AufAufVerbindung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
	}

	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public AufAufVerbindung(
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
		return ModelConstants.DOMAIN_LAYER; 
	}

}
