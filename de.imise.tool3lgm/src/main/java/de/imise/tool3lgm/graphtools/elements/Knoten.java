package de.imise.tool3lgm.graphtools.elements;

import java.util.List;

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
    public String getLabel(final String[] classesToShow) {
        return getName();
    }

    @Override
    public final String toXMLString() {
        return super.toXMLString();
    }

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        if (ModelConstants.isInterLayerStartClass(getClass())) {
            return new InterLayerConnectedNodeContainer(this, doc);
        }
        return new NodeContainer(this, doc);
    }

    @Override
    protected StringBuilder getXMLEntities() {
        return super.getXMLEntities();
    }

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        return super.putXMLFieldString(field, value);
    }

    /**
     * Gibt eine Liste der Container zurueck, die für diesen Knoten redundant sein koennen. (Beispielsweise in Aufgabe und Objekttyp ueberschrieben)
     *
     * @param doc
     * @return
     */
    public List<ElementContainer> getRedundanceTypes(final GraphDocument doc) {
        return null;
    }

    /**
     * Diese Funktion ist bis jetzt nur in Aufgabe überschrieben und sollte den String zurückliefern, der ueber dem Layer angezeigt wird, wenn die
     * Redundanzinformationen gewünscht werden.
     *
     * @param redundance
     * @param saturation
     * @return
     */
    public String getRedundanceString(final float redundance, final float saturation) {
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
