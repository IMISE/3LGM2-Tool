package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;

public class YEdGraphmlWriter extends GraphmlWriter {

    public YEdGraphmlWriter(final File file, final Szenario szenario) throws XMLStreamException, IOException {
        super(file, szenario);
    }

    @Override
    protected String getCreatedByComment() {
        return "Created by Tool3lgm " + Tool3lgmConstants.TOOL_VERSION;
    }

    @Override
    protected void writeXMLSchemaAttributes() throws XMLStreamException {
        writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writeAttribute("xmlns:java", "http://www.yworks.com/xml/yfiles-common/1.0/java");
        writeAttribute("xmlns:sys", "http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0");
        writeAttribute("xmlns:x", "http://www.yworks.com/xml/yfiles-common/markup/2.0");
        writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writeAttribute("xmlns:y", "http://www.yworks.com/xml/graphml");
        writeAttribute("xmlns:yed", "http://www.yworks.com/xml/yed/3");
        writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd");
    }

    private void writeKeyYFilesType(final String attFor, final String attId, final String yFilesType) throws XMLStreamException {
        writeEmptyElement("key", "for", attFor, "id", attId, "yfiles.type", yFilesType);
    }

    private void writeKeyGeneralType(final String attFor, final String attId, final String attName, final String attType) throws XMLStreamException {
        writeEmptyElement("key", "attr.name", attName, "attr.type", attType, "for", attFor, "id", attId);
    }

    @Override
    protected void writeKeys() throws XMLStreamException {
        for (TypeKeys typeKey : TypeKeys.values()) {
            String[] nameParts = typeKey.nameParts();
            if (nameParts.length == 2) { // 2 Teile -> yEd Attribute
                writeKeyYFilesType(nameParts[0], typeKey.getKeyID(), nameParts[1]);
            } else { // 3 Teile -> general Attribute
                writeKeyGeneralType(nameParts[0], typeKey.getKeyID(), nameParts[1], nameParts[2]);
            }
        }
    }

    private enum TypeKeys {
        graph_Description_string,
        port_portgraphics,
        port_portgeometry,
        port_portuserdata,
        node_url_string,
        node_description_string,
        node_nodegraphics,
        graphml_resources,
        edge_url_string,
        edge_description_string,
        edge_edgegraphics;

        public String[] nameParts() {
            StringTokenizer st = new StringTokenizer(name(), "_");
            String[] nameParts = new String[st.countTokens()];
            for (int i = 0; i < nameParts.length; i++) {
                nameParts[i] = st.nextToken();
            }
            return nameParts;
        }

        public String getKeyID() {
            return "d" + ordinal();
        }
    }

    @Override
    protected void writeGraphDescription() throws XMLStreamException {
        String description = szenario.getDescription();
        writeCDATAElementDataKey(TypeKeys.graph_Description_string.getKeyID(), description);
    }

    @Override
    protected void writeNodeContent(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d5"><![CDATA[Hallo]]></data>
        writeCDATAElementDataKey(TypeKeys.node_description_string.getKeyID(), nc.getElement().getDescription());
        //        <data key="d6">
        //...
        //      </data>
        writeStartElementDataKey(TypeKeys.node_nodegraphics.getKeyID()); // start data
        YGraphShape shape = (YGraphShape) getYGraphmlShape(nc);
        String shapeName = shape.toString();
        if (shape.isGenericNode()) {
            writeGenericNode(nc, shapeName);
        } else {
            writeShapeNode(nc, shapeName);
        }
        writeEndElement(); // end data
    }

    private void writeShapeNode(final NodeContainer nc, final String shapeName) throws XMLStreamException {
        //        <y:ShapeNode>
        //          <y:Geometry height="30.0" width="107.0" x="116.5" y="-345.0"/>
        //          <y:Fill color="#CCCCFF" transparent="false"/>
        //          <y:BorderStyle color="#000000" raised="false" type="line" width="1.0"/>
        //          <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.1328125" horizontalTextPosition="center" iconTextGap="4" modelName="custom" textColor="#000000" verticalTextPosition="bottom" visible="true" width="72.408203125" x="17.2958984375" y="5.93359375">Objekttyp 1<y:LabelModel>
        //              <y:SmartNodeLabelModel distance="4.0"/>
        //            </y:LabelModel>
        //            <y:ModelParameter>
        //              <y:SmartNodeLabelModelParameter labelRatioX="0.0" labelRatioY="0.0" nodeRatioX="0.0" nodeRatioY="0.0" offsetX="0.0" offsetY="0.0" upX="0.0" upY="-1.0"/>
        //            </y:ModelParameter>
        //          </y:NodeLabel>
        //          <y:Shape type="ellipse"/>
        //        </y:ShapeNode>
        writeStartElement("y:ShapeNode"); // start y:ShapeNode
        writeNodeGeometry(nc);
        writeEmptyElement("y:Fill", "color", getColorString(NodeRenderer.getColor(nc), false), "transparent", "false");
        writeEmptyElement("y:BorderStyle", "color", "#000000", "raised", "false", "type", "line", "width", "1.0");
        writeNodeLabel(nc);
        writeEmptyElement("y:Shape", "type", shapeName);
        writeEndElement(); // end y:ShapeNode
    }

