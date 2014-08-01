package de.imise.tool3lgm.graphtools.elements.node;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;

public final class Benutzungsschnittstelle extends Schnittstelle {
	
	public Benutzungsschnittstelle(){
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createPropertyDialog(tool3lgm.graphtools.GDCollection)
	 */
	@Override
	public ElementPropertyDialog createPropertyDialog(GDCollection gdcoll) {
		return super.createPropertyDialog(gdcoll);
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
		return false;
	}

/*	public static final Class[] MASTER_TYPES = {
		Anwendungsbaustein.class,
		RechAnwendungsbaustein.class,
		KonAnwendungsbaustein.class,
	};
	
	public final Class[] getMasterTypes()  {
		return MASTER_TYPES;
	}
*/
}
