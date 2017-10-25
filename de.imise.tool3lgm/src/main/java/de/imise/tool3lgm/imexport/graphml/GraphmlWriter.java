package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.Mapping;
import de.imise.util.htmlxml.IntendingXMLWriter;

public abstract class GraphmlWriter extends IntendingXMLWriter {

    protected final Szenario szenario;

    protected final Mapping standardLayout;

    public GraphmlWriter(final File file, final Szenario szenario) throws XMLStreamException, IOException {
        super(file, null);
        this.szenario = szenario;
        standardLayout = szenario.getMapping();
    }

    public void write(final int layer) throws XMLStreamException {
        writeStartDocument("UTF-8", "1.0");
        writeStartElementGraphml(); //start graphml
        writeKeys();
        writeGraph(layer);
        writeResources();
        writeEndElement(); //end graphml
    }

    protected abstract String getCreatedByComment();

    protected abstract void writeStartElementGraphml() throws XMLStreamException;

    protected abstract void writeKeys() throws XMLStreamException;

    protected void writeKeyYFilesType(final String attFor, final String attId, final String yFilesType) throws XMLStreamException {
        writeEmptyElement("key", "for", attFor, "id", attId, "yfiles.type", yFilesType);
    }

    protected void writeKeyGeneralType(final String attFor, final String attId, final String attName, final String attType) throws XMLStreamException {
        writeEmptyElement("key", "attr.name", attName, "attr.type", attType, "for", attFor, "id", attId);
    }

    private void writeGraph(final int layer) throws XMLStreamException {
        writeStartElement("graph", "edgedefault", "directed", "id", "G"); // start graph
        writeNodes(layer);
        writeEdges(layer);
        writeEndElement(); // end graph
    }

    private void writeNodes(final int layer) throws XMLStreamException {
        LayerContainer lc = szenario.getLayer(layer);
        for (NodeContainer nc : lc.getKnoten()) {
            writeStartElement("node", "id", nc.getHashString()); // start node
            writeNodeContent(nc);
            writeEndElement(); // end node
        }
    }

    protected void writeStartElementDataKey(final String key) throws XMLStreamException {
        writeStartElement("data", "key", key);
    }

    protected void writeEmptyElementDataKey(final String key) throws XMLStreamException {
        writeEmptyElement("data", "key", key);
    }

    protected abstract void writeNodeContent(NodeContainer nc) throws XMLStreamException;

    private void writeEdges(final int layer) throws XMLStreamException {
        LayerContainer lc = szenario.getLayer(layer);
        for (EdgeContainer ec : lc.getKanten()) {
            Edge edge = ec.getEdge();
            writeStartElement("edge", "id", edge.getHashString(), "source", edge.getStart().getHashString(), "target", edge.getEnd().getHashString()); // start egde
            writeEdgeContent(ec);
            writeEndElement(); // end edge
        }
    }

    protected abstract void writeEdgeContent(EdgeContainer ec) throws XMLStreamException;

    protected abstract void writeResources() throws XMLStreamException;

    public static final String getYGraphmlShapeName(final NodeContainer nc) {
        GraphElementLayout.SHAPE form = nc.getForm();
        if (form == null) {
            form = nc.getGraphDocument().getMapping().getStandardForm(nc);
        }
        switch (form) {
        case dreieck:
            return "triangle";
        case oval:
            return "ellipse";
        case rundeck:
            return "roundrectangle";
        case rhombus:
            return "diamond";
        case wabe:
            return "hexagon";
        case tonne:
            return "hexagon";
        case ordner:
            return "hexagon";
        default:
            return "rectangle";
        }
    }

}
