/*
 * Created on 16.01.2004 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.edge;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;

/**
 * @author Thomas
 */
public final class ObjObjVerbindung extends PartOfBeziehung {

    //    public static final Class[] stcl = {Objekttyp.class};
    public static final Class<? extends ModelElement> stcl = Objekttyp.class;
    public static final int[] scard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };

    public static final int[] ecard = {
            ModelConstants.ZERO, ModelConstants.UNLIMITED
    };
    public static final Class<? extends ModelElement> etcl = Objekttyp.class;

    //	public static final Class[] etcl = {Objekttyp.class};

    //	private static Object[][] stcl = {{Objekttyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
    //	private static Object[][] etcl = {{Objekttyp.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

    /**
	 * 
	 */
    public ObjObjVerbindung() {
        super();
    }

    /**
     * @param knot1
     * @param knot2
     */
    public ObjObjVerbindung(final ModelElement knot1, final ModelElement knot2) {
        super(knot1, knot2);
    }

    /**
     * @param knot1
     * @param knot2
     * @param registerInKnots
     */
    public ObjObjVerbindung(final ModelElement knot1, final ModelElement knot2, final boolean registerInKnots) {
        super(knot1, knot2, registerInKnots);
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

}
