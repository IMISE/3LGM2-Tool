package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.util.htmlxml.IntendingXMLWriter;

public abstract class GraphmlWriter extends IntendingXMLWriter {

    protected final Szenario szenario;

    protected final ElementsLayoutDefinition standardLayout;

    protected final int layer;

    /** Abstand zwischen den Ebenen */
    protected int LAYER_OFFSET_X = GraphDocument.INITIAL_PAGE_WIDTH / 10, LAYER_OFFSET_Y = GraphDocument.INITIAL_PAGE_HEIGHT / 10;

    /**
     * Offset der 0-Punkte der auktuellen Ebene ausgehend von der ersten Ebene
     */
    protected int currentSzenarioLayerOffsetX = 0, currentSzenarioLayerOffsetY = 0;

    /**
     * Offset der 0 Punkte der Elemente der aktuellen Ebene ausgehend von der
     * ersten Ebene
     */
    protected int currentSzenarioLayerElementsOffsetX = 0, currentSzenarioLayerElementsOffsetY = 0;

    public GraphmlWriter(final File file, final Szenario szenario, final int layer) throws XMLStreamException, IOException {
        super(file, null);
        this.szenario = szenario;
        this.layer = layer;
        standardLayout = szenario.getMapping();
    }

    public void write() throws XMLStreamException, IOException {
        writeStartDocument("UTF-8", "1.0", false);
        writeComment(getCreatedByComment());
        writeStartElement("graphml"); //start graphml
        writeXMLSchemaAttributes();
        writeKeys();
        writeGraph(layer, "G");
        writeResources();
        writeEndElement(); //end graphml
    }

    protected abstract String getCreatedByComment();

    protected abstract void writeXMLSchemaAttributes() throws XMLStreamException;

    protected abstract void writeKeys() throws XMLStreamException;

    protected abstract void writeGraphDescription() throws XMLStreamException;

    private void writeGraph(final int layer, final String graphID) throws XMLStreamException {
        writeStartElementGraph(graphID); // start graph
        writeLayer(layer, graphID);
        writeEndElement(); // end graph
    }

    private void writeStartElementGraph(final String id) throws XMLStreamException {
        writeStartElement("graph", "id", id, "edgedefault", "directed"); // start graph
    }

    private void writeStartElementNode(final String id) throws XMLStreamException {
        writeStartElement("node", "id", id); // start node
    }

    private final void writeLayer(final int layer, final String graphID) throws XMLStreamException {
        if (layer >= 0) {
            String subGraphIdPrefix = "G".equals(graphID) ? null : graphID;
            writeNodes(layer, subGraphIdPrefix);
            writeEdges(layer, subGraphIdPrefix);
        } else {
            for (int l = 0; l < ModelConstants.VISIBLE_LAYERS.length; l++) {
                updateCurrentSzenarioLayerOffset(l);
                String layerId = "n" + l;
                writeStartElementNode(layerId); // start node
                int layerIndex = ModelConstants.VISIBLE_LAYERS[l];
                writeLayerNodeData(layerIndex);
                writeGraph(layerIndex, layerId + ":");
                writeEndElement(); // end node
            }
        }
    }

    private void updateCurrentSzenarioLayerOffset(final int layerOffsetIndex) {
        int layerWidth = szenario.getPageWidth();
        int layerHeight = szenario.getPageHeight();
        currentSzenarioLayerOffsetX = layerOffsetIndex * layerWidth + layerOffsetIndex * LAYER_OFFSET_X;
        currentSzenarioLayerOffsetY = 0;
        currentSzenarioLayerElementsOffsetX = currentSzenarioLayerOffsetX + layerWidth / 2;
        currentSzenarioLayerElementsOffsetY = currentSzenarioLayerOffsetY + layerHeight / 2;
    }

    /** Schreibt die Data-Tags für den Layer-Knoten */
    protected abstract void writeLayerNodeData(final int layer) throws XMLStreamException;

    private void writeNodes(final int layer, final String idPrefix) throws XMLStreamException {
        String fullIdPrefix = Strings.isNullOrEmpty(idPrefix) ? null : idPrefix + ":";
        LayerContainer lc = szenario.getLayer(layer);
        for (NodeContainer nc : lc.getGraphNodeContainers()) {
            if (nc.isVisible()) {
                writeNode(nc, fullIdPrefix);
            }
        }
    }

    protected void writeNode(final NodeContainer nc, final String idPrefix) throws XMLStreamException {
        String id = Strings.isNullOrEmpty(idPrefix) ? nc.getID() : idPrefix + nc.getID();
        writeStartElementNode(id); // start node
        writeNodeContent(nc);
        writeEndElement(); // end node
    }

    protected final void writeElementDataKey(final String key, final String text) throws XMLStreamException {
        writeStartElementDataKey(key); //start data
        writeCharacters(text);
        writeEndElement(); // end data
    }

    protected final void writeStartElementDataKey(final String key) throws XMLStreamException {
        writeCDATAElementDataKey(key, null); // start data
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
     * Schreibt das Tag element. Ist cdata <code>null</code>, dann wird nur ein
     * StartElement, aber kein EndElement geschrieben. Ist cdata ein leerer
     * String "", dann wird nur ein leeres Element geschrieben (EmptyElement).
     * Ist cdata ein gültiger String, dann wird nur ein StartElement, dann cdata
     * und zuletzt ein EndElement geschrieben.
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

    private void writeEdges(final int layer, final String idPrefix) throws XMLStreamException {
        String fullIdPrefix = Strings.isNullOrEmpty(idPrefix) ? null : idPrefix + ":";
        LayerContainer lc = szenario.getLayer(layer);
        for (EdgeContainer ec : lc.getEdgeContainers()) {
            Edge edge = ec.getEdge();
            if (ec.isVisible(false, true)) {
                String id = edge.getID();
                String startId = edge.getStart().getID();
                String endId = edge.getEnd().getID();
                if (!Strings.isNullOrEmpty(idPrefix)) {
                    id = fullIdPrefix + id;
                    startId = fullIdPrefix + startId;
                    endId = fullIdPrefix + endId;
                }
                writeStartElement("edge", "id", id, "source", startId, "target", endId); // start egde
                writeEdgeContent(ec);
                writeEndElement(); // end edge
            }
        }
    }

    protected abstract void writeEdgeContent(EdgeContainer ec) throws XMLStreamException;

    protected abstract void writeResources() throws XMLStreamException, IOException;

    protected String getElementName(final ElementContainer ec) {
        ModelElement me = ec.getElement();
        String nameExtension = me.getNameExtension();
        String name = nameExtension.isEmpty() ? me.getClearName() : me.getClearName() + "\n" + nameExtension;
        return name;
    }

    public static final Color getColor(final NodeContainer nc) {
        Color col = nc.getColor();
        if (col == null) {
            GraphDocument doc = nc.getGraphDocument();
            col = doc.getMapping().getStandardBackGroundColor(nc);
        }
        return col;
    }

}