    private void writeGenericNode(final NodeContainer nc, final String shapeName) throws XMLStreamException {
        //        <y:GenericNode configuration="com.yworks.flowchart.dataBase">
        //            <y:Geometry height="40.0" width="60.0" x="120.0" y="-363.0"/>
        //            <y:Fill color="#FFFF00" color2="#FFFF00" transparent="false"/>
        //            <y:BorderStyle color="#000000" type="line" width="1.0"/>
        //            <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.1328125" horizontalTextPosition="center" iconTextGap="4" modelName="custom" textColor="#000000" verticalTextPosition="bottom" visible="true" width="119.142578125" x="-29.5712890625" y="10.93359375">Datenbaknsystem 1<y:LabelModel>
        //                <y:SmartNodeLabelModel distance="4.0"/>
        //                </y:LabelModel>
        //                <y:ModelParameter>
        //                    <y:SmartNodeLabelModelParameter labelRatioX="0.0" labelRatioY="0.0" nodeRatioX="0.0" nodeRatioY="0.0" offsetX="0.0" offsetY="0.0" upX="0.0" upY="-1.0"/>
        //                </y:ModelParameter>
        //             </y:NodeLabel>
        //         </y:GenericNode>
        writeStartElement("y:GenericNode", "configuration", shapeName); // start y:GenericNode
        writeNodeGeometry(nc);
        String colorString = getColorString(NodeRenderer.getColor(nc), false);
        writeEmptyElement("y:Fill", "color", colorString, "color2", colorString, "transparent", "false");
        writeEmptyElement("y:BorderStyle", "color", "#000000", "type", "line", "width", "1.0");
        writeNodeLabel(nc);
        writeEndElement(); // end y:GenericNode
    }

    private void writeNodeGeometry(final NodeContainer nc) throws XMLStreamException {
        double width = nc.getWidth();
        double height = nc.getHeight();
        String x = String.valueOf(nc.getX() - width / 2);
        String y = String.valueOf(nc.getY() - height / 2);
        writeEmptyElement("y:Geometry", "height", String.valueOf(height), "width", String.valueOf(width), "x", x, "y", y);
    }

    // 3LGM oben -> unten, links -> rechts -------> yEd modelPosition, alignment
    // center, center -------> c, center
    // center, left   -------> c, left
    // center, right  -------> c, right
    // top, center -------> t, center
    // top, left -------> t, left
    // top, rigt -------> t, right
    // bottom, center -------> b, center
    // bottom, left -------> b, left
    // bottom, rigt -------> b, right

    private String getAlignment(final NodeContainer nc) {
        int halign = nc.getHalign();
        if (halign == SwingConstants.LEFT) {
            return "left";
        }
        if (halign == SwingConstants.RIGHT) {
            return "right";
        }
        return "center";
    }

    private String getModelPosition(final NodeContainer nc) {
        int valign = nc.getValign();
        if (valign == SwingConstants.TOP) {
            return "t";
        }
        if (valign == SwingConstants.BOTTOM) {
            return "b";
        }
        return "c";
    }

    private String getFontStyle(final NodeContainer nc) {
        int fontStyle = nc.getFont().getStyle();
        if (fontStyle == Font.BOLD) {
            return "bold";
        } else if (fontStyle == Font.ITALIC) {
            return "italic";
        } else if (fontStyle == Font.BOLD + Font.ITALIC) {
            return "bolditalic";
        }
        return "plain";
    }

    private final void writeNodeLabel(final NodeContainer nc) throws XMLStreamException {
        writeStartElement("y:NodeLabel"); // start y:NodeLabel
        writeAttribute("alignment", getAlignment(nc)); //################################
        writeAttribute("autoSizePolicy", "node_width");
        writeAttribute("configuration", "CroppingLabel");
        writeAttribute("fontFamily", "Dialog");
        writeAttribute("fontSize", nc.getFont().getSize());
        writeAttribute("fontStyle", getFontStyle(nc));
        writeAttribute("hasBackgroundColor", "false");
        writeAttribute("hasLineColor", "false");
        writeAttribute("height", "0.0");
        writeAttribute("horizontalTextPosition", "center");
        writeAttribute("iconTextGap", "4");
        writeAttribute("modelName", "internal");
        writeAttribute("modelPosition", getModelPosition(nc)); //################################ c, t, b, l, r, tl, tr, bl, br
        writeAttribute("textColor", "#000000");
        writeAttribute("verticalTextPosition", "bottom");
        writeAttribute("visible", !nc.hideText());
        writeAttribute("width", "0.0");
        writeAttribute("x", "0.0");
        writeAttribute("y", "0.0");
        writeCharacters(getElementName(nc));
        writeEndElement(); // end y:NodeLabel
    }

