package de.imise.tool3lgm.graphtools.elements;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class Knoten extends ModelElement {

	/**
	 * 
	 */
	public Knoten() {
		super();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#clone()
	 */
	@Override
	public Object clone() {
		Knoten retVal;
		try {
			retVal = (Knoten) super.clone();
		} catch (Exception e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
			return null;
		}
		return retVal;
	}

	/**
	 * @param classesToShow
	 * @return
	 */
	public String getLabel(String[] classesToShow) {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#toXMLString()
	 */
	@Override
	public final String toXMLString() {
		return super.toXMLString();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ElementContainer createContainer(GraphDocument doc) {
		if (ModelConstants.isInterLayerStartClass(getClass()))
			return new InterLayerConnectedNodeContainer(this, doc);
		return new NodeContainer(this, doc);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getXMLEntities()
	 */
	@Override
	protected StringBuilder getXMLEntities() {
		return super.getXMLEntities();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#putXMLFieldString(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean putXMLFieldString(String field, String value) {
		return super.putXMLFieldString(field, value);
	}

	/**
	 * Gibt eine Liste der Container zurueck, die für diesen Knoten redundant sein koennen. 
	 * (Beispielsweise in Aufgabe und Objekttyp ueberschrieben)
	 * 
	 * @param doc
	 * @return
	 */
	public ArrayList<ElementContainer> getRedundanceTypes(GraphDocument doc) {
		return null;
	}

	/**
	 * Diese Funktion ist bis jetzt nur in Aufgabe überschrieben und sollte den String zurückliefern,
	 * der ueber dem Layer angezeigt wird, wenn die Redundanzinformationen gewünscht werden.
	 * 
	 * @param redundance
	 * @param saturation
	 * @return
	 */
	public String getRedundanceString(float redundance, float saturation) {
		return "";
	}

	/**
	 * Gibt einen {@link ActionIdentifier} wieder, der diesen {@link Knoten} identifiziert.
	 * 
	 * @return
	 */
	public ActionIdentifier getIdentifier() {
		return null;
	}
}
