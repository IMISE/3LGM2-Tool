package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author N.N.
 * @create Long time ago
 */
public class AufOrgKombination extends Knoten {
	
    /**
     * COMMENTME
     */
    @SuppressWarnings({ "rawtypes" })
    public static final Class[] COPY_DEPENDENCY = {
			ABKonfiguration.class,
			Organisationseinheit.class,
	};

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getCopyDependencies()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ModelElement>[] getCopyDependencies() {
		return COPY_DEPENDENCY;
	}

	/**
	 * 
	 */
	public AufOrgKombination() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#layerFor()
	 */
	@Override
	public int layerFor() {
		return ModelConstants.DOMAIN_LAYER;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#toString()
	 */
	@Override
	public String toString() {
		ArrayList<ModelElement> oe = getConnectedElements(Organisationseinheit.class);
		StringBuilder retVal = new StringBuilder(Tool3lgmConstants.getResString("in_oes"));
		retVal.append(": ");
		boolean first = true;
		for (int i = 0; i < oe.size(); i++) {
			if (!first) {
				retVal.append(", ");
			} else {
				first = false;
			}
			retVal.append(oe.get(i));
		}
		return retVal.toString();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		ElementPropertyDialog dialog = new ElementPropertyDialog(this, gdcoll);
		dialog.addTab(getResString("Organisationseinheit"), new NConnectionPanel(Organisationseinheit.class, dialog, true, true));
		dialog.addTab(getResString("Aufgabe"), new NConnectionPanel(Aufgabe.class, dialog, true, true));
		return dialog;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {return false;}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public boolean hasSortedKanten() {return false;}

}
