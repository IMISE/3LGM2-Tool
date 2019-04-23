package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author imi0wendt (01.11.2004)
 */
public final class Bendpoint extends Node {

    /**
     * ID der Kante, zu der der Knickpunkt gehört
     */
    private String edgeHash = "";

    /**
     * {@link EdgeContainer}, zu der der Knickpunkt gehört
     */
    private EdgeContainer kc = null;

    /**
     * Index des Knickpunktes auf dem {@link EdgeContainer}
     */
    private int index = 0;

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        return new BendpointContainer(this, doc);
    }

    /**
     * @return
     */
    public String getKantenHash() {
        return edgeHash;
    }

    /**
     * @param egdeHash
     */
    public void setKantenHash(final String egdeHash) {
        edgeHash = egdeHash == null ? "" : egdeHash;
    }

    @Override
    public boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("kantenHash")) {
            setKantenHash(value);
            return true;
        }
        if (field.equals("index")) {
            index = Integer.parseInt(value);
            return true;
        }

        return super.putXMLFieldString(field, value);
    }

    /**
     * @return
     */
    public EdgeContainer getOwner() {
        return kc;
    }

    /**
     * @param kc
     */
    public void setOwner(final EdgeContainer kc) {
        this.kc = kc;
        edgeHash = kc.getHashString();
    }

    /**
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return GraphDocument, in dem sich der Knickpunkt befindet.
     */
    public final GraphDocument getGraphDocument() {
        return kc.getGraphDocument();
    }

    /**
     * @return Den Container in dem Szenario, in dem der Knickpunkt dargestellt wird
     */
    public final BendpointContainer getBendpointContainer() {
        return (BendpointContainer) getContainer(getGraphDocument());
    }

    @Override
    public final int layerFor() {
        return kc.layerFor();
    }

    @Override
    protected final int getMaxContainerCount() {
        return 2;
    }

}
