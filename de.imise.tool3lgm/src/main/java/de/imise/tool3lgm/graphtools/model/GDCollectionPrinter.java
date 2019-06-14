package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.Sys;

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
        appendGraphDocument(gdcoll.getMainGraphDocument());
        for (Szenario szen : gdcoll.getSzenarios()) {
            appendGraphDocument(szen);
        }
        resetIndent();
        appendln();
        return sb;
    }

    private StringBuilder appendGraphDocument(final GraphDocument doc) {
        append(doc.getClass().getSimpleName()).append(": ").append(doc);
        appendln();
        for (int layerIndex : ModelConstants.LAYERS) {
            LayerContainer lc = doc.getLayer(layerIndex);
            appendln("Layer ", layerIndex, " Knoten: ", lc.getNodeContainerCount(), " Kanten: ", lc.getEdgeContainerCount(), " Knickpunkte: ", lc.getBendpointContainerCount());
            increaseIndent();
            appendln("Knoten");
            increaseIndent();
            for (NodeContainer nc : lc.getNodeContainersAlphabetical()) {
                appendElementContainer(nc);
            }
            decreaseIndent();
            appendln("Kanten");
            increaseIndent();
            for (EdgeContainer ec : lc.getEdgeContainers()) {
                appendElementContainer(ec);
            }
            decreaseIndent();
            decreaseIndent();
        }
        resetIndent();
        return sb;
    }

    private StringBuilder appendElementContainer(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        appendln(me.getClass().getSimpleName(), ": ", me);
        increaseIndent();
        appendIndent();
        append("hash=", me.getHashString(), "    idMe=", System.identityHashCode(me), "\tidCont=", System.identityHashCode(ec));
        if (me instanceof Edge) {
            Edge edge = (Edge) me;
            ModelElement start = edge.getStart();
            String startHash = start == null ? "null" : start.getHashString();
            ModelElement end = edge.getEnd();
            String endHash = end == null ? "null" : end.getHashString();
            append("\tstartHash=", startHash, "\tendHash=", endHash);
        }
        appendln();
        appendEdgesOfElement(me);
        decreaseIndent();
        return sb;
    }

    private StringBuilder appendEdgesOfElement(final ModelElement me) {
        for (Edge edge : me.getEdges()) {
            appendln("--> ", edge.getClass().getSimpleName(), ": ", edge);
            increaseIndent();
            //appendIndent();
            ModelElement start = edge.getStart();
            String startHash = start == null ? "null" : start.getHashString();
            ModelElement end = edge.getEnd();
            String endHash = end == null ? "null" : end.getHashString();
            appendln("hash=", edge.getHashString(), "    idEdge=", System.identityHashCode(edge), "\tstartHash=", startHash, "\tendHash=", endHash);
            decreaseIndent();
        }
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
        appendIndent();
        append(objects);
        appendln();
        return sb;
    }

    private StringBuilder appendIndent() {
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
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

    @Override
    public String toString() {
        return sb.toString();
    }

    public static final void print(final GDCollection gdcoll) {
        Sys.outn(2, new GDCollectionPrinter(gdcoll));
    }

    public static final void print(final GDCollectionOwner gdcollOwner) {
        GDCollection gdcoll = gdcollOwner.getCollection();
        Sys.outn(2, new GDCollectionPrinter(gdcoll));
    }

}
