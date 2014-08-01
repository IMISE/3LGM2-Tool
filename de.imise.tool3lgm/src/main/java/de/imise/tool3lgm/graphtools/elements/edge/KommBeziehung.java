/*
 * Created on 12.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.elements.edge;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ETNTPanel2;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;

/**
 * @author thomas
 */
public final class KommBeziehung extends Doppelkante {

    /**
     * COMMENTME
     */
    @SuppressWarnings("rawtypes")
	public static final Class[] COPY_DEPENDENCY = {
		EreignisNachrichtenTyp.class,
		EreignisDokumentenTyp.class,
	};
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getCopyDependencies()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ModelElement>[] getCopyDependencies() {
		return COPY_DEPENDENCY;
	}
	
//	public static final Class[] stcl = {Bausteinschnittstelle.class};
	public static final Class<? extends ModelElement> stcl = Bausteinschnittstelle.class;
	public static final int[] scard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};

	public static final int[] ecard = {ModelConstants.ZERO, ModelConstants.UNLIMITED};
	public static final Class<? extends ModelElement> etcl = Bausteinschnittstelle.class;
//	public static final Class[] etcl = {Bausteinschnittstelle.class};
	
//	private static Object[][] stcl = {{Bausteinschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 
//	private static Object[][] etcl = {{Bausteinschnittstelle.class, ModelConstants.ZERO, ModelConstants.UNLIMITED}}; 

	/**
	 * 
	 */
	public KommBeziehung() {
		super();
	}

	/**
	 * @param knot1
	 * @param knot2
	 */
	public KommBeziehung(ModelElement knot1, ModelElement knot2) {
		super(knot1, knot2);
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
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("etntueb"),new ETNTPanel2(EtntEtdtKombination.class,dialog));
		return dialog;
	}

}
