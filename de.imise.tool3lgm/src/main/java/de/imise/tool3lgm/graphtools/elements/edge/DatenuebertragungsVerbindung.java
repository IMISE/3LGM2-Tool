package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;

public final class DatenuebertragungsVerbindung extends Doppelkante {
	
//    public static final Class[] stcl = {PhysischerDVBaustein.class};
    public static final Class<? extends ModelElement> stcl = PhysischerDVBaustein.class;
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};
	public static final Class<? extends ModelElement> etcl = PhysischerDVBaustein.class;
//	public static final Class[] etcl = {PhysischerDVBaustein.class};
	
//	private static Object[][] stcl = {{PhysischerDVBaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
//	private static Object[][] etcl = {{PhysischerDVBaustein.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

	public DatenuebertragungsVerbindung() {
		super();
		setDirection(DOUBLE);
	}

	public DatenuebertragungsVerbindung(Knoten k1, Knoten k2) {
		super(k1,k2);
		setDirection(DOUBLE);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.PHYSICAL_LAYER; 
	}

}