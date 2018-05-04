package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

public class GDCollectionPrinter {

    private final GDCollection gdcoll;

    private final StringBuilder sb = new StringBuilder();

    private int indent = 0;

    public GDCollectionPrinter(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
        appendMainModelInfromation();
    }

    private StringBuilder appendMainModelInfromation() {
        appendln("Model name=", gdcoll.getName());
        increaseIndent();
        appendln(gdcoll.getMainGraphDocument());
        for (Szenario szen : gdcoll.getSzenarios()) {
            appendln(szen);
        }
        resetIndent();
        appendln();
        return sb;
    }

    private StringBuilder appendGraphDocument(final GraphDocument doc) {
        appendln(doc.getClass().getSimpleName()).append(": ").append(doc);
        for (int layerIndex : ModelConstants.LAYERS) {
            LayerContainer lc = doc.getLayer(layerIndex);
            appendln("Layer ", layerIndex, " Knoten: ", lc.getNodeContainerCount(), " Kanten: ", lc.getEdgeContainerCount(), " Knickpunkte: ", lc.getBendpointContainerCount());
            appendln("Knoten");
            increaseIndent();
            for (NodeContainer nc : lc.getNodeContainer()) {
                appendElementContainer(nc);
            }
            decreaseIndent();
            appendln("Kanten");
            increaseIndent();
            for (EdgeContainer ec : lc.getEdgeContainer()) {
                appendElementContainer(ec);
            }
            decreaseIndent();

        }
        resetIndent();
        return sb;
    }

    private StringBuilder appendNodeContainer(final NodeContainer nc) {
        ModelElement me = nc.getElement();
        appendln(me.getClass().getSimpleName(), ": ", me, " hash=", me.getHashString());
        return sb;
    }

    private StringBuilder appendElementContainer(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        appendln(me.getClass().getSimpleName(), ": ", me);
        return sb;
    }

    private StringBuilder appendln() {
        sb.append("\n");
        return sb;
    }

    private StringBuilder append(final Object... objects) {
        for (Object o : objects) {
            sb.append(o);
        }
        return sb;
    }

    private StringBuilder appendln(final Object... objects) {
        append(objects);
        appendln();
        return sb;
    }

    private StringBuilder appendIndent() {
        for (int i = 0; i < indent; i++) {
            append("\t");
        }
        return sb;
    }

    private int increaseIndent() {
        indent++;
        return indent;
    }

    private int decreaseIndent() {
        indent--;
        return indent;
    }

    private int resetIndent() {
        indent = 0;
        return indent;
    }

}
