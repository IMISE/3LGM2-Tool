package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.util.htmlxml.IntendingXMLWriter;

public abstract class GraphmlWriter extends IntendingXMLWriter {

    protected final Szenario szenario;

    public GraphmlWriter(final File file, final Szenario szenario) throws XMLStreamException, IOException {
        super(file, null);
        this.szenario = szenario;
    }

    public void write(final int layer) throws XMLStreamException {
        writeStartDocument("UTF-8", "1.0");
        writeComment(getCreatedByComment());
        writeStartElementGraphml(); //start graphml
        writeKeys();
        writeGraph(layer);
        writeEndElement(); //end graphml
    }

    protected abstract String getCreatedByComment();

    protected abstract void writeStartElementGraphml() throws XMLStreamException;

    protected abstract void writeKeys() throws XMLStreamException;

    private void writeKey(final String attFor, final String attId, final Object... attributes) throws XMLStreamException {
        writeEmptyElement("key", "for", attFor, "id", attId);
        writeAttributes(attributes);
    }

    protected void writeKeyYFilesType(final String attFor, final String attId, final String yFilesType) throws XMLStreamException {
        writeKey(attFor, attId, "yfiles.type", yFilesType);
    }

    protected void writeKeyGeneralType(final String attFor, final String attId, final String attName, final String attType) throws XMLStreamException {
        writeKey(attFor, attId, "attr.name", attName, "attr.type", attType);
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

    protected abstract void writeNodeContent(NodeContainer nc) throws XMLStreamException;

    private void writeEdges(final int layer) throws XMLStreamException {
        LayerContainer lc = szenario.getLayer(layer);
    }

    public static final String getYGraphmlShapeName(final NodeContainer nc) {
        GraphElementLayout.SHAPE form = nc.getForm();
        if (form == null) {
            GraphDocument doc = nc.getGraphDocument();
            form = doc.getMapping().getStandardForm(nc.getKnoten());
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
