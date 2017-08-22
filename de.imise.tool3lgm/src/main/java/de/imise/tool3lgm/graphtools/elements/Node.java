package de.imise.tool3lgm.graphtools.elements;

import java.util.List;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author N.N.
 * @create Long time ago
 */
public abstract class Node extends ModelElement {

    /**
     *
     */
    public Node() {
        super();
    }

    /**
     * @param classesToShow
     * @return
     */
    public String getLabel(final String[] classesToShow) {
        return getName();
    }

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        if (ModelConstants.isInterLayerStartClass(getClass())) {
            return new InterLayerConnectedNodeContainer(this, doc);
        }
        return new NodeContainer(this, doc);
    }

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        return super.putXMLFieldString(field, value);
    }

    /**
     * Gibt eine Liste der Container zurueck, die für diesen Node redundant sein koennen. (Beispielsweise in Aufgabe und Objekttyp ueberschrieben)
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

}