    @Override
    protected void writeEdgeContent(final EdgeContainer ec) throws XMLStreamException {
        //    <data key="d10">
        //    <y:PolyLineEdge>
        //      <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0">
        //        <y:Point x="30.0" y="-510.0"/>
        //      </y:Path>
        //      <y:LineStyle color="#000000" type="line" width="1.0"/>
        //      <y:Arrows source="none" target="delta"/>
        //      <y:EdgeLabel alignment="center" configuration="AutoFlippingLabel" distance="2.0" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" hasText="false" height="4.0" horizontalTextPosition="center" iconTextGap="4" modelName="custom" preferredPlacement="anywhere" ratio="0.5" textColor="#000000" verticalTextPosition="bottom" visible="true" width="4.0" x="88.56015555517583" y="-141.03710755185193">
        //        <y:LabelModel>
        //          <y:SmartEdgeLabelModel autoRotationEnabled="false" defaultAngle="0.0" defaultDistance="10.0"/>
        //        </y:LabelModel>
        //        <y:ModelParameter>
        //          <y:SmartEdgeLabelModelParameter angle="0.0" distance="28.47797277442824" distanceToCenter="false" position="right" ratio="0.048676984301588115" segment="-1"/>
        //        </y:ModelParameter>
        //        <y:PreferredPlacementDescriptor angle="0.0" angleOffsetOnRightSide="0" angleReference="absolute" angleRotationOnRightSide="co" distance="-1.0" frozen="true" placement="anywhere" side="anywhere" sideReference="relative_to_edge_flow"/>
        //      </y:EdgeLabel>
        //      <y:BendStyle smoothed="false"/>
        //    </y:PolyLineEdge>
        //  </data>
        writeEmptyElementDataKey(TypeKeys.edge_url_string.getKeyID());
        writeEmptyElementDataKey(TypeKeys.edge_description_string.getKeyID());
        writeStartElementDataKey(TypeKeys.edge_edgegraphics.getKeyID()); // start data
        writeStartElement("y:PolyLineEdge"); // start y:PolyLineEdge
        writeEdgePath(ec);
        writeEmptyElement("y:LineStyle", "color", getColorString(getEdgeColor(ec), false), "type", getEdgeType(ec), "width", "1.0");
        writeEdgeArrows(ec);
        writeEmptyElement("y:BendStyle", "smoothed", "false");
        writeEndElement(); // end y:PolyLineEdge
        writeEndElement(); // end data
    }

    private void writeEdgePath(final EdgeContainer ec) throws XMLStreamException {
        if (ec.getBendpointContainerCount() == 0) {
            writeEmptyElement("y:Path", "sx", "0.0", "sy", "0.0", "tx", "0.0", "ty", "0.0");
        } else {
            writeStartElement("y:Path", "sx", "0.0", "sy", "0.0", "tx", "0.0", "ty", "0.0"); // start y:Path
            for (BendpointContainer bc : ec.iterateBendpointContainers()) {
                writeEmptyElement("y:Point", "x", String.valueOf(bc.getX()), "y", String.valueOf(bc.getY()));
            }
            writeEndElement(); // end y:Path
        }
    }

    private Color getEdgeColor(final EdgeContainer ec) {
        Color color = ec.getColor();
        return color == null ? Color.BLACK : color;
    }

    private String getEdgeType(final EdgeContainer ec) {
        Edge edge = ec.getEdge();
        Class<? extends Edge> edgeClass = edge.getClass();
        String type = ModelConstants.isComposition(edgeClass) || ModelConstants.isPartOfEdge(edgeClass) ? "dashed" : "line";
        return type;
    }

    private void writeEdgeArrows(final EdgeContainer ec) throws XMLStreamException {
        Edge edge = ec.getEdge();
        int direction = edge.getDirection();
        String sourceArrow = direction == Edge.DOUBLE || direction == Edge.BACKWARD ? "delta" : "none";
        String targetArrow = direction == Edge.DOUBLE || direction == Edge.FORWARD ? "delta" : "none";
        writeEmptyElement("y:Arrows", "source", sourceArrow, "target", targetArrow);
    }

    @Override
    protected void writeResources() throws XMLStreamException {
        writeStartElementDataKey(TypeKeys.graphml_resources.getKeyID());
        writeEmptyElement("y:Resources");
        writeEndElement();
    }

    private static enum YGraphShape { // in dieser Schreibweise braucht das yEd-Format die shapes. Diese hier gehen in yFiles zwar auch, aber dann wird die Zeichenfarbe (fill-Attribut) ignoriert
        rectangle,
        triangle,
        ellipse,
        roundrectangle,
        diamond,
        hexagon,
        com_yworks_flowchart_dataBase,
        com_yworks_flowchart_predefinedProcess;

        @Override
        public String toString() {
            return isGenericNode() ? name().replaceAll("_", ".") : name();
        }

        public boolean isGenericNode() {
            return ordinal() >= com_yworks_flowchart_dataBase.ordinal();
        }
    }

    @Override
    protected YGraphShape getYGraphmlShape(final GraphElementLayout.SHAPE shape) {
        switch (shape) {
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
            return YGraphShape.com_yworks_flowchart_dataBase;
        case ordner:
            return YGraphShape.com_yworks_flowchart_predefinedProcess;
        default:
            return YGraphShape.rectangle;
        }
    }

}
