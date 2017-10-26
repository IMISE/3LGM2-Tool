package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
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
import de.imise.util.htmlxml.HTMLConverter;
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
        writeComment(getCreatedByComment());
        writeStartElementGraphml(); //start graphml
        writeKeys();
        writeGraph(layer);
        writeResources();
        writeEndElement(); //end graphml
    }

    protected abstract String getCreatedByComment();

    protected abstract void writeStartElementGraphml() throws XMLStreamException;

    protected abstract void writeKeys() throws XMLStreamException;

    protected abstract void writeGraphDescription() throws XMLStreamException;

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

    protected final void writeStartElementDataKey(final String key) throws XMLStreamException {
        writeCDATAElementDataKey(key, null);

    }

    protected final void writeEmptyElementDataKey(final String key) throws XMLStreamException {
        writeCDATAElementDataKey(key, "");
    }

    protected final void writeCDATAElementDataKey(final String key, final String cdata) throws XMLStreamException {
        writeElement("data", cdata, "key", key);
    }

    protected void writeCDATAElement(final String element, final String cdata, final String... attributes) throws XMLStreamException {
        writeElement(element, cdata, attributes);
    }

    /**
     * Schreibt das Tag element.
     * Ist cdata <code>null</code>, dann wird nur ein StartElement, aber kein EndElement geschrieben.
     * Ist cdata ein leerer String "", dann wird nur ein leeres Element geschrieben (EmptyElement).
     * Ist cdata ein gültiger String, dann wird nur ein StartElement, dann cdata und zuletzt ein EndElement geschrieben.
     *
     * @param element
     * @param cdata
     * @param attributes
     * @throws XMLStreamException
     */
    private void writeElement(final String element, final String cdata, final String... attributes) throws XMLStreamException {
        if (cdata == null) {
            writeStartElement(element, attributes);
        } else if (cdata.isEmpty()) {
            writeEmptyElement(element, attributes);
        } else {
            writeStartElement(element, attributes);
            writeCDATA(cdata);
            writeEndElement();
        }
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

    public static enum YGraphShape {
        rectangle,
        triangle,
        ellipse,
        roundrectangle,
        diamond,
        hexagon;

        public String upperCaseName() {
            return name().toUpperCase();
        }
    }

    public static final YGraphShape getYGraphmlShapeName(final NodeContainer nc) {
        GraphElementLayout.SHAPE form = nc.getForm();
        if (form == null) {
            form = nc.getGraphDocument().getMapping().getStandardForm(nc);
        }
        switch (form) {
        case dreieck:
            return YGraphShape.triangle;
        case oval:
            return YGraphShape.ellipse;
        case rundeck:
            return YGraphShape.roundrectangle;
        case rhombus:
            return YGraphShape.diamond;
        case wabe:
            return YGraphShape.hexagon;
        case tonne:
            return YGraphShape.hexagon;
        case ordner:
            return YGraphShape.hexagon;
        default:
            return YGraphShape.rectangle;
        }
    }

    private final StringBuilder colorBuilder = new StringBuilder("#");

    protected String getColorString(final Color color) {
        colorBuilder.setLength(1);
        HTMLConverter.appendHTMLColor(colorBuilder, color == null ? Color.black : color);
        return colorBuilder.toString();
    }

}
