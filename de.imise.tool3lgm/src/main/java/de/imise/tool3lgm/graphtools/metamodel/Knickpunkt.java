/*
 * Created on 01.11.2004
 */
package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author imi0wendt
 */
public class Knickpunkt extends Node {

    /**
     * COMMENTME
     */
    private String kantenHash = "";

    /**
     * COMMENTME
     */
    private EdgeContainer kc = null;

    /**
     * COMMENTME
     */
    private int index = 0;

    /**
     *
     */
    public Knickpunkt() {
        super();
    }

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        return new BendpointContainer(this, doc);
    }

    /**
     * @return
     */
    public String getKantenHash() {
        return kantenHash;
    }

    /**
     * @param kantenHash
     */
    public void setKantenHash(final String kantenHash) {
        this.kantenHash = kantenHash == null ? "" : kantenHash;
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
        kantenHash = kc.getHashString();
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
    public int layerFor() {
        return kc.layerFor();
    }

}
